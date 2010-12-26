package com.os.couchdb.client.document;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;

public class LuceneViewResult extends BaseModel {
	public static class SortOrder extends BaseModel {
		SortOrder(JSOModel pModel) {
			super(pModel);
		}

		public String getField() {
			return super.get("field");
		}

		public boolean isReverse() {
			return super.getModel().getBoolean("reverse");
		}

		public String getType() {
			return super.get("type");
		}
	}
	
	public static class Row extends BaseModel {
		Row(JSOModel pModel) {
			super(pModel);
		}
		
		public String getId() {
			return super.get("id");
		}
		
		public double getScore() {
			return super.getModel().getDouble("score");
		}
		
		public JSOModel getStoredFields() {
			if(super.getModel().hasKey("fields")) {
				JavaScriptObject jv = super.getModel().getNativeValue("fields");
				return JSOModel.fromJavascriptObject(jv);
			}
			return null;
		}
		
		public JSONArray getSortInfo() {
			if(super.getModel().hasKey("sort_order")) {
				JavaScriptObject jv = super.getModel().getNativeValue("sort_oder");
				return new JSONArray(jv);
			}
			return null;
		}
		
		public <T extends BaseDocument> T getDoc(BaseModelFactory<T> pFactory) {
			if(super.getModel().hasKey("doc")) {
				return pFactory.createInstance(super.getModel().getObject("doc"));
			}
			return null;
		}
	}

	private List<SortOrder> m_sortOrder;
	private List<Row> m_rows;

	public LuceneViewResult(JSOModel pModel) {
		super(pModel);
		m_sortOrder = super.getObjectList("sort_order", new BaseModelFactory<SortOrder>() {
			@Override
			public SortOrder createInstance(JSOModel pModel) {
				return new SortOrder(pModel);
			}
		});
		m_rows = super.getObjectList("rows", new BaseModelFactory<Row>() {
			@Override
			public Row createInstance(JSOModel pModel) {
				return new Row(pModel);
			}
		});
	}

	public String getETag() {
		return super.get("etag");
	}

	public double getFetchDuration() {
		return super.getModel().getDouble("fetch_duration");
	}

	public int getLimit() {
		return super.getModel().getInt("limit");
	}

	public int getSkip() {
		return super.getModel().getInt("skip");
	}

	public String getQuery() {
		return super.get("q");
	}

	public double getSearchDuration() {
		return super.getModel().getDouble("search_duration");
	}

	public List<SortOrder> getSortOrder() {
		return m_sortOrder;
	}

	public List<Row> getRows() {
		return m_rows;
	}
}
