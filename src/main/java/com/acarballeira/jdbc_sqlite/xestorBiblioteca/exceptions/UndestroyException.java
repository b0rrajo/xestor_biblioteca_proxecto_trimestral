package com.acarballeira.jdbc_sqlite.xestorBiblioteca.exceptions;
/**Non se pode destruir un elemento debido a relacións existentes
 */
public class UndestroyException extends Exception {
	public UndestroyException(String message) {
		super(message);
	}
}
