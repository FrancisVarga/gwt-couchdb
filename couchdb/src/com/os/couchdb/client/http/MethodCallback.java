package com.os.couchdb.client.http;

public interface MethodCallback<T> {
	public void onFailure(Method method, Throwable exception);
	public void onSuccess(Method method, T response);
	public boolean isHttpStatusCodeOk(int pCode);
}