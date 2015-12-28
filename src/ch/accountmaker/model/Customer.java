package ch.accountmaker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

public class Customer extends DataSupport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2725418402525624061L;

	private int id;
	
	private String name;
	
	private String qq;
	
	private String email;
	
	private Date lastEditTime;
	
	private int isNew;
	
	private int objId;
	
	private String objectId;
	
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getObjId() {
		if(objId==0) {
			objId = id;
		}
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public Date getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(Date lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
	
	private List<Document> documentList = new ArrayList<Document>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Document> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<Document> documentList) {
		this.documentList = documentList;
	}
	
	
	
}
