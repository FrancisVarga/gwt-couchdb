package com.os.couchdbjs.client.model;

public class ValueAndDocumentRow<D extends BaseDocument> extends ValueRow {
	private D m_doc;
	
	public ValueAndDocumentRow(JSOModel pModel) {
		super(pModel);
	}
	
	public D getDocument(BaseModelFactory<D> pDocFactory) {
		if(m_doc == null) {
			m_doc = pDocFactory.createInstance(super.getModel().getObject("doc"));
		}
		return m_doc;
	}
}