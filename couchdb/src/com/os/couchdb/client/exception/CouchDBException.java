package com.os.couchdb.client.exception;

@SuppressWarnings("serial")
public class CouchDBException extends RuntimeException {
	public CouchDBException(Throwable cause) {
		super(cause);
	}

	public CouchDBException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouchDBException(String message) {
		super(message);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		if(getMessage() != null) {
			sb.append("\"message\":\"").append(getMessage()).append("\"");
		}
		if(getCause() != null) {
			if(getMessage() != null) {
				sb.append(',');
			}
			sb.append("\"cause\":\"").append(getCause()).append("\"");
		}
		sb.append('}');
		return sb.toString();
	}
}