package ch.accountmaker.model.bmob;

import java.io.Serializable;

import ch.accountmaker.model.DocumentItemDetail;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class DocumentItemDetailB extends BmobObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8658077983037008926L;

	private Integer id;
	
	private Integer documentId;

	private Integer itemId;
	
	private String countingText;
	
	private Integer totalNumber;
	
	private String allText;
	
	private String resultText;
	
	private BmobDate lastEditTime;
	
	public DocumentItemDetailB(DocumentItemDetail d) {
		this.setTableName("DocumentItemDetail");
		this.id = d.getObjId();
		this.allText = d.getAllText();
		this.countingText = d.getCountingText();
		this.documentId = d.getDocumentId();
		this.itemId = d.getItemId();
		this.lastEditTime = new BmobDate(d.getLastEditTime());
		this.resultText = d.getResultText();
		this.totalNumber = d.getTotalNumber();
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getCountingText() {
		return countingText;
	}

	public void setCountingText(String countingText) {
		this.countingText = countingText;
	}

	public Integer getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}

	public String getAllText() {
		return allText;
	}

	public void setAllText(String allText) {
		this.allText = allText;
	}

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	public BmobDate getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(BmobDate lastEditTime) {
		this.lastEditTime = lastEditTime;
	}

	
	
}
