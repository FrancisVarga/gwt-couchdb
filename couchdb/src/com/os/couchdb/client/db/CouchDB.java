package com.os.couchdb.client.db;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.gears.client.blob.Blob;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.os.couchdb.client.document.AbstractViewResult;
import com.os.couchdb.client.document.BaseDocument;
import com.os.couchdb.client.document.ChangesResult;
import com.os.couchdb.client.document.CouchDBStatus;
import com.os.couchdb.client.document.DesignDocument;
import com.os.couchdb.client.document.DocumentInfo;
import com.os.couchdb.client.document.LuceneStatusInfo;
import com.os.couchdb.client.document.LuceneViewResult;
import com.os.couchdb.client.document.RevisionSet;
import com.os.couchdb.client.document.ViewAndDocumentsResult;
import com.os.couchdb.client.document.ViewResult;
import com.os.couchdb.client.exception.DatabaseEventException;
import com.os.couchdb.client.exception.HttpStatusExceptionFactory;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class CouchDB {
	private static final String DESIGN_DOCUMENT_PREFIX = "_design/";
	private static final String VIEW_DOCUMENT_INFIX = "view";
	private static final String LIST_DOCUMENT_INFIX = "list";
	private static final String SHOW_DOCUMENT_INFIX = "show";

	/**
	 * Name of the all docs view.
	 */
	private static final String ALL_DOCS = "_all_docs";

	private String m_name;
	private CouchDBServer m_server;
	private List<CouchDBEventHandler> m_eventHandlers = new ArrayList<CouchDBEventHandler>();

	public CouchDB() {
		String moduleURL = GWT.getModuleBaseURL();
		int idx = moduleURL.indexOf("/_design/" + GWT.getModuleName());
		if(idx > 0) {
			String baseUri = moduleURL.substring(0,idx);
			int idx1 = baseUri.lastIndexOf('/');
			String dbName = baseUri.substring(idx1+1,idx);
			baseUri = baseUri.substring(0,idx1);
			m_server = new CouchDBServerImpl(baseUri);
			m_name = dbName;
		} else {
			throw new IllegalArgumentException("Error parsing " + moduleURL);
		}
	}
	
	public CouchDB(CouchDB pOther,String pDBName) {
		this(pOther.getServer(),pDBName);
	}
	
	public CouchDB(String pHost, String pName) {
		this(new CouchDBServerImpl(pHost), pName);
	}

	/**
	 * Create a database object for the given host, port and database name.
	 * 
	 * @param pHost
	 * @param pPort
	 * @param pName
	 */
	public CouchDB(String pHost, int pPort, String pName) {
		this(new CouchDBServerImpl(pHost, pPort), pName);
	}

	/**
	 * Creates a database object for the given Server object and the given
	 * database name.
	 * 
	 * @param pServer
	 * @param pName
	 */
	public CouchDB(CouchDBServer pServer, String pName) {
		this.m_server = pServer;
		this.m_name = pName.toLowerCase();
	}

	public String getName() {
		return m_name;
	}

	public CouchDBServer getServer() {
		return m_server;
	}

	public List<CouchDBEventHandler> getEventHandlers() {
		return m_eventHandlers;
	}

	public void setEventHandlers(List<CouchDBEventHandler> pEventHandlers) {
		this.m_eventHandlers = pEventHandlers;
	}

	public void addEventHandler(CouchDBEventHandler pHandler) {
		m_eventHandlers.add(pHandler);
	}

	public void getStatus(final AsyncCallback<CouchDBStatus> pCallback) {
		m_server.get("/" + m_name + "/", new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onSuccess(JSOModel pResult) {
				pCallback.onSuccess(new CouchDBStatus(pResult));
			}

			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}

	public void compact(final AsyncCallback<Boolean> pCallback) {
		m_server.post("/" + m_name + "/_compact", null, new CouchDBAsyncCallbackVoid() {
			@Override
			public void onSuccess(Void pResult) {
				pCallback.onSuccess(Boolean.TRUE);
			}

			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_ACCEPTED;
			}
		});
	}

	public void getDesignDocument(String pDocId, AsyncCallback<DesignDocument> pCallback) {
		getDocument(new BaseModelFactory<DesignDocument>() {
			@Override
			public DesignDocument createInstance(JSOModel pModel) {
				return new DesignDocument(pModel);
			}
		}, DesignDocument.extendId(pDocId), pCallback);
	}

	public <D extends BaseDocument> void getDocument(BaseModelFactory<D> pFactory, String pDocId,
			AsyncCallback<D> pCallback) {
		getDocument(pFactory, pDocId, null, pCallback);
	}

	protected <D extends BaseDocument> void loadDocument(final BaseModelFactory<D> pFactory, String pUri,
			final AsyncCallback<D> pCallback) {
		m_server.get(pUri, new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				pCallback.onSuccess(pFactory.createInstance(pResult));
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}

	public <D extends BaseDocument> void getDocument(final BaseModelFactory<D> pFactory, String docId, String revision,
			final AsyncCallback<D> pCallback) {
		if (!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		String uri = "/" + m_name + "/" + docId;
		if (revision != null) {
			uri += "?rev=" + revision;
		}
		loadDocument(pFactory, uri, pCallback);
	}

	public <D extends BaseDocument> void getDocumentWithRevsList(final BaseModelFactory<D> pFactory, String docId,
			final AsyncCallback<D> pCallback) {
		if (!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		String uri = "/" + m_name + "/" + docId + "?revs=true";
		loadDocument(pFactory, uri, pCallback);
	}

	public <D extends BaseDocument> void getDocumentWithRevsInfo(final BaseModelFactory<D> pFactory, String docId,
			final AsyncCallback<D> pCallback) {
		if (!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		String uri = "/" + m_name + "/" + docId + "?revs_info=true";
		loadDocument(pFactory, uri, pCallback);
	}

	public <D extends BaseDocument> void getDocumentWithConflictsInfo(final BaseModelFactory<D> pFactory, String docId,
			final AsyncCallback<D> pCallback) {
		if (!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		String uri = "/" + m_name + "/" + docId + "?conflicts=true";
		loadDocument(pFactory, uri, pCallback);
	}

	public <D extends BaseDocument> void getDocumentRevisions(final BaseModelFactory<D> pFactory, String docId,
			final AsyncCallback<RevisionSet<D>> pCallback) {
		if (!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		String uri = "/" + m_name + "/" + docId + "?open_revs=all";
		m_server.get(uri,new CouchDBAsyncCallbackString() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(String pResult) {
				pCallback.onSuccess(new RevisionSet<D>(pResult, pFactory));
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}

	protected <D extends BaseDocument> void createOrUpdateDocument(final D pDoc, final AsyncCallback<D> pCallback) {
		String id = pDoc.getId();
		final boolean isCreate = (id == null);
		GWT.log("Create doc id=" + id, null);
		for (CouchDBEventHandler eventHandler : m_eventHandlers) {
			try {
				if (isCreate) {
					eventHandler.creatingDocument(this, pDoc);
				} else {
					eventHandler.updatingDocument(this, pDoc);
				}
			} catch (Exception e) {
				pCallback.onFailure(new DatabaseEventException(e));
				return;
			}
		}
		final String json = pDoc.getModel().toJson();
		CouchDBAsyncCallbackJSOModel callback = new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				GWT.log("createOrUpdateDoc success:" + pResult.toJson(), null);
				DocumentInfo di = new DocumentInfo(pResult);
				for (CouchDBEventHandler eventHandler : m_eventHandlers) {
					if (isCreate) {
						eventHandler.createdDocument(CouchDB.this, pDoc, di);
					} else {
						eventHandler.updatedDocument(CouchDB.this, pDoc, di);
					}
				}
				if (isCreate) {
					pDoc.setId(di.getId());
				}
				pDoc.setRevision(pDoc.getRevision());
				pCallback.onSuccess(pDoc);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_CREATED;
			}
		};
		if (isCreate) {
			m_server.post("/" + m_name + "/",json, callback);
		} else {
			if (!id.startsWith("_design/")) {
				id = URL.encodeComponent(id);
			}
			m_server.put("/" + m_name + "/" + id,json, callback);
		}
	}

	public <D extends BaseDocument> void createDocument(D pDoc, AsyncCallback<D> pCallback) {
		if (pDoc.getRevision() != null) {
			throw new IllegalStateException("Newly created docs can't have a revision ( is = " + pDoc.getRevision() + " )");
		}
		createOrUpdateDocument(pDoc, pCallback);
	}

	public <D extends BaseDocument> void updateDocument(D pDoc, AsyncCallback<D> pCallback) {
		if (pDoc.getId() == null) {
			throw new IllegalStateException("id must be set for updates");
		}
		if (pDoc.getRevision() == null) {
			throw new IllegalStateException("revision must be set for updates");
		}
		createOrUpdateDocument(pDoc, pCallback);
	}

	/**
	 * @param pDocuments
	 * @return
	 */
	public <D extends BaseDocument> void bulkCreateDocuments(List<D> pDocuments,
			AsyncCallback<List<DocumentInfo>> pCallback) {
		this.bulkCreateDocuments(pDocuments, false, pCallback);
	}

	/**
	 * Bulk creates the given list of documents.
	 * 
	 * @param pDocumentList
	 * @return
	 */
	public <D extends BaseDocument> void bulkCreateDocuments(final List<D> pDocumentList, boolean pAllOrNothing,
			final AsyncCallback<List<DocumentInfo>> pCallback) {
		for (D doc : pDocumentList) {
			boolean isCreate = doc.getId() == null;
			for (CouchDBEventHandler eventHandler : m_eventHandlers) {
				try {
					if (isCreate) {
						eventHandler.creatingDocument(this, doc);
					} else {
						eventHandler.updatingDocument(this, doc);
					}
				} catch (Exception e) {
					pCallback.onFailure(new DatabaseEventException(e));
					return;
				}
			}
		}
		JSOModel model = JSOModel.create();
		if (pAllOrNothing) {
			model.set("all_or_nothing", true);
		}
		JsArray<JSOModel> docs = JavaScriptObject.createArray().cast();
		for (int i = 0; i < pDocumentList.size(); i++) {
			docs.set(i, pDocumentList.get(i).getModel());
		}
		model.set("docs", docs);
		String json = model.toJson();
		final CouchDBAsyncCallbackJSOModel callback = new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				JsArray<JSOModel> jsArray = pResult.cast();
				List<DocumentInfo> result = new ArrayList<DocumentInfo>(jsArray.length());
				for (int i = 0; i < jsArray.length(); i++) {
					result.add(new DocumentInfo(jsArray.get(i)));
				}
				for (int i = 0; i < result.size(); i++) {
					D cdoc = pDocumentList.get(i);
					DocumentInfo cdi = result.get(i);
					if (!cdi.hasError()) {
						boolean isCreate = cdoc.getId() == null;
						for (CouchDBEventHandler eventHandler : m_eventHandlers) {
							if (isCreate) {
								eventHandler.createdDocument(CouchDB.this, cdoc, cdi);
							} else {
								eventHandler.updatedDocument(CouchDB.this, cdoc, cdi);
							}
						}
					}
				}
				pCallback.onSuccess(result);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_CREATED || pCode == HttpStatusExceptionFactory.SC_ACCEPTED;
			}
		};
		m_server.post("/" + m_name + "/_bulk_docs", json, callback);
	}

	public void delete(String docId, String revision, final AsyncCallback<String> pCallback) {
		for (CouchDBEventHandler eventHandler : m_eventHandlers) {
			try {
				eventHandler.deletingDocument(this, docId, revision);
			} catch (Exception e) {
				pCallback.onFailure(new DatabaseEventException(e));
			}
		}
		if (!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		m_server.delete("/" + m_name + "/" + docId + "?rev=" + revision,
				new CouchDBAsyncCallbackJSOModel() {
					@Override
					public void onFailure(Throwable pCaught) {
						pCallback.onFailure(pCaught);
					}

					@Override
					public void onSuccess(JSOModel pResult) {
						if (pResult.hasKey("rev")) {
							pCallback.onSuccess(pResult.get("rev"));
						} else {
							pCallback.onSuccess("???????");
						}
					}

					@Override
					public boolean isHttpStatusCodeOk(int pCode) {
						return pCode == HttpStatusExceptionFactory.SC_OK;
					}
				});
	}

	public <D extends BaseDocument> void bulkDeleteDocuments(List<D> documents,
			AsyncCallback<List<DocumentInfo>> pCallback) {
		this.bulkDeleteDocuments(documents, false, pCallback);
	}

	public <D extends BaseDocument> void bulkDeleteDocuments(final List<D> documents, boolean allOrNothing,
			final AsyncCallback<List<DocumentInfo>> pCallback) {
		JsArray<JSOModel> docsToDelete = JavaScriptObject.createArray().cast();
		for (int i = 0; i < documents.size(); i++) {
			BaseDocument doc = documents.get(i);
			JSOModel proxy = JSOModel.create();
			proxy.set("_id", doc.getId());
			proxy.set("_rev", doc.getRevision());
			proxy.set("_deleted", true);
			for (CouchDBEventHandler eventHandler : m_eventHandlers) {
				try {
					eventHandler.deletingDocument(this, doc.getId(), doc.getRevision());
				} catch (Exception e) {
					pCallback.onFailure(new DatabaseEventException(e));
					return;
				}
			}
			docsToDelete.set(i, proxy);
		}
		JSOModel model = JSOModel.create();
		if (allOrNothing) {
			model.set("all_or_nothing", true);
		}
		model.set("docs", docsToDelete);
		String json = model.toJson();
		final CouchDBAsyncCallbackJSOModel callback = new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				JsArray<JSOModel> jsArray = pResult.cast();
				List<DocumentInfo> result = new ArrayList<DocumentInfo>(jsArray.length());
				for (int i = 0; i < jsArray.length(); i++) {
					result.add(new DocumentInfo(jsArray.get(i)));
				}
				for (int i = 0; i < result.size(); i++) {
					D cdoc = documents.get(i);
					DocumentInfo cdi = result.get(i);
					if (!cdi.hasError()) {
						for (CouchDBEventHandler eventHandler : m_eventHandlers) {
							eventHandler.deletedDocument(CouchDB.this, cdoc.getId(), cdoc.getRevision(), null);
						}
					}
				}
				pCallback.onSuccess(result);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_ACCEPTED || pCode == HttpStatusExceptionFactory.SC_CREATED;
			}
		};
		m_server.post("/" + m_name + "/_bulk_docs", json, callback);
	}

	private String viewURIFromName(String viewName) {
		return getDesignURIFromNameAndInfix(viewName, VIEW_DOCUMENT_INFIX);
	}

	private String getDesignURIFromNameAndInfix(String viewName, String infix) {
		int slashPos = viewName.indexOf("/");
		if (slashPos < 0) {
			throw new IllegalArgumentException("viewName must contain a slash separating the design doc name from the "
					+ infix + " name");
		}
		return DESIGN_DOCUMENT_PREFIX + (viewName.substring(0, slashPos)) + "/_" + infix + "/"
				+ (viewName.substring(slashPos + 1));
	}

	/**
	 * Internal view query method.
	 * 
	 * @param <V>
	 *          type to parse the response into
	 * @param viewName
	 *          view name
	 * @param valueClass
	 *          runtime value type
	 * @param documentClass
	 *          runtime document type
	 * @param options
	 *          query options
	 * @param parser
	 *          parser to parse the response with
	 * @param keys
	 *          keys to query, if this is not <code>null</code>, a POST request
	 *          with the keys as JSON will be done.
	 * @return
	 */
	private <D extends BaseDocument> void queryViewInternal(String viewName, final BaseModelFactory<D> pDocumentFactory,
			FetchOptions options, KeySet keys, final AsyncCallback<AbstractViewResult> pCallback) {
		if (viewName == null) {
			throw new IllegalArgumentException("view name cannot be null");
		}
		String uri = "/" + this.m_name + "/" + viewName;
		final boolean isDocumentQuery = pDocumentFactory != null;
		if (isDocumentQuery) {
			if (options == null) {
				options = new FetchOptions();
			}
			options.includeDocs(true);
		}
		if (options != null) {
			uri += options.toQuery();
		}
		if (keys == null) {
			m_server.get(uri, new CouchDBAsyncCallbackJSOModel() {
				@Override
				public void onFailure(Throwable pCaught) {
					pCallback.onFailure(pCaught);
				}

				@Override
				public void onSuccess(JSOModel pResult) {
					if (isDocumentQuery) {
						ViewAndDocumentsResult<D> result = new ViewAndDocumentsResult<D>(pResult, pDocumentFactory);
						pCallback.onSuccess(result);
					} else {
						ViewResult result = new ViewResult(pResult);
						pCallback.onSuccess(result);
					}
				}

				@Override
				public boolean isHttpStatusCodeOk(int pCode) {
					return pCode == HttpStatusExceptionFactory.SC_OK;
				}
			});
		} else {
			m_server.post(uri, keys.getModel().toJson(), new CouchDBAsyncCallbackJSOModel() {
				@Override
				public void onFailure(Throwable pCaught) {
					pCallback.onFailure(pCaught);
				}

				@Override
				public void onSuccess(JSOModel pResult) {
					if (isDocumentQuery) {
						ViewAndDocumentsResult<D> result = new ViewAndDocumentsResult<D>(pResult, pDocumentFactory);
						pCallback.onSuccess(result);
					} else {
						ViewResult result = new ViewResult(pResult);
						pCallback.onSuccess(result);
					}
				}

				@Override
				public boolean isHttpStatusCodeOk(int pCode) {
					return pCode == HttpStatusExceptionFactory.SC_OK;
				}
			});
		}
	}

	public void listDocuments(FetchOptions options, AsyncCallback<AbstractViewResult> pCallback) {
		queryViewInternal(ALL_DOCS, null, options, null, pCallback);
	}

	public void listDocuments(FetchOptions options, List<String> pKeys, AsyncCallback<AbstractViewResult> pCallback) {
		queryViewInternal(ALL_DOCS, null, options, new KeySet(pKeys), pCallback);
	}

	public <D extends BaseDocument> void listDocuments(FetchOptions options, BaseModelFactory<D> pDocumentFactory,
			AsyncCallback<AbstractViewResult> pCallback) {
		queryViewInternal(ALL_DOCS, pDocumentFactory, options, null, pCallback);
	}

	public <D extends BaseDocument> void listDocuments(FetchOptions options, BaseModelFactory<D> pDocumentFactory,
			List<String> pKeys, AsyncCallback<AbstractViewResult> pCallback) {
		queryViewInternal(ALL_DOCS, pDocumentFactory, options, new KeySet(pKeys), pCallback);
	}

	public void queryView(String viewName, FetchOptions options, AsyncCallback<AbstractViewResult> pCallback) {
		queryViewInternal(viewURIFromName(viewName), null, options, null, pCallback);
	}

	public void queryView(String viewName, FetchOptions options, List<String> pKeys,
			AsyncCallback<AbstractViewResult> pCallback) {
		queryViewInternal(viewURIFromName(viewName), null, options, new KeySet(pKeys), pCallback);
	}

	public <D extends BaseDocument> void queryView(String viewName, FetchOptions options,
			BaseModelFactory<D> pDocFactory, AsyncCallback<AbstractViewResult> pCallback) {
		queryViewInternal(viewURIFromName(viewName), pDocFactory, options, null, pCallback);
	}

	public <D extends BaseDocument> void queryView(String viewName, FetchOptions options,
			BaseModelFactory<D> pDocFactory, List<String> pKeys, AsyncCallback<AbstractViewResult> pCallback) {
		queryViewInternal(viewURIFromName(viewName), pDocFactory, options, new KeySet(pKeys), pCallback);
	}

	public void queryChanges(ChangesFetchOptions options, final AsyncCallback<ChangesResult> pCallback) {
		String uri = "/" + this.m_name + "/_changes";
		if (options != null) {
			uri += options.toQuery();
		}
		m_server.get(uri, new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				ChangesResult result = new ChangesResult(pResult);
				pCallback.onSuccess(result);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}

	private String attachmentURI(String docId, String revision, String attachmentId) {
		if(!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		String uri = "/" + m_name + "/" + docId + "/" + attachmentId;
		if (revision != null) {
			uri += "?rev=" + revision;
		}
		return uri;
	}

	public void createOrUpdateAttachment(String docId, String revision, String attachmentId, String contentType,
			Blob pBlob, final AsyncCallback<String> pCallback) {
		m_server.put(attachmentURI(docId, revision, attachmentId), pBlob, contentType, new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				pCallback.onSuccess(pResult.get("rev"));
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_ACCEPTED || pCode == HttpStatusExceptionFactory.SC_CREATED;
			}
		});
	}

	public void deleteAttachment(String docId, String revision, String attachmentId, final AsyncCallback<String> pCallback) {
		m_server.delete(attachmentURI(docId, revision, attachmentId), new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				pCallback.onSuccess(pResult.get("rev"));
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_ACCEPTED || pCode == HttpStatusExceptionFactory.SC_CREATED;
			}
		});
	}

	public void getAttachment(String docId, final String attachmentId, final AsyncCallback<Blob> pCallback) {
		if(!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		m_server.get("/" + m_name + "/" + docId + "/" + attachmentId, new CouchDBAsyncCallbackBlob() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(Blob pResult) {
				pCallback.onSuccess(pResult);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_ACCEPTED || pCode == HttpStatusExceptionFactory.SC_CREATED;
			}
		});
	}

	public String getAttachmentURL(String docId, final String attachmentId) {
		if(!docId.startsWith("_design/")) {
			docId = URL.encodeComponent(docId);
		}
		return m_server.resolveURL("/" + m_name + "/" + docId + "/" + attachmentId);
	}

	public void queryShow(String showName, String docId, FetchOptions options, final CouchDBAsyncCallbackHttpRequest pCallback) {
		String uri = "/" + m_name + "/" + getDesignURIFromNameAndInfix(showName, SHOW_DOCUMENT_INFIX) + "/"
				+ URL.encodeComponent(docId);
		if (options != null) {
			uri += options.toQuery();
		}
		m_server.get(uri, pCallback);
	}

	/**
	 * Queries the specified list function with the specified view.
	 * 
	 * @param listName
	 *          Name of list including design doc (e.g. "designDocId/viewName")
	 * @param viewName
	 *          view name without design document
	 * @param options
	 * @return
	 */
	public void queryList(String listName, String viewName, FetchOptions options,
			final CouchDBAsyncCallbackHttpRequest pCallback) {
		String uri = "/" + m_name + "/" + getDesignURIFromNameAndInfix(listName, LIST_DOCUMENT_INFIX) + "/"
				+ URL.encodeComponent(viewName);
		if (options != null) {
			uri += options.toQuery();
		}
		m_server.get(uri, pCallback);
	}
	
	private String getLuceneURI(String pIndexName) {
		return "/" + m_name + "/_fti/" + pIndexName;
	}
	
	public void queryLucene(LuceneFetchOptions pOptions,String pIndexName, final AsyncCallback<LuceneViewResult> pCallback) {
		String uri = getLuceneURI(pIndexName) + pOptions.toQuery();
		m_server.get(uri, new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				LuceneViewResult result = new LuceneViewResult(pResult);
				pCallback.onSuccess(result);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}
	
	public void getLuceneStatusInfo(String pIndexName, final AsyncCallback<LuceneStatusInfo> pCallback) {
		String uri = getLuceneURI(pIndexName);
		m_server.get(uri, new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				LuceneStatusInfo result = new LuceneStatusInfo(pResult);
				pCallback.onSuccess(result);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK;
			}
		});
	}
	
	public void optimizeLucene(String pIndexName, final AsyncCallback<Boolean> pCallback) {
		String uri = getLuceneURI(pIndexName) + "/_optimize";
		m_server.post(uri,null, new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				pCallback.onSuccess(Boolean.TRUE);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_ACCEPTED || pCode == HttpStatusExceptionFactory.SC_CREATED;
			}
		});
	}
	
	public void expungeLucene(String pIndexName, final AsyncCallback<Boolean> pCallback) {
		String uri = getLuceneURI(pIndexName) + "/_expunge";
		m_server.post(uri,null, new CouchDBAsyncCallbackJSOModel() {
			@Override
			public void onFailure(Throwable pCaught) {
				pCallback.onFailure(pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				pCallback.onSuccess(Boolean.TRUE);
			}

			@Override
			public boolean isHttpStatusCodeOk(int pCode) {
				return pCode == HttpStatusExceptionFactory.SC_OK || pCode == HttpStatusExceptionFactory.SC_ACCEPTED || pCode == HttpStatusExceptionFactory.SC_CREATED;
			}
		});
	}
}