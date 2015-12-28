package ch.accountmaker.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.litepal.crud.DataSupport;

import com.ch.chframe.utils.DateUtils;

import android.util.SparseArray;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.bmob.DocumentItemDetailB;

public class DocumentItemDetail extends DataSupport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6726419716769305668L;

	private int id;

	public Document document;
	
	private int documentId;
	
	private int itemId;

	public Item item;

	private String countingText;

	private int totalNumber;

	private String allText;

	private String resultText;

	private Date lastEditTime;

	private int isNew;

	private int objId;

	private String objectId;
	
	
	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
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

	public DocumentItemDetail() {
	}

	public DocumentItemDetail(DocumentItemDetailB d, SparseArray<Document> docMap, SparseArray<Item> itemMap)
			throws ParseException {
		this.id = d.getId();
		this.allText = d.getAllText();
		this.countingText = d.getCountingText();
		this.document = docMap != null ? docMap.get(d.getDocumentId().intValue()) : null;
		this.item = itemMap != null ? itemMap.get(d.getItemId().intValue()) : null;
		this.lastEditTime = DateUtils.getDateFormat19().parse(d.getLastEditTime().getDate());
		this.resultText = d.getResultText();
		this.totalNumber = d.getTotalNumber();
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

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Document getDocument() {
		if(document==null) {
			document = DataService.getInstance().findDocumentByObjId(documentId);
		}
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Item getItem() {
		if(item == null) {
			item = DataService.getInstance().findItemByObjId(itemId);
		}
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getCountingText() {
		return countingText;
	}

	public void setCountingText(String countingText) {
		this.countingText = countingText;
	}

	public int getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}

	public String getAllText() {
		return allText;
	}

	public void setAllText(String allText) {
		this.allText = allText;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
