package com.os.couchdb.client.db;

import com.os.couchdb.client.document.Document;
import com.os.couchdb.client.document.DocumentInfo;

public interface CouchDBEventHandler {
	/**
	 * Called when a document is about to be created. Any exception thrown will
	 * prevent creation.
	 * 
	 * @param db
	 *          database
	 * @param document
	 *          Document
	 * @throws Exception
	 */
	<D extends Document> void creatingDocument(CouchDB db, D document) throws Exception;

	/**
	 * Called when after a document is created.
	 * 
	 * @param db
	 *          database
	 * @param document
	 *          Document
	 * @param response
	 *          response received from couchdb
	 */
	<D extends Document> void createdDocument(CouchDB db, D document, DocumentInfo response);

	/**
	 * Called when a document is about to be updated. Any exception thrown will
	 * prevent the update.
	 * 
	 * @param db
	 *          database
	 * @param document
	 *          Document
	 * @throws Exception
	 */
	<D extends Document> void updatingDocument(CouchDB db, D document) throws Exception;

	/**
	 * Called when after a document is updated.
	 * 
	 * @param db
	 *          database
	 * @param document
	 *          Document
	 * @param response
	 *          response received from couchdb
	 */
	<D extends Document> void updatedDocument(CouchDB db, D document, DocumentInfo response);

	/**
	 * Called when a document is about to be deleted. Any exception thrown will
	 * prevent the deletion.
	 * 
	 * @param db
	 *          database
	 * @param document
	 *          Document
	 * @throws Exception
	 */
	void deletingDocument(CouchDB db, String id, String rev) throws Exception;

	/**
	 * Called when after a document is deleted.
	 * 
	 * @param db
	 *          database
	 * @param document
	 *          Document
	 * @param response
	 *          response received from couchdb or <code>null</code> if the call
	 *          was a bulk delete.
	 */
	void deletedDocument(CouchDB db, String id, String rev, Object response);
}
