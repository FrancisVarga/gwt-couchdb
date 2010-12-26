package com.os.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.os.couchdbjs.client.annotations.UpdateHandlerFn;
import com.os.couchdbjs.client.base.AbstractUpdateHandler;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseRequest;

@UpdateHandlerFn(designDocName="dtest",name="update_1")
public class UpdateTest extends AbstractUpdateHandler {

	@Override
	public JsArray<JavaScriptObject> update(BaseDocument pDoc, BaseRequest pReq) {
		if("test".equals(pDoc.getId())) {
			pDoc.getModel().set(pReq.getQueryParam("field"), pReq.getQueryParam("value"));
			return createResponse(pDoc, "Add field " + pReq.getQueryParam("field") + "=" + pReq.getQueryParam("value"));
		}
		return createResponse(null, "No change");
	}
}
