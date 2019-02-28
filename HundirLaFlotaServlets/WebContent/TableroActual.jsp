<%@ page language="java" contentType="text/html; charset=ISO-8859-1" 
	import = "modelo.Partida"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Partida</title>
	<style>
		table, th, td {
			text-align: center;
		}
		form{
			text-align: center;
		}
		table {
		
			margin-left: auto; 
			margin-right: auto;
		}
		div{
			margin-left: 25%;
		}
		.Agua{
			background-color: blue;
		}
		.Tocado{
			background-color: yellow;
		}
		.Hundido{
			background-color: red;
		}
	</style>
</head>
<body>
	<div>
		<h1>Hundir la Flota</h1>
		
		<%
			String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
					 "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
			Partida partida = (Partida) request.getAttribute("Partida");
			int NUMFILAS = (Integer) request.getAttribute("filas");
			int NUMCOLUMNAS = (Integer) request.getAttribute("columnas");
			int NUMBARCOS = (Integer) request.getAttribute("barcos");
			int f = -1, c=-1;
			if(!(Boolean) request.getAttribute("nueva")){
				f = (Integer) request.getAttribute("fila");
				c = (Integer) request.getAttribute("columna");
			}
			
			String texto = "";
			
			if(f>=0){
				
				texto = "Página de resultados del disparo en ("+f+", "+c+"):";
				
				if(partida.getCasilla(f, c)>=-3) texto+=": Ok!";
			}else
				if(f==-1){
					texto = "NUEVA PARTIDA";
				}
			if(partida.barcosQueQuedan()==0) texto = "GAME OVER";
			out.print(texto);
			out.print("<br/>Barcos navegando: "+partida.barcosQueQuedan()+"<br/>Barcos hundidoa: "+(NUMBARCOS-partida.barcosQueQuedan()));
			out.print("<br/>Número de disparos efectuados: "+partida.getNumDisparos());
			out.print("</div>");
			out.print("<form action=\"HundirFlotaServlet\" method=\"GET\">");
			out.print("<table>");
			//La construcción de la tabla se hace así porque de esta forma más adelante podemos añadir un número diferente de filas y columnas.
			for(int fila = 0; fila<NUMFILAS+1; fila++){
				out.print("<tr>");
				for(int col = 0; col<NUMCOLUMNAS+2; col++){
					out.print("<td width=\"60 px\" class=\""+
							(
									//Aquí hastamos tres if ternarios anidados. De esta forma cambiamos las class del mar para que se muestren los colores de una forma automática
								col>0 && col<NUMCOLUMNAS+1 && fila>0 && partida.casillaDisparada(fila-1, col-1)
								? 
									partida.getCasilla(fila-1, col-1)==-1
									? 
										"Agua"
									:
										partida.estaHundido(partida.getCasilla(fila-1, col-1))
										?
											"Hundido"
										:
											"Tocado"
								:
									" "
							)
							+"\">");
					if(fila == 0){
						if (col == 0 || col == NUMCOLUMNAS +1){
							out.print(" ");
						}else{
							out.print(letras[(col-1)%letras.length]);
						}
					}else{
						if (col == 0 || col == NUMCOLUMNAS +1){
							out.print(fila);
						}else{
							// En caso de que se haya disparado no queremos que haya la opción de volver a disparar sobre la misma casilla. Para ello la marcamos como readonly.
							out.print("<input type=\"radio\" name=\"tablero\" value=\""+(fila-1)+"#"+(col-1)+"\" "+(partida.casillaDisparada(fila-1, col-1) || partida.barcosQueQuedan()==0?"disabled":"required")+"/>");
						}
					}
					out.print("</td>");
				}
				out.print("</tr>");
			}
			out.print("</table>");
			if(partida.barcosQueQuedan()!=0) out.print("<input type=\"submit\" value=\"Submit\"/>");
			out.print("</form>");
		%>
	<div>
		<a href="SolucionPartidaServlet">Muestra Solución</a><br/>
		<a href="NuevaPartidaServlet">Nueva Partida</a><br/>
		<a href="">Salir</a>
	</div>
</body>
</html>