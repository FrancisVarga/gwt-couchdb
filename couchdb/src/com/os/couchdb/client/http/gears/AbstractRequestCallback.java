package com.os.couchdb.client.http.gears;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.httprequest.HttpRequest;
import com.google.gwt.gears.client.httprequest.RequestCallback;
import com.os.couchdb.client.exception.HttpStatusExceptionFactory;
import com.os.couchdb.client.exception.ResponseFormatException;
import com.os.couchdb.client.http.MethodCallback;

/**
 * 
 */
public abstract class AbstractRequestCallback<T> implements RequestCallback {
	protected final GearsMethodImpl m_method;
	protected MethodCallback<T> m_callback;

	public AbstractRequestCallback(GearsMethodImpl pMethod, MethodCallback<T> pCallback) {
		this.m_method = pMethod;
		this.m_callback = pCallback;
	}

	public final void onResponseReceived(HttpRequest pRequest) {
		//GWT.log("onResponseReceived input : " + m_method.m_method + " " + m_method.m_uri,null);
		//GWT.log("onResponseReceived output : " + pRequest.getStatus() + "/'" + pRequest.getResponseText() + "'",null);
		//GWT.log("onResponseReceived output blob : " + pRequest.getResponseBlob().getLength() + "," + pRequest.getReadyState(),null);
		//GWT.log("onResponseReceived headers:" + pRequest.getAllResponseHeaders(),null);
		if(!m_callback.isHttpStatusCodeOk(pRequest.getStatus())) {
			m_callback.onFailure(m_method, HttpStatusExceptionFactory.makeException(pRequest));
		} else {
			try {
				m_callback.onSuccess(m_method, parseResult(pRequest));
			} catch(ResponseFormatException rfe) {
				m_callback.onFailure(m_method, rfe);
			} catch (Throwable e) {
				GWT.log("Could not parse response: " + e, e);
				m_callback.onFailure(m_method, new ResponseFormatException("onResponseReceived - parse result",e));
			}
		}
	}

	protected abstract T parseResult(HttpRequest pRequest) throws Exception;
}