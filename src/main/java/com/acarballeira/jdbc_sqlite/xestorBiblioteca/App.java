package com.acarballeira.jdbc_sqlite.xestorBiblioteca;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.time.LocalDate;
import com.acarballeira.jdbc_sqlite.xestorBiblioteca.exceptions.*;

public class App {

	static Scanner sc = new Scanner(System.in);
	static String url = "jdbc:sqlite:xestorBibliotecas.db";
	static Connection conexion;

	public static void main(String[] args) throws Exception {
		boolean exit = false;
		conexion = DriverManager.getConnection(url);
		database_check(conexion);

		exit = menu(exit);
		while (exit == false) {
			exit = menu(exit);
		}
	}

	private static boolean menu(boolean exit) throws Exception {
		System.out.println("=== XsTOR = XESTOR DE BIBLIOTECAS ===\r\n" + "-------------------------------------\r\n"
				+ "1. Xestión de préstamos\r\n" + "2. Xestión de catálogo\r\n" + "3. Xestión de lectores\r\n"
				+ "4. Gardar e sair\r\n" + "\r\n" + "0. Outras opcións do programa\r\n"
				+ "-------------------------------------\r\n" + "PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
		int menu = sc.nextInt();
		switch (menu) {
		case 1:
			int case1option;
			System.out.println("======= XESTION DE PRÉSTAMOS =======\r\n" + "------------------------------------\r\n"
					+ "1. Novo Préstamo/Devolución\r\n" + "2. Rexistro diario\r\n" + "3. Exportar rexistro diario\r\n"
					+ "------------------------------------\r\n");
			System.out.print("PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
			case1option = sc.nextInt();
			switch (case1option) {
			case 1:
				novoPrestamoDevolucion();
				break;
			case 2:
				rexistroPrestamoDevolucion();
				break;
			case 3:
				exportarRexistroPrestamoDevolucion();
				break;
			}
			break;
		case 2:
			int case2option;
			System.out.println("======= XESTION DE CATÁLOGO =======\r\n" + "-----------------------------------\r\n"
					+ "1. Rexistrar/Descatalogar obras\r\n" + "2. Consulta no catálogo\r\n"
					+ "3. Importar/Exportar catálogo\r\n" + "-----------------------------------\r\n");
			System.out.print("PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
			case2option = sc.nextInt();
			switch (case2option) {
			case 1:
				novoCatalogacionDescatalogacion();
				break;
			case 2:
				rexistroCatalogo();
				break;
			case 3:
				importarExportarCatalogo();
				break;
			}
			break;
		case 3:
			int case3option;
			System.out.println("======= XESTION DE LECTORES =======\r\n" + "------------------------------------\r\n"
					+ "1. Alta/Baixa de lectores\r\n" + "2. Consultar listado\r\n" + "3. Importar/Exportar listado\r\n"
					+ "------------------------------------\r\n");
			System.out.print("PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
			case3option = sc.nextInt();
			switch (case3option) {
			case 1:
				altaBaixaLectores();
				break;
			case 2:
				rexistroLectores();
				break;
			case 3:
				importarExportarLectores();
				break;
			}
			break;
		case 4:
			System.out.println("Gardando e saindo...");
			exit = true;
			break;
		case 0:
			int case0option;
			System.out.println("======= OUTRAS OPCIÓNS =======\r\n" + "-----------------------------------\r\n"
					+ "1. Realizar copia de seguridade\r\n" + "2. Revision de base de datos\r\n"
					+ "3. Restauran dende copia\r\n" + "-----------------------------------\r\n");
			System.out.print("PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
			case0option = sc.nextInt();
			switch (case0option) {
			case 1:
				novoCopiaSeguridade();
				break;
			case 2:
				database_check(conexion);
				break;
			case 3:
				restaurarConCopia();
				break;
			}
			break;
		default:
			System.out.println("error!: Opción non válida");
			break;
		}
		return exit;
	}



	/* XESTIÓN DE PRÉSTAMOS E DEVOLUCIÓNS 
	 * 1. novoPrestamoDevolucion: Permite facer novos péstamos e devolucións en función da operación desexada.
	 * 2. rexistroPrestamoDevolucion: Imprime un desglose diario na pantalla da información rexistrada no primeiro método
	 * 3. exportarRexistroPrestamoDevolucion: Permite exportar en TXT o desglose diario
	 */

	private static void exportarRexistroPrestamoDevolucion() throws Exception {
		Statement registro = conexion.createStatement();
		ResultSet resultado = registro.executeQuery("SELECT * FROM PrestamoDevolucion ORDER BY codOperacion");
		LocalDate today = LocalDate.now();
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter("rexistros_" + today + ".txt"))) {
			escritor.write("==================== REXISTRO DE PRÉSTAMOS E DEVOLUCIÓNS ====================\n" + "DATA - "
					+ today + "\n" + "=============================================================================\n");
			escritor.write(String.format("%-5s %-5s %-5s %-7s %-7s", "Rexistro", "Código Lector", "Código Libro",
					"Data de Préstamo", "Data de Devolución"));

			while (resultado.next()) {
				int codOperacion = resultado.getInt("codOperacion");
				String codLector = resultado.getString("codLector"), codObra = resultado.getString("codObra"),
						fechaPrestamo = resultado.getString("fechaPrestamo"),
						fechaDevolucion = resultado.getString("fechaDevolucion");
				escritor.write(String.format("%-5s %-5s %-5s %-7s %-7s", codOperacion, codLector, codObra,
						fechaPrestamo, fechaDevolucion));
			}

		} catch (Exception e) {
			System.out.println("error!: " + e.getMessage());
		}

	}

	private static void rexistroPrestamoDevolucion() throws Exception {
		Statement buscar = conexion.createStatement();
		ResultSet resultado = buscar.executeQuery("SELECT * FROM PrestamoDevolucion ORDER BY codOperacion");
		LocalDate today = LocalDate.now();
		System.out.printf("==================== REXISTRO DE PRÉSTAMOS E DEVOLUCIÓNS ====================\n" + "DATA - "
				+ today + "\n" + "=============================================================================\n");
		System.out.printf("%-5s %-5s %-5s %-7s %-7s", "Rexistro", "Código Lector", "Código Libro", "Data de Préstamo",
				"Data de Devolución");
		System.out.println();
		while (resultado.next()) {
			int codOperacion = resultado.getInt("codOperacion");
			String codLector = resultado.getString("codLector"), codObra = resultado.getString("codObra"),
					fechaPrestamo = resultado.getString("fechaPrestamo"),
					fechaDevolucion = resultado.getString("fechaDevolucion");
			System.out.printf("%-5s %-5s %-5s %-7s %-7s", codOperacion, codLector, codObra, fechaPrestamo,
					fechaDevolucion);
			System.out.println();
		}

	}

	private static void novoPrestamoDevolucion() throws Exception {
		sc.nextLine();
		String opcion, codLector, codObra;
		LocalDate today = LocalDate.now();
		boolean existeLector, existeLibro, disponibleLibro;
		System.out.print("Préstamo ou Devolución: ");
		opcion = sc.nextLine();
		switch (opcion) {
		// PRÉSTAMO
		case "Préstamo":
			String novoRexistro = """
					INSERT INTO PrestamoDevolucion (codLector, codObra, fechaPrestamo, fechaDevolucion) Values (?, ?, ?, '')
					""";
			String bloquearObra = """
					UPDATE Catalogo SET disponibleObra = 0 WHERE codObra = ?
					""";

			System.out.println("=== NOVO PRÉSTAMO DE LIBRO ===");

			System.out.print("Código de lector: ");
			codLector = sc.nextLine();
			existeLector = existeLector(codLector);
			if (existeLector == true) {
				System.out.print("Código de obra: ");
				codObra = sc.nextLine();
				existeLibro = existeLibro(codObra);
				disponibleLibro = disponibleLibro(codObra);
				if (existeLibro == true && disponibleLibro == true) {
					try (PreparedStatement insertar = conexion.prepareStatement(novoRexistro);
							PreparedStatement bloquear = conexion.prepareStatement(bloquearObra)) {
						
						insertar.setString(1, codLector);
						insertar.setString(2, codObra);
						insertar.setString(3, today.toString());
						insertar.executeUpdate();

						bloquear.setString(1, codObra);
						bloquear.executeUpdate();
					} catch (Exception e) {
						System.out.println("error na táboa!: " + e.getMessage());
					}
				} else {
					System.out.println("Obra non existe ou non dispoñible para emprésito");
				}
			} else {
				System.out.println("Lector non existe");
			}
			break;
		// DEVOLUCIÓN
		case "Devolución":
			String novaDevolucion = """
					UPDATE PrestamoDevolucion SET fechaDevolucion = ? WHERE codObra = ? AND fechaDevolucion = ''
					""";
			String desbloquearObra = """
					UPDATE Catalogo SET disponibleObra = 1 WHERE codObra = ?
					""";

			System.out.println("=== NOVA DEVOLUCIÓN DE LIBRO ===");
			System.out.print("Código de obra: ");
			codObra = sc.nextLine();
			try (PreparedStatement devolver = conexion.prepareStatement(novaDevolucion);
					PreparedStatement desbloquear = conexion.prepareStatement(desbloquearObra)) {
				devolver.setString(1, today.toString());
				devolver.setString(2, codObra);
				devolver.executeUpdate();
				desbloquear.setString(1, codObra);
				desbloquear.executeUpdate();
			} catch (Exception e) {
				System.out.println("error na táboa!: " + e.getMessage());
			}
			break;
		}
	}

	/* XESTIÓN DE CATALOGO
	 * 1. novaCatalogacionDescatalogación:Permite dar de alta novas obras e dalas de baixa
	 * 2. rexistroCatalogo: Imprime un desglose do catálogo na pantalla da información rexistrada no primeiro método
	 * 3. importarExportarCatalogo: Permite a creacion de novos rexistros no catálogo de forma masiva e exportalos en formato CSV
	 */

	private static void importarExportarCatalogo() {
		sc.nextLine();
		String opcion, archivo = "exportCatalogo.csv";
		String sqlInsert = "INSERT INTO Catalogo (codObra, tituloObra, autorObra, editorialObra, anoObra, disponibleObra) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		String sqlSelect = "SELECT codObra, tituloObra, autorObra, editorialObra, anoObra, disponibleObra FROM Catalogo";
		System.out.print("Importar ou Exportar: ");
		opcion = sc.nextLine();
		switch (opcion) {
		case "Importar":
			int insertados = 0;
			int rechazados = 0;
			boolean primeraLinea = true;

			try (BufferedReader br = new BufferedReader(new FileReader(archivo));
					PreparedStatement pstmt = conexion.prepareStatement(sqlInsert)) {

				String linea;
				while ((linea = br.readLine()) != null) {
					// 1. Opcional: Saltar cabecera si el CSV tiene nombres de columnas en la
					// primera línea
					if (primeraLinea) {
						primeraLinea = false;
						if (linea.toLowerCase().startsWith("codobra;")) {
							continue;
						}
					}

					// 2. Validar número de columnas (-1 mantiene campos vacíos al final)
					String[] datos = linea.split(";", -1);
					if (datos.length != 6) {
						System.out.println("Línea incorrecta (número de columnas): " + linea);
						rechazados++;
						continue;
					}

					// 3. Validar campos vacíos o solo espacios
					if (datos[0].isBlank() || datos[1].isBlank() || datos[2].isBlank() || datos[3].isBlank()
							|| datos[4].isBlank() || datos[5].isBlank()) {
						System.out.println("Faltan datos obligatorios: " + linea);
						rechazados++;
						continue;
					}

					// 4. Limpiar espacios y parsear enteros
					String codObra = datos[0].trim();
					String tituloObra = datos[1].trim();
					String autorObra = datos[2].trim();
					String editorialObra = datos[3].trim();
					int anoObra;
					int disponibleObra;

					try {
						anoObra = Integer.parseInt(datos[4].trim());
						disponibleObra = Integer.parseInt(datos[5].trim());

						// Validación extra lógica: disponibleObra suele ser 0 o 1
						if (disponibleObra != 0 && disponibleObra != 1) {
							System.out.println("Valor de disponibilidad inválido (debe ser 0 o 1): " + linea);
							rechazados++;
							continue;
						}
					} catch (NumberFormatException e) {
						System.out.println("Formato numérico incorrecto (año o disponibilidad): " + linea);
						rechazados++;
						continue;
					}

					// 5. Insertar en BD y controlar duplicados de Primary Key (codObra)
					try {
						pstmt.setString(1, codObra);
						pstmt.setString(2, tituloObra);
						pstmt.setString(3, autorObra);
						pstmt.setString(4, editorialObra);
						pstmt.setInt(5, anoObra);
						pstmt.setInt(6, disponibleObra);

						pstmt.executeUpdate();
						insertados++;

					} catch (SQLException e) {
						// En SQLite el código de error para UNIQUE/PRIMARY KEY constraint es 19
						// También comprobamos el mensaje por compatibilidad
						if (e.getErrorCode() == 19 || e.getMessage().toLowerCase().contains("unique")
								|| e.getMessage().toLowerCase().contains("primary key")) {
							System.out.println("Registro duplicado (codObra ya existe): " + codObra);
						} else {
							System.out.println("Error SQL al insertar línea [" + linea + "]: " + e.getMessage());
						}
						rechazados++;
					}
				}

				System.out.println("--- Resumen de importación ---");
				System.out.println("Registros insertados: " + insertados);
				System.out.println("Registros rechazados: " + rechazados);

			} catch (IOException | SQLException e) {
				System.err.println("Error crítico durante la importación: " + e.getMessage());
			}

			break;
		case "Exportar":
			int exportados = 0;

			try (Statement stmt = conexion.createStatement();
					ResultSet rs = stmt.executeQuery(sqlSelect);
					BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {

				// Ignorar cabeceira
				bw.write("codObra;tituloObra;autorObra;editorialObra;anoObra;disponibleObra");
				bw.newLine();

				while (rs.next()) {
					// split de ";"
					String codObra = limpiarTexto(rs.getString("codObra"));
					String tituloObra = limpiarTexto(rs.getString("tituloObra"));
					String autorObra = limpiarTexto(rs.getString("autorObra"));
					String editorialObra = limpiarTexto(rs.getString("editorialObra"));
					int anoObra = rs.getInt("anoObra");
					int disponibleObra = rs.getInt("disponibleObra");

					String lineaCSV = String.join(";", codObra, tituloObra, autorObra, editorialObra,
							String.valueOf(anoObra), String.valueOf(disponibleObra));

					bw.write(lineaCSV);
					bw.newLine();
					exportados++;
				}

				System.out.println("Exportación completada. Total registros exportados: " + exportados);

			} catch (IOException | SQLException e) {
				System.err.println("Error durante la exportación a CSV: " + e.getMessage());
			}

			break;
		}
	}

	private static void rexistroCatalogo() throws Exception {
		String disponible;
		Statement buscar = conexion.createStatement();
		ResultSet resultado = buscar.executeQuery("SELECT * FROM Catalogo ORDER BY codObra");
		LocalDate today = LocalDate.now();
		System.out.printf("========================== CATÁLOGO BIBLIOTECARIO ==========================\n" + "DATA - "
				+ today + "\n" + "============================================================================\n");
		System.out.printf("%-5s %-5s %-5s %-7s %-7s %-7s", "Código Libro", "Título", "Autor", "Editorial", "Ano",
				"Dispoñible");
		System.out.println();
		while (resultado.next()) {
			String codObra = resultado.getString("codObra"), tituloObra = resultado.getString("tituloObra"),
					autorObra = resultado.getString("autorObra"), editorialObra = resultado.getString("editorialObra");
			int anoObra = resultado.getInt("anoObra"), disponibleObra = resultado.getInt("disponibleObra");
			if (disponibleObra == 1) {
				disponible = "SÍ";
			} else {
				disponible = "NO";
			}
			System.out.printf("%-5s %-5s %-5s %-7s %-7s %-7s", codObra, tituloObra, autorObra, editorialObra, anoObra,
					disponible);
		}
	}

	private static void novoCatalogacionDescatalogacion() throws Exception {
		sc.nextLine();
		String codObra, tituloObra, autorObra, editorialObra, opcion;
		int anoObra, disponibleObra;

		boolean existeLibro;
		System.out.print("Catalogar ou Descatalogar: ");
		opcion = sc.nextLine();
		switch (opcion) {
		// CATALOGACIÓN
		case "Catalogar":
			String novaCatalogacion = """
					INSERT INTO Catalogo (codObra, tituloObra, autorObra, editorialObra, anoObra) Values (?, ?, ?, ?, ?)
					""";
			System.out.println("=== NOVA CATALOGACIÓN ===");
			System.out.print("Código de Obra: ");
			codObra = sc.nextLine();
			existeLibro = existeLibro(codObra);
			if (existeLibro == false) {
				System.out.print("Título: ");
				tituloObra = sc.nextLine();
				System.out.print("Autor: ");
				autorObra = sc.nextLine();
				System.out.print("Editorial: ");
				editorialObra = sc.nextLine();
				System.out.print("Ano de publicacion: ");
				anoObra = sc.nextInt();
				try (PreparedStatement insertar = conexion.prepareStatement(novaCatalogacion)) {
					insertar.setString(1, codObra);
					insertar.setString(2, tituloObra);
					insertar.setString(3, autorObra);
					insertar.setString(4, editorialObra);
					insertar.setInt(5, anoObra);
					insertar.executeUpdate();
				}
				try {
					existeLibro = existeLibro(codObra);
					if (existeLibro == false) {
						throw new NotRegisteredException("error inesperado: Non se rexistrou o libro na táboa");
					} else {
						System.out.println("Rexistado con éxito!");
					}
				} catch (Exception e) {
					System.out.println("error na táboa!: " + e.getMessage());
				}
			} else {
				System.out.println("Existe unha obra con este código");
			}
			break;
		// DESCATALOGACIÓN
		case "Descatalogar":
			String disponible;
			String novaDescatalogacion = """
					DELETE FROM Catalogo WHERE codObra = ?
					""";
			String comprobar = """
					SELECT * FROM Catalogo WHERE codObra = ? LIMIT 1
					""";

			System.out.println("=== NOVA DESCATALOGACIÓN ===");
			System.out.print("Código de obra: ");
			codObra = sc.nextLine();
			try (PreparedStatement seguro = conexion.prepareStatement(comprobar)) {
				seguro.setString(1, codObra);
				System.out.println("Atopáronse os seguintes documentos: ");
				System.out.printf("%-13s %-20s %-18s %-15s %-6s %-10s%n", "Código Libro", "Título", "Autor",
						"Editorial", "Ano", "Dispoñible");
				System.out.println("=====================================================================================");
				ResultSet rs = seguro.executeQuery();
				while (rs.next()) {
					codObra = rs.getString("codObra");
					tituloObra = rs.getString("tituloObra");
					autorObra = rs.getString("autorObra");
					editorialObra = rs.getString("editorialObra");
					anoObra = rs.getInt("anoObra");
					disponibleObra = rs.getInt("disponibleObra");
					if (disponibleObra == 1) {
						disponible = "SÍ";
					} else {
						disponible = "NO";
					}
					System.out.printf("%-13s %-20s %-18s %-15s %-6d %-10s%n", codObra, tituloObra, autorObra,
							editorialObra, anoObra, disponible);
				}

				boolean descatalogable = disponibleLibro(codObra);
				if (descatalogable) {
					try (PreparedStatement borrar = conexion.prepareStatement(novaDescatalogacion)) {
						borrar.setString(1, codObra);
						borrar.executeUpdate();
						System.out.println("Obra descatalogada correctamente.");
					}
				} else {
					throw new UndestroyException("Non se pode eliminar, actualmente en préstamo");
				}
			} catch (Exception e) {
				System.out.println("error na táboa!: " + e.getMessage());
			}
			break;
		}
	}
	
	/* XESTIÓN DE LECTORES
	 * 1. novaAltaBaixa:Permite dar de alta novos usuarios e dalos de baixa
	 * 2. rexistroLectores: Imprime un desglose da listaxe de lectores na pantalla da información rexistrada no primeiro método
	 * 3. importarExportarCatalogo: Permite a creacion de novos rexistros na listaxe de forma masiva e exportalos en formato JSON
	 */
	
	private static void importarExportarLectores() throws Exception {
		
		
	}

	private static void rexistroLectores() throws Exception {
		Statement buscar = conexion.createStatement();
		ResultSet resultado = buscar.executeQuery("SELECT * FROM Lectores ORDER BY codObra");
		LocalDate today = LocalDate.now();
		System.out.printf("========================== LISTADO DE LECTORES ==========================\n" + "DATA - "
				+ today + "\n" + "============================================================================\n");
		System.out.printf("%-5s %-5s %-5s %-7s %-7s", "Código Lector", "DNI", "Nome", "Apelidos",
				"Idade");
		System.out.println();
		while (resultado.next()) {
			String codLector = resultado.getString("codLector"), dniLector = resultado.getString("dniLector"),
					nombreLector = resultado.getString("nombreLector"), apellidosLector = resultado.getString("apellidosLector");
			int edadLector = resultado.getInt("edadLector");
			System.out.printf("%-5s %-5s %-5s %-7s %-7s", codLector, dniLector, nombreLector, apellidosLector, edadLector);
		}
	}

	private static void altaBaixaLectores() throws Exception {
		sc.nextLine();
		String codLector, dniLector, nombreLector, apellidosLector, opcion;
		int edadLector;
		boolean existeLector;
		System.out.print("Alta ou Baixa: "); opcion=sc.nextLine();
		switch(opcion) {
		//ALTA DE USUARIO
		case "Alta":
			String novaAlta = """
			INSERT INTO Lectores (codLector, dniLector, nombreLector, apellidosLector, edadLector) Values (?, ?, ?, ?, ?)
			""";
			System.out.println("=== NOVA ALTA DE USUARIO ===");
			System.out.print("Código de Lector: ");
			codLector = sc.nextLine();
			existeLector = existeLector(codLector);
			if (existeLector == false) {
				System.out.print("DNI: ");
				dniLector = sc.nextLine();
				System.out.print("Nombre: ");
				nombreLector = sc.nextLine();
				System.out.print("Apellidos: ");
				apellidosLector = sc.nextLine();
				System.out.print("Edad: ");
				edadLector = sc.nextInt();
		try (PreparedStatement insertar = conexion.prepareStatement(novaAlta)) {
			insertar.setString(1, codLector);
			insertar.setString(2, dniLector);
			insertar.setString(3, nombreLector);
			insertar.setString(4, apellidosLector);
			insertar.setInt(5, edadLector);
			insertar.executeUpdate();
		}
		try {
			existeLector = existeLector(codLector);
			if (existeLector == false) {
				throw new NotRegisteredException("error inesperado: Non se rexistrou o lector na táboa");
			} else {
				System.out.println("Rexistado con éxito!");
			}
		} catch (Exception e) {
			conexion.rollback();
			System.out.println("error na táboa!: " + e.getMessage());
		}
	} else {
		System.out.println("Existe unha obra con este código");
	}
			break;
	//BAIXA DE USUARIOS
		case "Baixa":
			String novaBaixa = """
					DELETE FROM Lectores WHERE codLector = ?
					""";
			String comprobar = """
					SELECT * FROM Lectores WHERE codLector = ? LIMIT 1
					""";

			System.out.println("=== NOVA BAIXA DE USUARIOS ===");
			System.out.print("Código de lector: ");
			codLector = sc.nextLine();
			try (PreparedStatement seguro = conexion.prepareStatement(comprobar)) {
				seguro.setString(1, codLector);
				System.out.println("Atopáronse os seguintes datos: ");
				System.out.printf("%-14s %-12s %-15s %-20s %-6s%n", "Código Lector", "DNI", "Nome", "Apelidos",
						"Idade");
				System.out.println("=============================================================================");
				ResultSet rs = seguro.executeQuery();
				while (rs.next()) {
					codLector = rs.getString("codLector");
					dniLector = rs.getString("dniLector");
					nombreLector = rs.getString("nombreLector");
					apellidosLector = rs.getString("apellidosLector");
					edadLector = rs.getInt("edadLector");
					System.out.printf("%-14s %-12s %-15s %-20s %-6d%n", codLector, dniLector, nombreLector,
							apellidosLector, edadLector);
				}

				boolean baixaSegura = lectorConPrestamo(codLector);
				if (!baixaSegura) {
					try (PreparedStatement borrar = conexion.prepareStatement(novaBaixa)) {
						borrar.setString(1, codLector);
						borrar.executeUpdate();
						System.out.println("Usuario dado de baixa correctamente.");
					}
				} else {
					throw new UndestroyException("Existen préstamos neste usuario");
				}
			} catch (Exception e) {
				System.out.println("error na táboa!: " + e.getMessage());
			}
			break;
		}
	}
	

	/*
	 * MÉTODOS DE SEGURIDADE 1. disponibleLibro: Revisa se un libro está dispoñible
	 * para emprésito 2. existeLibro: Comproba que o libro existe e está rexistrado
	 * na táboa de catálogo 3. existeLector: Comproba que o lector existe e está
	 * rexistrado na táboa de lectores 4. integridad: Revisa que as táboas das bases
	 * de datos están ben construídas 5. limpiarTexto: Permite evitar texto nú,
	 * puntos, comas e outros dentro dos campos de texto
	 */

	private static void restaurarConCopia() {
	}

	private static void novoCopiaSeguridade() {
	}

	private static boolean disponibleLibro(String codObra) throws Exception {
		String comprobar = """
				SELECT disponibleObra FROM Catalogo WHERE codObra = ?
				""";
		boolean status;
		PreparedStatement codigo = conexion.prepareStatement(comprobar);
		codigo.setString(1, codObra);
		ResultSet rs = codigo.executeQuery();
		if (rs.getInt("disponibleObra") == 0) {
			status = false;
		} else {
			status = true;
		}
		return status;
	}
	private static boolean lectorConPrestamo(String codLector) throws Exception{
		boolean status = false;
	    String comprobar = """
	            SELECT 1 FROM PrestamoDevolucion WHERE codLector = ? AND fechaDevolucion = '' LIMIT 1
	            """;
	    try (PreparedStatement ps = conexion.prepareStatement(comprobar)) {
	        ps.setString(1, codLector);
	        try (ResultSet rs = ps.executeQuery()) {
	            status = rs.next();
	        }
	    }
	    return status;
	}
	private static boolean existeLibro(String codObra) throws Exception {

		String comprobar = """
				SELECT 1 FROM Catalogo WHERE codObra = ? LIMIT 1
				""";
		PreparedStatement codigo = conexion.prepareStatement(comprobar);
		codigo.setString(1, codObra);
		ResultSet rs = codigo.executeQuery();
		return rs.next();
	}
	private static boolean existeLector(String codLector) throws Exception{
	String comprobar = """
			SELECT 1 FROM Lectores WHERE codLector = ? LIMIT 1
			""";
	PreparedStatement codigo = conexion.prepareStatement(comprobar);
	codigo.setString(1, codLector);
	ResultSet rs = codigo.executeQuery();
	return rs.next();
	}
	private static void database_check(Connection conexion) throws Exception {
		Statement sentencia = conexion.createStatement();
		try {
			String sqlCatalogo = """
					CREATE TABLE IF NOT EXISTS Catalogo(
					codObra TEXT PRIMARY KEY,
					tituloObra TEXT NOT NULL,
					autorObra TEXT NOT NULL,
					editorialObra TEXT NOT NULL,
					anoObra INTEGER NOT NULL,
					disponibleObra INTEGER NOT NULL DEFAULT 1
					)
					""";
			sentencia.execute(sqlCatalogo);
			String sqlLectores = """
					CREATE TABLE IF NOT EXISTS Lectores(
					codLector TEXT PRIMARY KEY,
					dniLector TEXT NOT NULL,
					nombreLector TEXT NOT NULL,
					apellidosLector TEXT NOT NULL,
					edadLector INTEGER NOT NULL
					)
					""";
			sentencia.execute(sqlLectores);
			String sqlPrestamoDevolucion = """
					CREATE TABLE IF NOT EXISTS PrestamoDevolucion(
					codOperacion INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
					codLector TEXT NOT NULL,
					codObra TEXT NOT NULL,
					fechaPrestamo TEXT NOT NULL,
					fechaDevolucion TEXT NOT NULL,
					FOREIGN KEY (codLector) REFERENCES Lectores(codLector),
					FOREIGN KEY (codObra) REFERENCES Catalogo(codObra)
					)
					""";
			sentencia.execute(sqlPrestamoDevolucion);
		} catch (Exception e) {
			System.out.println("error!: " + e.getMessage());
		}

	}
	private static String limpiarTexto(String texto) {
		if (texto == null)
			return "";
		return texto.replace(";", ",").trim();
	}
}
