package com.os.couchdb.client.document;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class ValueRow extends BaseModel {
	private Object m_value;
	private Object m_key;

	private static Object getJavaValueFromJsValue(JSOModel pModel,String pKey) {
		String type = pModel.getType(pKey);
		if("string".equals(type)) {
			return pModel.get(pKey);
		} else if("number".equals(type)) {
			return pModel.getDouble(pKey);
		} else if("boolean".equals(type)) {
			return pModel.getBoolean(pKey) ? Boolean.TRUE : Boolean.FALSE;
		} else if("date".equals(type)) {
			return pModel.getDate(pKey);
		} else if("array".equals(type)) {
			return pModel.getNativeValue(pKey);
		} else if("null".equals(type) || "undefined".equals(type)) {
			return null;
		} else {
			return JSOModel.fromJavascriptObject(pModel.getNativeValue(pKey));
		}
	}
	
	public ValueRow(JSOModel pModel) {
		super(pModel);
		m_key = getJavaValueFromJsValue(pModel, "key");
		m_value = getJavaValueFromJsValue(pModel, "value");
	}
	
	public String getId() {
		return super.get("id");
	}

	public Object getKey() {
		return m_key;
	}

	/**
	 * Returns the value mapped to this row.
	 */
	public Object getValue() {
		return m_value;
	}
}