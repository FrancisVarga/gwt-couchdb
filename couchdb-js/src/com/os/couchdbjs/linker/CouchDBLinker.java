package com.os.couchdbjs.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Response;
import org.jcouchdb.document.DesignDocument;
import org.jcouchdb.document.View;
import org.jcouchdb.exception.NotFoundException;

/**
 */
@LinkerOrder(Order.POST)
public final class CouchDBLinker extends AbstractLinker {
	private String m_host = "localhost";
	private int m_port = 5984;
	private String m_dbName = "xxx";
	private String m_adminName = null;
	private String m_adminPswd = null;

	private String m_designName = "dn";
	private String m_name = "vn";
	private String m_jsType = null;

	public CouchDBLinker() throws NoSuchAlgorithmException {
	}

	@Override
	public String getDescription() {
		return "CouchDB App linker";
	}

	private String getCfgValue(ConfigurationProperty pProp) {
		/**
		 * List<String> vals = pProp.getValues(); if(vals == null || vals.isEmpty())
		 * { return null; } return vals.get(0);
		 */
		return pProp.getValue();
	}

	private void setupParams(LinkerContext pContext) {
		SortedSet<ConfigurationProperty> props = pContext.getConfigurationProperties();
		for (ConfigurationProperty prop : props) {
			if ("couchdb_name".equals(prop.getName())) {
				String dbName = getCfgValue(prop);
				if (dbName != null) {
					m_dbName = dbName;
				}
			} else if ("couchdb_host".equals(prop.getName())) {
				String dbHost = getCfgValue(prop);
				if (dbHost != null) {
					m_host = dbHost;
				}
			} else if ("couchdb_port".equals(prop.getName())) {
				String dbPort = getCfgValue(prop);
				if (dbPort != null) {
					try {
						m_port = Integer.parseInt(dbPort);
					} catch (Exception ex) {
					}
				}
			} else if ("couchdb_admin".equals(prop.getName())) {
				String dbAdmin = getCfgValue(prop);
				if (dbAdmin != null) {
					m_adminName = dbAdmin;
				}
			} else if ("couchdb_pass".equals(prop.getName())) {
				String dbPass = getCfgValue(prop);
				if (dbPass != null) {
					m_adminPswd = dbPass;
				}
			} else if ("js_type".equals(prop.getName())) {
				m_jsType = getCfgValue(prop);
			} else if ("design_name".equals(prop.getName())) {
				m_designName = getCfgValue(prop);
			} else if ("name".equals(prop.getName())) {
				m_name = getCfgValue(prop);
			}
		}
	}

	@Override
	public ArtifactSet link(TreeLogger pLogger, LinkerContext pContext, ArtifactSet pArtifacts)
			throws UnableToCompleteException {
		ArtifactSet toReturn = new ArtifactSet(pArtifacts);
		SortedSet<EmittedArtifact> emitted = toReturn.find(EmittedArtifact.class);
		setupParams(pContext);
		uploadScript(pLogger, pContext, emitted);
		return toReturn;
	}

	private void uploadScript(TreeLogger pLogger, LinkerContext pContext, SortedSet<EmittedArtifact> pArtifacts)
			throws UnableToCompleteException {
		pLogger = pLogger.branch(TreeLogger.DEBUG, "uploadScript", null);
		Database db = new Database(m_host, m_port, m_dbName);
		if (m_adminName != null) {
			Map<String, String> pm = new HashMap<String, String>();
			pm.put("name", m_adminName);
			pm.put("password", m_adminPswd);
			Response resp = db.getServer().post("/_session", pm);
			if (resp.getCode() != 200) {
				pLogger.log(TreeLogger.WARN, "Cannot authenticate :" + resp.getCode() + ":" + resp.getContentAsString());
			}
		}
		DesignDocument dd = null;
		try {
			dd = db.getDesignDocument(m_designName);
		} catch (NotFoundException nex) {
			dd = new DesignDocument(m_designName);
			db.createDocument(dd);
		}
		for (EmittedArtifact artifact : pArtifacts) {
			if (artifact.isPrivate()) {
				// These artifacts won't be in the module output directory
				continue;
			}
			String path = artifact.getPartialPath();
			pLogger.log(TreeLogger.DEBUG, "artifact path : " + path, null);
			if (path.endsWith(".js")) {
				InputStream in = artifact.getContents(pLogger);
				byte[] buffer = new byte[4096];
				int read;
				ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
				try {
					while ((read = in.read(buffer)) != -1) {
						baos.write(buffer, 0, read);
					}
					in.close();
					String script = new String(baos.toByteArray());
					pLogger.log(TreeLogger.DEBUG, "upload resource = " + path + " as " + m_jsType);
					String pMimeType = "application/x-javascript";
					dd = db.getDesignDocument(m_designName);
					if ("map".equals(m_jsType)) {
						View view = dd.getView(m_name);
						if (view == null) {
							view = new View(script);
						} else {
							view.setMap(script);
						}
						dd.getViews().put(m_name, view);
						db.createOrUpdateDocument(dd);
					} else if ("reduce".equals(m_jsType)) {
						View view = dd.getView(m_name);
						if (view == null) {
							pLogger.log(Type.ERROR, "Cannot have reduce without map for " + m_name);
							throw new UnableToCompleteException();
						} else {
							view.setReduce(script);
						}
						dd.getViews().put(m_name, view);
						db.createOrUpdateDocument(dd);
					} else if ("show".equals(m_jsType)) {
						dd.addShowFunction(m_name, script);
						db.createOrUpdateDocument(dd);
					} else if ("list".equals(m_jsType)) {
						dd.addListFunction(m_name, script);
						db.createOrUpdateDocument(dd);
					} else if ("filter".equals(m_jsType)) {
						Map<String,String> ft = (Map<String,String>)dd.getProperty("filters");
						if(ft == null) {
							ft = new HashMap<String, String>();
							dd.setProperty("filters", ft);
						}
						ft.put(m_name, script);
						db.createOrUpdateDocument(dd);
					} else if ("validate".equals(m_jsType)) {
						dd.setValidateOnDocUpdate(script);
						db.createOrUpdateDocument(dd);
					} else if ("fulltext".equals(m_jsType)) {
						Map<String,Map<String,String>> ft = (Map<String,Map<String,String>>)dd.getProperty("fulltext");
						if(ft == null) {
							ft = new HashMap<String, Map<String,String>>();
							dd.setProperty("fulltext", ft);
						}
						Map<String,String> sm = ft.get(m_name);
						if(sm == null) {
							sm = new HashMap<String, String>();
							ft.put(m_name, sm);
						}
						sm.put("index",script);
						db.createOrUpdateDocument(dd);
					} else if ("update".equals(m_jsType)) {
						Map<String,String> ft = (Map<String,String>)dd.getProperty("updates");
						if(ft == null) {
							ft = new HashMap<String, String>();
							dd.setProperty("updates", ft);
						}
						ft.put(m_name, script);
						db.createOrUpdateDocument(dd);
					}
				} catch (Exception e) {
					pLogger.log(TreeLogger.ERROR, "Unable to read/upload artifact " + artifact.getPartialPath(), e);
					throw new UnableToCompleteException();
				}
			}
		}
	}
}