package com.os.couchdb.client.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.gears.client.blob.Blob;
import com.google.gwt.gears.client.httprequest.HttpRequest;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.http.client.Response;
import com.os.couchdb.client.document.ReplicationInfo;
import com.os.couchdb.client.document.StatsInfo;
import com.os.couchdb.client.document.UUIDResponse;
import com.os.couchdb.client.document.UserInfo;
import com.os.couchdb.client.exception.HttpStatusExceptionFactory;
import com.os.couchdb.client.http.BaseModelCallback;
import com.os.couchdb.client.http.BlobCallback;
import com.os.couchdb.client.http.HttpResponseCallback;
import com.os.couchdb.client.http.JSOModelCallback;
import com.os.couchdb.client.http.JsonCallback;
import com.os.couchdb.client.http.Method;
import com.os.couchdb.client.http.TextCallback;
import com.os.couchdb.client.http.Resource;
import com.os.couchdb.client.http.StringListCallback;
import com.os.couchdb.client.http.VoidCallback;
import com.os.couchdb.client.http.gears.HttpRequestCallback;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class CouchDBServerImpl implements CouchDBServer {
	private Resource m_resource;
	
	public CouchDBServerImpl() {
		this("localhost",DEFAULT_PORT);
	}
	
	public CouchDBServerImpl(String pHostName,int pPort) {
		m_resource = new Resource("http://" + pHostName + ":" + pPort);
	}

	public CouchDBServerImpl(String uri) {
		m_resource = new Resource(uri);
	}
	
	@Override
	public void createDatabase(String pName, final AsyncCallback<Boolean> pCallback) {
		Resource dbRes = m_resource.resolve("/" + pName + "/");
		Method putMethod = dbRes.put();
		putMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				if(pResponse.getBoolean("ok")) {
					pCallback.onSuccess(Boolean.TRUE);
				} else {
					pCallback.onSuccess(Boolean.FALSE);
				}
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_CREATED;
			}
		});
	}

	@Override
	public void deleteDatabase(String pName,final AsyncCallback<Boolean> pCallback) {
		Resource dbRes = m_resource.resolve("/" + pName + "/");
		Method deleteMethod = dbRes.delete();
		deleteMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				if(pResponse.getBoolean("ok")) {
					pCallback.onSuccess(Boolean.TRUE);
				} else {
					pCallback.onSuccess(Boolean.FALSE);
				}
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}
	
	@Override
	public void listDatabases(final AsyncCallback<List<String>> pCallback) {
		Resource dbRes = m_resource.resolve("/_all_dbs?cookie=" + System.currentTimeMillis());
		Method getMethod = dbRes.get();
		getMethod.send(new StringListCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, List<String> pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}

	@Override
	public void getUUIDs(int pCount,final AsyncCallback<List<String>> pCallback) {
		if(pCount <= 0) {
			pCount = 1;
		}
		Resource dbRes = m_resource.resolve("/_uuids?count=" + pCount);
    Method getMethod = dbRes.get();
    getMethod.send(new BaseModelCallback<UUIDResponse>() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, UUIDResponse pResponse) {
				pCallback.onSuccess(pResponse.getUUIDList());
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
    }, new BaseModelFactory<UUIDResponse> () {
			@Override
			public UUIDResponse createInstance(JSOModel pModel) {
				return new UUIDResponse(pModel);
			}
    });
	}

	@Override
	public void replicate(String pSource,String pTarget,final AsyncCallback<ReplicationInfo> pCallback) {
		ReplicationOptions ro = new ReplicationOptions();
		ro.setSource(pSource);
		ro.setTarget(pTarget);
		replicate(ro, pCallback);
	}
	
	@Override
	public void replicate(ReplicationOptions pOptions,final AsyncCallback<ReplicationInfo> pCallback) {
		Resource dbRes = m_resource.resolve("/_replicate");
    Method postMethod = dbRes.post().json(pOptions);
    postMethod.send(new BaseModelCallback<ReplicationInfo>() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, ReplicationInfo pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_ACCEPTED || pCode == HttpStatusExceptionFactory.SC_CONTINUE;
			}
    }, new BaseModelFactory<ReplicationInfo> () {
			@Override
			public ReplicationInfo createInstance(JSOModel pModel) {
				return new ReplicationInfo(pModel);
			}
    });
	}

  public void authenticate(String pUserName,String pPasswd,final AsyncCallback<Boolean> pCallback) {
  	Map<String, String> credentials = new HashMap<String, String>();
  	credentials.put("name", pUserName);
  	credentials.put("password", pPasswd);
		Resource dbRes = m_resource.resolve("/_session");
		Method postMethod = dbRes.post().form(credentials);
		postMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(pResponse.getBoolean("ok"));
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
  }
  
  public void isAuthenticated(final AsyncCallback<UserInfo> pCallback) {
		Resource dbRes = m_resource.resolve("/_session");
		Method putMethod = dbRes.get();
		putMethod.send(new BaseModelCallback<UserInfo>() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, UserInfo pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		},new BaseModelFactory<UserInfo>() {
			@Override
			public UserInfo createInstance(JSOModel pModel) {
				return new UserInfo(pModel);
			}
		});
  }

	@Override
	public void get(String pUri, final CouchDBAsyncCallbackJSOModel pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method getMethod = dbRes.get();
		getMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}

	@Override
	public void get(String pUri, final CouchDBAsyncCallbackJSON pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method getMethod = dbRes.get();
		getMethod.send(new JsonCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSONValue pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}
	
	@Override
	public void get(String pUri, final CouchDBAsyncCallbackString pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method getMethod = dbRes.get();
		getMethod.send(new TextCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, String pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}

	@Override
	public void get(String pUri, final CouchDBAsyncCallbackBlob pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method getMethod = dbRes.get(true);
		getMethod.send(new BlobCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, Blob pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}

	@Override
	public void get(String pUri, final CouchDBAsyncCallbackHttpRequest pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method getMethod = dbRes.get(true);
		getMethod.send(new HttpRequestCallback() {
			@Override
			public void onSuccess(Method pMethod, HttpRequest pResponse) {
				pCallback.onSuccess(pResponse);
			}
			
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}

	@Override
	public void get(String pUri, final CouchDBAsyncCallbackHttpResponse pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method getMethod = dbRes.get();
		getMethod.send(new HttpResponseCallback() {
			@Override
			public void onSuccess(Method pMethod, Response pResponse) {
				pCallback.onSuccess(pResponse);
			}
			
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}
	
	@Override
	public void delete(String pUri, final CouchDBAsyncCallbackJSOModel pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method deleteMethod = dbRes.delete();
		deleteMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}

	@Override
	public void getStats(String pFilter,final AsyncCallback<StatsInfo> pCallback) {
		pFilter = pFilter == null ? "" : pFilter;
		Resource dbRes = m_resource.resolve("/_stats" + pFilter);
		Method getMethod = dbRes.get();
		getMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(new StatsInfo(pResponse));
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}

	@Override
	public void getConfig(final AsyncCallback<JSOModel> pCallback) {
		Resource dbRes = m_resource.resolve("/_config");
		Method getMethod = dbRes.get();
		getMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}
	
	@Override
	public void post(String pUri, String pBody,final CouchDBAsyncCallbackJSOModel pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method postMethod = dbRes.post();
		if(pBody != null && pBody.length() > 0) {
			postMethod.json(pBody);
		}
		postMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}

	@Override
	public void post(String pUri, String pBody,final CouchDBAsyncCallbackVoid pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method postMethod = dbRes.post();
		if(pBody != null && pBody.length() > 0) {
			postMethod.json(pBody);
		}
		postMethod.send(new VoidCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, Void pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}
	
	@Override
	public void put(String pUri,final CouchDBAsyncCallbackJSOModel pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method putMethod = dbRes.put();
		putMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}

	@Override
	public void put(String pUri, String pBody,final CouchDBAsyncCallbackJSOModel pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method putMethod = dbRes.put();
		if(pBody != null && pBody.length() > 0) {
			putMethod.json(pBody);
		}
		putMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}
	
	@Override
	public void put(String pUri, Blob pBody,String contentType, final CouchDBAsyncCallbackJSOModel pCallback) {
		Resource dbRes = m_resource.resolve(pUri);
		Method putMethod = dbRes.put(true);
		putMethod.blob(pBody,contentType);
		putMethod.send(new JSOModelCallback() {
			@Override
			public void onFailure(Method pMethod, Throwable pException) {
				pCallback.onFailure(pException);
			}

			@Override
			public void onSuccess(Method pMethod, JSOModel pResponse) {
				pCallback.onSuccess(pResponse);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCallback.isHttpStatusCodeOk(pCode);
			}
		});
	}
	
	public String resolveURL(String pPath) {
		return m_resource.resolve(pPath).getUri() + (m_resource.getQuery() != null ? "?" + m_resource.getQuery() : "");
	}
}