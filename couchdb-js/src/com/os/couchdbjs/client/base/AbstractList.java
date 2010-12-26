package com.os.couchdbjs.client.base;

import com.google.gwt.core.client.JavaScriptObject;
import com.os.couchdbjs.client.model.BaseHeader;
import com.os.couchdbjs.client.model.BaseRequest;
import com.os.couchdbjs.client.model.BaseResponse;
import com.os.couchdbjs.client.model.JSOModel;
import com.os.couchdbjs.client.model.ValueRow;

public abstract class AbstractList {
	public AbstractList() {
		jsExport();
	}
	
	private final native void jsExport() /*-{
		$wnd.list = this.@com.os.couchdbjs.client.base.AbstractList::listInternal(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;

	public final native void send(String pValue) /*-{
		send(pValue);
	}-*/;

	public final native JavaScriptObject getRowNative() /*-{
		return getRow();
	}-*/;

	public final native void startNative(JavaScriptObject pObject) /*-{
		start(pObject);
	}-*/;
	
	public final void start(BaseResponse pResp) {
		startNative(pResp.getModel());
	}
	
	public final ValueRow getRow() {
		JavaScriptObject row = getRowNative();
		if(row != null) {
			return new ValueRow(JSOModel.fromJavascriptObject(row));
		} else {
			return null;
		}
	}
	
	public final void listInternal(JavaScriptObject pHead,JavaScriptObject pReq) {
		list((pHead != null) ? new BaseHeader(JSOModel.fromJavascriptObject(pHead)) : null,new BaseRequest(JSOModel.fromJavascriptObject(pReq)));
	}
	
	public abstract void list(BaseHeader pHeader,BaseRequest pRequest);
}
