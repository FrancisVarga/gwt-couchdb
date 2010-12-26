package com.os.couchdb.client.exception;

import com.os.couchdb.client.model.JSOModel;

@SuppressWarnings("serial")
public class FailedStatusCodeException extends Exception {
	private int m_httpStatus;
	private String m_httpStatusMessage;
	private String m_couchError = "";
	private String m_couchReason = "";
	
	FailedStatusCodeException(int pCode,String pCodeMessage,String pContent) {
		super(pCodeMessage);
		m_httpStatus = pCode;
		m_httpStatusMessage = pCodeMessage;
		try {
			if(pContent != null && pContent.length() > 0) {
				JSOModel contentModel = JSOModel.fromJson(pContent);
				if(contentModel.hasKey("error")) {
					m_couchError = contentModel.get("error");
					m_couchReason = contentModel.get("reason");
				} 
			}
		} catch(Throwable th) {
			th.printStackTrace();
		}
	}

	public int getStatusCode() {
		return m_httpStatus;
	}

	public String getStatusMessage() {
		return m_httpStatusMessage;
	}

	public String getCouchError() {
		return m_couchError;
	}

	public String getCouchReason() {
		return m_couchReason;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("{");
		sb.append("\"Status\":").append(m_httpStatus).append(',');
		sb.append("\"Message\":\"").append(m_httpStatusMessage).append("\"");
		if(m_couchError != null) {
			sb.append(',');
			sb.append("\"err\":\"").append(m_couchError).append("\",");
			sb.append("\"reason\":\"").append(m_couchReason).append("\"");
		}
		sb.append('}');
		return sb.toString();
	}
}