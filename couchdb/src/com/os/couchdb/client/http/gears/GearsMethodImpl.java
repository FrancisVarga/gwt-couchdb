package com.os.couchdb.client.http.gears;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.blob.Blob;
import com.google.gwt.gears.client.httprequest.HttpRequest;
import com.google.gwt.gears.client.httprequest.RequestCallback;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.os.couchdb.client.exception.CouchDBException;
import com.os.couchdb.client.exception.ResponseFormatException;
import com.os.couchdb.client.http.BaseModelCallback;
import com.os.couchdb.client.http.BaseModelListCallback;
import com.os.couchdb.client.http.BlobCallback;
import com.os.couchdb.client.http.HttpResponseCallback;
import com.os.couchdb.client.http.JSOModelCallback;
import com.os.couchdb.client.http.JsonCallback;
import com.os.couchdb.client.http.Method;
import com.os.couchdb.client.http.Resource;
import com.os.couchdb.client.http.StringListCallback;
import com.os.couchdb.client.http.TextCallback;
import com.os.couchdb.client.http.VoidCallback;
import com.os.couchdb.client.http.XmlCallback;
import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

/**
 * 
 */
public class GearsMethodImpl implements Method {
	protected HttpRequest m_request;
	protected String m_method;
	protected String m_uri;
	protected String m_requestData;
	protected Blob m_requestBlob;

	public GearsMethodImpl(Resource pResource, String pMethod) {
		String uri = pResource.getUri();
		if (pResource.getQuery() != null) {
			uri += "?" + pResource.getQuery();
		}
		m_method = pMethod;
		m_uri = uri;
		Factory factory = Factory.getInstance();
		m_request = factory.createHttpRequest();
		if ("PUT".equals(m_method) || "DELETE".equals(m_method) || "COPY".equals(m_method)) {
			m_request.open("POST", m_uri);
			header("X-HTTP-Method-Override", m_method);
		} else {
			m_request.open(m_method, m_uri);
		}
	}

	public GearsMethodImpl header(String pHeader, String pValue) {
		m_request.setRequestHeader(pHeader, pValue);
		return this;
	}

	public GearsMethodImpl headers(Map<String, String> pHeaders) {
		if (pHeaders != null) {
			for (Entry<String, String> entry : pHeaders.entrySet()) {
				header(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public GearsMethodImpl text(String pData) {
		defaultContentType(Resource.CONTENT_TYPE_TEXT);
		m_requestData = pData;
		return this;
	}

	public GearsMethodImpl json(JSONValue pData) {
		defaultContentType(Resource.CONTENT_TYPE_JSON);
		m_requestData = pData.toString();
		return this;
	}

	public GearsMethodImpl json(String pData) {
		defaultContentType(Resource.CONTENT_TYPE_JSON);
		m_requestData = pData;
		return this;
	}

	public GearsMethodImpl json(BaseModel pData) {
		defaultContentType(Resource.CONTENT_TYPE_JSON);
		m_requestData = pData.getModel().toJson();
		return this;
	}

	public GearsMethodImpl json(List<BaseModel> pData) {
		defaultContentType(Resource.CONTENT_TYPE_JSON);
		m_requestData = BaseModel.toJson(pData);
		return this;
	}

	public GearsMethodImpl xml(Document pData) {
		defaultContentType(Resource.CONTENT_TYPE_XML);
		m_requestData = pData.toString();
		return this;
	}

	public GearsMethodImpl form(Map<String, String> pData) {
		defaultContentType(Resource.CONTENT_TYPE_FORM);
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> e : pData.entrySet()) {
			if (!first) {
				sb.append('&');
			}
			sb.append(URL.decodeComponent(e.getKey())).append('=').append(URL.encodeComponent(e.getValue()));
			first = false;
		}
		m_requestData = sb.toString();
		return this;
	}

	public GearsMethodImpl blob(Blob pBlob, String pContentType) {
		defaultContentType(pContentType);
		m_requestBlob = pBlob;
		return this;
	}

	private void send(RequestCallback pCallback) {
		try {
			GWT.log("Sending http request: " + m_method + " " + m_uri, null);
			if (m_requestData != null && m_requestData.length() > 0) {
				m_request.send(m_requestData, pCallback);
			} else if (m_requestBlob != null && m_requestBlob.getLength() > 0) {
				m_request.send(m_requestBlob, pCallback);
			} else {
				m_request.send(pCallback);
			}
		} catch (Throwable th) {
			GWT.log("Error in send", th);
		}
	}

	public void send(final HttpRequestCallback pCallback) {
		send(new AbstractRequestCallback<HttpRequest>(this, pCallback) {
			@Override
			protected HttpRequest parseResult(HttpRequest pRequest) throws Exception {
				return pRequest;
			}
		});
	}

	public void send(final VoidCallback pCallback) {
		send(new AbstractRequestCallback<Void>(this, pCallback) {
			@Override
			protected Void parseResult(HttpRequest pRequest) throws Exception {
				return null;
			}
		});
	}

	public void send(final TextCallback pCallback) {
		defaultAcceptType(Resource.CONTENT_TYPE_TEXT);
		send(new AbstractRequestCallback<String>(this, pCallback) {
			@Override
			protected String parseResult(HttpRequest pRequest) throws Exception {
				return pRequest.getResponseText();
			}
		});
	}

	public void send(final JsonCallback pCallback) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		send(new AbstractRequestCallback<JSONValue>(this, pCallback) {
			@Override
			protected JSONValue parseResult(HttpRequest pRequest) throws Exception {
				String content = pRequest.getResponseText();
				if (content != null && content.length() > 0) {
					return JSONParser.parse(content);
				}
				return null;
			}
		});
	}

	public void send(final JSOModelCallback callback) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		send(new AbstractRequestCallback<JSOModel>(this, callback) {
			@Override
			protected JSOModel parseResult(HttpRequest pRequest) throws Exception {
				String content = pRequest.getResponseText();
				if (content != null && content.length() > 0) {
					JSONValue jsonValue = JSONParser.parse(content);
					if(jsonValue.isObject() != null) {
						return JSOModel.fromJson(jsonValue);
					} else {
						throw new ResponseFormatException("Response was NOT a valid JSON document : '" + pRequest.getResponseText() + "'", null);
					}
				} else if(pRequest.getStatus() == 200) {
					throw new ResponseFormatException("Response was NOT a valid JSON document : '" + pRequest.getResponseText() + "'", null);
				} else {
					return JSOModel.create();
				}
			}
		});
	}

	public void send(final XmlCallback callback) {
		defaultAcceptType(Resource.CONTENT_TYPE_XML);
		send(new AbstractRequestCallback<Document>(this, callback) {
			@Override
			protected Document parseResult(HttpRequest pRequest) throws Exception {
				try {
					return XMLParser.parse(pRequest.getResponseText());
				} catch (Throwable e) {
					throw new ResponseFormatException("Response was NOT a valid XML document : '" + pRequest.getResponseText() + "'", e);
				}
			}
		});
	}

	public void send(final BlobCallback callback) {
		send(new AbstractRequestCallback<Blob>(this, callback) {
			@Override
			protected Blob parseResult(HttpRequest pRequest) throws Exception {
				return m_request.getResponseBlob();
			}
		});
	}

	public <T extends BaseModel> void send(final BaseModelCallback<T> callback, final BaseModelFactory<T> pFactory) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		send(new AbstractRequestCallback<T>(this, callback) {
			@Override
			protected T parseResult(HttpRequest pRequest) throws Exception {
				JSONValue jsonValue = JSONParser.parse(pRequest.getResponseText());
				if(jsonValue.isObject() != null) {
					JSOModel jsoModel = JSOModel.fromJson(jsonValue);
					if (pFactory != null) {
						return pFactory.createInstance(jsoModel);
					} else {
						return (T)new BaseModel(jsoModel);
					}
				} else {
					throw new ResponseFormatException("Response was NOT a valid JSON document : '" + pRequest.getResponseText() + "'", null);
				}
			}
		});
	}

	public <T extends BaseModel> void send(final BaseModelListCallback<T> callback, final BaseModelFactory<T> pFactory) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		send(new AbstractRequestCallback<List<T>>(this, callback) {
			@Override
			protected List<T> parseResult(HttpRequest pRequest) throws Exception {
				JSONValue jsonValue = JSONParser.parse(pRequest.getResponseText());
				if(jsonValue.isArray() != null) {
					JsArray<JSOModel> jsoArray = jsonValue.isArray().getJavaScriptObject().cast();
					List<T> baseModelList = new ArrayList<T>(jsoArray.length());
					for (int i = 0; i < jsoArray.length(); i++) {
						if (pFactory != null) {
							baseModelList.add(pFactory.createInstance(jsoArray.get(i)));
						} else {
							baseModelList.add((T)new BaseModel(jsoArray.get(i)));
						}
					}
					return baseModelList;
				} else {
					throw new ResponseFormatException("Response was NOT a valid JSON array : '" + pRequest.getResponseText() + "'", null);
				}
			}
		});
	}

	public void send(final StringListCallback callback) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		send(new AbstractRequestCallback<List<String>>(this, callback) {
			@Override
			protected List<String> parseResult(HttpRequest pRequest) throws Exception {
				JSONValue jsonValue = JSONParser.parse(pRequest.getResponseText());
				if(jsonValue.isArray() != null) {
					JsArrayString jsoArray = jsonValue.isArray().getJavaScriptObject().cast();
					List<String> baseModelList = new ArrayList<String>(jsoArray.length());
					for (int i = 0; i < jsoArray.length(); i++) {
						baseModelList.add(jsoArray.get(i));
					}
					return baseModelList;
				} else {
					throw new ResponseFormatException("Response was NOT a valid JSON array : '" + pRequest.getResponseText() + "'", null);
				}
			}
		});
	}

	protected void defaultContentType(String type) {
		header(Resource.HEADER_CONTENT_TYPE, type);
	}

	protected void defaultAcceptType(String type) {
		header(Resource.HEADER_ACCEPT, type);
	}

	@Override
	public HttpRequest getGearsHttpRequest() {
		return m_request;
	}

	@Override
	public Request getHttpRequest() {
		throw new CouchDBException("Invalid call - getHttpRequest");
	}

	@Override
	public Response getHttpResponse() {
		throw new CouchDBException("Invalid call - getHttpResponse");
	}

	@Override
	public void send(HttpResponseCallback pCallback) {
		throw new CouchDBException("Invalid call - send(HttpResponseCallback)");
	}
}