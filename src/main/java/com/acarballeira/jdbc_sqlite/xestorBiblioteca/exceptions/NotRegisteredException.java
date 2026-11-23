package com.acarballeira.jdbc_sqlite.xestorBiblioteca.exceptions;
/**Elemento non rexistrado nunha táboa
 */
public class NotRegisteredException extends Exception {
	public NotRegisteredException(String message) {
		super(message);
	}
}
