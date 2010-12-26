package com.os.couchdb.client.document;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.os.couchdb.client.model.BaseModel;
import com.os.couchdb.client.model.JSOModel;

public class StatsInfo {
	public static final Map<String,String[]> __KNOWN_KEYS;
	static {
		__KNOWN_KEYS = new HashMap<String, String[]>();
		__KNOWN_KEYS.put("httpd_request_methods", new String[] {
				"MOVE","GET","POST","PUT","DELETE","HEAD","COPY"
		});
		__KNOWN_KEYS.put("couchdb", new String[] {
				"open_databases","open_os_files","database_writes","database_reads","request_time"
		});
		__KNOWN_KEYS.put("httpd_status_codes", new String[] {
				"409","412","202","404","304","200","201","500","301","403","401","405","400"
		});
		__KNOWN_KEYS.put("httpd", new String[] {
				"clients_requesting_changes","requests","view_reads","temporary_view_reads","bulk_requests"
		});
	}
	
	public static class StatsInfoRecord extends BaseModel {
		private StatsInfoRecord(JSOModel pModel) {
			super(pModel);
		}
		
		public String getDescription() {
			return getModel().get("description");
		}

		public double getCurrent() {
			return getModel().getDouble("current");
		}

		public double getSum() {
			return getModel().getDouble("sum");
		}

		public double getMean() {
			return getModel().getDouble("mean");
		}

		public double getStdDev() {
			return getModel().getDouble("stddev");
		}

		public double getMin() {
			return getModel().getDouble("min");
		}

		public double getMax() {
			return getModel().getDouble("max");
		}
	}
	
	private Map<String, Map<String,StatsInfoRecord>> m_data;
	
	public StatsInfo(JSOModel pModel) {
		m_data = new HashMap<String, Map<String,StatsInfoRecord>>();
		JsArrayString keys = pModel.keys();
		for(int i=0;i < keys.length();i++) {
			JSOModel model = pModel.getObject(keys.get(i));
			Map<String,StatsInfoRecord> m = new HashMap<String, StatsInfoRecord>();
			JsArrayString ka = model.keys();
			for(int j=0;j < ka.length();j++) {
				m.put(ka.get(j),new StatsInfoRecord(model.getObject(ka.get(j))));
			}
			m_data.put(keys.get(i),m);
		}
	}
	
	public Map<String,StatsInfoRecord> getData(String pKey) {
		return m_data.get(pKey);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<String, Map<String,StatsInfoRecord>> e: m_data.entrySet()) {
			for(Map.Entry<String, StatsInfoRecord> e1 : e.getValue().entrySet()) {
				sb.append(e.getKey()).append('/').append(e1.getKey()).
				append(':').append(e1.getValue().getModel().toJson()).append('\n');
			}
		}
		return sb.toString();
	}
}
