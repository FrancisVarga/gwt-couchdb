package com.os.client;

import com.os.couchdbjs.client.annotations.ShowFn;
import com.os.couchdbjs.client.base.AbstractShow;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseRequest;
import com.os.couchdbjs.client.model.BaseResponse;

@ShowFn(designDocName="dtest",showName="show_2")
public class ShowTest2 extends AbstractShow {

	@Override
	public BaseResponse show(BaseDocument pDoc, BaseRequest pReq) {
		BaseResponse resp = new BaseResponse();
		resp.addHeader("Content-Type", "application/json");
		resp.setBody(pDoc.toJson(pDoc));
		return resp;
	}

}
