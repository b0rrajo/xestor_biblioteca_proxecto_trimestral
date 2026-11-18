package com.acarballeira.jdbc_sqlite.xestorBiblioteca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.time.LocalDate;

public class App {
	
	static Scanner sc = new Scanner(System.in);
	static String url="jdbc:sqlite:xestorBibliotecas.db";
	static Connection conexion;
    public static void main(String[] args) throws Exception{
    	boolean exit = false;
    	conexion = DriverManager.getConnection(url);
    	database_check(conexion);
    	
    	exit=menu(exit);
    	while(exit==false) {
    		exit = menu(exit);
    	}
    }
    
	private static void database_check(Connection conexion) throws Exception {
		/* Database Check
		 * Función de seguridad que revisa si existen las tablas dentro de la base de datos.
		 * Caso de existir, no realiza nada.
		 * Caso de NO existir, construye las tablas.
		 */
		Statement sentencia = conexion.createStatement();
		try {
			String sqlCatalogo="""
					CREATE TABLE IF NOT EXISTS Catalogo(
					codObra TEXT PRIMARY KEY,
					tituloObra TEXT NOT NULL,
					autorObra TEXT NOT NULL,
					editorialObra TEXT NOT NULL,
					anoObra INTEGER NOT NULL
					disponibleObra INTEGER NOT NULL DEFAULT 1
					)
					""";
			sentencia.execute(sqlCatalogo);
			String sqlLectores="""
					CREATE TABLE IF NOT EXISTS Lectores(
					codLector TEXT PRIMARY KEY,
					dniLector TEXT NOT NULL,
					nombreLector TEXT NOT NULL,
					apellidosLector TEXT NOT NULL,
					edadLector INTEGER NOT NULL
					)
					""";
			sentencia.execute(sqlLectores);
			String sqlPrestamoDevolucion="""
					CREATE TABLE IF NOT EXISTS PrestamoDevolucion(
					codOperacion INTEGER PRIMARY KEY AUTOINCREMENT,
					codLector TEXT NOT NULL,
					codObra TEXT NOT NULL,
					fechaPrestamo TEXT NOT NULL,
					fechaDevolucion TEXT NOT NULL,
					FOREIGN KEY (codLector) REFERENCES Lectores(codLector),
					FOREIGN KEY (codObra) REFERENCES Catalogo(codObra)
					)
					""";
			sentencia.execute(sqlPrestamoDevolucion);
		}catch(Exception e) {
			System.out.println("error!: " + e.getMessage());
		}
		
	}

	private static boolean menu(boolean exit) throws Exception {
		System.out.println("=== XsTOR = XESTOR DE BIBLIOTECAS ===\r\n"
				+ "-------------------------------------\r\n"
				+ "1. Xestión de préstamos\r\n"
	          	+ "2. Xestión de catálogo\r\n"
	          	+ "3. Xestión de lectores\r\n"
	         	+ "4. Gardar e sair\r\n"
	       		+ "-------------------------------------\r\n"
	       		+ "PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
		int menu = sc.nextInt();
		switch(menu) {
		case 1: 
			int case1option;
			System.out.println("======= XESTION DE PRÉSTAMOS =======\r\n"
					+ "------------------------------------\r\n"
					+ "1. Novo Préstamo/Devolución\r\n"
					+ "2. Rexistro diario\r\n"
					+ "3. Exportar rexistro diario\r\n"
					+ "------------------------------------\r\n");
			System.out.print("PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
			case1option=sc.nextInt();
			switch(case1option) {
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
			System.out.println("======= XESTION DE CATÁLOGO =======\r\n"
					+ "-----------------------------------\r\n"
					+ "1. Rexistrar/Descatalogar obras\r\n"
	          		+ "2. Consulta no catálogo\r\n"
	          		+ "3. Importar/Exportar catálogo\r\n"
	       			+ "-----------------------------------\r\n");
			System.out.print("PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
			case2option=sc.nextInt();
			break;
		case 3: 
			int case3option;
			System.out.println("======= XESTION DE LECTORES =======\r\n"
					+ "------------------------------------\r\n"
					+ "1. Alta/Baixa de lectores\r\n"
					+ "2. Consultar listado\r\n"
					+ "3. Importar/Exportar listado\r\n"
					+ "------------------------------------\r\n");
			System.out.print("PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
			case3option=sc.nextInt();
			break;
		case 4: 
			System.out.println("Gardando e saindo...");
			exit=true;
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
	
	private static void exportarRexistroPrestamoDevolucion() {
		// TODO Auto-generated method stub
		
	}

	private static void rexistroPrestamoDevolucion() throws Exception {
		Statement buscar = conexion.createStatement();
		ResultSet resultado = buscar.executeQuery("SELECT * FROM PrestamoDevolucion ORDER BY codOperacion");
		LocalDate today = LocalDate.now();
		System.out.printf("==================== REXISTRO DE PRÉSTAMOS E DEVOLUCIÓNS ====================\n"
						+ "DATA - " + today + "\n"
						+ "=============================================================================\n");
		System.out.printf("%-5s %-5s %-5s %-7s %-7s", "Rexistro","Código Lector", "Código Libro", "Data de Préstamo", "Data de Devolución");
		while(resultado.next()) {
			int codOperacion=resultado.getInt("codOperacion");
			String codLector=resultado.getString("codLector"), codObra=resultado.getString("codObra"), fechaPrestamo=resultado.getString("fechaPrestamo"), fechaDevolucion=resultado.getString("fechaDevolucion");
			System.out.printf("%-5s %-5s %-5s %-7s %-7s", codOperacion, codLector, codObra, fechaPrestamo, fechaDevolucion);
		}

		
	}

	private static void novoPrestamoDevolucion() throws Exception {
		String opcion, codLector, codObra;
		LocalDate today = LocalDate.now();
		boolean existeLector, existeLibro, disponibleLibro;
		System.out.print("Préstamo ou Devolución: "); opcion=sc.nextLine();
		switch(opcion) {
		//PRÉSTAMO
		case "Préstamo":
			String novoRexistro="""
			INSERT INTO PrestamoDevolucion (codLector, codObra, fechaPrestamo) Values (?, ?, ?)
			""";
			String bloquearObra="""
					UPDATE Catalogo SET disponibleObra = 0 WHERE codObra = ?
					""";
			
			System.out.print("=== NOVO PRÉSTAMO DE LIBRO ===");
			System.out.print("Código de lector: "); codLector=sc.nextLine();
			existeLector=existeLector(codLector);
			if(existeLector==true) {
				System.out.print("Código de obra: "); codObra=sc.nextLine();
				existeLibro=existeLibro(codObra);
				disponibleLibro=disponibleLibro(codObra);
				if(existeLibro==true && disponibleLibro==true) {
					try(PreparedStatement insertar = conexion.prepareStatement(novoRexistro); PreparedStatement bloquear = conexion.prepareStatement(bloquearObra)){
						insertar.setString(1, codLector);
						insertar.setString(2, codObra);
						insertar.setString(3, today.toString());
						insertar.executeUpdate();
						
						bloquear.setString(1, codObra);
						bloquear.executeUpdate();
					}catch(Exception e){
						conexion.rollback();
						System.out.println("error na táboa!: " + e.getMessage());
					}
				}else {
					System.out.println("Obra non existe ou non dispoñible para emprésito");
				}
			}
			else{
			System.out.println("Lector non existe");
			}
			break;
		//DEVOLUCIÓN	
		case "Devolución":
			String novaDevolucion="""
			UPDATE PrestamoDevolucion SET fechaDevolucion = ? WHERE codObra = ? AND fechaDevolucion IS NULL
			""";
			String desbloquearObra="""
					UPDATE Catalogo SET disponibleObra = 1 WHERE codObra = ?
					""";
			
			System.out.print("=== NOVA DEVOLUCIÓN DE LIBRO ===");
			System.out.print("Código de obra: "); codObra=sc.nextLine();
			try(PreparedStatement devolver = conexion.prepareStatement(novaDevolucion); PreparedStatement desbloquear = conexion.prepareStatement(desbloquearObra)){
				devolver.setString(1, today.toString());
				devolver.setString(2, codObra);
				devolver.executeUpdate();
				desbloquear.setString(1, codObra);
				desbloquear.executeUpdate();
			}catch(Exception e){
				conexion.rollback();
				System.out.println("error na táboa!: " + e.getMessage());
			}
			break;
		}
	}
	
	
	private static boolean disponibleLibro(String codObra) throws Exception{
		String comprobar = """
				SELECT disponibleObra FROM Catalogo WHERE codObra = ?
				""";
		boolean status;
		PreparedStatement codigo = conexion.prepareStatement(comprobar);
		codigo.setString(1, codObra);
		ResultSet rs = codigo.executeQuery();
		if(rs.getInt("disponibleObra")==0) {
			status=false;
		}
		else {
			status=true;
		}
		return status;
	}
	
	private static boolean existeLibro(String codObra) throws Exception {
		String comprobar = """
				SELECT 1 FROM Lectores WHERE codLector = ? LIMIT 1
				""";
		PreparedStatement codigo = conexion.prepareStatement(comprobar);
		codigo.setString(1, codObra);
		ResultSet rs = codigo.executeQuery();
		return rs.next();
	}

	private static boolean existeLector(String codLector) throws Exception {
		String comprobar = """
				SELECT 1 FROM Lectores WHERE codLector = ? LIMIT 1
				""";
		PreparedStatement codigo = conexion.prepareStatement(comprobar);
		codigo.setString(1, codLector);
		ResultSet rs = codigo.executeQuery();
		return rs.next();
		
	}
}
