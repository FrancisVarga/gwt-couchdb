package com.os.couchdb.client.document;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public abstract class AbstractViewResult extends BaseModel {
	public AbstractViewResult(JSOModel pModel) {
		super(pModel);
	}

	public int getTotalRows() {
		return super.getModel().getInt("total_rows");
	}

	public int getOffset() {
		return super.getModel().getInt("offset");
	}
}