package com.os.couchdbjs.client.base;

import com.google.gwt.core.client.JavaScriptObject;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseRequest;
import com.os.couchdbjs.client.model.JSOModel;

public abstract class AbstractFilter {
	public AbstractFilter() {
		jsExport();
	}
	
	private final native void jsExport() /*-{
		$wnd.filter = this.@com.os.couchdbjs.client.base.AbstractFilter::filterInternal(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;

	public final boolean filterInternal(JavaScriptObject pDoc,JavaScriptObject pReq) {
		return filter(new BaseDocument(JSOModel.fromJavascriptObject(pDoc)),new BaseRequest(JSOModel.fromJavascriptObject(pReq)));
	}
	
	public abstract boolean filter(BaseDocument pDoc,BaseRequest pRequest);
}
