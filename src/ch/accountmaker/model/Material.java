package ch.accountmaker.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.litepal.crud.DataSupport;

import com.ch.chframe.utils.DateUtils;

import ch.accountmaker.model.bmob.MaterialB;
import cn.bmob.v3.datatype.BmobDate;

public class Material extends DataSupport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6744556260668392471L;

	private int id;

	private String name;

	private int price;

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

	public Material() {
	}

	public Material(MaterialB m) throws ParseException {
		this.id = m.getId();
		this.lastEditTime = DateUtils.getDateFormat19().parse(m.getLastEditTime().getDate());
		this.name = m.getName();
		this.price = m.getPrice();
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

}
