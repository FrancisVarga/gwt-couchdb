package com.os.couchdbjs.client.base;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseRequest;
import com.os.couchdbjs.client.model.BaseResponse;
import com.os.couchdbjs.client.model.JSOModel;

public abstract class AbstractUpdateHandler {
	public AbstractUpdateHandler() {
		jsExport();
	}
	
	private final native void jsExport() /*-{
		$wnd.update = this.@com.os.couchdbjs.client.base.AbstractUpdateHandler::updateInternal(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;
	
	public JsArray<JavaScriptObject> updateInternal(JavaScriptObject pDoc,JavaScriptObject pReq) {
		BaseDocument doc = pDoc != null ? new BaseDocument(JSOModel.fromJavascriptObject(pDoc)) : null;
		return update(doc,new BaseRequest(JSOModel.fromJavascriptObject(pReq)));
	}
	
	public final void throwException(String pType,String pMessage) {
		throw new JavaScriptException(pType, pMessage);
	}
	
	public JsArray<JavaScriptObject> createResponse(BaseDocument pDoc,String pBody) {
		BaseResponse resp = new BaseResponse();
		resp.setBody(pBody);
		JsArray<JavaScriptObject> result = JavaScriptObject.createArray().cast();
		result.set(0, pDoc != null ? pDoc.getModel() : null);
		result.set(1, resp.getModel());
		return result;
	}

	public JsArray<JavaScriptObject> createResponse(BaseDocument pDoc,String pContentType,String pBody) {
		BaseResponse resp = new BaseResponse();
		resp.setBody(pBody);
		resp.addHeader("Content-Type", pContentType);
		JsArray<JavaScriptObject> result = JavaScriptObject.createArray().cast();
		result.set(0, pDoc != null ? pDoc.getModel() : null);
		result.set(1, resp.getModel());
		return result;
	}

	public JsArray<JavaScriptObject> createResponse(BaseDocument pDoc,BaseResponse pResp) {
		JsArray<JavaScriptObject> result = JavaScriptObject.createArray().cast();
		result.set(0, pDoc != null ? pDoc.getModel() : null);
		result.set(1, pResp.getModel());
		return result;
	}
	
	public abstract JsArray<JavaScriptObject> update(BaseDocument pDoc,BaseRequest pReq);
}
