package jp.aegif.oc.cmis_js_console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;

public class CmisSessionFactory {

	public CmisRepository login(String principal, String password, String url, String repositoryId) {

		Map<String, String> parameter = new HashMap<String, String>();

		// user credentials
		parameter.put(SessionParameter.USER, principal);
		parameter.put(SessionParameter.PASSWORD, password);

		// session locale
		parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
		parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "");

		// repository
		parameter.put(SessionParameter.REPOSITORY_ID, repositoryId);
		parameter. put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		String coreAtomUri = url;
		parameter.put(SessionParameter.ATOMPUB_URL, coreAtomUri);

		SessionFactory f = SessionFactoryImpl.newInstance();
		Session session = f.createSession(parameter);
		OperationContext operationContext = session.createOperationContext(null,
				true, true, false, IncludeRelationships.BOTH, null, false, null, true, 100);
		session.setDefaultContext(operationContext);

		CmisRepository repo = new CmisRepository(session);
		return repo;
	}

	public File getLocalFile(String path) {
		
		File f = new File(path);
		if ( !f.exists() ) {
			throw new RuntimeException("file does not exists.");
		}
		return f;
	}
}
