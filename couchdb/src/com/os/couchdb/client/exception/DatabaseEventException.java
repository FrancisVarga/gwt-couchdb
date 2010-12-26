package com.os.couchdb.client.exception;

@SuppressWarnings("serial")
public class DatabaseEventException extends CouchDBException {
	public DatabaseEventException(String pMessage,Throwable pCause) {
		super(pMessage,pCause);
	}

	public DatabaseEventException(String pMessage) {
		super(pMessage);
	}

	public DatabaseEventException(Throwable pCause) {
		super(pCause);
	}
}
