package com.os.couchdb.client.http;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.os.couchdb.client.exception.HttpStatusExceptionFactory;
import com.os.couchdb.client.exception.ResponseFormatException;

public abstract class AbstractRequestCallback<T> implements RequestCallback {
	protected final MethodImpl m_method;
	protected MethodCallback<T> m_callback;

	public AbstractRequestCallback(MethodImpl pMethod, MethodCallback<T> pCallback) {
		this.m_method = pMethod;
		this.m_callback = pCallback;
	}

	public final void onResponseReceived(Request pRequest,Response pResponse) {
		m_method.m_request = pRequest;
		m_method.m_response = pResponse;
		GWT.log("onResponseReceived output : " + pResponse.getStatusCode() + "/'" + pResponse.getText() + "'",null);
		GWT.log("onResponseReceived headers:" + pResponse.getHeadersAsString(),null);
		if(!m_callback.isHttpStatusCodeOk(pResponse.getStatusCode())) {
			m_callback.onFailure(m_method, HttpStatusExceptionFactory.makeException(pResponse));
		} else {
			try {
				m_callback.onSuccess(m_method, parseResult(pResponse));
			} catch(ResponseFormatException rfe) {
				m_callback.onFailure(m_method, rfe);
			} catch (Throwable e) {
				GWT.log("Could not parse response: " + e, e);
				m_callback.onFailure(m_method, new ResponseFormatException("onResponseReceived - parse result",e));
			}
		}
	}

	protected abstract T parseResult(Response pResponse) throws Exception;

}
