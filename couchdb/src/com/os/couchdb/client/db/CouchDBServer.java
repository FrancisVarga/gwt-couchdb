package com.os.couchdb.client.db;

import java.util.List;

import com.google.gwt.gears.client.blob.Blob;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.os.couchdb.client.document.ReplicationInfo;
import com.os.couchdb.client.document.StatsInfo;
import com.os.couchdb.client.document.UserInfo;
import com.os.couchdb.client.model.JSOModel;

public interface CouchDBServer {
  public final static int DEFAULT_PORT = 5984;

  /**
   * Returns a list with all database names.
   *
   * @return
   */
  public void listDatabases(AsyncCallback<List<String>> pCallback);

  /**
   * Creates the database with the given name
   * @param name
   * @return <code>true</code> if the database could be created, <code>false</code> if they already existed
   */
  public void createDatabase(String name,AsyncCallback<Boolean> pCallback);

  /**
   * Deletes the database with the given name
   * @param name
   */
  public void deleteDatabase(String name,AsyncCallback<Boolean> pCallback);

  /**
   * Send a GET request to the given URI
   * @param uri
   * @return
   */
  public void get(String uri,CouchDBAsyncCallbackJSOModel pCallback);
  public void get(String uri,CouchDBAsyncCallbackString pCallback);
  public void get(String uri,CouchDBAsyncCallbackHttpRequest pCallback);
	public void get(String pUri,CouchDBAsyncCallbackHttpResponse pCallback);
  public void get(String uri,CouchDBAsyncCallbackJSON pCallback);
  /////////////////////////////////////////////////////////////
  // GEARS
  public void get(String uri,CouchDBAsyncCallbackBlob pCallback);
  /////////////////////////////////////////////////////////////
  /**
   * Send a PUT request to the given URI
   * @param uri
   * @return
   */
  public void put(String uri,CouchDBAsyncCallbackJSOModel pCallback);

  /**
   * Send a PUT request to the given URI with
   * the given body
   * @param uri
   * @return
   */
  public void put(String uri, String body,CouchDBAsyncCallbackJSOModel pCallback);
  /////////////////////////////////////////////////////////////
  // GEARS
  public void put(String uri, Blob blob, String contentType, CouchDBAsyncCallbackJSOModel pCallback);
  /////////////////////////////////////////////////////////////

  /**
   * Send a POST request to the given URI with
   * the given body
   * @param uri
   * @return
   */
  public void post(String uri, String body,CouchDBAsyncCallbackJSOModel pCallback);
  public void post(String uri, String body,CouchDBAsyncCallbackVoid pCallback);


  /**
   * Send a DELETE request to the given URI
   *
   * @param uri
   * @return
   */
  public void delete(String uri,CouchDBAsyncCallbackJSOModel pCallback);

  /**
   * Get couchdb runtime statistics.
   * 
   * @param filter    filter for the stats (e.g. "/couchdb/request_time") or <code>null</code> in which case the output will be unfiltered.
   * @return  stats map
   */
  public void getStats(String filter,AsyncCallback<StatsInfo> pCallback);
  public void getConfig(AsyncCallback<JSOModel> pCallback);
  
  /**
   * Trigger replication from a database to another database. Both database can be either local names
   * (e.g. "exampleDB") or full URLs (e.g. "http://admin:password@example.org/exampleDB" ).
   * 
   * Replication is always a directed from source to target. if you want to replicate in both directions
   * you have to make two calls to {@link #replicate(String, String, boolean)}. 
   * 
   * @param source        source database name or URL
   * @param target        target database name or URL
   * @param continuous    if <code>true</code>, start continuous replication.
   * @param replication info
   */
  public void replicate(String source,String target, AsyncCallback<ReplicationInfo> pCallback);
  public void replicate(ReplicationOptions pOptions, AsyncCallback<ReplicationInfo> pCallback);


  /**
   * Requests a list of uuids from the CouchDB server
   * @param count     number of uuids to request
   * @return
   */
  public void getUUIDs(int count,AsyncCallback<List<String>> pCallback);
  
  public void authenticate(String pUserName,String pPasswd,AsyncCallback<Boolean> pCallback);
  public void isAuthenticated(AsyncCallback<UserInfo> pCallback);
  
  public String resolveURL(String pPath);  
}