package jp.aegif.oc.cmis_js_console;

import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.mozilla.javascript.NativeObject;

public class CmisRepository {

	private Session session;
	
	public CmisRepository(Session session) {
		this.session = session;
	}
	
	public CmisNode getRootFolder() {
		return new CmisNode(session.getRootFolder(), this.session);		
	}
	
	public String[] getAllTypes() {
         List<Tree<ObjectType>> descendants = 
        		 session.getTypeDescendants(null, -1, true);
         
         List<String> typeNames = new ArrayList<String>();
         for(Tree<ObjectType> tree : descendants) {
        	 ObjectType type = tree.getItem();
        	 typeNames.add(type.getDisplayName());
         }
         
         return typeNames.toArray(new String[0]);
	}
	
	//TODO implement later
	private TypeDefinition validateTypeDefinition(NativeObject definitionJson, 
			boolean isDocument, boolean isPrimary) {
		return null;
	}
	
	//TODO implement later
	public void addType(NativeObject definitionJson, boolean isDocument, boolean isPrimary) {
		System.out.println(definitionJson.getClass().toString());

	}
	
	//TODO implement
	public void deleteType(String typeName) {
		
	}
	
	public CmisNode[] searchSimple(String query, boolean searchAllVersions) {
		ItemIterable<QueryResult> results = this.session.query(query, searchAllVersions);
		
		List<CmisNode> resultNodes = new ArrayList<CmisNode>();
		
		for(QueryResult result : results) {
			String objectId = (String)result.getPropertyByQueryName("cmis:objectId").getFirstValue();
			CmisObject cmisObject = this.session.getObject(objectId);
			resultNodes.add(new CmisNode(cmisObject, this.session));
		}
		
		return resultNodes.toArray(new CmisNode[0]);
	}
	
}
