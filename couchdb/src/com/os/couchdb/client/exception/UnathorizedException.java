package com.os.couchdb.client.exception;

@SuppressWarnings("serial")
public class UnathorizedException extends FailedStatusCodeException {
	public UnathorizedException(int pCode,String pCodeMessage,String pContent) {
		super(pCode,pCodeMessage,pContent);
	}
}
