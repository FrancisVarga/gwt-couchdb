package com.os.couchdb.client.http;

import java.util.List;
import java.util.Map;

import com.google.gwt.gears.client.blob.Blob;
import com.google.gwt.gears.client.httprequest.HttpRequest;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Document;
import com.os.couchdb.client.http.gears.HttpRequestCallback;
import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.BaseModelFactory;

public interface Method {
	public Method header(String pHeader, String pValue);
	public Method headers(Map<String, String> pHeaders);
	public Method text(String pData);
	public Method json(JSONValue pData);
	public Method json(String pData);
	public Method json(BaseModel pData);
	public Method json(List<BaseModel> pData);
	public Method xml(Document pData);
	public Method form(Map<String, String> pData);
	public Method blob(Blob pBlob, String pContentType);
	public void send(HttpRequestCallback pCallback);
	public void send(HttpResponseCallback pCallback);
	public void send(VoidCallback pCallback);
	public void send(TextCallback pCallback);
	public void send(JsonCallback pCallback);
	public void send(JSOModelCallback callback);
	public void send(final XmlCallback callback);
	public void send(final BlobCallback callback);
	public <T extends BaseModel> void send(final BaseModelCallback<T> pCallback, final BaseModelFactory<T> pFactory);
	public <T extends BaseModel> void send(final BaseModelListCallback<T> callback, final BaseModelFactory<T> pFactory);
	public void send(final StringListCallback callback);
	public HttpRequest getGearsHttpRequest();
	public Request getHttpRequest();
	public Response getHttpResponse();
}
