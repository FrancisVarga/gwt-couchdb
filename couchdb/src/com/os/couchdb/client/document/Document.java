package com.os.couchdb.client.document;

import java.util.Map;

public interface Document {
  String getId();
  void setId(String id);
  String getRevision();
  void setRevision(String revision);
  Map<String,Attachment> getAttachments();
  void setAttachments(Map<String,Attachment> attachments);
}
