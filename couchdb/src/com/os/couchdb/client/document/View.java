package com.os.couchdb.client.document;

import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;
import com.os.couchdb.share.Util;

public class View extends BaseModel {
	public View() {
		super();
	}

	public View(JSOModel pModel) {
		super(pModel);
	}
	
	public View(String pMapFn) {
		setMap(pMapFn);
	}

	public View(String pMapFn, String pReduceFn) {
		setMap(pMapFn);
		setReduce(pReduceFn);
	}

	public String getMap() {
		return super.getModel().get("map");
	}

	public void setMap(String pMapFn) {
		if (pMapFn != null && pMapFn.length() > 0) {
			super.getModel().set("map", pMapFn);
		} else {
			super.getModel().unset("map");
		}
	}

	public String getReduce() {
		return super.getModel().get("reduce");
	}

	public void setReduce(String pReduceFn) {
		if (pReduceFn != null && pReduceFn.length() > 0) {
			super.getModel().set("reduce", pReduceFn);
		} else {
			super.getModel().unset("reduce");
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof View) {
			View that = (View) obj;
			result = Util.equals(this.getMap(), that.getMap()) && Util.equals(this.getReduce(), that.getReduce());
		}
		return result;
	}

	@Override
	public int hashCode() {
		return 17 + 37 * Util.safeHashcode(this.getMap()) + 37 * Util.safeHashcode(this.getReduce());
	}
}