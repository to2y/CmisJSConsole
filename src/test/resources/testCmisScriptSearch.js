var repo = cmis.login("admin","admin","http://localhost:8080/core/atom/bedroom", "bedroom");

nodes = repo.searchSimple("select * from cmis:document where cmis:name LIKE 'scriptTest%'", false);
//nodes = repo.searchSimple("select * from cmis:document", false);

out.println("---- search results ---");
for(var key in nodes) {
	out.println(nodes[key].name);
}