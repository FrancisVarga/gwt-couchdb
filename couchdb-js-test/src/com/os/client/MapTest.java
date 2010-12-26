package com.os.client;

import com.os.couchdbjs.client.annotations.ViewMapFn;
import com.os.couchdbjs.client.base.AbstractViewMap;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.JSOModel;

@ViewMapFn(designDocName="dtest",viewName="view_1")
public class MapTest extends AbstractViewMap {
	public MapTest() {
		super();
	}
	
	@Override
	public void map(BaseDocument pModel) {
		if(!"test".equals(pModel.getId())) {
			JSOModel jso = JSOModel.create();
			jso.set("_id", "t1");
			jso.set("c1","v1");
			emit("emitStr",jso);
		} else {
			emit("emitStr", 3.14);
		}
	}

}
