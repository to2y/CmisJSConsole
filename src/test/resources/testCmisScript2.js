var repo = cmis.login("admin","admin","http://localhost:8080/core/atom/bedroom", "bedroom");

types = repo.allTypes;

out.println("---");
for(var key in types) {
	out.println(types[key]);
}

//make typeDef json
var typeDef = {};

typeDef.id = "dummyId";

repo.addType(typeDef, true, true);

out.println("----");