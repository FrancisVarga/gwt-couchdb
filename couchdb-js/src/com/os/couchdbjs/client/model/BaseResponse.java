package com.os.couchdbjs.client.model;

import java.util.Map;

public class BaseResponse extends BaseModel {
	public BaseResponse() {
		super();
	}
	
	public void setCode(int pCode) {
		super.getModel().set("code",pCode);
	}
	
	public int getCode() {
		return super.getModel().getInt("code");
	}
	
	public void addHeader(String pName,String pValue) {
		super.addKeyValue("headers", pName, pValue);
	}
	
	public Map<String,String> getHeaders() {
		return super.getStringMap("headers");
	}
	
	public String getHeader(String pName) {
		return super.getStringMapValue("headers", pName);
	}
	
	public void setJSON(String pJSON) {
		super.getModel().set("json", pJSON);
		super.getModel().unset("body");
		super.getModel().unset("base64");
	}
	
	public void setJSON(BaseModel pModel) {
		setJSON(BaseModel.toJson(pModel));
	}
	
	public void setBody(String pBody) {
		super.getModel().set("body", pBody);
		super.getModel().unset("json");
		super.getModel().unset("base64");
	}
	
	public void setBody(byte[] pBody) {
		super.getModel().set("base64", Base64.encode(pBody));
		super.getModel().unset("json");
		super.getModel().unset("body");
	}
}