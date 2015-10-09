package jp.aegif.oc.cmis_js_console;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class CmisJSEngine {

	public static void eval(Reader reader, String scriptName) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();

		//cmis session factory
		CmisSessionFactory cmisSessionFactory = new CmisSessionFactory();
		Object sessionFactory = Context.javaToJS(cmisSessionFactory, scope);
		ScriptableObject.putProperty(scope, "cmis", sessionFactory);

		//System.out
		Object wrappedOut = Context.javaToJS(System.out, scope);
		ScriptableObject.putProperty(scope, "out", wrappedOut);

		try {
			cx.evaluateReader(scope, reader, scriptName, 0, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			Context.exit();
		}
	}
		
}
