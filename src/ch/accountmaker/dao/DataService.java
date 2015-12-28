package ch.accountmaker.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.DuplicateFormatFlagsException;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.database.Cursor;
import android.provider.ContactsContract.Contacts.Data;
import ch.accountmaker.model.Customer;
import ch.accountmaker.model.Document;
import ch.accountmaker.model.DocumentItemDetail;
import ch.accountmaker.model.Item;
import ch.accountmaker.model.Material;
import ch.accountmaker.model.TimeStamp;
import ch.accountmaker.utils.StringUtils;

public class DataService {

	private static DataService instance;

	public static DataService getInstance() {
		if (null == instance) {
			synchronized (DataService.class) {
				if (null == instance) {
					instance = new DataService();
				}
			}
		}
		return instance;
	}

	public List<Item> queryAllItems() {
		return DataSupport.findAll(Item.class);
	}

	public List<Item> queryPageItems(int pageSize, int offset) {
		return DataSupport.limit(pageSize).order("id desc").offset(offset).find(Item.class);
	}

	public List<Document> queryAllDocuments(boolean isEager) {
		return DataSupport.findAll(Document.class, isEager);
	}

	public List<Document> queryPageDocuments(int pageSize, int offset) {
		// String sql = "select * from document d join customer c on
		// d.receiverId = c.objId limit = ?,?";
		// Cursor findBySQL = DataSupport.findBySQL(sql,
		// String.valueOf(pageSize), String.valueOf(offset));

		return DataSupport.limit(pageSize).offset(offset).order("id desc").find(Document.class, true);
	}

	public List<Customer> queryAllCustomers() {
		return DataSupport.findAll(Customer.class);
	}

	public List<Material> queryAllMaterials() {
		return DataSupport.findAll(Material.class);
	}

	public boolean isItemAlreadyExist(String name, String material, int oldId) {
		int count = DataSupport.where("name=? and material=? and id != ?", name, material, String.valueOf(oldId))
				.count(Item.class);
		if (count > 0) {
			return true;
		} else {
			return false;
		}

	}

	public Item findItemByNameAndMaterial(String name, String material) {
		List<Item> items = DataSupport.where("name = ? and material = ?", name, material).find(Item.class);
		if (items != null && items.size() > 0)
			return items.get(0);
		else
			return null;
	}

	public List<DocumentItemDetail> queryDetailByDocId(int id) {
		List<DocumentItemDetail> list = DataSupport.where("documentId = ?", String.valueOf(id))
				.find(DocumentItemDetail.class, true);
		return list;
	}

	public Customer findCustomerByObjId(int customerId) {
		List<Customer> list = DataSupport.where("objId = ?", String.valueOf(customerId)).find(Customer.class);
		if (list != null && list.size() >= 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<DocumentItemDetail> queryAllDetail() {
		return DataSupport.findAll(DocumentItemDetail.class, true);
	}

	public List<Item> findItemsByCondition(Item condition) {
		List<Item> list;
		String where;
		// List<String> params = new ArrayList<>();
		// if(condition!=null) {
		// String name = condition.getName();
		// String material = condition.getMaterial();
		// if(!StringUtils.isNullBlank(name)) {
		// where = "";
		// params.add(name);
		//
		// }
		// }
		// DataSupport.where(s).find(Item.class);
		return null;
	}

	public List<Customer> queryNewCustomers(Date time, boolean isNew) {
		int newFlag = isNew ? 1 : 0;
		List<Customer> cList = DataSupport
				.where("lastEditTime > ? and isNew = ? ", String.valueOf(time.getTime()), String.valueOf(newFlag))
				.find(Customer.class);
		return cList;
	}

	public List<Item> queryNewItems(Date time, boolean isNew) {
		int newFlag = isNew ? 1 : 0;
		List<Item> cList = DataSupport
				.where("lastEditTime > ? and isNew = ? ", String.valueOf(time.getTime()), String.valueOf(newFlag))
				.find(Item.class);
		return cList;
	}

	public String queryNewItemsIds(Date time, boolean isNew) {
		int newFlag = isNew ? 1 : 0;

		StringBuilder builder = new StringBuilder();
		String sql = "select id from item where lastEditTime > ? and isNew = ?";
		Cursor cursor = DataSupport.findBySQL(sql, String.valueOf(time.getTime()), String.valueOf(newFlag));
		if (cursor.moveToNext()) {
			do {
				builder.append(String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))));
				builder.append(",");
			} while (cursor.moveToNext());
		}
		cursor.close();
		if (builder.length() > 1) {
			builder.delete(builder.length() - 1, builder.length());
		}
		return builder.toString();

	}

	public List<Item> queryNewItemsWithoutObjId(Date time, boolean isNew) {
		int newFlag = isNew ? 1 : 0;
		String sql = "select * from item where lastEditTime > ? and isNew = ? and objectId is null";
		Cursor cursor = DataSupport.findBySQL(sql, String.valueOf(time.getTime()), String.valueOf(newFlag));
		List<Item> cList = getItemFromCursor(cursor);

		return cList;
	}

	public List<Item> queryNewItemsWithObjId(Date time, boolean isNew) {
		int newFlag = isNew ? 1 : 0;
		String sql = "select * from item where lastEditTime > ? and isNew = ? and objectId is not null";
		Cursor cursor = DataSupport.findBySQL(sql, String.valueOf(time.getTime()), String.valueOf(newFlag));
		List<Item> cList = getItemFromCursor(cursor);

		return cList;
	}

	private List<Item> getItemFromCursor(Cursor cursor) {
		List<Item> cList = new ArrayList<>();
		if (cursor.moveToNext()) {
			do {
				Item item = new Item();
				item.setId(cursor.getInt(cursor.getColumnIndex("id")));
				item.setIsNew(cursor.getInt(cursor.getColumnIndex("isnew")));
				item.setLastEditTime(new Date(cursor.getLong(cursor.getColumnIndex("lastedittime"))));
				item.setLossRatio(cursor.getString(cursor.getColumnIndex("lossratio")));
				item.setMaterial(cursor.getString(cursor.getColumnIndex("material")));
				item.setName(cursor.getString(cursor.getColumnIndex("name")));
				item.setPrice(cursor.getInt(cursor.getColumnIndex("price")));
				item.setUnit(cursor.getString(cursor.getColumnIndex("unit")));
				item.setWeight(cursor.getInt(cursor.getColumnIndex("weight")));
				cList.add(item);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return cList;
	}

	public List<Document> queryNewDocuments(Date time, boolean isNew) {
		int newFlag = isNew ? 1 : 0;
		List<Document> cList = DataSupport
				.where("lastEditTime > ? and isNew = ? ", String.valueOf(time.getTime()), String.valueOf(newFlag))
				.find(Document.class, true);
		return cList;
	}

	public List<DocumentItemDetail> queryNewDocumentItemDetails(Date time, boolean isNew) {
		int newFlag = isNew ? 1 : 0;
		List<DocumentItemDetail> cList = DataSupport
				.where("lastEditTime > ? and isNew = ? ", String.valueOf(time.getTime()), String.valueOf(newFlag))
				.find(DocumentItemDetail.class, true);
		return cList;
	}

	public List<Material> queryNewMaterials(Date time, boolean isNew) {
		int newFlag = isNew ? 1 : 0;
		List<Material> cList = DataSupport
				.where("lastEditTime > ? and isNew = ? ", String.valueOf(time.getTime()), String.valueOf(newFlag))
				.find(Material.class);
		return cList;
	}

	public TimeStamp getTimeStamp() {
		TimeStamp ts = DataSupport.find(TimeStamp.class, 1);
		if (ts != null) {
			return ts;
		} else {
			Date date = new Date(1990, 1, 1);
			ts.setLastDownloadTime(date);
			ts.setLastUploadTime(date);
			ts.save();
			return ts;
		}

	}

	public Item saveItem(Item item) {
		item.save();
		if (item.getObjId() == 0) {
			item.setObjId(item.getId());
			item.save();
		}
		return item;
	}

	public void saveItems(List<Item> l) {
		DataSupport.saveAll(l);
	}

	public Document saveDocument(Document doc) {
		if (doc.getReceiver() != null) {
			doc.setReceiverId(doc.getReceiver().getObjId());
		}
		doc.save();
		if (doc.getObjId() == 0) {
			doc.setObjId(doc.getId());
			doc.save();
		}
		return doc;
	}

	public void saveDocuments(List<Document> l) {
		DataSupport.saveAll(l);
	}

	public Material saveMaterial(Material m) {
		m.save();
		if (m.getObjId() == 0) {
			m.setObjId(m.getId());
			m.save();
		}
		return m;
	}

	public void saveMaterials(List<Material> l) {
		DataSupport.saveAll(l);
	}

	public Customer saveCustomer(Customer c) {
		c.save();
		if (c.getObjId() == 0) {
			c.setObjId(c.getId());
			c.save();
		}
		return c;
	}

	public void saveCustomers(List<Customer> l) {
		DataSupport.saveAll(l);
	}

	public DocumentItemDetail saveDetail(DocumentItemDetail d) {
		if (d.getDocument() != null) {
			d.setDocumentId(d.getDocument().getObjId());
		}
		if (d.getItem() != null) {
			d.setItemId(d.getItem().getObjId());
		}
		d.save();
		if (d.getObjId() == 0) {
			d.setObjId(d.getId());
			d.save();
		}

		return d;
	}

	public void saveDetails(List<DocumentItemDetail> l) {
		DataSupport.saveAll(l);
	}

	public Document findDocumentByObjId(int docId) {
		List<Document> list = DataSupport.where("objId = ?", String.valueOf(docId)).find(Document.class);
		if (list != null && list.size() >= 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public DocumentItemDetail findDocumentItemDetailById(int detailId) {
		return DataSupport.find(DocumentItemDetail.class, detailId);
	}

	public Item findItemByObjId(int itemId) {
		List<Item> list = DataSupport.where("objId = ?", String.valueOf(itemId)).find(Item.class);
		if (list != null && list.size() >= 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public Material findMaterialByObjId(int mId) {
		List<Material> list = DataSupport.where("objId = ?", String.valueOf(mId)).find(Material.class);
		if (list != null && list.size() >= 1) {
			return list.get(0);
		} else {
			return null;
		}

	}
	
	
	public List<Document> queryRecentDocumentList(int maxSize) {
		return DataSupport.limit(maxSize).order("lastEditTime desc").find(Document.class);
	}
}
