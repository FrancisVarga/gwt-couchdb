package com.os.couchdb.client.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.UnsafeNativeLong;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.os.couchdb.client.http.Defaults;

/**
 * Java overlay of a JavaScriptObject.
 */
public abstract class JSOModel extends JavaScriptObject {
	/**
	 * JSON Wrapper to Boost performance, specially in arrays More information in
	 * http ://weblogs.asp.net/yuanjian/archive/2009/03/22/json-performance-
	 * comparison -of-eval-new-function-and-json.aspx Conclusions: choose eval in
	 * IE6, 7 choose native JSON in IE8 choose new Function in Firefox2, 3 choose
	 * eval in Safari4 eval has the same performance as new Function on the whole
	 * when you use the other browsers.
	 */

	protected static JavaScriptObject parse(String text) {
		JSONValue val = JSONParser.parse(text);
		if(val.isObject() != null)
			return val.isObject().getJavaScriptObject();
		else if(val.isArray() != null)
			return val.isArray().getJavaScriptObject();
		else 
			return null;
	}

	protected static final String toJson(JavaScriptObject pObj) {
		JSONValue jsonValue = new JSONObject(pObj);
		return jsonValue.toString();
	}
	
	// wrapper
	/**
	protected static native JavaScriptObject parse(String text)/-{
		var Browser = function() {
		               var o = {
		                   ie: 0,
		                   opera: 0,
		                   gecko: 0,
		                   webkit: 0
		               };
		               var ua = navigator.userAgent, m;
		               if ( ( /KHTML/ ).test( ua ) ) {
		                   o.webkit = 1;
		               }
		               // Modern WebKit browsers are at least X-Grade
		               m = ua.match(/AppleWebKit\/([^\s]*)/);
		               if (m&&m[1]) {
		                   o.webkit=parseFloat(m[1]);
		               }

		               if (!o.webkit) { // not webkit
		                   // @todo check Opera/8.01 (J2ME/MIDP; Opera Mini/2.0.4509/1316; fi; U; ssr)
		                   m=ua.match(/Opera[\s\/]([^\s]*)/);
		                   if (m&&m[1]) {
		                       o.opera=parseFloat(m[1]);
		                   } else { // not opera or webkit
		                       m=ua.match(/MSIE\s([^;]*)/);
		                       if (m&&m[1]) {
		                           o.ie=parseFloat(m[1]);
		                       } else { // not opera, webkit, or ie
		                           m=ua.match(/Gecko\/([^\s]*)/);
		                           if (m) {
		                               o.gecko=1; // Gecko detected, look for revision
		                               m=ua.match(/rv:([^\s\)]*)/);
		                               if (m&&m[1]) {
		                                   o.gecko=parseFloat(m[1]);
		                               }
		                           }
		                       }
		                   }
		               }
		               return o;
		           }();
		var __json = null;
		if ( typeof JSON !== "undefined" ) {
			__json = JSON;
		}
		var browser = Browser;
		if ( __json !== null ) {
				return __json.parse( text );
		}
		if ( browser.gecko ) {
		   return new Function( "return " + text )();
		}
		return eval( "(" + text + ")" );
	}-/;

	protected static final native String toJson(JavaScriptObject pObj) /-{
		(function () {
		   var m = {  // A character conversion map
		           '\b': '\\b', '\t': '\\t',  '\n': '\\n', '\f': '\\f',
		           '\r': '\\r', '"' : '\\"',  '\\': '\\\\'
		       },
		       s = { // Map type names to functions for serializing those types
		           'boolean': function (x) { return String(x); },
		           'null': function (x) { return "null"; },
		           number: function (x) { return isFinite(x) ? String(x) : 'null'; },
		           string: function (x) {
		               if (/["\\\x00-\x1f]/.test(x)) {
		                   x = x.replace(/([\x00-\x1f\\"])/g, function(a, b) {
		                       var c = m[b];
		                       if (c) {
		                           return c;
		                       }
		                       c = b.charCodeAt();
		                       return '\\u00' + Math.floor(c / 16).toString(16) + (c % 16).toString(16);
		                  });
		              }
		              return '"' + x + '"';
		          },
		          array: function (x) {
		              var a = ['['], b, f, i, l = x.length, v;
		              for (i = 0; i < l; i += 1) {
		                  v = x[i];
		                  f = s[typeof v];
		                  if (f) {
		                      v = f(v);
		                      if (typeof v == 'string') {
		                          if (b) {
		                              a[a.length] = ',';
		                          }
		                          a[a.length] = v;
		                          b = true;
		                      }
		                  }
		              }
		              a[a.length] = ']';
		              return a.join('');
		          },
		          object: function (x) {
		              if (x) {
		                  if (x instanceof Array) {
		                      return s.array(x);
		                  }
		                  var a = ['{'], b, f, i, v;
		                  for (i in x) {
		                      v = x[i];
		                      f = s[typeof v];
		                      if (f) {
		                          v = f(v);
		                          if (typeof v == 'string') {
		                              if (b) {
		                                  a[a.length] = ',';
		                              }
		                              a.push(s.string(i), ':', v);
		                              b = true;
		                          }
		                      }
		                  }
		                  a[a.length] = '}';
		                  return a.join('');
		              }
		              return 'null';
		          }
		      };

		  return s.object(pObj);
			})();
	}-/;
  */

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
		if(key in this) 
			return "" + this[key];
	  else
	    return null;
	}-*/;

	public final native String get(String key, String defaultValue) /*-{
		if(key in this)
		  return ("" + this[key]);
		else  
		  return defaultValue;
	}-*/;

	public final native Date getNativeDate(String key) /*-{
		return this[key];
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

	public final void set(String key, JSONValue value) {
		if (value.isArray() != null) {
			set(key, value.isArray().getJavaScriptObject());
		} else if (value.isObject() != null) {
			set(key, value.isObject().getJavaScriptObject());
		} else if (value.isBoolean() != null) {
			set(key, value.isBoolean().booleanValue());
		} else if (value.isNumber() != null) {
			set(key, value.isNumber().doubleValue());
		} else if (value.isString() != null) {
			set(key, value.isString().stringValue());
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
		DateTimeFormat dtf = DateTimeFormat.getFormat(Defaults.getDateFormat());
		set(key, dtf.format(value));
	};

	public final native void unset(String key) /*-{
		if(typeof this[key] !== 'undefined') {
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
			DateTimeFormat dtf = DateTimeFormat.getFormat(Defaults.getDateFormat());
			return dtf.parse(get(key));
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
		if(key in this)
			return this[key];
	  else
	  	return null;
	}-*/;

	public final native JsArray<JSOModel> getArray(String key) /*-{
		if(key in this)
		  return this[key];
		else 
			return new Array();
	}-*/;

	public final native JsArrayString getStringArray(String key) /*-{
		if(key in this)
			return this[key];
		else
		  return new Array();
	}-*/;

	public final native JavaScriptObject getNativeValue(String key) /*-{
		if(key in this)
			return this[key];
	  else
	  	return null;
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

	public static final JSOModel fromJson(JSONValue pValue) {
		return pValue.isObject().getJavaScriptObject().cast();
	}

	public static final JsArray<JSOModel> arrayFromJson(JSONValue pValue) {
		return pValue.isArray().getJavaScriptObject().cast();
	};

	public static final JsArrayString arrayStringFromJson(String pString) {
		return parse(pString).cast();
	}
}