package com.os.couchdb.client.document;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class CouchDBStatus extends BaseModel {
	public CouchDBStatus(JSOModel pData) {
		super(pData);
	}

	public String getName() {
		return super.get("db_name");
	}

	public long getDocumentCount() {
		return m_data.getLong("doc_count");
	}

	public long getDeletedDocumentCount() {
		return m_data.getLong("doc_del_count");
	}

	public int getUpdateSequence() {
		return m_data.getInt("update_seq");
	}

	public boolean isCompactRunning() {
		return m_data.getBoolean("compact_running");
	}

	public long getDiskSize() {
		return m_data.getLong("disk_size");
	}

	@Override
	public String toString() {
		return m_data.toJson();
	}
}
