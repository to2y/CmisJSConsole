package jp.aegif.oc.cmis_js_console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class CmisJSEngine {

	private Scriptable scope;
	private Context cx;

	public static CmisJSEngine newInstance() {
		return new CmisJSEngine();
	}

	private void prepareContext() {
		cx = Context.enter();
		scope = cx.initStandardObjects();

		//cmis session factory
		CmisSessionFactory cmisSessionFactory = new CmisSessionFactory();
		Object sessionFactory = Context.javaToJS(cmisSessionFactory, scope);
		ScriptableObject.putProperty(scope, "cmis", sessionFactory);

		//System.out
		Object wrappedOut = Context.javaToJS(System.out, scope);
		ScriptableObject.putProperty(scope, "out", wrappedOut);
	}

	public void eval(Reader reader, String scriptName) throws IllegalAccessException, InstantiationException, InvocationTargetException {

		this.prepareContext();

		this.evalInternal(reader, scriptName);
	}
	
	
	private void evalInternal(Reader reader, String scriptName) {
		try {
			cx.evaluateReader(scope, reader, scriptName, 0, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			Context.exit();
		}	
	}


	public void eval(String filePath, Map params) throws Exception {
		this.prepareContext();
		
		//inject parameter map

		Object paramsJS = Context.javaToJS(params, scope);
		ScriptableObject.putProperty(scope, "params", paramsJS);

		Reader reader = getReaderFromFilePath(filePath);
		this.evalInternal(reader, filePath);
	}

	public static Reader getReaderFromFilePath(String filePath) throws IOException {
		File file = new File(filePath);

		if ( !file.exists() ) {
			throw new RuntimeException("script file does not exists");
		}

		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader reader = new BufferedReader(isr);

		return reader;
	}

	public static void main(String[] args) throws Exception {
		//read commandLine argument file
		if ( args.length < 1) {
			throw new RuntimeException("please specify script file");
		}

		Reader reader = getReaderFromFilePath(args[0]);

		//exec
		CmisJSEngine.newInstance().eval(reader, args[0]); 
	}

}
