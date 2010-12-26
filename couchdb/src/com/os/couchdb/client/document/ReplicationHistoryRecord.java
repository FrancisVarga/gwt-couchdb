package com.os.couchdb.client.document;

import java.util.Date;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class ReplicationHistoryRecord extends BaseModel {
	public ReplicationHistoryRecord(JSOModel pModel) {
		super(pModel);
	}
	
	public String getSessionId() {
		return super.getModel().get("session_id");
	}

	public Date getStartTime() {
		return super.getModel().getDate("start_time");
	}
	
	public Date getEndTime() {
		return super.getModel().getDate("end_time");
	}
	
	public int getStartLastSeq() {
		return super.getModel().getInt("start_last_seq");
	}
	
	public int getEndLastSeq() {
		return super.getModel().getInt("end_last_seq");
	}
	
	public int getRecordedSeq() {
		return super.getModel().getInt("recorded_seq");
	}

	public int getMissingChecked() {
		return super.getModel().getInt("missing_checked");
	}
	
	public int getMissingFound() {
		return super.getModel().getInt("missing_found");
	}

	public int getDocsRead() {
		return super.getModel().getInt("docs_read");
	}

	public int getDocsWritten() {
		return super.getModel().getInt("docs_written");
	}

	public int getDocsWrittenFailures() {
		return super.getModel().getInt("doc_write_failures");
	}
}
