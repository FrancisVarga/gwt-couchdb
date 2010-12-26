package com.os.client;

import com.os.couchdbjs.client.annotations.ValidateDocUpdateFn;
import com.os.couchdbjs.client.base.AbstractValidateDocUpdate;
import com.os.couchdbjs.client.model.BaseDocument;

@ValidateDocUpdateFn(designDocName="dtest")
public class ValidateTest extends AbstractValidateDocUpdate {
	@Override
	public void validate(BaseDocument pNewDoc, BaseDocument pOldDoc, UserCtx pUserCtx) {
		if("test".equals(pNewDoc.getId())) {
			forbidden("Cannot change test");
		}
	}
}
