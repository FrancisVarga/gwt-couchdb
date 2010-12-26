package com.os.couchdb.client.exception;

import com.google.gwt.gears.client.httprequest.HttpRequest;
import com.google.gwt.http.client.Response;

public class HttpStatusExceptionFactory {
  public static final int SC_ACCEPTED = 202;
  public static final int SC_BAD_GATEWAY = 502;
  public static final int SC_BAD_REQUEST = 400;
  public static final int SC_CONFLICT = 409;
  public static final int SC_CONTINUE = 100;
  public static final int SC_CREATED = 201;
  public static final int SC_EXPECTATION_FAILED = 417;
  public static final int SC_FORBIDDEN = 403;
  public static final int SC_GATEWAY_TIMEOUT = 405;
  public static final int SC_GONE = 410;
  public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
  public static final int SC_INTERNAL_SERVER_ERROR = 500;
  public static final int SC_LENGTH_REQUIRED = 411;
  public static final int SC_METHOD_NOT_ALLOWED = 405;
  public static final int SC_MOVED_PERMANENTLY = 301;
  public static final int SC_MOVED_TEMPORARILY = 302;
  public static final int SC_MULTIPLE_CHOICES = 300;
  public static final int SC_NO_CONTENT = 204;
  public static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
  public static final int SC_NOT_ACCEPTABLE = 406;
  public static final int SC_NOT_FOUND = 404;
  public static final int SC_NOT_IMPLEMENTED = 501;
  public static final int SC_NOT_MODIFIED = 304;
  public static final int SC_OK = 200;
  public static final int SC_PARTIAL_CONTENT = 206;
  public static final int SC_PAYMENT_REQUIRED = 402;
  public static final int SC_PRECONDITION_FAILED = 412;
  public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
  public static final int SC_REQUEST_ENTITY_TOO_LARGE = 413;
  public static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
  public static final int SC_RESET_CONTENT = 205;
  public static final int SC_SEE_OTHER = 303;
  public static final int SC_SERVICE_UNAVAILABLE = 503;
  public static final int SC_SWITCHING_PROTOCOLS = 101;
  public static final int SC_TEMPORARY_REDIRECT = 307;
  public static final int SC_UNAUTHORIZED = 401;
  public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
  public static final int SC_USE_PROXY = 305;
	
	public static FailedStatusCodeException makeException(HttpRequest pRequest) {
		switch(pRequest.getStatus()) {
		case SC_CONFLICT:
			return new ConflictException(pRequest.getStatus(),pRequest.getStatusText(),pRequest.getResponseText());
		case SC_NOT_FOUND:
			return new NotFoundException(pRequest.getStatus(),pRequest.getStatusText(),pRequest.getResponseText());
		case SC_UNAUTHORIZED:
			return new UnathorizedException(pRequest.getStatus(),pRequest.getStatusText(),pRequest.getResponseText());
		case SC_PRECONDITION_FAILED:
			return new DocumentValidationException(pRequest.getStatus(),pRequest.getStatusText(),pRequest.getResponseText());
		default:
			return new FailedStatusCodeException(pRequest.getStatus(),pRequest.getStatusText(),pRequest.getResponseText());
		}
	}
	
	public static FailedStatusCodeException makeException(Response pResponse) {
		switch(pResponse.getStatusCode()) {
		case SC_CONFLICT:
			return new ConflictException(pResponse.getStatusCode(),pResponse.getStatusText(),pResponse.getText());
		case SC_NOT_FOUND:
			return new NotFoundException(pResponse.getStatusCode(),pResponse.getStatusText(),pResponse.getText());
		case SC_UNAUTHORIZED:
			return new UnathorizedException(pResponse.getStatusCode(),pResponse.getStatusText(),pResponse.getText());
		case SC_PRECONDITION_FAILED:
			return new DocumentValidationException(pResponse.getStatusCode(),pResponse.getStatusText(),pResponse.getText());
		default:
			return new FailedStatusCodeException(pResponse.getStatusCode(),pResponse.getStatusText(),pResponse.getText());
		}
	}
}