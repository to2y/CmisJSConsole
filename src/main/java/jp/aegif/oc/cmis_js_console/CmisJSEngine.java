package jp.aegif.oc.cmis_js_console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	public static void main(String[] args) throws Exception {
		//read commandLine argument file
		if ( args.length < 1) {
			throw new RuntimeException("please specify script file");
		}
		
		File file = new File(args[0]);
		
		if ( !file.exists() ) {
			throw new RuntimeException("script file does not exists");
		}
		
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader reader = new BufferedReader(isr); 
		
		//exec
		CmisJSEngine.eval(reader, args[0]); 
	}
		
}
