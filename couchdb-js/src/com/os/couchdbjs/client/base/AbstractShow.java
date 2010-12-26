package com.os.couchdbjs.client.base;

import com.google.gwt.core.client.JavaScriptObject;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseRequest;
import com.os.couchdbjs.client.model.BaseResponse;
import com.os.couchdbjs.client.model.JSOModel;

public abstract class AbstractShow {
	public AbstractShow() {
		jsExport();
	}
	
	private final native void jsExport() /*-{
		$wnd.show = this.@com.os.couchdbjs.client.base.AbstractShow::showInternal(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public JavaScriptObject showInternal(JavaScriptObject pDoc,JavaScriptObject pReq) {
		BaseResponse resp = show((pDoc != null) ? new BaseDocument(JSOModel.fromJavascriptObject(pDoc)) : null,new BaseRequest(JSOModel.fromJavascriptObject(pReq)));
		return resp.getModel();
	}
	
	public abstract BaseResponse show(BaseDocument pDoc,BaseRequest pReq);
}