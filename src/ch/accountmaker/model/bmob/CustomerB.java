package ch.accountmaker.model.bmob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.accountmaker.model.Customer;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class CustomerB extends BmobObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2725418402525624061L;

	private Integer id;
	
	private String name;
	
	private String qq;
	
	private String email;
	
	private BmobDate lastEditTime;
	
	public CustomerB(ch.accountmaker.model.Customer c) {
		this.setTableName("Customer");
		this.id = c.getObjId();
		this.name = c.getName();
		this.qq = c.getQq();
		this.email = c.getEmail();
		this.setLastEditTime(new BmobDate(c.getLastEditTime()));
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

	public BmobDate getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(BmobDate lastEditTime) {
		this.lastEditTime = lastEditTime;
	}

}
