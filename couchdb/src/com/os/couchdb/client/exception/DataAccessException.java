package com.os.couchdb.client.exception;

@SuppressWarnings("serial")
public class DataAccessException extends FailedStatusCodeException {
	DataAccessException(int pCode,String pCodeMessage,String pContent) {
		super(pCode,pCodeMessage,pContent);
	}
}
