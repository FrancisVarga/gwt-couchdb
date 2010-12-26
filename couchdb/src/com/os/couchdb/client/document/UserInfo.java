package com.os.couchdb.client.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JsArrayString;
import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class UserInfo extends BaseModel {
	public static class UserCtx extends BaseModel {
		private Set<String> m_roles;
		
		UserCtx(JSOModel pModel) {
			super(pModel);
			m_roles = new HashSet<String>();
			JsArrayString roles = super.getModel().getStringArray("roles");
			for(int i=0;i < roles.length();i++) {
				m_roles.add(roles.get(i));
			}
		}
		
		public String getName() {
			return get("name");
		}
	}
	
	public static class AuthInfo extends BaseModel {
		private List<String> m_handlers;
		
		AuthInfo(JSOModel pModel) {
			super(pModel);
			m_handlers = new ArrayList<String>();
			JsArrayString handlers = super.getModel().getStringArray("authentication_handlers");
			for(int i=0;i < handlers.length();i++) {
				m_handlers.add(handlers.get(i));
			}
		}
		
		public String getAuthenticationDB() {
			return get("authentication_db");
		}
		
		public String getAuthenticationHandler() {
			return get("authenticated");
		}
	}
	
	private UserCtx m_userCtx;
	private AuthInfo m_authInfo;
	
	public UserInfo(JSOModel pModel) {
		super(pModel);
		m_userCtx = new UserCtx(super.getModel().getObject("userCtx")); 
		m_authInfo = new AuthInfo(super.getModel().getObject("info")); 
	}
	
	public boolean isOk() {
		return super.getModel().getBoolean("ok");
	}
	
	public String getName() {
		return m_userCtx.getName();
	}
	
	public boolean hasRole(String pRole) {
		return m_userCtx.m_roles.contains(pRole);
	}
	
	public boolean isAdmin() {
		return hasRole("_admin");
	}
	
	public List<String> getAuthHandlers() {
		return m_authInfo.m_handlers;
	}
	
	public String getAuthenticationDB() {
		return m_authInfo.getAuthenticationDB();
	}

	public String getAuthenticationHandler() {
		return m_authInfo.getAuthenticationHandler();
	}
}