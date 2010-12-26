package com.os.couchdb.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.os.couchdb.share.MediaTypeUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Response;
import org.jcouchdb.document.DesignDocument;
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
	
  public CouchDBLinker() throws NoSuchAlgorithmException {
  }

  @Override
  public String getDescription() {
    return "CouchDB App linker";
  }

  
  private String getCfgValue(ConfigurationProperty pProp) {
  	List<String> vals = pProp.getValues();
  	if(vals == null || vals.isEmpty()) {
  		return null;
  	}
  	return vals.get(0);
  	/**
  	return pProp.getValue();
  	*/
  }
  
  private void setupParams(LinkerContext pContext) {
  	SortedSet<ConfigurationProperty> props = pContext.getConfigurationProperties();
  	for(ConfigurationProperty prop : props) {
  		if("couchdb_name".equals(prop.getName())) {
  			String dbName = getCfgValue(prop);
  			if(dbName != null) {
  				m_dbName = dbName;
  			}
  		} else if("couchdb_host".equals(prop.getName())) {
  			String dbHost = getCfgValue(prop);
  			if(dbHost != null) {
  				m_host = dbHost;
  			}
  		} else if("couchdb_port".equals(prop.getName())) {
  			String dbPort = getCfgValue(prop);
  			if(dbPort != null) {
  				try {
  					m_port = Integer.parseInt(dbPort);
  				} catch(Exception ex) {
  				}
  			}
  		} else if("couchdb_admin".equals(prop.getName())) {
  			String dbAdmin = getCfgValue(prop);
  			if(dbAdmin != null) {
  				m_adminName = dbAdmin;
  			}
  		} else if("couchdb_pass".equals(prop.getName())) {
  			String dbPass = getCfgValue(prop);
  			if(dbPass != null) {
  				m_adminPswd = dbPass;
  			}
  		}
  	}
  }
  
  @Override
  public ArtifactSet link(TreeLogger pLogger, LinkerContext pContext,ArtifactSet pArtifacts) throws UnableToCompleteException {
    ArtifactSet toReturn = new ArtifactSet(pArtifacts);
    SortedSet<EmittedArtifact> emitted = toReturn.find(EmittedArtifact.class);
    setupParams(pContext);
    uploadApp(pLogger, pContext, emitted);
    return toReturn;
  }

  private void uploadApp(TreeLogger pLogger, LinkerContext pContext, SortedSet<EmittedArtifact> pArtifacts) throws UnableToCompleteException {
    pLogger = pLogger.branch(TreeLogger.DEBUG, "uploadApp", null);
    Database db = new Database(m_host,m_port,m_dbName);
    if(m_adminName != null) {
    	Map<String,String> pm = new HashMap<String, String>();
    	pm.put("name", m_adminName);
    	pm.put("password", m_adminPswd);
    	Response resp = db.getServer().post("/_session",pm);
    	if(resp.getCode() != 200) {
    		pLogger.log(TreeLogger.WARN, "Cannot authenticate :" + resp.getCode() + ":" + resp.getContentAsString());
    	}
    }
    DesignDocument dd = null;
    try {
    	dd = db.getDesignDocument(pContext.getModuleName());
    } catch(NotFoundException nex) {
    	dd = new DesignDocument(pContext.getModuleName());
    	db.createDocument(dd);
    }
    for (EmittedArtifact artifact : pArtifacts) {
      if (artifact.isPrivate()) {
        // These artifacts won't be in the module output directory
        continue;
      }
      String path = artifact.getPartialPath();
      InputStream in = artifact.getContents(pLogger);
      byte[] buffer = new byte[4096];
      int read;
      ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
      try {
        while ((read = in.read(buffer)) != -1) {
          baos.write(buffer, 0, read);
        }
        in.close();
        pLogger.log(TreeLogger.DEBUG, "load reasource = " + path);
        String pMimeType = MediaTypeUtil.getMediaTypeForName(path);
      	dd = db.getDesignDocument(pContext.getModuleName());
        db.createAttachment(dd.getId(), dd.getRevision(), path, pMimeType, baos.toByteArray());
      } catch (Exception e) {
        pLogger.log(TreeLogger.ERROR, "Unable to read/upload artifact " + artifact.getPartialPath(), e);
        throw new UnableToCompleteException();
      }
    }
  }
}