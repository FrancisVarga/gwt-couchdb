package com.os.couchdbjs.client.base;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.UnsafeNativeLong;

public abstract class AbstractViewReduce {
	public AbstractViewReduce() {
		jsExport();
	}

	private final native void jsExport() /*-{
		$wnd.reduce = this.@com.os.couchdbjs.client.base.AbstractViewReduce::reduceInternal(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Z);
	}-*/;

	public final native int sum(JsArrayInteger pArray) /*-{
		return sum(pArray);
	}-*/;

	public final native double sum(JsArrayNumber pArray) /*-{
		return sum(pArray);
	}-*/;

	public final native JavaScriptObject wrap(int i) /*-{
		return new Number(i);
	}-*/;

	public final native JavaScriptObject wrap(float f) /*-{
		return new Number(f);
	}-*/;

	public final native JavaScriptObject wrap(String s) /*-{
		return new String(s);
	}-*/;

	@UnsafeNativeLong
	public final native JavaScriptObject wrapAsDate(long f) /*-{
		return new Date(f);
	}-*/;

	public final JavaScriptObject wrap(Date pDate) {
		return wrapAsDate(pDate.getTime());
	}

	public final JavaScriptObject reduceInternal(JavaScriptObject pKeys, JavaScriptObject pValues, boolean pReReduce) {
		JsArray<JavaScriptObject> keys = null;
		if (pKeys != null) {
			keys = pKeys.cast();
		}
		JsArray<JavaScriptObject> values = pValues.cast();
		return reduce(keys, values, pReReduce);
	}

	public abstract JavaScriptObject reduce(JsArray<JavaScriptObject> pKeys, JsArray<JavaScriptObject> pValues,
			boolean pReReduce);
}
