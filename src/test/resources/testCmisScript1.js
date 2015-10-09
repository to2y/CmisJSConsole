var repo = cmis.login("admin","admin","http://localhost:8080/core/atom/bedroom", "bedroom");

var rootFolder = repo.getRootFolder();

out.println(rootFolder.name);

out.println(rootFolder.children);

subfolder = rootFolder.children[0];

out.println(subfolder.name);


f2 = subfolder.createFolder("fromJS");
out.println(f2.name);


props = f2.properties;
out.println(props);
for(var key in props) {
	out.println("- " + key + ":" + props[key]);
}
out.println(props["cmis:objectId"]);
props["cmis:createdBy"] = "test";

for(var key in props) {
	out.println("- " + key + ":" + props[key]);
}

f2.properties["cmis:description"] = "f2 folder";

f2.save();

//f2.createDocument("scriptTestDoc");
var file = cmis.getLocalFile("/Users/totanitakeshi/git/CmisJSConsole/README.md");

var doc1 = f2.createDocument("scriptTestDoc", file);

var doc2 = f2.createDocument("scriptTestDoc2","this is content");

doc2.properties["cmis:description"] = "this is really doc2";
doc2.save();

var file2 = cmis.getLocalFile("/Users/totanitakeshi/git/CmisJSConsole/README.md");
doc2.updateContent(file2);

doc1.remove();

/*
 * out.println("----");
children = subfolder.children;
for(var key in children) {
	out.println(children[key].name)
	if ( children[key].name == "fromJS") {
		out.println("here");
		children[key].remove();
	}
}
*/

//f2.remove();

