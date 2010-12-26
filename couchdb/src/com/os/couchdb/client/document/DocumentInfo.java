package com.os.couchdb.client.document;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class DocumentInfo extends BaseModel {
	public DocumentInfo(JSOModel pModel) {
		super(pModel);
	}
	
	public String getId() {
		return super.getModel().get("id");
	}

	public void setId(String id) {
		super.getModel().set("id", id);
	}

	public String getRevision() {
		return super.getModel().get("rev");
	}

	public void setRevision(String revision) {
		super.getModel().set("rev", revision);
	}

	public boolean isOk() {
		return super.getModel().getBoolean("ok");

	}

	public void setOk(boolean ok) {
		super.getModel().set("ok", ok);
	}
	
	public String getError() {
		return super.getModel().get("error");
	}

	public String getReason() {
		return super.getModel().get("reason");
	}
	
	public boolean hasError() {
		return super.getModel().hasKey("error");
	}
}