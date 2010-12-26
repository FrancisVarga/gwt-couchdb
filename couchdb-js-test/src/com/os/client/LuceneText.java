package com.os.client;

import com.os.couchdbjs.client.annotations.FulltextFn;
import com.os.couchdbjs.client.base.AbstractFulltext;
import com.os.couchdbjs.client.model.BaseDocument;

@FulltextFn(designDocName="dtest",idxName="idx_1")
public class LuceneText extends AbstractFulltext {
	@Override
	public LuceneDocument index(BaseDocument pDoc) {
		if(pDoc.getModel().hasKey("tag")) {
			LuceneDocument ldoc = createLuceneDocument();
			Options opts = new Options();
			opts.setFieldName("tag");
			ldoc.add(pDoc.get("tag"),opts);
			return ldoc;
		}
		return null;
	}

}
