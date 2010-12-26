package com.os.couchdb.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.os.couchdb.client.db.CouchDB;
import com.os.couchdb.client.document.Document;

public class DocumentCreatedEvent extends GwtEvent<DocumentCreatedEvent.DocumentCreatedEventHandler> {
	public static interface DocumentCreatedEventHandler extends EventHandler {
		public void documentCreated(DocumentCreatedEvent pEvent);
	}
	
	public static Type<DocumentCreatedEventHandler> TYPE = new GwtEvent.Type<DocumentCreatedEventHandler>();
	
	private CouchDB m_db;
	private Document m_document;
	
	public DocumentCreatedEvent(CouchDB pDb,Document pDoc) {
		setDb(pDb);
		setDocument(pDoc);
	}
	
	protected void setDb(CouchDB db) {
		this.m_db = db;
	}

	public CouchDB getDb() {
		return m_db;
	}

	protected void setDocument(Document document) {
		this.m_document = document;
	}

	public Document getDocument() {
		return m_document;
	}

	@Override
	protected void dispatch(DocumentCreatedEventHandler pHandler) {
		pHandler.documentCreated(this);
	}

	@Override
	public GwtEvent.Type<DocumentCreatedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public static GwtEvent.Type<DocumentCreatedEventHandler> getType() {
		return TYPE;
	}
}
