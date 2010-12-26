package com.os.couchdb.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class ViewResult extends AbstractViewResult {
	private List<ValueRow> m_rows;
	
	public ViewResult(JSOModel pModel) {
		super(pModel);
		List<ValueRow> result = new ArrayList<ValueRow>();
		JsArray<JSOModel> jsoArray = getModel().getArray("rows");
		for(int i=0;i < jsoArray.length();i++) {
			JSOModel jsoValue = jsoArray.get(i);
			if(jsoValue != null) {
				result.add(new ValueRow(jsoValue));
			} else {
				result.add(null);
			}
		}
	}

	public List<ValueRow> getRows() {
		return m_rows;
	}
}