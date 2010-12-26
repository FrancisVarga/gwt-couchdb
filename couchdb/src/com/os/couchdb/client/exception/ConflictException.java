package com.os.couchdb.client.exception;

@SuppressWarnings("serial")
public class ConflictException extends FailedStatusCodeException {
	ConflictException(int pCode,String pCodeMessage,String pContent) {
		super(pCode,pCodeMessage,pContent);
	}
}
