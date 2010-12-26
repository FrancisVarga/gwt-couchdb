package com.os.couchdb.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class ViewAndDocumentsResult<D extends BaseDocument> extends AbstractViewResult {
	private List<ValueAndDocumentRow<D>> m_rows;
	
	public ViewAndDocumentsResult(JSOModel pModel,BaseModelFactory<D> pDocFactory) {
		super(pModel);
		List<ValueAndDocumentRow<D>> result = new ArrayList<ValueAndDocumentRow<D>>();
		JsArray<JSOModel> jsoArray = getModel().getArray("rows");
		for(int i=0;i < jsoArray.length();i++) {
			JSOModel jsoValue = jsoArray.get(i);
			if(jsoValue != null) {
				result.add(new ValueAndDocumentRow<D>(jsoValue, pDocFactory));
			} else {
				result.add(null);
			}
		}
	}

	public List<ValueAndDocumentRow<D>> getRows() {
		return m_rows;
	}
}