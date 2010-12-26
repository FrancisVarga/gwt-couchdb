package com.os.couchdbjs.client.base;

import com.google.gwt.core.client.JavaScriptObject;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.JSOModel;

public abstract class AbstractViewMap {
	public AbstractViewMap() {
		jsExport();
	}

	public final native void jsExport() /*-{
		$wnd.map = this.@com.os.couchdbjs.client.base.AbstractViewMap::mapInternal(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;

	public final native void emit(JavaScriptObject pKey, JavaScriptObject pValue) /*-{
		emit(pKey,pValue);
	}-*/;

	public final native void emit(JavaScriptObject pKey, int pValue) /*-{
		emit(pKey,pValue);
	}-*/;

	public final native void emit(JavaScriptObject pKey, double pValue) /*-{
		emit(pKey,pValue);
	}-*/;

	public final native void emit(String pKey, JavaScriptObject pValue) /*-{
		emit(pKey,pValue);
	}-*/;

	public final native void emit(String pKey, int pValue) /*-{
		emit(pKey,pValue);
	}-*/;

	public final native void emit(String pKey, double pValue) /*-{
		emit(pKey,pValue);
	}-*/;

	public void mapInternal(JavaScriptObject pDoc) {
		map(new BaseDocument(JSOModel.fromJavascriptObject(pDoc)));
	}

	public void map(BaseDocument pModel) {
	}
}