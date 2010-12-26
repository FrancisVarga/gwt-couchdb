package com.os.couchdbjs.client.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.UnsafeNativeLong;

/**
 * Java overlay of a JavaScriptObject.
 */
public abstract class JSOModel extends JavaScriptObject {
	protected static native JavaScriptObject parse(String text) /*-{
		return JSON.parse(text);
	}-*/;

	protected static native final String toJson(JavaScriptObject pObj) /*-{
		return JSON.stringify(pObj);
	}-*/;
	

	// Overlay types always have protected, zero-arg constructors
	protected JSOModel() {
	}

	/**
	 * Create an empty instance.
	 * 
	 * @return new Object
	 */
	public static JSOModel create() {
		return JavaScriptObject.createObject().cast();
	};

	/**
	 * Convert a JSON encoded string into a JSOModel instance.
	 * <p/>
	 * Expects a JSON string structured like '{"foo":"bar","number":123}'
	 * 
	 * @return a populated JSOModel object
	 */
	public static JSOModel fromJson(String jsonString) {
		return parse(jsonString).cast();
	};
	
	public static JSOModel fromJavascriptObject(JavaScriptObject pObject) {
		return pObject.cast();
	}

	/**
	 * Convert a JSON encoded string into an array of JSOModel instance.
	 * <p/>
	 * Expects a JSON string structured like '[{"foo":"bar","number":123}, {...}]'
	 * 
	 * @return a populated JsArray
	 */
	public static JsArray<JSOModel> arrayFromJson(String jsonString) {
		return parse(jsonString).cast();
	};

	public final native boolean hasKey(String key) /*-{
		return key in this;
	}-*/;

	public final native JsArrayString keys() /*-{
		var a = new Array();
		for (var p in this) { a.push(p); }
		return a;
	}-*/;

	@Deprecated
	public final Set<String> keySet() {
		JsArrayString array = keys();
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < array.length(); i++) {
			set.add(array.get(i));
		}
		return set;
	}

	public final native String get(String key) /*-{
		return (key in this) ? ("" + this[key]) : null;
	}-*/;

	public final native String get(String key, String defaultValue) /*-{
		return (key in this) ? ("" + this[key]) : defaultValue;
	}-*/;

	public final native Date getNativeDate(String key) /*-{
		return (key in this) ? this[key] : null;
	}-*/;

	public final native void set(String key, String value) /*-{
		this[key] = value;
	}-*/;

	public final native void set(String key, boolean value) /*-{
		this[key] = value;
	}-*/;

	public final native void set(String key, int value) /*-{
		this[key] = value;
	}-*/;

	@UnsafeNativeLong
	public final native void set(String key, long value) /*-{
		this[key] = value;
	}-*/;

	public final native void set(String key, double value) /*-{
		this[key] = value;
	}-*/;

	public final native void set(String key, JSOModel value) /*-{
		this[key] = value;
	}-*/;

	public final native void set(String key, JavaScriptObject value) /*-{
		this[key] = value;
	}-*/;

	public final void setOrClear(String key, String value) {
		if (value != null) {
			this.set(key, value);
		} else {
			this.unset(key);
		}
	}

	public final void setDateAsMillis(String key, Date value) {
		set(key, value.getTime());
	};

	public final void setDateAsArray(String key, Date value) {
		JsArrayInteger jsArray = JavaScriptObject.createArray().cast();
		jsArray.set(0, value.getYear());
		jsArray.set(1, value.getMonth());
		jsArray.set(2, value.getDate());
		set(key, jsArray);
	};

	public final void setTimeAsArray(String key, Date value) {
		JsArrayInteger jsArray = JavaScriptObject.createArray().cast();
		jsArray.set(0, value.getHours());
		jsArray.set(1, value.getMinutes());
		jsArray.set(2, value.getSeconds());
		set(key, jsArray);
	};

	public final void setDateTimeAsArray(String key, Date value) {
		JsArrayInteger jsArray = JavaScriptObject.createArray().cast();
		jsArray.set(0, value.getYear());
		jsArray.set(1, value.getMonth());
		jsArray.set(2, value.getDate());
		jsArray.set(3, value.getHours());
		jsArray.set(4, value.getMinutes());
		jsArray.set(5, value.getSeconds());
		set(key, jsArray);
	};

	public final void set(String key, Date value) {
		//DateTimeFormat dtf = DateTimeFormat.getFormat(Defaults.getDateFormat());
		//set(key, dtf.format(value));
	};

	public final native void unset(String key) /*-{
		if(key in this) {
			delete this[key];
		}
	}-*/;

	public final int getInt(String key) {
		String type = getType(key);
		if ("string".equals(type) || "number".equals(type)) {
			return Integer.parseInt(get(key));
		} else {
			return 0;
		}
	}

	public final boolean getBoolean(String key) {
		String type = getType(key);
		if ("string".equals(type) || "boolean".equals(type)) {
			return Boolean.parseBoolean(get(key));
		} else {
			return false;
		}
	}

	public final long getLong(String key) {
		String type = getType(key);
		if ("string".equals(type) || "number".equals(type)) {
			return Long.parseLong(get(key));
		} else {
			return 0;
		}
	}

	public final double getDouble(String key) {
		String type = getType(key);
		if ("string".equals(type) || "number".equals(type)) {
			return Double.parseDouble(get(key));
		} else {
			return 0;
		}
	}

	public final Date getDate(String key) {
		String type = getType(key);
		if ("number".equals(type)) {
			return new Date(getLong(key));
		} else if ("date".equals(type)) {
			return getNativeDate(key);
		} else if ("string".equals(type)) {
			//DateTimeFormat dtf = DateTimeFormat.getFormat(Defaults.getDateFormat());
			//return dtf.parse(get(key));
		} else if ("array".equals(type)) {
			JsArrayInteger jsAray = getNativeValue(key).cast();
			Date date = new Date();
			date.setYear(jsAray.get(0));
			date.setMonth(jsAray.get(1));
			date.setDate(jsAray.get(2));
			if (jsAray.length() > 3) {
				date.setHours(jsAray.get(3));
				date.setMinutes(jsAray.get(4));
				date.setSeconds(jsAray.get(5));
			}
			return date;
		}
		return null;
	}

	public final Date getTime(String key) {
		String type = getType(key);
		if ("array".equals(type)) {
			JsArrayInteger jsAray = getNativeValue(key).cast();
			Date date = new Date();
			date.setHours(jsAray.get(0));
			date.setMinutes(jsAray.get(1));
			date.setSeconds(jsAray.get(2));
			return date;
		}
		return null;
	}

	public final native JSOModel getObject(String key) /*-{
		return (key in this) ? this[key] : null;
	}-*/;

	public final native JsArray<JSOModel> getArray(String key) /*-{
		return (key in this) ? this[key] : new Array();
	}-*/;

	public final native JsArrayString getStringArray(String key) /*-{
		return (key in this) ? this[key] : new Array();
	}-*/;

	public final native JavaScriptObject getNativeValue(String key) /*-{
		return (key in this) ?  this[key] : null;
	}-*/;

	public final native String getType(String key) /*-{
		var value = this[key];
		var typeName = typeof value;
		if(typeName === 'string' || typeName === 'number' || typeName === 'boolean' || typeName === 'undefined' || typeName === 'function') {
			if(typeName === 'number' && !isFinite(value)) {
				return 'undefined';
			}
			return typeName;
		}
		if(typeName === 'object') {
			if(!value) {
				return 'null';
			}
			if(typeof value.split === 'function') {
				return 'string';
			}
			if(typeof value.getDate === 'function') {
				return 'date';
			}
			if (typeof value.length === 'number' && typeof value.splice === 'function') {
				return 'array';
			}
			return 'object';
		}
		return 'string';
	}-*/;

	public final boolean isString(String key) {
		return "string".equals(getType(key));
	}

	public final boolean isNumber(String key) {
		return "number".equals(getType(key));
	}

	public final boolean isBoolean(String key) {
		return "boolean".equals(getType(key));
	}

	public final boolean isArray(String key) {
		return "array".equals(getType(key));
	}

	public final boolean isDate(String key) {
		return "date".equals(getType(key));
	}

	public final boolean isObject(String key) {
		return "object".equals(getType(key));
	}

	public final boolean isNull(String key) {
		String type = getType(key);
		return "null".equals(type) || "undefined".equals(type);
	}

	public final void set(String key, List<JSOModel> values) {
		JsArray<JSOModel> array = JavaScriptObject.createArray().cast();
		for (int i = 0; i < values.size(); i++) {
			array.set(i, values.get(i));
		}
		setArray(key, array);
	}

	protected final native void setArray(String key, JsArray<JSOModel> values) /*-{
		this[key] = values;
	}-*/;

	public final String toJson() {
		return toJson(this);
	}

	public static final String toJson(List<JSOModel> values) {
		JsArray<JSOModel> arrayVals = JavaScriptObject.createArray().cast();
		int idx = 0;
		for (JSOModel model : values) {
			arrayVals.set(idx++, model);
		}
		return toJson(arrayVals);
	}

	public static final String toJson(JsArray<JSOModel> values) {
		return toJson(values);
	}

	public static final String asJson(JavaScriptObject pObj) {
		return toJson(pObj);
	}

	public static final JsArrayString arrayStringFromJson(String pString) {
		return parse(pString).cast();
	}
}