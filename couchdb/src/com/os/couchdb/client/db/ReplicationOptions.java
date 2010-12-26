package com.os.couchdb.client.db;

import java.util.Map;

import com.os.couchdb.client.model.BaseModel;

public class ReplicationOptions extends BaseModel {
	public ReplicationOptions() {
		super();
	}

	public void setSource(String pSource) {
		super.getModel().set("source", pSource);
	}

	public void setTarget(String pTarget) {
		super.getModel().set("target", pTarget);
	}

	public void setContinuous(boolean pContinuous) {
		if(pContinuous) {
			super.getModel().set("continuous", pContinuous);
		} else {
			super.getModel().unset("continuous");
		}
	}

	public void setFilter(String pFilter) {
		super.getModel().set("filter", pFilter);
	}

	public void setFilterParams(String pFilterParams) {
		super.getModel().set("filter_params", pFilterParams);
	}

	public void setFilterParams(Map<String,String> pFilterParams) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(Map.Entry<String, String> e : pFilterParams.entrySet()) {
			if(!first) {
				sb.append('&');
			}
			sb.append(e.getKey()).append('=').append(e.getValue());
			first = false;
		}
		setFilterParams(sb.toString());
	}
	
	public void setCreateTarget(boolean pValue) {
		if(pValue) {
			super.getModel().set("create_target", true);
		}
	}
}
