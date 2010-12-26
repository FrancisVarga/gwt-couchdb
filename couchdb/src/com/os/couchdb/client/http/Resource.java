package com.os.couchdb.client.http;

import java.util.Map;

import com.google.gwt.http.client.URL;
import com.os.couchdb.client.http.gears.GearsMethodImpl;

/**
 */
public class Resource {
	public static final String CONTENT_TYPE_TEXT = "plain/text";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_XML = "application/xml";
	public static final String CONTENT_TYPE_RSS = "application/rss+xml";
	public static final String CONTENT_TYPE_ATOM = "application/atom+xml";
	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

	public static final String HEADER_ACCEPT = "Accept";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";

	String m_uri;
	String m_query;

	private Map<String, String> m_headers = defaultHeaders();

	public Resource(String uri) {
		int pos = uri.indexOf('?');
		if (pos >= 0) {
			this.m_uri = uri.substring(0, pos);
			this.m_query = uri.substring(pos + 1);
		} else {
			this.m_uri = uri;
		}
	}

	public Resource(String uri, String query) {
		this.m_uri = uri;
		this.m_query = query;
	}

	public Method get() {
		return get(false);
	}

	public Method get(boolean pGearsVersion) { 
		if(pGearsVersion) {
			return new GearsMethodImpl(this, "GET").headers(m_headers);
		} else {
			return new MethodImpl(this, "GET").headers(m_headers);
		}
	}
	
	public Method put() {
		return put(false);
	}

	public Method put(boolean pGearsVersion) { 
		if(pGearsVersion) {
			return new GearsMethodImpl(this, "PUT").headers(m_headers);
		} else {
			return new MethodImpl(this, "PUT").headers(m_headers);
		}
	}
	
	public Method post() {
		return post(false);
	}

	public Method post(boolean pGearsVersion) { 
		if(pGearsVersion) {
			return new GearsMethodImpl(this, "POST").headers(m_headers);
		} else {
			return new MethodImpl(this, "POST").headers(m_headers);
		}
	}
	
	public Method delete() {
		return delete(false);
	}

	public Method delete(boolean pGearsVersion) { 
		if(pGearsVersion) {
			return new GearsMethodImpl(this, "DELETE").headers(m_headers);
		} else {
			return new MethodImpl(this, "DELETE").headers(m_headers);
		}
	}
	
	public Method head() {
		return new MethodImpl(this, "HEAD").headers(m_headers);
	}

	public Method options() {
		return new MethodImpl(this, "OPTIONS").headers(m_headers);
	}

	public String getUri() {
		return m_uri;
	}

	public String getQuery() {
		return m_query;
	}

	protected Map<String, String> defaultHeaders() {
		return null;
	}

	// TODO: support fancier resolutions
	public Resource resolve(String path) {
		return new Resource(m_uri + path);
	}

	public Resource addQueryParam(String key, String value) {
		key = URL.encodeComponent(key);
		value = URL.encodeComponent(value);
		String q = m_query == null ? "" : m_query + "&";
		return new Resource(m_uri, q + key + "=" + value);
	}
}