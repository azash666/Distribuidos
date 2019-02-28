package cliente;

import javax.ws.rs.NotFoundException;

public class TestAgenda {

	public static void main(String[] args) {

		GestorContactos gestor = new GestorContactos();
		String cadena = "";
		String UriContacto;

		try {	    
			UriContacto = gestor.nuevoContacto("Pedro", "666666666");
			System.out.println("\nCreado contacto con URI: " + UriContacto);
		}
		catch (RuntimeException ex) {
			System.out.println(ex.getMessage());
		}

		try {
			cadena = gestor.getContacto(1);
			System.out.println("\nLeido contacto con id 1: " + cadena);
		}
		catch (NotFoundException ex) {
			System.out.println(ex.getMessage());
		}

		try {	 	 
			UriContacto = gestor.nuevoContacto("Ana", "612345678");
			System.out.println("\nCreado contacto con URI: " + UriContacto);
		}
		catch (RuntimeException ex) {
			System.out.println(ex.getMessage());
		}
		

		try {	 	 
			String[] contactos = gestor.getListaContactos();
			System.out.println("LISTADO DE CONTACTOS");
			for(String singleContacto : contactos) {
				System.out.println(singleContacto);
			}
		}
		catch (RuntimeException ex) {
			System.out.println(ex.getMessage());
		}

		try {	
			gestor.actualizaContacto(1, "Juan", "666778899");
			System.out.println("\nActualizado contacto con id 1");
		}
		catch (NotFoundException ex) {
			System.out.println(ex.getMessage());
		}
	

		try {	
			cadena = gestor.getContacto(1);
			System.out.println("\nLeido contacto con id 1: " + cadena);
		}
		catch (NotFoundException ex) {
			System.out.println(ex.getMessage());
		}

		try {	
			cadena = gestor.getContacto(5);
			System.out.println("\nLeido contacto con id 5: " + cadena);
		}
		catch (NotFoundException ex) {
			System.out.println(ex.getMessage());
		}

		try {	
			gestor.borraContacto(1);
			System.out.println("\nBorrado contacto con id 1: " + cadena);
		}
		catch (NotFoundException ex) {
			System.out.println(ex.getMessage());
		}

		try {	
			gestor.getContacto(1);
			System.out.println("\nLeido contacto con id 1: " + cadena);
		}
		catch (NotFoundException ex) {
			System.out.println(ex.getMessage());
		}

		try {	  	 
			gestor.borraContacto(2);
			System.out.println("\nBorrado contacto con id 2: " + cadena);
		}
		catch (NotFoundException ex) {
			System.out.println(ex.getMessage());
		}

	}

}
