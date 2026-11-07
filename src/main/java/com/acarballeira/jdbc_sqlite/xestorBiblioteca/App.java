package com.acarballeira.jdbc_sqlite.xestorBiblioteca;

import java.util.Scanner;

public class App {
	static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
    	
    }
    
	private static void menu() {
		System.out.println("=== XsTOR = XESTOR DE BIBLIOTECAS ===\r\n"
				+ "-------------------------------------\r\n"
				+ "1. Xestión de préstamos\r\n"
	          	+ "2. Xestión de catálogo\r\n"
	         	+ "3. Outros\r\n"
	       		+ "-------------------------------------\r\n"
	       		+ "PREMA NUNHA DAS SEGUINTES OPCIÓNS: ");
		int menu = sc.nextInt();
		
	}
}
