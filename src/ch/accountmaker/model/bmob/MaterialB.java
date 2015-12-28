package ch.accountmaker.model.bmob;

import java.io.Serializable;

import ch.accountmaker.model.Material;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class MaterialB extends BmobObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -184875945625962667L;

	private Integer id;
	
	private String name;
	
	private Integer price;

	private BmobDate lastEditTime;
	
	public MaterialB(Material m) {
		this.setTableName("Material");
		this.id = m.getObjId();
		this.lastEditTime = new BmobDate(m.getLastEditTime());
		this.name = m.getName();
		this.price = m.getPrice();
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

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public BmobDate getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(BmobDate lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
	

	
}
