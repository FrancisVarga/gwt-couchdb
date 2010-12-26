package com.os.couchdb.client.db;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.URL;

public class LuceneFetchOptions {
	private static class SortField {
		String m_fieldName;
		String m_type = "string";
		boolean m_descending;

		private String getValidType(String pType) {
			if("float".equalsIgnoreCase(pType)) {
				return "float";
			} else if("double".equalsIgnoreCase(pType)) {
				return "double";
			} else if("int".equalsIgnoreCase(pType)) {
				return "int";
			} else if("long".equalsIgnoreCase(pType)) {
				return "long";
			} else if("date".equalsIgnoreCase(pType)) {
				return "date";
			}
			return "string";
		}
		
		SortField(String pFieldName,String pType,boolean pDescending) {
			m_fieldName = pFieldName;
			m_type = getValidType(pType);
			m_descending = pDescending;
		}
		
		SortField(String pFieldName) {
			this(pFieldName,"string",false);
		}
		
		SortField(String pFieldName,boolean pDescending) {
			this(pFieldName,"string",pDescending);
		}
		
		SortField(String pFieldName,String pType) {
			this(pFieldName,pType,false);
		}
	}
	
	private String m_query;
	private boolean m_forceJSon;
	private boolean m_includeDocs;
	private int m_limit = -1;
	private int m_skip = -1;
	private boolean m_staleOk;
	private boolean m_debug;
	private List<SortField> m_sortFields;

	public LuceneFetchOptions() {
	}

	public void setQuery(String query) {
		this.m_query = query;
	}

	public String getQuery() {
		return m_query;
	}

	public void setForceJSon(boolean forceJSon) {
		this.m_forceJSon = forceJSon;
	}

	public boolean isForceJSon() {
		return m_forceJSon;
	}

	public void setIncludeDocs(boolean includeDocs) {
		this.m_includeDocs = includeDocs;
	}

	public boolean isIncludeDocs() {
		return m_includeDocs;
	}

	public void setLimit(int limit) {
		this.m_limit = limit;
	}

	public int getLimit() {
		return m_limit;
	}

	public void setSkip(int skip) {
		this.m_skip = skip;
	}

	public int getSkip() {
		return m_skip;
	}

	public void setStaleOk(boolean staleOk) {
		this.m_staleOk = staleOk;
	}

	public boolean isStaleOk() {
		return m_staleOk;
	}

	public void setDebug(boolean debug) {
		this.m_debug = debug;
	}

	public boolean isDebug() {
		return m_debug;
	}

	public void setSortFields(List<SortField> sortFields) {
		this.m_sortFields = sortFields;
	}

	public List<SortField> getSortFields() {
		return m_sortFields;
	}
	
	public void addSortField(SortField pField) {
		if(m_sortFields != null) {
			m_sortFields = new ArrayList<SortField>();
		}
		m_sortFields.add(pField);
	}
	
	public void addSort(String pField) {
		addSortField(new SortField(pField));
	}

	public void addSort(String pField,boolean pDescending) {
		addSortField(new SortField(pField,pDescending));
	}
	
	public void addSort(String pField,String pType) {
		addSortField(new SortField(pField,pType));
	}
	
	public void addSort(String pField,String pType,boolean pDescending) {
		addSortField(new SortField(pField,pType,pDescending));
	}
	
	private String buildSortString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(SortField sf : m_sortFields) {
			if(!first) {
				sb.append(',');
			}
			if(sf.m_descending) {
				sb.append("\\");
			}
			sb.append(sf.m_fieldName);
			if(!"string".equals(sf.m_type)) {
				sb.append(':').append(sf.m_type);
			}
		}
		return sb.toString();
	}
	
	public String toQuery() {
		if(m_query == null) {
			throw new IllegalArgumentException("query cannot be null");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		sb.append("q=").append(URL.encodeComponent(m_query));
		if(m_limit >= 0) {
			sb.append("&limit=").append(m_limit);
		}
		if(m_skip > 0) {
			sb.append("&skip=").append(m_skip);
		}
		if(m_includeDocs) {
			sb.append("&include_docs=true");
		}
		if(m_staleOk) {
			sb.append("&stale=ok");
		}
		if(m_debug) {
			sb.append("&debug=true");
		}
		if(m_forceJSon) {
			sb.append("&force_json=true");
		}
		if(m_sortFields != null && !m_sortFields.isEmpty()) {
			sb.append("&sort=").append(URL.encodeComponent(buildSortString()));
		}
		return sb.toString();
	}
}