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


public class GestorContactos {

	// URI del recurso que permite acceder a la agenda
	final private String baseURI = "http://localhost:8080/AgendaWS/servicios/agenda/";
	Client cliente = null;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de la clase
	 */
	public GestorContactos()  {
		this.cliente = ClientBuilder.newClient();
	}

	/**
	 * Crea un nuevo contacto
	 * Realiza una peticion POST a la URI {baseURI}
	 * @param	nombre		nombre del nuevo contacto
	 * @param	telefono	telefono del nuevo contacto
	 * @throws	RuntimeException	si falla la creación
	 */
	public String nuevoContacto(String nombre, String telefono) throws RuntimeException  {

		String cadenaContacto = nombre + "#" + telefono;

		// Crea el nuevo contacto mediante un POST pasando sus datos en el cuerpo del mensaje
		Response response = cliente.target(baseURI)
				.request().post(Entity.text(cadenaContacto));

		if (response.getStatus() != 201) 
			throw new RuntimeException("Fallo al crear el contacto " + cadenaContacto); // 201 = CREATED
		// Obtiene la informació sobre el URI del nuevo recurso contacto de la cabecera 'Location' en la respuesta
		String recursoContacto = response.getLocation().toString();
		response.close();

		//System.out.println("Instancio un nuevo contacto con URI: " + recursoContacto);
		return recursoContacto;
	}

	/**
	 * Borra un contacto con un identificador dado
	 * Realiza una peticion DELETE a la URI {baseURI}/{idContacto}
	 * @param	nombre	identificador del contacto a borrar
	 * @throws	NotFoundException	si no se ha encontrado el contacto
	 */
	public void borraContacto(int idContacto) throws NotFoundException  {	
		// Borra el contacto mediante un DELETE pasando su identificador en la ruta del URI
		Response response = cliente.target(baseURI).path(""+idContacto)
				.request().delete();
		if (response.getStatus() == 404) { // 404 = NOT_FOUND
			throw new NotFoundException("Contacto a borrar con id: " + idContacto + " no encontrado");
		}	  
		response.close();
	}

	/**
	 * Actualiza los datos de un contacto con un identificador dado
	 * Realiza una peticion PUT a la URI {baseURI}/{idContacto}?nombre={nombre}&telefono={telefono}
	 * @param	idContacto	identificador del contacto a borrar
	 * @param	nombre		nuevo nombre del contacto
	 * @param	telefono	nuevo teléfono del contacto
	 * @throws	NotFoundException	si no se ha encontrado el contacto
	 */
	public void actualizaContacto( int idContacto, String nombre, String telefono) throws NotFoundException  {
		// Actualiza el contacto mediante un PUT pasando 
		// el identifiador en la ruta del URI
		// y los nuevos datos en la cadena de consulta (query string).
		// Pasamos el cuerpo como un texto vacío porque PUT no admite null como argumento
		Response response = cliente.target(baseURI).path(""+idContacto)
				.queryParam("nombre", nombre)
				.queryParam("telefono", telefono)
				.request()
				.put(Entity.text(""));

		if (response.getStatus() == 404) { // 404 = NOT_FOUND
			throw new NotFoundException("Contacto a actualizar con id: " + idContacto + " no encontrado");
		}

		response.close();
	}

	/**
	 * Obtiene los datos de un contacto.
	 * Realiza una peticion GET a la URI {baseURI}/{idContacto}
	 * @param	idContacto	identificador del contacto
	 * @return			cadena con informacion sobre el barco "nombre#numero"
	 * @throws	NotFoundException	si no se ha encontrado el contacto
	 */
	public String getContacto( int idContacto) throws NotFoundException  {
		Response response = cliente.target(baseURI).path(""+idContacto)
				.request(MediaType.TEXT_PLAIN).get();

		if (response.getStatus() == 404) { // 404 = NOT_FOUND
			throw new NotFoundException("Contacto a leer con id: " + idContacto + " no encontrado");
		}
		else {
			// Lee la cadena con el contacto del cuerpo (Entity) del mensaje
			String cadenaContacto = response.readEntity(String.class);
			response.close();
			return cadenaContacto;
		}
	}
	
	public String[] getListaContactos() {
		Response response = cliente.target(baseURI).request(MediaType.TEXT_PLAIN).get();
		if (response.getStatus()/10 != 20) throw new RuntimeException("Fallo al buscar");
		String cadena = response.readEntity(String.class);
		response.close();
		
		return cadena.split(";");
	}


} // fin clase
