package cliente;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class GestorPartidas {

	// URI del recurso que permite acceder al juego
	final private String baseURI = "http://localhost:8080/com.flota.ws/servicios/partidas/";
	Client cliente = null;
	// Para guardar el target que obtendrá con la operación nuevaPartida y que le permitirá jugar la partida creada
	private WebTarget targetPartida = null;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de la clase
	 * Crea el cliente
	 */
	public GestorPartidas()  {
		this.cliente = ClientBuilder.newClient();
	}

	/**
	 * Crea una nueva partida
	 * Realiza una peticion POST a la URI {baseURI}/{numFilas}/{numColumnas}/{numBarcos}
	 * @param	numFilas	numero de filas del tablero
	 * @param	numColumnas	numero de columnas del tablero
	 * @param	numBarcos	numero de barcos
	 */
	public void nuevaPartida(int numFilas, int numColumnas, int numBarcos)   {

		Response response = cliente.
				target(baseURI).path(numFilas+"/"+numColumnas+"/"+numBarcos)
				.request().post(Entity.xml(""));

		if (response.getStatus() != 201) throw new RuntimeException("Fallo al crear partida. Error "+ response.getStatus());
		// Obtiene la informació sobre el URI del nuevo recurso partida de la cabecera 'Location' en la respuesta
		String recursoPartida = response.getLocation().toString();
		// se guarda la URI de la partida en el atributo targetPartida para poder usarla en los otros metodos que acceden a ella
		this.targetPartida = cliente.target(recursoPartida);
		response.close();

		System.out.println("Instancio una nueva partida con id: " + recursoPartida);
	}

	/******
	RESPUESTAS A LAS PREGUNTAS:
	a) El objeto se crea en el servidor en cuanto el cliente llama a 
					cliente.target(baseURI).path(numFilas+"/"+numColumnas+"/"+numBarcos).request().post(Entity.xml(""));
		pero se guarda una referencia a la partida que se le devuelve con el Location de la respuesta,
		siendo este el identificador de la partida.
		
	b) Recibe el lugar donde se puede encontrar la partida y se lo guarda como una variable local en this.targetPartida
	
	c) Poder decirle al servidor de qué partida es propietario, para que sólo haya un cliente por partida.
	******/
	
	/**
	 * Borra la partida en juego
	 */
	public void borraPartida()   {		
        Response response = targetPartida.request().delete();
        if (response.getStatus()/10 != 20) throw new RuntimeException("Fallo al borrar partida");
	}



	/**
	 * Prueba una casilla y devuelve el resultado
	 * @param	fila	fila de la casilla
	 * @param	columna	columna de la casilla
	 * @return			resultado de la prueba: AGUA, TOCADO, ya HUNDIDO, recien HUNDIDO
	 */
	public int pruebaCasilla( int fila, int columna)   {
		
		Response response = targetPartida
				.queryParam("fila", fila)
				.queryParam("columna", columna)
				.request(MediaType.TEXT_PLAIN)
				.put(Entity.text(""));
		if (response.getStatus()/10 != 20) throw new RuntimeException("Fallo al acceder a pruebaCasilla");
		String cadena = response.readEntity(String.class);
		response.close();
		
		return Integer.parseInt(cadena);
	}

	/**
	 * Obtiene los datos de un barco.
	 * @param	idBarco	identificador del barco
	 * @return			cadena con informacion sobre el barco "fila#columna#orientacion#tamanyo"
	 */
	public String getBarco( int idBarco)   {
        
		Response response = targetPartida.path(""+idBarco).request().get();
		if (response.getStatus()/10 != 20) throw new RuntimeException("Fallo al acceder a getBarco");
		String cadena = response.readEntity(String.class);
		response.close();
		return cadena; // A MODIFICAR
	}


	/**
	 * Devuelve la informacion sobre todos los barcos
	 * Realiza una peticion GET a la URI {baseURI}/{idPartida}/solucion
	 * @return			vector de cadenas con la informacion de cada barco
	 */
	protected String[] getSolucion() {
		String cadena = targetPartida.path("/solucion")
				.request().get(String.class);
		try {
			// Instancia el constructor de objetos de tipo Document
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Obtiene la informacion del cuerpo de la peticion
			Document doc = builder.parse(new InputSource(new StringReader(cadena)));
			// Devuelve el resultado de convertir la informacion de formato XML a un vector de cadenas
			return XMLASolucion(doc);
		}
		catch (Exception e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
	}
	
	/**
	 * Procesa un Document XML y lo convierte en la solucion de la partida
	 * @return			vector de cadenas con la informacion de cada barco
	 */
	protected String[] XMLASolucion(Document doc) {
		int numBarcos=0;
		Element root = doc.getDocumentElement(); // Accede a la etiqueta raiz: 'solucion'
		// Obtiene el numero de barcos del atributo 'tam'
		if (root.getAttribute("tam") != null && !root.getAttribute("tam").trim().equals(""))
			numBarcos = Integer.valueOf(root.getAttribute("tam"));
		// Accede a la informacion de los barcos a partir de las etiquetas 'barco'
		// y la almacena en el vector de cadenas a devolver
		NodeList nodes = root.getChildNodes();
		String[] solucion = new String[numBarcos];
		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);
			if (element.getTagName().equals("barco")) {
				solucion[i] = element.getTextContent();
			}
			else System.out.println("[getSolucion: ] Error en el nombre de la etiqueta");
		}
		return solucion;
	}


} // fin clase
