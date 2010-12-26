package com.os.client;

import com.os.couchdbjs.client.annotations.FilterFn;
import com.os.couchdbjs.client.base.AbstractFilter;
import com.os.couchdbjs.client.model.BaseDocument;
import com.os.couchdbjs.client.model.BaseRequest;

@FilterFn(designDocName="dtest",filterName="filter_1")
public class FilterTest extends AbstractFilter {

	@Override
	public boolean filter(BaseDocument pDoc, BaseRequest pRequest) {
		return pDoc.getId().startsWith("_design/");
	}
}
