package com.os.couchdb.client.document;

import java.util.List;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class LuceneStatusInfo extends BaseModel {
	private List<String> m_fields;
	
	public LuceneStatusInfo(JSOModel pModel) {
		super(pModel);
		m_fields = super.getStringList("fields");
	}
	
	public boolean isCurrent() {
		return super.getModel().getBoolean("current");
	}
	
	public boolean isOptimized() {
		return super.getModel().getBoolean("optimized");
	}
	
	public long getDiskSize() {
		return super.getModel().getLong("disk_size");
	}
	
	public long getDocCount() {
		return super.getModel().getLong("doc_count");
	}
	
	public long getDocDelCount() {
		return super.getModel().getLong("doc_del_count");
	}
	
	public String getLastModified() {
		return super.get("last_modified");
	}
	
	public int getRefCount() {
		return super.getModel().getInt("ref_count");
	}
	
	public List<String> getStoredFields() {
		return m_fields;
	}
}
