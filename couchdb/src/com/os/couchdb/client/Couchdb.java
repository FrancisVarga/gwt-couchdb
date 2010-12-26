package com.os.couchdb.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.blob.Blob;
import com.google.gwt.gears.client.desktop.Desktop;
import com.google.gwt.gears.client.desktop.File;
import com.google.gwt.gears.client.desktop.OpenFilesHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.os.couchdb.client.db.CouchDB;
import com.os.couchdb.client.db.FetchOptions;
import com.os.couchdb.client.db.LuceneFetchOptions;
import com.os.couchdb.client.db.ReplicationOptions;
import com.os.couchdb.client.document.AbstractViewResult;
import com.os.couchdb.client.document.BaseDocument;
import com.os.couchdb.client.document.CouchDBStatus;
import com.os.couchdb.client.document.DesignDocument;
import com.os.couchdb.client.document.LuceneStatusInfo;
import com.os.couchdb.client.document.LuceneViewResult;
import com.os.couchdb.client.document.ReplicationInfo;
import com.os.couchdb.client.document.RevisionSet;
import com.os.couchdb.client.document.StatsInfo;
import com.os.couchdb.client.document.UserInfo;
import com.os.couchdb.client.document.Attachment;
import com.os.couchdb.client.model.BaseModelFactory;
import com.os.couchdb.client.model.JSOModel;
import com.os.couchdb.share.MediaTypeUtil;

public class Couchdb implements EntryPoint {
	private CouchDB m_db;
	
	private ListBox m_couchDBOp;
	private Button m_testOp;
	private DialogBox m_dlgBox;
	private TextArea m_dlgBoxResult;
	private Label m_dlgOpLabel;
	
	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable pE) {
				Window.alert("ERROR " + pE.getMessage());
			}
		});
		
		m_db = new CouchDB();
		
		m_testOp = new Button("Test");
		m_couchDBOp = new ListBox();
		m_couchDBOp.setVisibleItemCount(1);
		m_couchDBOp.addItem("LIST Databases");
		m_couchDBOp.addItem("Create Database test");
		m_couchDBOp.addItem("DROP Database test");
		m_couchDBOp.addItem("GET UUIDS");
		m_couchDBOp.addItem("Replicate animals - animals_rep");
		m_couchDBOp.addItem("Authenticate");
		m_couchDBOp.addItem("IS Authenticated");
		m_couchDBOp.addItem("GET stats");
		m_couchDBOp.addItem("GET config");
		m_couchDBOp.addItem("GET DB animals status");
		m_couchDBOp.addItem("COMPACT DB animals");
		m_couchDBOp.addItem("GET DB _design/d1");
		m_couchDBOp.addItem("CREATE DB doc test");
		m_couchDBOp.addItem("UPDATE DB doc test");
		m_couchDBOp.addItem("GET DB doc test");
		m_couchDBOp.addItem("GET DB doc test with revs");
		m_couchDBOp.addItem("GET DB doc test with revs_infos");
		m_couchDBOp.addItem("GET DB doc test with conflicts");
		m_couchDBOp.addItem("GET DB doc test revisions");
		m_couchDBOp.addItem("DELETE DB doc test");
		m_couchDBOp.addItem("LIST docs");
		m_couchDBOp.addItem("LIST docs full");
		m_couchDBOp.addItem("Query view");
		m_couchDBOp.addItem("Update attachment");
		m_couchDBOp.addItem("Download attachment");
		m_couchDBOp.addItem("View attachment");
		m_couchDBOp.addItem("Lucene status");
		m_couchDBOp.addItem("Lucene query");
		m_couchDBOp.addItem("Lucene optimize");
		m_couchDBOp.addItem("Lucene expunge");
		m_couchDBOp.setSelectedIndex(0);
		
		// We can add style names to widgets
		m_testOp.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(m_couchDBOp);
		RootPanel.get("sendButtonContainer").add(m_testOp);

		// Focus the cursor on the name field when the app loads
		m_couchDBOp.setFocus(true);

		// Create the popup dialog box
		m_dlgBox = new DialogBox();
		m_dlgBox.setText("CouchDB Call");
		m_dlgBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		m_dlgOpLabel = new Label();
		m_dlgBoxResult = new TextArea();
		m_dlgBoxResult.setWidth("400px");
		m_dlgBoxResult.setHeight("300px");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending op to the server:</b>"));
		dialogVPanel.add(m_dlgOpLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(m_dlgBoxResult);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		m_dlgBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				m_dlgBox.hide();
				m_testOp.setEnabled(true);
				m_testOp.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				checkOp();
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void checkOp() {
				m_testOp.setEnabled(false);
				m_dlgBox.center();
				m_dlgOpLabel.setText(m_couchDBOp.getItemText(m_couchDBOp.getSelectedIndex()));
				switch(m_couchDBOp.getSelectedIndex()) {
				case 0:
					listDatabases();
					break;
				case 1:
					createDB();
					break;
				case 2:
					dropDB();
					break;
				case 3:
					getUUIDS();
					break;
				case 4:
					replicateDB();
					break;
				case 5:
					authenticate();
					break;
				case 6:
					isAuthenticated();
					break;
				case 7:
					getStats();
					break;
				case 8:
					getConfig();
					break;
				case 9:
					getDBStatus();
					break;
				case 10:
					compactDB();
					break;
				case 11:
					getDesignDoc();
					break;
				case 12:
					createdDoc();
					break;
				case 13:
					updateDoc();
					break;
				case 14:
					loadDoc();
					break;
				case 15:
					loadDocWithRevs();
					break;
				case 16:
					loadDocWithRevInfo();
					break;
				case 17:
					loadDocWithConlicts();
					break;
				case 18:
					loadDocRevisions();
					break;
				case 19:
					deleteDoc();
					break;
				case 20:
					listDocs();
					break;
				case 21:
					listFullDocs();
					break;
				case 22:
					queryView();
					break;
				case 23:
					updateAttachment();
					break;
				case 24:
					downloadAttachment();
					break;
				case 25:
					viewAttachment();
					break;
				case 26:
					luceneStatus();
					break;
				case 27:
					luceneQuery();
					break;
				case 28:
					luceneOptimize();
					break;
				case 29:
					luceneExpunge();
					break;
				}
			}
		}
		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		m_testOp.addClickHandler(handler);
	}
	
	protected void listDatabases() {
		m_db.getServer().listDatabases(new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(List<String> pResult) {
				StringBuffer sb = new StringBuffer();
				sb.append("Databases:\n");
				for(String s : pResult) {
					sb.append(s).append('\n');
				}
				m_dlgBoxResult.setText(sb.toString());
			}
		});
		
	}
	
	protected void createDB() {
		m_db.getServer().createDatabase("test",new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(Boolean pResult) {
				m_dlgBoxResult.setText(pResult.booleanValue() ? "Created DB test" : "DB not created!!!");
			}
		});
	}
	
	protected void dropDB() {
		m_db.getServer().deleteDatabase("test",new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(Boolean pResult) {
				m_dlgBoxResult.setText(pResult.booleanValue() ? "Deleted DB test" : "DB not deleted");
			}
		});
	}
	
	protected void getUUIDS() {
		m_db.getServer().getUUIDs(3,new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(List<String> pResult) {
				StringBuffer sb = new StringBuffer();
				sb.append("UUIDS:\n");
				for(String s : pResult) {
					sb.append(s).append('\n');
				}
				m_dlgBoxResult.setText(sb.toString());
			}
		});
	}

	protected void replicateDB() {
		ReplicationOptions ro = new ReplicationOptions();
		ro.setSource("animals");
		ro.setTarget("animals_rep");
		m_db.getServer().replicate(ro,new AsyncCallback<ReplicationInfo>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(ReplicationInfo pResult) {
				m_dlgBoxResult.setText(pResult.getModel().toJson());
			}
		});
	}

	protected void authenticate() {
		m_db.getServer().authenticate("viorel","mememe",new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(Boolean pResult) {
				m_dlgBoxResult.setText(pResult.booleanValue() ? "Authenticated" : "Not authenticated");
			}
		});
	}
	
	protected void isAuthenticated() {
		//m_db.getServer().setCredentials("viorel", "mememe");
		m_db.getServer().isAuthenticated(new AsyncCallback<UserInfo>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(UserInfo pResult) {
				m_dlgBoxResult.setText("UserInfo:" + pResult.getModel().toJson());
			}
		});
	}

	protected void getStats() {
		m_db.getServer().getStats(null,new AsyncCallback<StatsInfo>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(StatsInfo pResult) {
				m_dlgBoxResult.setText("Stats:" + pResult.toString());
			}
		});
	}
	
	protected void getConfig() {
		m_db.getServer().getConfig(new AsyncCallback<JSOModel>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(JSOModel pResult) {
				m_dlgBoxResult.setText("Config:" + pResult.toJson());
			}
		});
	}
	
	protected void getDBStatus() {
		m_db.getStatus(new AsyncCallback<CouchDBStatus>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(CouchDBStatus pResult) {
				m_dlgBoxResult.setText("Status:" + pResult.getModel().toJson());
			}
		});
	}
	
	protected void compactDB() {
		m_db.compact(new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(Boolean pResult) {
				m_dlgBoxResult.setText(pResult.booleanValue() ? "DB compacted" : "DB not compacted");
			}
		});
	}
	
	protected void getDesignDoc() {
		m_db.getDesignDocument("d1",new AsyncCallback<DesignDocument>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(DesignDocument pResult) {
				m_dlgBoxResult.setText("Design doc d1:" + pResult.getModel().toJson());
			}
		});
	}
	
	protected void createdDoc() {
		BaseDocument doc = new BaseDocument();
		doc.setId("test");
		doc.getModel().set("key1","1");
		doc.getModel().set("key2",2);
		m_db.createDocument(doc, new AsyncCallback<BaseDocument>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				m_dlgBoxResult.setText("Created doc:" + pResult.getModel().toJson());
			}
		});
	}

	protected void updateDoc() {
		m_db.getDocument(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<BaseDocument>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				Window.alert("Doc:" + pResult.getModel().toJson());
				pResult.getModel().set("key4","4-" + System.currentTimeMillis());
				m_db.updateDocument(pResult, new AsyncCallback<BaseDocument>() {
					@Override
					public void onFailure(Throwable pCaught) {
						m_dlgBoxResult.setText("Error:" + pCaught);
					}

					@Override
					public void onSuccess(BaseDocument pResult) {
						m_dlgBoxResult.setText("Updated doc:" + pResult.getModel().toJson());
					}
				});
			}
		});
	}
	
	protected void loadDoc() {
		m_db.getDocument(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<BaseDocument>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				m_dlgBoxResult.setText("Loaded doc:" + pResult.getModel().toJson());
			}
		});
	}
	
	protected void loadDocWithRevs() {
		m_db.getDocumentWithRevsList(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<BaseDocument>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				m_dlgBoxResult.setText("Loaded doc:" + pResult.getModel().toJson());
			}
		});
	}
	
	protected void loadDocWithRevInfo() {
		m_db.getDocumentWithRevsInfo(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<BaseDocument>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				m_dlgBoxResult.setText("Loaded doc:" + pResult.getModel().toJson());
			}
		});
	}
	
	protected void loadDocWithConlicts() {
		m_db.getDocumentWithConflictsInfo(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<BaseDocument>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				m_dlgBoxResult.setText("Loaded doc:" + pResult.getModel().toJson());
			}
		});
	}
	
	protected void loadDocRevisions() {
		m_db.getDocumentRevisions(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<RevisionSet<BaseDocument>>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(RevisionSet<BaseDocument> pResult) {
				m_dlgBoxResult.setText("Loaded doc:" + pResult);
			}
		});
	}
	
	protected void deleteDoc() {
		m_db.getDocument(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<BaseDocument>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				Window.alert("Doc:" + pResult.getModel().toJson());
				m_db.delete(pResult.getId(),pResult.getRevision(), new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable pCaught) {
						m_dlgBoxResult.setText("Error:" + pCaught);
					}

					@Override
					public void onSuccess(String pResult) {
						m_dlgBoxResult.setText("Deleted doc:" + pResult);
					}
				});
			}
		});
	}
	
	protected void listDocs() {
		m_db.listDocuments(new FetchOptions(),new AsyncCallback<AbstractViewResult>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(AbstractViewResult pResult) {
				m_dlgBoxResult.setText("Docs:" + pResult.getModel().toJson());
			}
		}); 
	}
	
	protected void listFullDocs() {
		FetchOptions fo = new FetchOptions();
		fo.includeDocs(true);
		m_db.listDocuments(fo,new AsyncCallback<AbstractViewResult>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(AbstractViewResult pResult) {
				m_dlgBoxResult.setText("Docs:" + pResult.getModel().toJson());
			}
		}); 
	}
	
	protected void queryView() {
		FetchOptions fo = new FetchOptions();
		fo.includeDocs(true);
		m_db.queryView("d1/v1",fo,new AsyncCallback<AbstractViewResult>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(AbstractViewResult pResult) {
				m_dlgBoxResult.setText("Docs:" + pResult.getModel().toJson());
			}
		}); 
	}

	private String getMainFileName(String pName) {
		int idx = pName.lastIndexOf('/');
		if(idx >= 0) {
			pName = pName.substring(idx+1);
		}
		//return pName.replace('.', '_');
		return pName;
	}
	
	protected void updateAttachment() {
		Desktop desktop = Factory.getInstance().createDesktop();
		desktop.openFiles(new OpenFilesHandler() {
			@Override
			public void onOpenFiles(OpenFilesEvent pEvent) {
			  final File[] files =	pEvent.getFiles();
			  if(files != null && files.length == 1) {
					m_db.getDocument(new BaseModelFactory<BaseDocument>() {
						@Override
						public BaseDocument createInstance(JSOModel pModel) {
							return new BaseDocument(pModel);
						}
					}, "test",new AsyncCallback<BaseDocument>() {

						@Override
						public void onFailure(Throwable pCaught) {
							m_dlgBoxResult.setText("Error:" + pCaught);
						}

						@Override
						public void onSuccess(BaseDocument pResult) {
							String rev = pResult.getRevision();
			        String mimeType = MediaTypeUtil.getMediaTypeForName(files[0].getName());
							m_db.createOrUpdateAttachment("test",rev,getMainFileName(files[0].getName()), mimeType, files[0].getBlob(),new AsyncCallback<String>() {
								@Override
								public void onFailure(Throwable pCaught) {
									m_dlgBoxResult.setText("Error:" + pCaught);
								}

								@Override
								public void onSuccess(String pResult) {
									m_dlgBoxResult.setText("OK Rev:" + pResult);
								}
							});
						}
					});
			  }
			}
		}, true);
	}
	
	protected void downloadAttachment() {
		m_db.getDocument(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<BaseDocument>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				Map<String,Attachment> at = pResult.getAttachments();
				if(!at.isEmpty()) {
					final String attName = at.keySet().iterator().next();
					m_db.getAttachment("test", attName, new AsyncCallback<Blob>() {
						@Override
						public void onSuccess(Blob pResult) {
							m_dlgBoxResult.setText("OK Blob len:" + pResult.getLength() + " for name " + attName);
						}
						
						@Override
						public void onFailure(Throwable pCaught) {
							m_dlgBoxResult.setText("Error:" + pCaught);
						}
					});
				}
			}
		});
	}
	
	protected void viewAttachment() {
		m_db.getDocument(new BaseModelFactory<BaseDocument>() {
			@Override
			public BaseDocument createInstance(JSOModel pModel) {
				return new BaseDocument(pModel);
			}
		}, "test",new AsyncCallback<BaseDocument>() {

			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(BaseDocument pResult) {
				Map<String,Attachment> at = pResult.getAttachments();
				if(!at.isEmpty()) {
					final String attName = at.keySet().iterator().next();
					Window.open(m_db.getAttachmentURL("test", attName),"xx","");
				}
			}
		});
	}
	
	protected void luceneStatus() {
		m_db.getLuceneStatusInfo("lucene/all",new AsyncCallback<LuceneStatusInfo>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(LuceneStatusInfo pResult) {
				m_dlgBoxResult.setText("Lucene status:" + pResult.getModel().toJson());
			}
		}); 
	}
	
	protected void luceneQuery() {
		LuceneFetchOptions fo = new LuceneFetchOptions();
		fo.setIncludeDocs(true);
		fo.setQuery("attachment:Guice");
		m_db.queryLucene(fo, "lucene/all",new AsyncCallback<LuceneViewResult>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(LuceneViewResult pResult) {
				m_dlgBoxResult.setText("Docs:" + pResult.getModel().toJson());
			}
		}); 
	}
	
	protected void luceneOptimize() {
		m_db.optimizeLucene("lucene/all",new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(Boolean pResult) {
				m_dlgBoxResult.setText("Optimize OK");
			}
		}); 
	}
	
	protected void luceneExpunge() {
		m_db.expungeLucene("lucene/all",new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable pCaught) {
				m_dlgBoxResult.setText("Error:" + pCaught);
			}

			@Override
			public void onSuccess(Boolean pResult) {
				m_dlgBoxResult.setText("Expunge OK");
			}
		}); 
	}
}
