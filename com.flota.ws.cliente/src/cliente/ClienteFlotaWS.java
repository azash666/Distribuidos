package cliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class ClienteFlotaWS {


	// Datos para inicializar la partida.
	private static final int NUMFILAS=8, NUMCOLUMNAS=8, NUMBARCOS=6;

	// Atributos de la partida guardados en el juego para simplificar su implementación.
	private int quedan = NUMBARCOS, disparos = 0;

	// El juego se encarga de crear y modificar la interfaz gráfica.
	private GuiTablero guiTablero = null;

	// Lo utilizaremos para llamar a metodos para la nueva partida.
	GestorPartidas partida;


	/**
	 * Lo que vamos a realizar con esta clase es iniciar un contacto con el servidor mediante
	 * RMI ina ves que ya hemos recivdo mediante esto IntServidorRMI, solicitamos una IntServidorPartidasRMI.
	 * */

	public static void main(String args[]) {

		System.out.println("**************** Client ****************");
		ClienteFlotaWS cliente = new ClienteFlotaWS();
		cliente.run();
		cliente.ejecuta();
	} //end main


	/**
	 * Se encarga de realizar la conexión con el servidor mediante RMI.
	 * */

	private void run(){

		try {
			
			//Inicializacion del gestor y creación de la nueva partida
			partida = new GestorPartidas();
			partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
			
			
		} // end try
		catch (Exception e) {
			System.out.println(
					"Exception in ClienteFlota: " + e);
		}
	} // end run

	private void ejecuta() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				guiTablero = new GuiTablero(NUMFILAS, NUMCOLUMNAS);
				guiTablero.dibujaTablero();
			}
		});
	} // end ejecuta


	/******************************************************************************************/
	/*********************  CLASE INTERNA GuiTablero   ****************************************/
	/******************************************************************************************/
	private class GuiTablero {

		private int numFilas, numColumnas;

		private JFrame frame = null;        // Tablero de juego
		private JLabel estado = null;       // Texto en el panel de estado
		private JButton buttons[][] = null; // Botones asociados a las casillas de la partida

		/**
		 * Constructor de una tablero dadas sus dimensiones
		 */
		GuiTablero(int numFilas, int numColumnas) {
			this.numFilas = numFilas;
			this.numColumnas = numColumnas;
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		/**
		 * Dibuja el tablero de juego y crea la partida inicial
		 */
		public void dibujaTablero() {
			anyadeMenu();
			anyadeGrid(numFilas, numColumnas);
			anyadePanelEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
			frame.setSize(300, 300);
			frame.setVisible(true);
		} // end dibujaTablero

		/**
		 * Anyade el menu de opciones del juego y le asocia un escuchador
		 */
		private void anyadeMenu() {
			// POR IMPLEMENTAR
			JMenuBar menuBar;		//La barra del menu

			menuBar = new JMenuBar(); // Crea MenuBar
			MenuListener escuchador = new MenuListener();

			menuOpciones(menuBar, escuchador);

			frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		} // end anyadeMenu

		private void menuOpciones(JMenuBar menuBar, MenuListener escuchador) {
			JMenu menu;
			//Añadimos el menuListener

			//Añadimos los JMenuItem
			menu = new JMenu("Opciones");
			menuBar.add(menu); //Añadimos el menu a la barra de menu.
			JMenuItem menuItem;		//Para hacer cada uno de los items del menu

			menuItem = new JMenuItem("Mostrar solucion");
			menuItem.putClientProperty("dat","solucion");
			menuItem.addActionListener(escuchador);
			menu.add(menuItem);
			menuItem = new JMenuItem("Nueva partida");
			menuItem.putClientProperty("dat","nueva");
			menuItem.addActionListener(escuchador);
			menu.add(menuItem);
			menuItem = new JMenuItem("Salir");
			menuItem.putClientProperty("dat","salir");
			menuItem.addActionListener(escuchador);
			menu.add(menuItem);
		}

		


		/**
		 * Anyade el panel con las casillas del mar y sus etiquetas.
		 * Cada casilla sera un boton con su correspondiente escuchador
		 * @param nf	numero de filas
		 * @param nc	numero de columnas
		 */
		private void anyadeGrid(int nf, int nc) {
			JPanel panelGrill = new JPanel();
			GridLayout gl = new GridLayout(nf+1,nc+2);
			gl.setHgap(0);
			gl.setVgap(0);
			buttons = new JButton[nf][nc];
			panelGrill.setLayout(gl);
			configurarRegilla(panelGrill,nf+1,nc+2);
			frame.getContentPane().add(panelGrill, BorderLayout.CENTER);


		} // end anyadeGrid


		/**Método auxiliar.*/
		private void configurarRegilla(JPanel panel, int nf, int nc){
			ButtonListener escuchador = new ButtonListener();
			int a = (int)'A';
			char[] letras = new char[26];
			for (int i = 0; i< letras.length; i++) {
				letras[i] = (char) (a + i);
			}


			for (int i=0; i<nc; i++)
				//Primero añadimos los numeros

				if (i>0 && i<nc-1) 	panel.add(new JLabel("  "+i));
				else				panel.add(new JLabel(""));

			for (int fila = 0; fila < nf-1; fila++)
				for (int columna = 0; columna < nc; columna++) {
					if(columna >0 && columna < nc-1) {
						//Ahora añadimos los botones
						buttons[fila][columna-1] = new JButton();
						buttons[fila][columna-1].putClientProperty("fila", fila);
						buttons[fila][columna-1].putClientProperty("columna", columna-1);
						buttons[fila][columna-1].addActionListener(escuchador);
						panel.add(buttons[fila][columna-1]);
					}else {
						//Ahora añadimos las letras de inicio y de final
						panel.add(new JLabel("  "+letras[fila]));
					}
				}

		}


		/**
		 * Anyade el panel de estado al tablero
		 * @param cadena	cadena inicial del panel de estado
		 */
		private void anyadePanelEstado(String cadena) {
			JPanel panelEstado = new JPanel();
			estado = new JLabel(cadena);
			panelEstado.add(estado);
			// El panel de estado queda en la posición SOUTH del frame
			frame.getContentPane().add(panelEstado, BorderLayout.SOUTH);
		} // end anyadePanel Estado

		/**
		 * Cambia la cadena mostrada en el panel de estado
		 * @param cadenaEstado	nuevo estado
		 */
		public void cambiaEstado(String cadenaEstado) {
			estado.setText(cadenaEstado);
		} // end cambiaEstado

		/**
		 * Muestra la solucion de la partida y marca la partida como finalizada
		 */
		public void muestraSolucion() {

			/**Recorremos la cuadricula poniendo los cuadrados de agua y los boarcos en morado.*/

			for (int i = 0; i < NUMFILAS; i++){
				for (int j = 0; j < NUMCOLUMNAS; j++)
				{
					JButton aux = buttons[i][j];
					int dato;
					dato = partida.pruebaCasilla(i,j);
					if(dato == -1)
						guiTablero.pintaBoton(aux, Color.CYAN);
					else
						guiTablero.pintaBoton(aux, Color.MAGENTA);
				}
			}
		} // end muestraSolucion


		/**
		 * Pinta un barco como hundido en el tablero
		 * @param cadenaBarco	cadena con los datos del barco codifificados como
		 *                      "filaInicial#columnaInicial#orientacion#tamanyo"
		 */
		public void pintaBarcoHundido(String cadenaBarco) {
			String[] parts = cadenaBarco.split("#");

			int cantidad = Integer.parseInt(parts[3]),
					fila = Integer.parseInt(parts[0]),
					columna = Integer.parseInt(parts[1]);


			for (int i = 0; i < cantidad; i++)
			{
				guiTablero.pintaBoton(buttons[fila][columna],Color.RED);
				if(parts[2].equals("H"))
					columna++;
				else
					fila++;
			}
		} // end pintaBarcoHundido

		/**
		 * Pinta un botón de un color dado
		 * @param b			boton a pintar
		 * @param color		color a usar
		 */
		public void pintaBoton(JButton b, Color color) {
			b.setBackground(color);
			// El siguiente código solo es necesario en Mac OS X
			b.setOpaque(true);
			b.setBorderPainted(false);
		} // end pintaBoton

		/**
		 * Limpia las casillas del tablero pintándolas del gris por defecto
		 */
		public void limpiaTablero() {
			for (int i = 0; i < numFilas; i++) {
				for (int j = 0; j < numColumnas; j++) {
					buttons[i][j].setBackground(null);
					buttons[i][j].setOpaque(true);
					buttons[i][j].setBorderPainted(true);
				}
			}
		} // end limpiaTablero

		/**
		 * 	Destruye y libera la memoria de todos los componentes del frame
		 */
		public void liberaRecursos() {
			frame.dispose();
		} // end liberaRecursos


	} // end class GuiTablero

	/******************************************************************************************/
	/*********************  CLASE INTERNA MenuListener ****************************************/
	/******************************************************************************************/

	/**
	 * Clase interna que escucha el menu de Opciones del tablero
	 *
	 */
	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem dat = (JMenuItem) e.getSource();	//Elección del menú

			switch((String)dat.getClientProperty("dat")){
			case "solucion":
				guiTablero.muestraSolucion();
				break;
			case "nueva":
				partida.borraPartida();
				partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
				disparos = 0;
				quedan = NUMBARCOS;
				guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
				guiTablero.limpiaTablero();
				break;
			case "salir":
				partida.borraPartida();
				guiTablero.liberaRecursos();
				break;
			}

		} // end actionPerformed

	} // end class MenuListener



	/******************************************************************************************/
	/*********************  CLASE INTERNA ButtonListener **************************************/
	/******************************************************************************************/
	/**
	 * Clase interna que escucha cada uno de los botones del tablero
	 * Para poder identificar el boton que ha generado el evento se pueden usar las propiedades
	 * de los componentes, apoyandose en los metodos putClientProperty y getClientProperty
	 */
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// POR IMPLEMENTAR

			if(quedan == 0)return;/**Si ya no quedan barcos no ocurre nada.*/

			disparos++;
			JButton aux = (JButton) e.getSource();	/**Obtenemos el botón de la cuadrícula.*/
			int valorBoton = 0;
			valorBoton = partida.pruebaCasilla((int)aux.getClientProperty("fila"),(int)aux.getClientProperty("columna"));

			if(aux.isBorderPainted()) { /**Si el botón no se ha pulsado*/
				Color color = null;
				/**Según el valor le ponemos un valor u otro.*/
				switch (valorBoton){
				case -1: color=Color.CYAN;break;
				case -2: color=Color.orange;break;
				case -3: color=Color.RED;break;
				default:
				}

				guiTablero.pintaBoton(aux, color);

				/**Si el barco se ha undido*/
				if(valorBoton==-3){
					valorBoton = partida.pruebaCasilla((int)aux.getClientProperty("fila"),(int)aux.getClientProperty("columna"));
					guiTablero.pintaBarcoHundido(partida.getBarco(valorBoton));
					/**Quitamos un barco del marcador.*/
					quedan--;
				}
			}

			/**Si ya no quedan barcos, el marcador pasa a la pantalla final,
			 * de lo contrario actualiza el marcador.
			 * */
			if(quedan == 0)	guiTablero.cambiaEstado("GAME OVER en " + disparos + " disparos.");
			else      		guiTablero.cambiaEstado("Disparos: " + disparos + "    Barcos restantes: " + quedan);
		} // end actionPerformed

	} // end class ButtonListener

	

}
