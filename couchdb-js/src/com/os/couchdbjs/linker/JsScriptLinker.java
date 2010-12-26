package com.os.couchdbjs.linker;

import java.util.Set;
import java.util.SortedSet;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.dev.About;
import com.google.gwt.dev.util.DefaultTextOutput;
import com.google.gwt.dev.util.Util;

/**
 * A Linker for producing a single JavaScript file from a GWT module. The use of
 * this Linker requires that the module has exactly one distinct compilation
 * result.
 */
@LinkerOrder(Order.PRIMARY)
public class JsScriptLinker extends AbstractLinker {
	private String m_jsType;
	private String m_designName = "dn";
	private String m_name = "vn";
	
	public String getDescription() {
		return "JS Single Script";
	}
	
  private String getCfgValue(ConfigurationProperty pProp) {
  	/**
  	List<String> vals = pProp.getValues();
  	if(vals == null || vals.isEmpty()) {
  		return null;
  	}
  	return vals.get(0);
  	*/
  	return pProp.getValue();
  }
  
  private void setupParams(LinkerContext pContext) {
  	SortedSet<ConfigurationProperty> props = pContext.getConfigurationProperties();
  	for(ConfigurationProperty prop : props) {
  		if("js_type".equals(prop.getName())) {
  			m_jsType = getCfgValue(prop);
  		} else if("design_name".equals(prop.getName())) {
 				m_designName = getCfgValue(prop);
  		} else if("name".equals(prop.getName())) {
 				m_name = getCfgValue(prop);
  		}
  	}
  }
  
  private String getPartialPath() {
  	if("map".equals(m_jsType)) {
  		return m_designName + "/views/" + m_name + "/map.js"; 
  	} else if("reduce".equals(m_jsType)) {
  		return m_designName + "/views/" + m_name + "/reduce.js"; 
  	} else if("show".equals(m_jsType)) {
  		return m_designName + "/shows/" + m_name + ".js"; 
  	} else if("list".equals(m_jsType)) {
  		return m_designName + "/lists/" + m_name + ".js"; 
  	} else if("filter".equals(m_jsType)) {
  		return m_designName + "/filters/" + m_name + ".js"; 
  	} else if("fulltext".equals(m_jsType)) {
  		return m_designName + "/fulltext/" + m_name + ".js"; 
  	} else if("update".equals(m_jsType)) {
  		return m_designName + "/update/" + m_name + ".js"; 
  	} else if("validate".equals(m_jsType)) {
  		return m_designName + "/validat_doc_update.js"; 
  	}
  	return null;
  }
	
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException {
		ArtifactSet toReturn = new ArtifactSet(artifacts);
		setupParams(context);
		toReturn.add(emitSelectionScript(logger, context, artifacts));
		return toReturn;
	}

	protected EmittedArtifact emitSelectionScript(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException {
		DefaultTextOutput out = new DefaultTextOutput(true);
		if("map".equals(m_jsType)) {
			out.print("function(doc) {");
		} else if("reduce".equals(m_jsType)) {
			out.print("function(keys,values) {");
		} else if("show".equals(m_jsType)) {
			out.print("function(doc,req) {");
		} else if("list".equals(m_jsType)) {
			out.print("function(head,req) {");
		} else if("filter".equals(m_jsType)) {
			out.print("function(doc,req) {");
		} else if("fulltext".equals(m_jsType)) {
			out.print("function(doc) {");
		} else if("validate".equals(m_jsType)) {
			out.print("function(newDoc,oldDoc,userCtx) {");
		} else if("update".equals(m_jsType)) {
			out.print("function(doc,req) {");
		}
		out.newlineOpt();
		out.print("var global_gwt = {};");
		out.newlineOpt();
		out.print("(function () {");
		out.newlineOpt();
		out.print("var $gwt_version = '" + About.GWT_VERSION_NUM + "';");
		out.newlineOpt();
		out.print("var $wnd = global_gwt;");
		out.newlineOpt();
		out.print("var $stats = null;");
		out.newlineOpt();
		// Find the single CompilationResult
		Set<CompilationResult> results = artifacts.find(CompilationResult.class);
		if (results.size() != 1) {
			logger = logger.branch(TreeLogger.ERROR, "The module must have exactly one distinct" + " permutation when using the " + getDescription() + " Linker.",
			    null);
			throw new UnableToCompleteException();
		}
		CompilationResult result = results.iterator().next();
		out.print(result.getJavaScript());
		out.newlineOpt();
		out.print("gwtOnLoad(null,null,null);");
		out.print("})();");
		out.newlineOpt();
		if("map".equals(m_jsType)) {
			out.print("global_gwt.map(doc);");
		} else if("reduce".equals(m_jsType)) {
			out.print("return global_gwt.reduce(keys,values);");
		} else if("show".equals(m_jsType)) {
			out.print("return global_gwt.show(doc,req);");
		} else if("list".equals(m_jsType)) {
			out.print("return global_gwt.list(head,req);");
		} else if("filter".equals(m_jsType)) {
			out.print("return global_gwt.filter(doc,req);");
		} else if("fulltext".equals(m_jsType)) {
			out.print("return global_gwt.fulltext(doc);");
		} else if("validate".equals(m_jsType)) {
			out.print("global_gwt.validate(newDoc,oldDoc,userCtx);");
		} else if("update".equals(m_jsType)) {
			out.print("return global_gwt.update(doc,req);");
		}
		out.newlineOpt();
		out.print("};");
		out.newlineOpt();
		//byte[] selectionScriptBytes = Util.getBytes(out.toString());
		String jsCode = context.optimizeJavaScript(logger, out.toString());
		jsCode = jsCode.replace('\n', ';');
		byte[] selectionScriptBytes = Util.getBytes(jsCode);
		return emitBytes(logger, selectionScriptBytes, getPartialPath());
	}
}
