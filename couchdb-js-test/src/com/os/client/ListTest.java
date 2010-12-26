package com.os.client;

import java.util.Map;

import com.os.couchdbjs.client.annotations.ListFn;
import com.os.couchdbjs.client.base.AbstractList;
import com.os.couchdbjs.client.model.BaseHeader;
import com.os.couchdbjs.client.model.BaseModel;
import com.os.couchdbjs.client.model.BaseRequest;
import com.os.couchdbjs.client.model.BaseResponse;
import com.os.couchdbjs.client.model.ValueRow;
import com.os.couchdbjs.client.util.XMLWriter;

@ListFn(designDocName="dtest",listName="list_xml")
public class ListTest extends AbstractList {
	@Override
	public void list(BaseHeader pHeader, BaseRequest pRequest) {
		BaseResponse resp = new BaseResponse();
		resp.addHeader("Content-Type", "text/xml");
		start(resp);
		XMLWriter xml = XMLWriter.createWriter();
		xml.writeStartDocument(false);
		ValueRow vl = getRow();
		xml.writeStartElement("documents");
		while(vl != null) {
			xml.writeStartElement("doc");
			xml.writeAttributeString("id", vl.getId());
			Map<String, String> fields = vl.getFields();
			for(String key : fields.keySet()) {
				xml.writeElementString(key, fields.get(key));
			}
			xml.writeEndElement();
			vl = getRow();
		}
		xml.writeEndElement();
		send(xml.flush());
	}
}
