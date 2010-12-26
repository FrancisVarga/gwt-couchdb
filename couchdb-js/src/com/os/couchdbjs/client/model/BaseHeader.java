package com.os.couchdbjs.client.model;

public class BaseHeader extends BaseModel {
	public BaseHeader(JSOModel pModel) {
		super(pModel);
	}
	
	public int getTotalRows() {
		return super.getModel().getInt("total_rows");
	}
	
	public int getOffset() {
		return super.getModel().getInt("offset");
	}
}
