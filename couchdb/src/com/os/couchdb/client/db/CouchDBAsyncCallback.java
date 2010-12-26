package com.os.couchdb.client.db;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CouchDBAsyncCallback<T> extends AsyncCallback<T> {
	public boolean isHttpStatusCodeOk(int pCode);
}
