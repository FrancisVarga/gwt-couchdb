package com.os.couchdb.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class ChangesResult extends BaseModel {
	public static class Row extends BaseModel {
		private int m_sequence;
		private String m_id;
		private List<String> m_revs;
		private boolean m_deleted;
		
		protected Row(JSOModel pModel) {
			super(pModel);
			m_id = pModel.get("id");
			m_sequence = pModel.getInt("seq");
			m_deleted = pModel.getBoolean("deleted");
			JsArray<JSOModel> revs = pModel.getNativeValue("changes").cast();
			m_revs = new ArrayList<String>(revs.length());
			for(int i=0;i < revs.length();i++) {
				m_revs.add(revs.get(i).get("rev"));
			}
		}

		public int getSequence() {
			return m_sequence;
		}

		public String getId() {
			return m_id;
		}

		public boolean isDeleted() {
			return m_deleted;
		}
		
		public List<String> getRevisions() {
			return m_revs;
		}
	}
	
	private int m_lastSequence;
	private List<Row> m_rows;
	
	public ChangesResult(JSOModel pModel) {
		super(pModel);
		m_lastSequence = pModel.getInt("last_seq");
		m_rows = super.getObjectList("result", new BaseModelFactory<Row>() {
			@Override
			public Row createInstance(JSOModel pModel) {
				return new Row(pModel);
			}
		});
	}

	public int getLastSequence() {
		return m_lastSequence;
	}

	public List<Row> getRows() {
		return m_rows;
	}
}
