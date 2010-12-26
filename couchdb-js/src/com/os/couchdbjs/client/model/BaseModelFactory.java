package com.os.couchdbjs.client.model;

public interface BaseModelFactory<T extends BaseModel> {
	public T createInstance(JSOModel pModel);
}
