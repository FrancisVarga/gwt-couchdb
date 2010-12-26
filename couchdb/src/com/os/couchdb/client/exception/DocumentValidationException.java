package com.os.couchdb.client.exception;

@SuppressWarnings("serial")
public class DocumentValidationException extends DataAccessException {
	DocumentValidationException(int pCode,String pCodeMessage,String pContent) {
		super(pCode,pCodeMessage,pContent);
	}
}
