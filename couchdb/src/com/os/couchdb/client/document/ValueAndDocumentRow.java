package com.os.couchdb.client.document;

import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class ValueAndDocumentRow<D extends BaseDocument> extends ValueRow {
	private D m_doc;
	
	public ValueAndDocumentRow(JSOModel pModel,BaseModelFactory<D> pDocFactory) {
		super(pModel);
		m_doc = pDocFactory.createInstance(super.getModel().getObject("doc"));
	}
	
	public D getDocument() {
		return m_doc;
	}
}