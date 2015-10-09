package jp.aegif.oc.cmis_js_console;

import org.apache.chemistry.opencmis.client.api.Session;

public class CmisSession {
	
	private Session session;

	public CmisSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	
}
