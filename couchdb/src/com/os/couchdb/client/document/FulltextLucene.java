package com.os.couchdb.client.document;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class FulltextLucene extends BaseModel {
	public static class Defaults extends BaseModel {
		public static String[] __TYPES_OPTS = new String[] {
			"string",
			"date",
			"double",
			"float",
			"int",
			"long"
		};
		public static String[] __STORE_OPTS = new String[] {
			"no","yes"
		};
		public static String[] __ANALYZER_OPTS = new String[] {
			"analyzed","analyzed_no_norms","no","not_analyzed","not_analized_no_norms"
		};
		
		public Defaults() {
			super();
			super.getModel().set("field", "default");
			super.getModel().set("type", "string");
			super.getModel().set("store", "no");
			super.getModel().set("index", "analyzed");
		}
		
		public Defaults(JSOModel pModel) {
			super(pModel);
		}
		
		public void setField(String pName) {
			super.getModel().set("field", pName);
		}
		
		public void setType(String pType) {
			super.getModel().set("type", pType);
		}
		
		public void setStore(String pStore) {
			super.getModel().set("store", pStore);
		}
		
		public void setIndex(String pIndex) {
			super.getModel().set("index", pIndex);
		}
	}
	
	public FulltextLucene() {
		super();
	}
	
	public FulltextLucene(JSOModel pModel) {
		super(pModel);
	}
	
	public FulltextLucene(String pIndexFn) {
		super();
		setIndexFn(pIndexFn);
	}
	
	public void setIndexFn(String pIndexFn) {
		super.getModel().set("index",pIndexFn);
	}
	
	public String getIndexFn() {
		return super.getModel().get("index");
	}
	
	public void setAnalyzer(String pAnalizer) {
		super.getModel().set("analyzer",pAnalizer);
	}
	
	public String getAnalyzer() {
		return super.getModel().get("analyzer");
	}
	
	public Defaults getDefaults() {
		return new Defaults(super.getModel().getObject("defaults"));
	}
	
	public void setDefaults(Defaults pDefaults) {
		super.getModel().set("defaults", pDefaults.getModel());
	}
}