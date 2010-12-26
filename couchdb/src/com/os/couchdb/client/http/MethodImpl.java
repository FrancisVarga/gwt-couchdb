package com.os.couchdb.client.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.gears.client.blob.Blob;
import com.google.gwt.gears.client.httprequest.HttpRequest;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.os.couchdb.client.exception.CouchDBException;
import com.os.couchdb.client.exception.ResponseFormatException;
import com.os.couchdb.client.http.gears.HttpRequestCallback;
import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class MethodImpl implements Method {
	private static class MethodRequestBuilder extends RequestBuilder {
		public MethodRequestBuilder(String method, String url) {
			super(method, url);
      setHeader("X-HTTP-Method-Override", method);
		}
	}	
	protected RequestBuilder m_requestBuilder;
	protected Request m_request;
	protected Response m_response;

	public MethodImpl(Resource pResource, String pMethod) {
		String uri = pResource.getUri();
		if( pResource.getQuery() != null ) {
		    uri += "?" + pResource.getQuery();
		}
    m_requestBuilder = new MethodRequestBuilder(pMethod, uri);
	}

	public MethodImpl header(String pHeader, String pValue) {
		m_requestBuilder.setHeader(pHeader, pValue);
		return this;
	}

	public MethodImpl headers(Map<String, String> pHeaders) {
		if (pHeaders != null) {
			for (Entry<String, String> entry : pHeaders.entrySet()) {
				header(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	public MethodImpl text(String pData) {
		defaultContentType(Resource.CONTENT_TYPE_TEXT);
		m_requestBuilder.setRequestData(pData);
		return this;
	}

	public MethodImpl json(JSONValue pData) {
		defaultContentType(Resource.CONTENT_TYPE_JSON);
		m_requestBuilder.setRequestData(pData.toString());
		return this;
	}

	public MethodImpl json(String pData) {
		defaultContentType(Resource.CONTENT_TYPE_JSON);
		m_requestBuilder.setRequestData(pData);
		return this;
	}

	public MethodImpl json(BaseModel pData) {
		defaultContentType(Resource.CONTENT_TYPE_JSON);
		m_requestBuilder.setRequestData(pData.getModel().toJson());
		return this;
	}

	public MethodImpl json(List<BaseModel> pData) {
		defaultContentType(Resource.CONTENT_TYPE_JSON);
		m_requestBuilder.setRequestData(BaseModel.toJson(pData));
		return this;
	}

	public MethodImpl xml(Document pData) {
		defaultContentType(Resource.CONTENT_TYPE_XML);
		m_requestBuilder.setRequestData(pData.toString());
		return this;
	}

	public MethodImpl form(Map<String, String> pData) {
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
		m_requestBuilder.setRequestData(sb.toString());
		return this;
	}

	private void send(final RequestCallback pCallback) throws RequestException {
		m_requestBuilder.setCallback(new RequestCallback() {
			@Override
			public void onResponseReceived(Request pRequest, Response pResponse) {
				pCallback.onResponseReceived(pRequest, pResponse);
			}
			
			@Override
			public void onError(Request pRequest, Throwable pException) {
				pCallback.onError(pRequest, pException);
			}
		});
    GWT.log("Sending http request: " + m_requestBuilder.getHTTPMethod()+" "+m_requestBuilder.getUrl(), null);
    String content = m_requestBuilder.getRequestData();
    if( content!=null && content.length()>0) {
        GWT.log(content, null);
    }
    m_request = m_requestBuilder.send();
	}

	public void send(final HttpRequestCallback pCallback) {
		throw new CouchDBException("Invalid request : HttpRequestCallback");
	}
	
	public void send(final HttpResponseCallback pCallback) {
		try {
			send(new AbstractRequestCallback<Response>(this, pCallback) {
				protected Response parseResult(Response pResponse) throws Exception {
					return pResponse;
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public void send(final VoidCallback pCallback) {
		try {
			send(new AbstractRequestCallback<Void>(this, pCallback) {
				@Override
				protected Void parseResult(Response pResponse) throws Exception {
					return null;
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public void send(final TextCallback pCallback) {
		defaultAcceptType(Resource.CONTENT_TYPE_TEXT);
		try {
			send(new AbstractRequestCallback<String>(this, pCallback) {
				@Override
				protected String parseResult(Response pResponse) throws Exception {
					return pResponse.getText();
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public void send(final JsonCallback pCallback) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		try {
			send(new AbstractRequestCallback<JSONValue>(this, pCallback) {
				@Override
				protected JSONValue parseResult(Response pResponse) throws Exception {
					String content = pResponse.getText();
					if (content != null && content.length() > 0) {
						return JSONParser.parse(content);
					}
					return null;
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public void send(final JSOModelCallback pCallback) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		try {
			send(new AbstractRequestCallback<JSOModel>(this, pCallback) {
				@Override
				protected JSOModel parseResult(Response pResponse) throws Exception {
					JSONValue jsonValue = JSONParser.parse(pResponse.getText());
					if(jsonValue.isObject() != null) {
						return JSOModel.fromJson(jsonValue);
					} else {
						throw new ResponseFormatException("Response was NOT a valid JSON document : '" + pResponse.getText() + "'", null);
					}
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public void send(final XmlCallback pCallback) {
		defaultAcceptType(Resource.CONTENT_TYPE_XML);
		try {
			send(new AbstractRequestCallback<Document>(this, pCallback) {
				@Override
				protected Document parseResult(Response pResponse) throws Exception {
					try {
						return XMLParser.parse(pResponse.getText());
					} catch (Throwable e) {
						throw new ResponseFormatException("Response was NOT a valid XML document : '" + pResponse.getText() + "'", e);
					}
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public void send(final BlobCallback callback) {
		throw new CouchDBException("Invalid call - send(BlobCallback)");
	}

	public <T extends BaseModel> void send(final BaseModelCallback<T> pCallback, final BaseModelFactory<T> pFactory) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		try {
			send(new AbstractRequestCallback<T>(this, pCallback) {
				@Override
				protected T parseResult(Response pResponse) throws Exception {
					JSONValue jsonValue = JSONParser.parse(pResponse.getText());
					if(jsonValue.isObject() != null) {
						JSOModel jsoModel = JSOModel.fromJson(jsonValue);
						if (pFactory != null) {
							return pFactory.createInstance(jsoModel);
						} else {
							return (T)new BaseModel(jsoModel);
						}
					} else {
						throw new ResponseFormatException("Response was NOT a valid JSON document : '" + pResponse.getText() + "'", null);
					}
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public <T extends BaseModel> void send(final BaseModelListCallback<T> pCallback, final BaseModelFactory<T> pFactory) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		try {
			send(new AbstractRequestCallback<List<T>>(this, pCallback) {
				@Override
				protected List<T> parseResult(Response pResponse) throws Exception {
					JSONValue jsonValue = JSONParser.parse(pResponse.getText());
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
						throw new ResponseFormatException("Response was NOT a valid JSON array : '" + pResponse.getText() + "'", null);
					}
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public void send(final StringListCallback pCallback) {
		defaultAcceptType(Resource.CONTENT_TYPE_JSON);
		try {
			send(new AbstractRequestCallback<List<String>>(this, pCallback) {
				@Override
				protected List<String> parseResult(Response pResponse) throws Exception {
					JSONValue jsonValue = JSONParser.parse(pResponse.getText());
					if(jsonValue.isArray() != null) {
						JsArrayString jsoArray = jsonValue.isArray().getJavaScriptObject().cast();
						List<String> baseModelList = new ArrayList<String>(jsoArray.length());
						for (int i = 0; i < jsoArray.length(); i++) {
							baseModelList.add(jsoArray.get(i));
						}
						return baseModelList;
					} else {
						throw new ResponseFormatException("Response was NOT a valid JSON array : '" + pResponse.getText() + "'", null);
					}
				}

				@Override
				public void onError(Request pRequest, Throwable pException) {
					pCallback.onFailure(m_method, pException);
				}
			});
		} catch(Throwable th) {
			pCallback.onFailure(this,new CouchDBException(th));
		}
	}

	public HttpRequest getGearsHttpRequest() {
		throw new CouchDBException("Invalid call - getGearsHttpRequest");
	}

	public Request getHttpRequest() {
		return m_request;
	}
	
	public Response getHttpResponse() {
		return m_response;
	}

	protected void defaultContentType(String type) {
		header(Resource.HEADER_CONTENT_TYPE, type);
	}

	protected void defaultAcceptType(String type) {
		header(Resource.HEADER_ACCEPT, type);
	}

	@Override
	public Method blob(Blob pBlob, String pContentType) {
		throw new CouchDBException("Invalid call - blob");
	}
}
