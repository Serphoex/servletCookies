import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Servlet2")
public class Servlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Servlet2() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		
		Map<String, String> cookies = new HashMap<>();
		Cookie[] arreglo = request.getCookies();
		if (arreglo != null) {
			for (Cookie c : arreglo) {
				cookies.put(c.getName(), URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8));
			}
		}

		if (!cookies.containsKey("nombre")) {
			out.print("<p>No se encontraron cookies. <a href='index.html'>Volver al formulario</a></p>");
			out.close();
			return;
		}

		String nombre = cookies.get("nombre");
		String apellido = cookies.getOrDefault("apellido", "");
		String genero = cookies.getOrDefault("genero", "");
		String color = cookies.getOrDefault("color", "");

		int edad = 0;
		try {
			edad = Integer.parseInt(cookies.getOrDefault("edad", "0"));
		} catch (NumberFormatException e) {
			
		}
		boolean suscrito = Boolean.parseBoolean(cookies.get("suscrito"));

		out.print("<h3>Hola " + Servlet1.escapar(nombre) + " " + Servlet1.escapar(apellido) + "</h3>");
		out.print("<table border='1' cellpadding='6' style='border-collapse:collapse'>");
		out.print("<tr><th>Cookie</th><th>Valor</th></tr>");
		fila(out, "nombre", nombre);
		fila(out, "apellido", apellido);
		fila(out, "edad", String.valueOf(edad));
		fila(out, "genero", genero);
		fila(out, "color", color);
		fila(out, "suscrito", String.valueOf(suscrito));
		out.print("</table>");
		out.print("<p>" + (edad >= 18 ? "Es mayor de edad." : "Es menor de edad.") + " "
				+ (suscrito ? "Está suscrito a las noticias." : "No está suscrito a las noticias.") + "</p>");
		out.print("<a href='index.html'>Volver</a>");
		out.close();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	private void fila(PrintWriter out, String nombre, String valor) {
		out.print("<tr><td>" + nombre + "</td><td>" + Servlet1.escapar(valor) + "</td></tr>");
	}
}