package com.os.couchdb.client.db;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.os.couchdb.client.model.JSOModel;

public class FetchOptions {
	private Map<String, Object> m_content = new HashMap<String, Object>();

	final static Set<String> JSON_ENCODED_OPTIONS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("key",
			"startkey", "endkey")));

	public FetchOptions() {
	}

	public FetchOptions(Map<String, Object> map) {
		for (Map.Entry<String, Object> e : map.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}

	/**
	 * Copies the options of the given Options object if it is not
	 * <code>null</code>.
	 * 
	 * @param options
	 *          Options to be copied, can be <code>null</code>.
	 */
	public FetchOptions(FetchOptions options) {
		if (options != null) {
			// options values are allready encoded thus need all to be added unencoded
			for (String key : options.keys()) {
				putUnencoded(key, options.get(key));
			}
		}
	}

	public FetchOptions(String key, Object value) {
		putUnencoded(key, value);
	}

	public FetchOptions put(String key, Object value) {
		if (JSON_ENCODED_OPTIONS.contains(key)) {
			return putEncoded(key, value);
		} else {
			return putUnencoded(key, value);
		}
	}

	protected String jsonEncode(Object value) {
		if (value instanceof JavaScriptObject) {
			return JSOModel.asJson((JavaScriptObject) value);
		}
		if (value instanceof Number || value instanceof Boolean) {
			return String.valueOf(value);
		}
		if (value instanceof Date) {
			return String.valueOf(((Date) value).getTime());
		}
		return String.valueOf(value); // / !!!!!!!!!!!!!!!!!!!!!! TO DO
	}

	protected FetchOptions putEncoded(String key, Object value) {
		String json = jsonEncode(value);
		m_content.put(key, json);
		return this;
	}

	protected FetchOptions putUnencoded(String key, Object value) {
		m_content.put(key, value);
		return this;
	}

	public FetchOptions key(Object key) {
		return putEncoded("key", key);
	}

	public FetchOptions startKey(Object key) {
		return putEncoded("startkey", key);
	}

	public FetchOptions startKeyDocId(String docId) {
		return putUnencoded("startkey_docid", docId);
	}

	public FetchOptions endKey(Object key) {
		return putEncoded("endkey", key);
	}

	public FetchOptions endKeyDocId(String docId) {
		return putUnencoded("endkey_docid", docId);
	}

	public FetchOptions limit(int limit) {
		return putUnencoded("limit", limit);
	}

	public FetchOptions update(boolean update) {
		return putUnencoded("update", update);
	}

	public FetchOptions descending(boolean update) {
		return putUnencoded("descending", update);
	}

	public FetchOptions skip(int skip) {
		return putUnencoded("skip", skip);
	}

	public FetchOptions group(boolean group) {
		return putUnencoded("group", group);
	}

	public FetchOptions groupLevel(int groupLevel) {
		return putUnencoded("group_level", groupLevel);
	}
	
	public FetchOptions stale() {
		return putUnencoded("stale", "ok");
	}

	public FetchOptions reduce(boolean reduce) {
		return putUnencoded("reduce", reduce);
	}

	public FetchOptions includeDocs(boolean includeDocs) {
		return putUnencoded("include_docs", includeDocs);
	}

	public String toQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		boolean first = true;
		for (String key : keys()) {
			if (!first) {
				sb.append("&");
			}
			sb.append(key).append("=");
			sb.append(URL.encode(get(key).toString()));
			first = false;
		}
		if (sb.length() <= 1) {
			return "";
		} else {
			return sb.toString();
		}
	}

	public Object get(String key) {
		return m_content.get(key);
	}

	/**
	 * Can be imported statically to have a syntax a la
	 * <code>option().count(1);</code>.
	 * 
	 * @return new Option instance
	 */
	public static FetchOptions option() {
		return new FetchOptions();
	}

	public Set<String> keys() {
		return m_content.keySet();
	}
}