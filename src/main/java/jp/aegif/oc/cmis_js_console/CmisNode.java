package jp.aegif.oc.cmis_js_console;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;


public class CmisNode {

	private CmisObject cmisObject;
	private Session session;
	private NativeObject propsObj;
	private NativeArray aclObj;

	public CmisNode() {}

	public CmisNode(CmisObject cmisObject, Session session) {
		this.cmisObject = cmisObject;
		this.session = session;
	}

	public String getName() {
		this.getProperties();
		return (String)this.propsObj.get("cmis:name", propsObj);
	}

	public void setName(String name) {
		this.getProperties();
		this.propsObj.put("cmis:name", propsObj, name);
	}

	public CmisNode[] getChildren() {

		if ( this.cmisObject instanceof Folder) {
			ItemIterable<CmisObject> children = ((Folder)this.cmisObject).getChildren();
			ArrayList<CmisNode> childrenList = new ArrayList<CmisNode>();
			for(CmisObject child : children) {
				childrenList.add(new CmisNode(child, this.session));
			}
			return childrenList.toArray(new CmisNode[0]);
		}
		else {
			throw new RuntimeException("this object is not folder");
		}
	}

	public boolean isDocument() {
		return ( this.cmisObject instanceof Document);		
	}

	public boolean isFolder() {
		return ( this.cmisObject instanceof Folder);
	}

	private Folder assertAndCastToFolder() {
		if ( !isFolder()) {
			throw new RuntimeException("cannot create folder");
		}
		return (Folder)this.cmisObject;
	}

	private Document assetAndCastToDocument() {
		if ( !isDocument() ) {
			throw new RuntimeException("cannot create folder");
		}
		return (Document)this.cmisObject;
	}

	public CmisNode createFolder(String name) {
		Folder f = this.assertAndCastToFolder();

		Map<String, String> params = new HashMap<String, String>();
		params.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		params.put(PropertyIds.NAME, name);

		Folder folder = f.createFolder(params);
		return new CmisNode(folder,this.session);
	}

	public CmisNode createDocument(String name, File file) {
		Folder f = this.assertAndCastToFolder();

		Map<String, String> params = new HashMap<String, String>();
		params.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		params.put(PropertyIds.NAME, name);

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
			ContentStream contentStream = new ContentStreamImpl(name,
					BigInteger.valueOf(file.length()), "application/octet-stream", fis);

			Document doc = f.createDocument(params, contentStream, VersioningState.MAJOR);
			return new CmisNode(doc, this.session);	
		} catch (FileNotFoundException e) {
			throw new RuntimeException("file does not exists.");
		}
		finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public CmisNode createDocument(String name, String content) {
		Folder f = this.assertAndCastToFolder();

		Map<String, String> params = new HashMap<String, String>();
		params.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		params.put(PropertyIds.NAME, name);

		ByteArrayInputStream bais = null;

		try {
			byte[] buf = content.getBytes("UTF-8");
			bais = new ByteArrayInputStream(buf);

			ContentStream contentStream = new ContentStreamImpl(name,
					BigInteger.valueOf(buf.length), "application/octet-stream", bais);

			Document doc = f.createDocument(params, contentStream, VersioningState.MAJOR);

			return new CmisNode(doc, this.session);	
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateContent(File file) {
		Document doc = this.assetAndCastToDocument();
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);

			ContentStream contentStream = new ContentStreamImpl(doc.getName(),
					BigInteger.valueOf(file.length()), "application/octet-stream", fis);

			doc.setContentStream(contentStream, true);

		} catch (FileNotFoundException e) {
			throw new RuntimeException("file not found");
		}
	}

	public void remove() {
		this.cmisObject.delete();
	}

	public void remove(boolean cascade) {
		if ( !cascade ) {
			this.remove();
		}
		else {
			Folder f = this.assertAndCastToFolder();
			f.deleteTree(true, null, false);
		}
	}

	private void refreshProperties() {
		List<Property<?>> props = this.cmisObject.getProperties();
		this.propsObj = new NativeObject();

		for(Property<?> p : props) {
			propsObj.put(p.getDefinition().getDisplayName(), 
					propsObj, p.getValueAsString());
		}	
	}
	
	public NativeObject getProperties() {

		if ( this.propsObj == null) {
			this.refreshProperties();
		}

		return this.propsObj;
	}

	private Map<String, Object> makeUpdateProperties(){
		List<Property<?>> props = this.cmisObject.getProperties();

		Map<String, Object> updateProperties = new HashMap<String, Object>();

		//update props with Updatability is READWRITE
		//TODO: consider value type, currently assume String only
		for(Property<?> p : props) {
			if ( p.getDefinition().getUpdatability() == Updatability.READWRITE) {
				String id = p.getDefinition().getDisplayName();

				String originalValue = p.getValueAsString();
				String targetValue = (String)this.propsObj.get((String)id, null);

				if ( (originalValue != null && !originalValue.equals(targetValue)) ||
						(originalValue == null && targetValue != null)
						) {
					updateProperties.put(id, targetValue);
				}
			}
		}
		return updateProperties;
	}
	
	public void save() {

		Map<String, Object> updateProperties = this.makeUpdateProperties();

		if ( updateProperties.size() > 0 ) {
			this.cmisObject.updateProperties(updateProperties);
			this.refreshProperties();
		}
	}

	//Version and checkin/checkout 
	public boolean isVersionable() {
		Document doc = this.assetAndCastToDocument();
		return ((DocumentType)doc.getType()).isVersionable();
	}

	public CmisNode checkout() {
		Document doc = this.assetAndCastToDocument();
		ObjectId id = doc.checkOut();
		Document pwc = (Document)this.session.getObject(id);
		return new CmisNode(pwc, this.session);
	}

	public boolean isPwc() {
		Document doc = this.assetAndCastToDocument();
		return doc.isPrivateWorkingCopy();
	}

	public void checkin(boolean isMajor, String comment) {

		Document doc = this.assetAndCastToDocument();
		if ( !this.isPwc()) {
			throw new RuntimeException("this is not PWC");
		}

		//construct props from pwc's cache? contentStream? 
		Map<String, Object> updateProperties = this.makeUpdateProperties();
		ContentStream stream = doc.getContentStream();
		
		doc.checkIn(isMajor, updateProperties, stream, comment);		
	}

	public void cancelCheckout() {
		Document doc = this.assetAndCastToDocument();
		if ( !this.isPwc()) {
			throw new RuntimeException("this is not PWC");
		}
		doc.cancelCheckOut();
	}
	
	public CmisNode[] getAllVersions() {
		Document doc = this.assetAndCastToDocument();
		List<Document> versions = doc.getAllVersions();
		List<CmisNode> retVersions = new ArrayList<CmisNode>();
		for(Document versionedDoc: versions ) {
			retVersions.add(new CmisNode(versionedDoc, this.session));
		}
		return retVersions.toArray(new CmisNode[0]);
	}
	
	public NativeObject getVersionInfo() {
		Document doc = this.assetAndCastToDocument();
		NativeObject versionInfo = new NativeObject();
		
		versionInfo.put("label", versionInfo, doc.getVersionLabel());
		versionInfo.put("isMajorVersion", versionInfo, doc.isMajorVersion());
		versionInfo.put("isLatestMajorVersion", versionInfo, doc.isLatestMajorVersion());
		versionInfo.put("sersionSeriesId", versionInfo, doc.getVersionSeriesId());
		versionInfo.put("checkinComment", versionInfo, doc.getCheckinComment());
		
		return versionInfo;
	}
	
	private void getPermissionsInternal() {
		Acl acl = this.cmisObject.getAcl();
		List<Ace> aces = acl.getAces();
		
		this.aclObj = new NativeArray(aces.size());
		for(int j = 0 ; j < aces.size() ; j++ ) {
			Ace ace = aces.get(j);
			String principal = ace.getPrincipalId();
			List<String> permissions = ace.getPermissions();
			NativeArray permArray = new NativeArray(permissions.size());
			for(int i = 0 ; i < permissions.size() ; i++ ) {
				permArray.put(i, permArray, permissions.get(i));
			}
			
			NativeObject aceObj = new NativeObject();
			aceObj.put("principal", aceObj, principal);
			aceObj.put("permissions", aceObj, permArray);
			
			aclObj.put(j, aclObj, aceObj);
		}
	}
	
	public NativeArray getPermissions() {
		if ( this.aclObj == null) {
			this.getPermissionsInternal();
		}
		return this.aclObj;
	}
	
	//TODO implement
	public void addPermission(String principal, String permission) {
		
	}
	
	public void removePermission(String principal, String permission) {
		
	}
}
