import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/Servlet1")
public class Servlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	
	private static final Pattern TEXTO = Pattern.compile("^\\p{L}{2,30}$");
	private static final Set<String> GENEROS = Set.of("Masculino", "Femenino", "Otro");
	private static final Set<String> COLORES = Set.of("Rojo", "Azul", "Verde", "Amarillo");
	private static final int MAX_AGE = 60 * 60; // 1 hora

	public Servlet1() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String nombre = limpiar(request.getParameter("nombre"));
		String apellido = limpiar(request.getParameter("apellido"));
		String edadTxt = limpiar(request.getParameter("edad"));
		String genero = limpiar(request.getParameter("genero"));
		String color = limpiar(request.getParameter("color"));
		
		boolean suscrito = "true".equals(request.getParameter("suscrito"));

		
		List<String> errores = new ArrayList<>();

		if (!TEXTO.matcher(nombre).matches()) {
			errores.add("Nombre inválido: solo letras (sin espacios, números ni símbolos), entre 2 y 40 caracteres.");
		}
		if (!TEXTO.matcher(apellido).matches()) {
			errores.add("Apellido inválido: solo letras (sin espacios, números ni símbolos), entre 2 y 40 caracteres.");
		}

		int edad = 0;
		try {
			edad = Integer.parseInt(edadTxt);
			if (edad < 1 || edad > 120) {
				errores.add("La edad debe estar entre 1 y 120.");
			}
		} catch (NumberFormatException e) {
			errores.add("La edad debe ser un número entero.");
		}

		if (!GENEROS.contains(genero)) {
			errores.add("Seleccione un género válido.");
		}
		if (!COLORES.contains(color)) {
			errores.add("Seleccione un color válido.");
		}

		PrintWriter out = response.getWriter();

		if (!errores.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("<h3>Hay errores en el formulario:</h3><ul>");
			for (String err : errores) {
				out.print("<li>" + escapar(err) + "</li>");
			}
			out.print("</ul><a href='index.html'>Volver</a>");
			out.close();
			return;
		}

		
		agregarCookie(response, "nombre", nombre);
		agregarCookie(response, "apellido", apellido);
		agregarCookie(response, "edad", String.valueOf(edad));
		agregarCookie(response, "genero", genero);
		agregarCookie(response, "color", color);
		agregarCookie(response, "suscrito", String.valueOf(suscrito));

		out.print("<h3>Bienvenido " + escapar(nombre) + " " + escapar(apellido) + "</h3>");
		out.print("<p>Se guardaron 6 cookies.</p>");
		out.print("<form action='Servlet2' method='post'>");
		out.print("<input type='submit' value='Ver cookies'>");
		out.print("</form>");
		out.close();
	}

	
	private void agregarCookie(HttpServletResponse response, String nombre, String valor) {
		Cookie ck = new Cookie(nombre, URLEncoder.encode(valor, StandardCharsets.UTF_8));
		ck.setMaxAge(MAX_AGE);
		ck.setPath("/");
		ck.setHttpOnly(true);
		response.addCookie(ck);
	}

	private String limpiar(String s) {
		return s == null ? "" : s.trim();
	}

	
	static String escapar(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}
}