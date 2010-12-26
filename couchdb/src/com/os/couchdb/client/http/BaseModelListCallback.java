package com.os.couchdb.client.http;

import java.util.List;

import com.os.couchdb.client.model.BaseModel;

public interface BaseModelListCallback<T extends BaseModel> extends MethodCallback<List<T>> {
}
