package ch.accountmaker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import ch.accountmaker.dao.DataService;

public class Document extends DataSupport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4916798088191702919L;

	private int id;

	private String title;

	public Customer receiver;
	
	private int receiverId;


	private String timeText;

	private int beginYear;
	
	private int beginMonth;
	
	
	private int endYear;
	
	private int endMonth;

	private boolean isPrinted;

	private boolean isCompleted;
	
	private Date lastEditTime;
	
	private int isNew;
	
	private int objId;
	
	private String objectId;
	
	public int getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	
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

	public List<DocumentItemDetail> documentDetailList = new ArrayList<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Customer getReceiver() {
		if(receiver==null) {
			receiver = DataService.getInstance().findCustomerByObjId(receiverId);
		}
		return receiver;
	}

	public void setReceiver(Customer receiver) {
		if(receiver!=null) {
			setReceiverId(receiver.getObjId());
		}
		this.receiver = receiver;
	}

	public int getBeginYear() {
		return beginYear;
	}

	public void setBeginYear(int beginYear) {
		this.beginYear = beginYear;
	}

	public int getBeginMonth() {
		return beginMonth;
	}

	public void setBeginMonth(int beginMonth) {
		this.beginMonth = beginMonth;
	}


	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public boolean isPrinted() {
		return isPrinted;
	}

	public void setPrinted(boolean isPrinted) {
		this.isPrinted = isPrinted;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public List<DocumentItemDetail> getDocumentDetailList() {
		if (documentDetailList == null || documentDetailList.size() == 0)
			documentDetailList = DataService.getInstance().queryDetailByDocId(objId);
		return documentDetailList;
	}

	public void setDocumentDetailList(List<DocumentItemDetail> documentDetailList) {
		this.documentDetailList = documentDetailList;
	}

	public String getTimeText() {
		return timeText;
	}

	public void setTimeText(String timeText) {
		this.timeText = timeText;
	}
}
