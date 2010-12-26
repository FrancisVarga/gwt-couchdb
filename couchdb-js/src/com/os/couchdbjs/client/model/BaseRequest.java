package com.os.couchdbjs.client.model;

import java.util.List;
import java.util.Map;

public class BaseRequest extends BaseModel {
	private Map<String, String> m_cookieMap;
	private Map<String, String> m_infoMap;
	private Map<String, String> m_queryMap;
	private Map<String, String> m_formMap;
	private String[] m_pathInfo;
	private UserCtx m_userCtx;

	public static class UserCtx extends BaseModel {
		private List<String> m_roles = null;

		UserCtx(JSOModel pModel) {
			super(pModel);
		}

		public String getDB() {
			return get("db");
		}

		public String getUserName() {
			return get("name");
		}

		public List<String> getRoles() {
			if (m_roles == null) {
				m_roles = getStringList("roles");
			}
			return m_roles;
		}
	}

	public BaseRequest(JSOModel pModel) {
		super(pModel);
	}

	public String getBody() {
		return get("body");
	}

	public String getMethod() {
		return get("method");
	}

	public String getCookie(String pName) {
		if (m_cookieMap == null) {
			m_cookieMap = super.getStringMap("cookie");
		}
		return m_cookieMap.get(pName);
	}

	public String getQueryParam(String pName) {
		if (m_queryMap == null) {
			m_queryMap = super.getStringMap("query");
		}
		return m_queryMap.get(pName);
	}

	public String getFormParam(String pName) {
		if (m_formMap == null) {
			m_formMap = super.getStringMap("form");
		}
		return m_formMap.get(pName);
	}

	public String[] getPathInfo() {
		if (m_pathInfo == null) {
			m_pathInfo = super.getStringList("path").toArray(new String[0]);
		}
		return m_pathInfo;
	}

	public Map<String, String> getInfoMap() {
		if (m_infoMap == null) {
			m_infoMap = super.getStringMap("info");
		}
		return m_infoMap;
	}

	public UserCtx getUserCtx() {
		if (m_userCtx == null) {
			m_userCtx = new UserCtx(JSOModel.fromJavascriptObject(super.getModel().getNativeValue("userCtx")));
		}
		return m_userCtx;
	}
}