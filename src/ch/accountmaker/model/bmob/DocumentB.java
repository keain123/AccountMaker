package ch.accountmaker.model.bmob;

import java.io.Serializable;

import ch.accountmaker.model.Document;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class DocumentB extends BmobObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5503844881237119311L;

	private Integer id;

	private String title;

	private Integer customerId;

	private String timeText;

	private Integer beginYear;
	
	private Integer beginMonth;
	
	private Integer endYear;
	
	private Integer endMonth;

	private Boolean isPrinted;

	private Boolean isCompleted;
	
	private BmobDate lastEditTime;
	
	public DocumentB(Document d) {
		this.setTableName("Document");
		this.id = d.getObjId();
		this.beginMonth = d.getBeginMonth();
		this.beginYear = d.getBeginYear();
		this.customerId = d.getReceiver()!=null?d.getReceiver().getId():0;
		this.endMonth = d.getEndMonth();
		this.endYear = d.getEndYear();
		this.isCompleted = d.isCompleted();
		this.isPrinted = d.isPrinted();
		this.lastEditTime = new BmobDate(d.getLastEditTime());
		this.timeText = d.getTimeText();
		this.title = d.getTitle();
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getTimeText() {
		return timeText;
	}

	public void setTimeText(String timeText) {
		this.timeText = timeText;
	}

	public Integer getBeginYear() {
		return beginYear;
	}

	public void setBeginYear(Integer beginYear) {
		this.beginYear = beginYear;
	}

	public Integer getBeginMonth() {
		return beginMonth;
	}

	public void setBeginMonth(Integer beginMonth) {
		this.beginMonth = beginMonth;
	}

	public Integer getEndYear() {
		return endYear;
	}

	public void setEndYear(Integer endYear) {
		this.endYear = endYear;
	}

	public Integer getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(Integer endMonth) {
		this.endMonth = endMonth;
	}

	public Boolean getIsPrinted() {
		return isPrinted;
	}

	public void setIsPrinted(Boolean isPrinted) {
		this.isPrinted = isPrinted;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public BmobDate getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(BmobDate lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
	
	

}
