package com.os.couchdb.client.exception;

@SuppressWarnings("serial")
public class ResponseFormatException extends CouchDBException {
	public ResponseFormatException(String message, Throwable e) {
		super(message, e);
	}
}