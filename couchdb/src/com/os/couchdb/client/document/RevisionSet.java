package com.os.couchdb.client.document;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class RevisionSet<D extends BaseDocument> {
	private String m_jsonValue;
	private List<String> m_missingRevs = new ArrayList<String>();
	private List<String> m_deletedRevs = new ArrayList<String>();
	private List<D> m_okRevs = new ArrayList<D>();
	
	public RevisionSet(String pStr,BaseModelFactory<D> pFactory) {
		m_jsonValue = pStr;
		JSONValue jsonValue = JSONParser.parse(pStr);
		JSONArray jsArray = jsonValue.isArray();
		List<JSOModel> list = new ArrayList<JSOModel>(jsArray.size());
		for(int i=0;i < jsArray.size();i++) {
			list.add(JSOModel.fromJson(jsArray.get(i)));
		}
		for(JSOModel m : list) {
			if(m.hasKey("ok")) {
				m_okRevs.add(pFactory.createInstance(m.getObject("ok")));
			} else if(m.hasKey("missing")) {
				m_missingRevs.add(m.get("missing"));
			} else if(m.hasKey("deleted")) {
				m_deletedRevs.add(m.get("deleted"));
			}
		}
	}
	
	public List<String> getMissingRevs() {
		return m_missingRevs;
	}
	
	public List<String> getDeletedRevs() {
		return m_deletedRevs;
	}
	
	public List<D> getOkRevs() {
		return m_okRevs;
	}
	
	public String toString() {
		return m_jsonValue;
	}
}