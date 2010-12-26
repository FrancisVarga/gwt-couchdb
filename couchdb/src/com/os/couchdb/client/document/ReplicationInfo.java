package com.os.couchdb.client.document;

import java.util.List;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class ReplicationInfo extends BaseModel {
	private List<ReplicationHistoryRecord> m_history;
	
	public ReplicationInfo(JSOModel pModel) {
		super(pModel);
		m_history = super.getObjectList("history", new BaseModelFactory<ReplicationHistoryRecord>() {
			@Override
			public ReplicationHistoryRecord createInstance(JSOModel pModel) {
				return new ReplicationHistoryRecord(pModel);
			}
		});
	}
	
	public String getSourceUpdateSequencePosition() {
		return super.getModel().get("source_last_seq");
	}

	public String getSessionId() {
		return super.getModel().get("session_id");
	}

	public boolean isOk() {
		return super.getModel().getBoolean("ok");
	}

	public List<ReplicationHistoryRecord> getHistory() {
		return m_history;
	}
}