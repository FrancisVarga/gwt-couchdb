package com.os.couchdbjs.client.base;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseModel;
import com.os.couchdbjs.client.model.JSOModel;

public abstract class AbstractFulltext {
	public static class Options extends BaseModel {
		public Options() {
			super();
		}

		public Options setFieldName(String pName) {
			super.getModel().set("field", pName);
			return this;
		}

		public Options setTypeString() {
			super.getModel().set("type", "string");
			return this;
		}

		public Options setTypeDouble() {
			super.getModel().set("type", "double");
			return this;
		}

		public Options setTypeFloat() {
			super.getModel().set("type", "float");
			return this;
		}

		public Options setTypeInteger() {
			super.getModel().set("type", "integer");
			return this;
		}

		public Options setTypeLong() {
			super.getModel().set("type", "long");
			return this;
		}

		public Options setTypeDate() {
			super.getModel().set("type", "date");
			return this;
		}

		public Options setStored() {
			super.getModel().set("store", "yes");
			return this;
		}

		public Options setNotStored() {
			super.getModel().set("store", "no");
			return this;
		}

		public Options setIndexAnalyzed() {
			super.getModel().set("index", "analyzed");
			return this;
		}

		public Options setIndexAnalyzedNoNorms() {
			super.getModel().set("index", "analyzed_no_norms");
			return this;
		}

		public Options setIndexNo() {
			super.getModel().set("index", "no");
			return this;
		}

		public Options setIndexNotAnalyzed() {
			super.getModel().set("index", "not_analyzed");
			return this;
		}

		public Options setIndexNotAnalyzedNoNorms() {
			super.getModel().set("index", "not_analyzed_no_norms");
			return this;
		}
	}

	public static class LuceneDocument extends JavaScriptObject {
		protected LuceneDocument() {
		}

		public final native void add(String pValue) /*-{
			this.add(pValue);
		}-*/;

		public final native void add(int pValue) /*-{
			this.add(pValue);
		}-*/;

		public final native void add(double pValue) /*-{
			this.add(pValue);
		}-*/;

		public final native void add(Date pValue) /*-{
			this.add(pValue);
		}-*/;

		public final void add(String pValue,Options pOptions) {
			addInternal(pValue, pOptions.getModel());
		}

		public final void add(int pValue,Options pOptions) {
			addInternal(pValue, pOptions.getModel());
		}
		
		public final void add(double pValue,Options pOptions) {
			addInternal(pValue, pOptions.getModel());
		}
		
		public final void add(Date pValue,Options pOptions) {
			addInternal(pValue, pOptions.getModel());
		}
		
		private final native void addInternal(String pValue, JavaScriptObject pOptions) /*-{
			this.add(pValue,pOptions);
		}-*/;

		private final native void addInternal(int pValue, JavaScriptObject pOptions) /*-{
			this.add(pValue,pOptions);
		}-*/;

		private final native void addInternal(double pValue, JavaScriptObject pOptions) /*-{
			this.add(pValue,pOptions);
		}-*/;

		private final native void addInternal(Date pValue, JavaScriptObject pOptions) /*-{
			this.add(pValue,pOptions);
		}-*/;
		
		final static native LuceneDocument createNative() /*-{
			return new Document();
		}-*/;
	}

	public AbstractFulltext() {
		jsExport();
	}

	public LuceneDocument createLuceneDocument() {
		return LuceneDocument.createNative();
	}
	
	private final native void jsExport() /*-{
		$wnd.fulltext = this.@com.os.couchdbjs.client.base.AbstractFulltext::indexInternal(Lcom/google/gwt/core/client/JavaScriptObject;);
	}-*/;

	public final JavaScriptObject indexInternal(JavaScriptObject pDoc) {
		LuceneDocument doc = index(new BaseDocument(JSOModel.fromJavascriptObject(pDoc)));
		return doc;
	}

	public abstract LuceneDocument index(BaseDocument pDoc);
}