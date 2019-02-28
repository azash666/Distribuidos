package es.uji.miservlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ServletDeBienvenida
 */
@WebServlet("/ServletDeBienvenida")
public class ServletDeBienvenida extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDeBienvenida() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String nombre = request.getParameter("nombre").toString();
		String apellidos = request.getParameter("apellidos").toString();
		
		out.println("<html>");
		out.println("	<head>");
		out.println("		<title>Servlet ServletDeBienvenida</title>");
		out.println("	</head>");
		out.println("	<body>");
		out.println("		<h2>Página de bienvenida en "+request.getContextPath()+"</h2>");
		out.println("		<p>Bienvenido "+nombre+" "+apellidos+"</p>");
		out.println("	</body>");
		out.println("</html>");
		
		out.close();
	}

}
