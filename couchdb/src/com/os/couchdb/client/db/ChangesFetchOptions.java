package com.os.couchdb.client.db;

import com.google.gwt.http.client.URL;

public class ChangesFetchOptions {
	private boolean m_descending;
	private int m_since = -1;
	private int m_limit = -1;
	private String m_filter;
	private String m_filterParams;
	
	public ChangesFetchOptions() {
	}

	public ChangesFetchOptions setDescending(boolean descending) {
		this.m_descending = descending;
		return this;
	}

	public boolean isDescending() {
		return m_descending;
	}

	public ChangesFetchOptions setSince(int since) {
		this.m_since = since;
		return this;
	}

	public int getSince() {
		return m_since;
	}

	public ChangesFetchOptions setLimit(int limit) {
		this.m_limit = limit;
		return this;
	}

	public int getLimit() {
		return m_limit;
	}

	public ChangesFetchOptions setFilter(String filter) {
		this.m_filter = filter;
		return this;
	}

	public String getFilter() {
		return m_filter;
	}

	public ChangesFetchOptions setFilterParams(String filterParams) {
		this.m_filterParams = filterParams;
		return this;
	}

	public String getFilterParams() {
		return m_filterParams;
	}
	
	public String toQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		boolean first = true;
		if(m_descending) {
			sb.append("descending=true");
			first = false;
		}
		if(m_limit > 0) {
			if(!first) {
				sb.append('&');
			}
			sb.append("limit=").append(m_limit);
			first = false;
		}
		if(m_since > 0) {
			if(!first) {
				sb.append('&');
			}
			sb.append("since=").append(m_since);
			first = false;
		}
		if(m_filter != null && m_filter.length() > 0) {
			if(!first) {
				sb.append('&');
			}
			sb.append("filter=").append(URL.encodeComponent(m_filter));
			if(m_filterParams != null) {
				sb.append('&');
				sb.append("filter_params=").append(URL.encodeComponent(m_filterParams));
			}
		}
		if(sb.length() > 1) {
			return sb.toString();
		} else {
			return "";
		}
	}
}
