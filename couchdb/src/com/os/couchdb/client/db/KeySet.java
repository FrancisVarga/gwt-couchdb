package com.os.couchdb.client.db;

import java.util.List;

import com.os.couchdb.client.model.BaseModel;

public class KeySet extends BaseModel {
	public KeySet(List<String> pKeys) {
		super();
		super.setStringList("keys", pKeys);
	}
}