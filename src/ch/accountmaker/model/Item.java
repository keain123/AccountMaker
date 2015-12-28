package ch.accountmaker.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.litepal.crud.DataSupport;

import ch.accountmaker.model.bmob.ItemB;
import com.ch.chframe.utils.DateUtils;

public class Item extends DataSupport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4597638102006450868L;

	private int id;

	private String name;

	private int weight;

	private String unit;

	/**
	 * 单位为分
	 */
	private int price;

	private String lossRatio;

	private String material;

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

	public Item() {
	}

	public Item(ItemB item) throws ParseException {

		this.id = item.getId();
		this.lossRatio = item.getLossRatio();
		this.lastEditTime = DateUtils.getDateFormat19().parse(item.getLastEditTime().getDate());
		this.material = item.getMaterial();
		this.name = item.getName();
		this.price = item.getPrice();
		this.unit = item.getUnit();
		this.weight = item.getWeight();
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
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

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getLossRatio() {
		return lossRatio;
	}

	public void setLossRatio(String lossRatio) {
		this.lossRatio = lossRatio;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Date getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(Date lastEditTime) {
		this.lastEditTime = lastEditTime;
	}

}
