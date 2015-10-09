var repo = cmis.login("admin","admin","http://localhost:8080/core/atom/bedroom", "bedroom");

var rootFolder = repo.getRootFolder();
subfolder = rootFolder.children[0];
f2 = subfolder.createFolder("coci");

var file = cmis.getLocalFile("/Users/totanitakeshi/git/CmisJSConsole/README.md");
var doc1 = f2.createDocument("scriptTestDoc", file);

var pwc = doc1.checkout();

//show pwc's properties
props = pwc.properties;
out.println("pwc's props");
for(var key in props) {
	out.println("- " + key + ":" + props[key]);
}

//pwc.cancelCheckout();
props["cmis:description"] = "this is pwc";
var file2 = cmis.getLocalFile("/Users/totanitakeshi/git/CmisJSConsole/pom.xml");
pwc.updateContent(file2);
pwc.save();

//checkin
pwc.checkin(true,"major update!");

//get version related info
var versions = doc1.allVersions;
for(var key in versions) {
	out.println(versions[key].name);
	out.println(JSON.stringify(versions[key].versionInfo));
}

//f2.remove(true);