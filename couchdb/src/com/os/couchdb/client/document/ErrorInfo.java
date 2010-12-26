package com.os.couchdb.client.document;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class ErrorInfo extends BaseModel {
	public ErrorInfo(JSOModel pModel) {
		super(pModel);
	}
	
	public String getError() {
		return super.getModel().get("error");
	}
	
	public String getReason() {
		return super.getModel().get("reason");
	}
}
