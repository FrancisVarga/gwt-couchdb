package com.os.couchdbjs.client.base;

import java.util.List;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseModel;
import com.os.couchdbjs.client.model.JSOModel;

public abstract class AbstractValidateDocUpdate {
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
	
	public AbstractValidateDocUpdate() {
		jsExport();
	}
	
	private final native void jsExport() /*-{
		$wnd.validate = this.@com.os.couchdbjs.client.base.AbstractValidateDocUpdate::validateInternal(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public final native void unauthorized(String pMessage) /*-{
		throw({unauthorized: pMessage});
	}-*/;

	public final native void forbidden(String pMessage) /*-{
		throw({forbidden: pMessage});
	}-*/;
	
	public final void validateInternal(JavaScriptObject pNewDoc,JavaScriptObject pOldDoc,JavaScriptObject pUserCtx) {
		BaseDocument newDoc = new BaseDocument(JSOModel.fromJavascriptObject(pNewDoc));
		BaseDocument oldDoc = pOldDoc != null ? new BaseDocument(JSOModel.fromJavascriptObject(pOldDoc)) : null;
		UserCtx userCtx = new UserCtx(JSOModel.fromJavascriptObject(pUserCtx));
		validate(newDoc,oldDoc,userCtx);
	}
	
	public abstract void validate(BaseDocument pNewDoc,BaseDocument pOldDoc,UserCtx pUserCtx);
}