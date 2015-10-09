package jp.aegif.oc.cmis_js_console.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import jp.aegif.oc.cmis_js_console.CmisJSEngine;
import junit.framework.TestCase;

public class CmisEmbedTest extends TestCase {

	public void testRhinoRunFromFile() throws IllegalAccessException, InstantiationException, InvocationTargetException {
		
		BufferedReader in = this.getReader("/testCmisScript1.js");		
		CmisJSEngine.eval(in, "testCmisScript1.js");
		
	}
	
	public void testTypesRunFromFile() throws IllegalAccessException, InstantiationException, InvocationTargetException {
		
		BufferedReader in = this.getReader("/testCmisScript2.js");		
		CmisJSEngine.eval(in, "testCmisScript2.js");
		
	}
	
	public void testTCheckOutIn() throws IllegalAccessException, InstantiationException, InvocationTargetException {
		
		BufferedReader in = this.getReader("/testCmisScript3.js");		
		CmisJSEngine.eval(in, "testCmisScript3.js");
		
	}	
	
	private BufferedReader getReader(String scriptFilePath) {
        return new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(scriptFilePath)));
	}
}
