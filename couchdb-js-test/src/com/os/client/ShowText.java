package com.os.client;

import com.os.couchdbjs.client.annotations.ShowFn;
import com.os.couchdbjs.client.base.AbstractShow;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseRequest;
import com.os.couchdbjs.client.model.BaseResponse;

@ShowFn(designDocName="dtest",showName="show_1")
public class ShowText extends AbstractShow {
	@Override
	public BaseResponse show(BaseDocument pDoc, BaseRequest pReq) {
		BaseResponse resp = new BaseResponse();
		resp.setBody("<B>This is a doc: " + pDoc.getId() + " </B> " + pDoc.get("field"));
		return resp;
	}
}
