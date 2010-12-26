package com.os.couchdb.client.document;

import java.util.Map;

import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;
import com.os.couchdb.share.Util;

public class DesignDocument extends BaseDocument {
	public final static String PREFIX = "_design/";
	public final static String LANG_JAVASCRIPT = "javascript";

	public DesignDocument(String id, String revision) {
		super();
		setId(id);
		setRevision(revision);
		setLanguage(LANG_JAVASCRIPT);
	}

	public DesignDocument() {
		this(null, null);
	}

	public DesignDocument(String pDocId) {
		this(pDocId, null);
	}
	
	public DesignDocument(JSOModel pModel) {
		super(pModel);
	}

	/**
	 * Sets the id for the design document ( the "_design/" prefix which will be
	 * added automatically )
	 */
	@Override
	public void setId(String pDocId) {
		super.setId(extendId(pDocId));
	}

	@Override
	public String getId() {
		return super.getId();// .replace("%2F", "/");
	}

	public String getLanguage() {
		return super.get("language");
	}

	public void setLanguage(String pLanguage) {
		super.getModel().setOrClear("language", pLanguage);
	}

	public String getValidateOnDocUpdate() {
		return super.get("validate_doc_update");
	}

	public void setValidateOnDocUpdate(String pValidateOnDocUpdate) {
		super.getModel().setOrClear("validate_doc_update", pValidateOnDocUpdate);
	}

	public Map<String, View> getViews() {
		return super.getObjectMap("views", new BaseModelFactory<View>() {
			@Override
			public View createInstance(JSOModel pModel) {
				return new View(pModel);
			}
		});
	}

	public View getView(String pViewName) {
		return super.getObjectMapValue("views", pViewName, new BaseModelFactory<View>() {
			@Override
			public View createInstance(JSOModel pModel) {
				return new View(pModel);
			}
		});
	}

	/**
	 * Sets all views of the given design document.
	 */
	public void setViews(Map<String, View> pViews) {
		super.setObjectMap("views", pViews);
	}

	/**
	 * Adds a view to this design document.
	 */
	public void addView(String pName, View pView) {
		super.addKeyValue("views", pName, pView);
	}

	/**
	 * Ensures that the id has the design document prefix and returns the id
	 */
	public static String extendId(String pDocId) {
		if (pDocId != null) {
			if (!pDocId.startsWith(PREFIX)) {
				pDocId = PREFIX + pDocId;
			}
		}
		return pDocId;
	}

	/**
	 * Equality based on id, language and view comparison <em>without</em>
	 * revision comparison. This method basically checks if the other design
	 * document has exactly the same id, views and language.
	 * 
	 */
	public boolean equalsIncludingContent(DesignDocument pThat) {
		return Util.equals(this.getId(), pThat.getId()) && Util.equals(this.getLanguage(), pThat.getLanguage())
				&& Util.equals(this.getViews(), pThat.getViews());

	}

	public void setShowFunctions(Map<String, String> pShows) {
		super.setStringMap("shows", pShows);
	}

	public Map<String, String> getShowFunctions() {
		return super.getStringMap("shows");
	}

	public void setListFunctions(Map<String, String> pLists) {
		super.setStringMap("lists", pLists);
	}

	public Map<String, String> getListFunctions() {
		return super.getStringMap("lists");
	}

	/**
	 * Adds a show function to the design document
	 * 
	 */
	public void addShowFunction(String pName, String pShowFn) {
		super.addKeyValue("shows", pName, pShowFn);
	}

	/**
	 * Adds a list function to the design document
	 * 
	 */
	public void addListFunction(String pName, String pListFn) {
		super.addKeyValue("lists", pName, pListFn);
	}
	
	public Map<String, FulltextLucene> getLuceneIndexes() {
		return super.getObjectMap("fulltext", new BaseModelFactory<FulltextLucene>() {
			@Override
			public FulltextLucene createInstance(JSOModel pModel) {
				return new FulltextLucene(pModel);
			}
		});
	}

	public FulltextLucene getLuceneIndex(String pViewName) {
		return super.getObjectMapValue("fulltext", pViewName, new BaseModelFactory<FulltextLucene>() {
			@Override
			public FulltextLucene createInstance(JSOModel pModel) {
				return new FulltextLucene(pModel);
			}
		});
	}

	/**
	 * Sets all views of the given design document.
	 */
	public void setLuceneIndexes(Map<String, FulltextLucene> pLuceneIdx) {
		super.setObjectMap("fulltext", pLuceneIdx);
	}

	public void addLuceneIndex(String pName, FulltextLucene pLuceneIdx) {
		super.addKeyValue("fulltext", pName, pLuceneIdx);
	}
}