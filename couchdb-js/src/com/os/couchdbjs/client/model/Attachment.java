package com.os.couchdbjs.client.model;

/**
 * Represents a couchdb document attachment. The attachment mechanism mirrors
 * the functionality present in couchdb itself. You will only be able to
 * indirectly use the data property contained in {@link Attachment} to create
 * attachments inlined with the document. When you query a document with
 * attachments, the attachments will have a <code>null</code> data property and
 * the stub property will be set to <code>true</code>.
 * 
 * This limitation has its origin in the way couchdb works and is deliberately
 * kept that way to not introduce additional queries.
 * 
 */
public class Attachment extends BaseModel {
	public Attachment() {
		super();
	}
	
	public Attachment(JSOModel pModel) {
		super(pModel);
	}

	public Attachment(String pContentType, byte[] pData) {
		super();
		setContentType(pContentType);
		setData(Base64.encode(pData));
	}

	public String getContentType() {
		return super.get("content_type");
	}

	public void setContentType(String pContentType) {
		super.getModel().set("content_type", pContentType);
	}

	/**
	 * Returns the Base64 encoded representation of this attachment's content.
	 * <em>note that you cannot read attachment data this way</em>. the property
	 * is only used internally to create inlined attachments and is set to
	 * <code>null</code> when reading documents.
	 * 
	 * This limitation has its origin in the way couchdb works and is deliberately
	 * kept that way to not introduce additional queries.
	 */
	public String getData() {
		return super.get("data");
	}

	public void setData(String pData) {
		super.getModel().set("data", pData);
	}

	public long getLength() {
		return super.getModel().getLong("length");
	}

	public void setLength(long pLength) {
		super.getModel().set("length", pLength);
	}

	public boolean isStub() {
		return super.getModel().getBoolean("stub");
	}

	/**
	 * Returns the revision position of this attachment that will change if the
	 * attachment gets updated.
	 */
	public String getRevPos() {
		return super.get("revpos");
	}

	/**
	 * Sets the revision position of this attachment.
	 */
	public void setRevPos(String pRevPos) {
		super.getModel().set("revpos", pRevPos);
	}
}