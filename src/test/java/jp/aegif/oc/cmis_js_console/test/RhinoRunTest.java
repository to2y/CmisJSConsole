package jp.aegif.oc.cmis_js_console.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import junit.framework.TestCase;

public class RhinoRunTest extends TestCase {

	public void testRhinoRun() {
		Context cx = Context.enter();
		
		Scriptable scope = cx.initStandardObjects();
		
		String s = "1+2;"
				+ "var tmp = 12;"
				+ ""
				+ "tmp";
		Object result = cx.evaluateString(scope, s, "cmd.log", 1, null);
		
		assertEquals("12", Context.toString(result));

	}
	
	public void testRhinoRunFromFile() {
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		BufferedReader in = this.getReader("/testScript1.js");
		
		Object result;
		try {
			result = cx.evaluateReader(scope, in, "testScript.js", 0, null);
			assertEquals("6", Context.toString(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			cx.exit();
		}
		
	}
	
	private BufferedReader getReader(String scriptFilePath) {
        return new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(scriptFilePath)));
	}
}
