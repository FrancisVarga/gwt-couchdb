package com.os.couchdbjs.client.model;

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
	}
	
	public String getId() {
		return super.get("id");
	}

	public Object getKey() {
		if(m_key == null) {
			m_key = getJavaValueFromJsValue(getModel(), "key");
		}
		return m_key;
	}

	/**
	 * Returns the value mapped to this row.
	 */
	public Object getValue() {
		if(m_value == null) {
			m_value = getJavaValueFromJsValue(getModel(), "value");
		}
		return m_value;
	}
}