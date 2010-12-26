package com.os.couchdb.client.document;

import java.util.List;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class UUIDResponse extends BaseModel {
	List<String> m_uuids;
	
	public UUIDResponse(JSOModel pModel) {
		super(pModel);
		m_uuids = super.getStringList("uuids");
	}
	
	public List<String> getUUIDList() {
		return m_uuids;
	}
}