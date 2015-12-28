package ch.accountmaker.model.bmob;

import java.io.Serializable;

import ch.accountmaker.model.Item;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class ItemB extends BmobObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4132327859407751571L;

	private Integer id;
	
	private String name;
	
	private Integer weight;
	
	private String unit;
	
	/**
	 * 单位为分
	 */
	private Integer price;
	
	private String lossRatio;
	
	private String material;
	
	private BmobDate lastEditTime;
	
	public ItemB(Item item) {
		this.setTableName("Item");
		this.id = item.getObjId();
		this.lossRatio = item.getLossRatio();
		this.lastEditTime = new BmobDate(item.getLastEditTime());
		this.material = item.getMaterial();
		this.name = item.getName();
		this.price = item.getPrice();
		this.unit = item.getUnit();
		this.weight = item.getWeight();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getLossRatio() {
		return lossRatio;
	}

	public void setLossRatio(String lossRatio) {
		this.lossRatio = lossRatio;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public BmobDate getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(BmobDate lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
	
}
