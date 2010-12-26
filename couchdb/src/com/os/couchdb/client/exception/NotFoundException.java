package com.os.couchdb.client.exception;

@SuppressWarnings("serial")
public class NotFoundException extends FailedStatusCodeException {
	public NotFoundException(int pCode,String pCodeMessage,String pContent) {
		super(pCode,pCodeMessage,pContent);
	}
}
