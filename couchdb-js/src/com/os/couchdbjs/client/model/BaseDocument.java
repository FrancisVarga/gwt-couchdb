package com.os.couchdbjs.client.model;

import java.util.List;
import java.util.Map;

public class BaseDocument extends BaseModel implements Document {
	public static class RevisionList extends BaseModel {
		public RevisionList() {
			super();
		}
		
		public RevisionList(JSOModel pModel) {
			super(pModel);
		}
		
		public int getStart() {
			return super.getModel().getInt("start");
		}
		
		public List<String> getIds() {
			return super.getStringList("ids");
		}
	}
	
	public static class RevInfo extends BaseModel {
		public static enum RevStatus {
			available,disk,missing,deleted;
		}
		
		public RevInfo(JSOModel pModel) {
			super(pModel);
		}
		
		public String getRevId() {
			return super.get("rev");
		}
		
		public RevStatus getStatus() {
			return RevStatus.valueOf(super.get("status"));
		}
	}
	
	public BaseDocument() {
		super();
	}

	public BaseDocument(JSOModel pModel) {
		super(pModel);
	}

	public String getId() {
		return super.getModel().get("_id");
	}

	public void setId(String id) {
		super.getModel().setOrClear("_id", id);
	}

	public String getRevision() {
		if(super.getModel().hasKey("_rev")) {
			return super.getModel().get("_rev");
		} else {
			return null;
		}
	}

	public void setRevision(String revision) {
		super.getModel().setOrClear("_rev", revision);
	}

	/**
	 * Two documents are equal if they have the same id and the same revision.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Document) {
			Document that = (Document) obj;
			return Util.equals(this.getId(), that.getId()) && Util.equals(this.getRevision(), that.getRevision());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 17 + Util.safeHashcode(getId()) * 37 + Util.safeHashcode(getRevision()) * 37;
	}

	public Map<String, Attachment> getAttachments() {
		return super.getObjectMap("_attachments",new BaseModelFactory<Attachment>() {
			@Override
			public Attachment createInstance(JSOModel pModel) {
				return new Attachment(pModel);
			}
		});
	}

	public void setAttachments(Map<String, Attachment> pAttachments) {
		super.setObjectMap("_attachments", pAttachments);
	}

	public void addAttachment(String name, Attachment pAttachment) {
		super.addKeyValue("_attachments", name, pAttachment);
	}
	
	public RevisionList getRevisionList() {
		JSOModel revList = super.getModel().getObject("_revisions");
		if(revList != null) {
			return new RevisionList(revList);
		} else {
			return null;
		}
	}
	
	public List<RevInfo> getRevisionInfoList() {
		return super.getObjectList("_revs_info", new BaseModelFactory<RevInfo>() {
			@Override
			public RevInfo createInstance(JSOModel pModel) {
				return new RevInfo(pModel);
			}
		});
	}
	
	public List<String> getConflicts() {
		return super.getStringList("_conflicts");
	}

	public List<String> getDeletedConflicts() {
		return super.getStringList("_deleted_conflicts");
	}
	
	public boolean isDeleted() {
		return super.getModel().getBoolean("_deleted");
	}
	
	public void setDeleted(boolean pDeleted) {
		if(pDeleted) {
			super.getModel().set("_deleted", pDeleted);
		} else {
			super.getModel().unset("_deleted");
		}
	}
}