package ch.accountmaker.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import com.ch.chframe.utils.DateUtils;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseArray;
import ch.accountmaker.R;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.Customer;
import ch.accountmaker.model.Document;
import ch.accountmaker.model.DocumentItemDetail;
import ch.accountmaker.model.Item;
import ch.accountmaker.model.Material;
import ch.accountmaker.model.TimeStamp;
import ch.accountmaker.model.bmob.CustomerB;
import ch.accountmaker.model.bmob.DocumentB;
import ch.accountmaker.model.bmob.DocumentItemDetailB;
import ch.accountmaker.model.bmob.ItemB;
import ch.accountmaker.model.bmob.MaterialB;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SynchronizeInstance {
	
	private static SynchronizeInstance mInstance;
	
	private SynchronizeInstance() {
	}
	
	public static SynchronizeInstance getInstance() {
		if(mInstance == null) {
			synchronized(SynchronizeInstance.class) {
				if(mInstance == null) {
					mInstance = new SynchronizeInstance();
				}
			}
		}
		return mInstance;
	}
	
	// TODO 上传完毕后下载一次全列表，获取objectId
	

	public static void synchronize(Context context) {
		uploadNewData(context);
		updateOldData(context);
	}

	public static void downloadServerData(final Activity activity, FragmentManager fragmentManager) {

		int total = DataSupport.count(Item.class) + DataSupport.count(Document.class)
				+ DataSupport.count(Customer.class) + DataSupport.count(DocumentItemDetail.class)
				+ DataSupport.count(Material.class);
		if (total > 0) {
			SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
				public void onPositiveActionClicked(com.rey.material.app.DialogFragment fragment) {
					new DownloadAsyncTask().execute(activity);
					super.onPositiveActionClicked(fragment);

				};

				@Override
				public void onNegativeActionClicked(DialogFragment fragment) {
					super.onNegativeActionClicked(fragment);
					return;
				}
			};
			builder.message("本机有数据，是否删除本机所有数据\n并下载网络数据？").title("下载全部数据").positiveAction("是").negativeAction("取消");
			DialogFragment fragment = DialogFragment.newInstance(builder);
			fragment.show(fragmentManager, null);
		} else {
			// TODO 直接下载
			new DownloadAsyncTask().execute(activity);
		}

	}

	/**
	 * 上传服务器上没有的信息
	 * 
	 * @param context
	 */
	public static void uploadNewData(final Context context) {
		int uploadTimes = 0;//每次50条，要分n次上传，此变量统计上传次数
		Set<String> set = Collections.synchronizedSet(new HashSet<String>());

		TimeStamp timeStamp = DataService.getInstance().getTimeStamp();
		long time = timeStamp.getLastUploadTime().getTime();
		time = time - 100000;

		// Item
		final List<ch.accountmaker.model.Item> itemList = DataService.getInstance().queryNewItems(new Date(time), true);
		final List<BmobObject> bItemList = new ArrayList<>();
		ItemB newItem;
		for (ch.accountmaker.model.Item item : itemList) {// 转为bmob的数据类型
			newItem = new ItemB(item);
			bItemList.add(newItem);
			item.setIsNew(0);
		}
		uploadTimes += saveNewObject(context, bItemList, itemList);

		// Customer
		final List<Customer> customerList = DataService.getInstance().queryNewCustomers(new Date(time), true);
		final List<BmobObject> bCustomerList = new ArrayList<>();
		CustomerB newCustomer;
		for (Customer c : customerList) {
			newCustomer = new CustomerB(c);
			bCustomerList.add(newCustomer);
			c.setIsNew(0);
		}
		uploadTimes += saveNewObject(context, bCustomerList, customerList);

		// Document
		final List<ch.accountmaker.model.Document> docList = DataService.getInstance().queryNewDocuments(new Date(time),
				true);
		final List<BmobObject> bDocList = new ArrayList<>();
		DocumentB newdoc;
		for (ch.accountmaker.model.Document c : docList) {
			newdoc = new DocumentB(c);
			bDocList.add(newdoc);
			c.setIsNew(0);
		}
		uploadTimes += saveNewObject(context, bDocList, docList);

		// DocumentItemDetail
		final List<ch.accountmaker.model.DocumentItemDetail> detailList = DataService.getInstance()
				.queryNewDocumentItemDetails(new Date(time), true);
		final List<BmobObject> bDetailList = new ArrayList<>();
		DocumentItemDetailB newDetail;
		for (ch.accountmaker.model.DocumentItemDetail c : detailList) {
			newDetail = new DocumentItemDetailB(c);
			bDetailList.add(newDetail);
			c.setIsNew(0);
		}
		uploadTimes += saveNewObject(context, bDetailList, detailList);

		// Material
		final List<ch.accountmaker.model.Material> materialList = DataService.getInstance()
				.queryNewMaterials(new Date(time), true);
		final List<BmobObject> bMaterialList = new ArrayList<>();
		MaterialB newMaterial;
		for (ch.accountmaker.model.Material c : materialList) {
			newMaterial = new MaterialB(c);
			bMaterialList.add(newMaterial);
			c.setIsNew(0);
		}
		uploadTimes += saveNewObject(context, bMaterialList, materialList);

	}

	/**
	 * 分为50条一个list
	 * 
	 * @param bmobObjectList
	 * @return
	 */
	private static int saveNewObject(Context context, List<BmobObject> bmobObjectList,
			List<? extends DataSupport> originalList) {
		List<List<BmobObject>> updateList = new ArrayList<>();
		List<List<? extends DataSupport>> updateOriginalList = new ArrayList<>();
		int count = bmobObjectList.size();
		int alreadyCount = 0;
		if (count > 50) {// 先分为每50条一次，生成一个list
			int times = count / 50;
			for (int i = 0; i < times; i++) {

				List<BmobObject> subList = bmobObjectList.subList(i * 50, i * 50 + 50);
				List<? extends DataSupport> subOriginalList = originalList.subList(i * 50, i * 50 + 50);
				alreadyCount += 50;
				updateList.add(subList);
				updateOriginalList.add(subOriginalList);
			}
		}
		// 若还有剩下的，作为一个list
		if (count > 0) {
			List<BmobObject> subList = bmobObjectList.subList(alreadyCount, count);
			List<? extends DataSupport> subOriginalList = originalList.subList(alreadyCount, count);
			updateList.add(subList);
			updateOriginalList.add(subOriginalList);
		}

		for (int i = 0; i < updateList.size(); i++) {
			final List<BmobObject> l = updateList.get(i);
			final List<? extends DataSupport> ol = updateOriginalList.get(i);
			new BmobObject().insertBatch(context, l, new SaveListener() {

				@Override
				public void onSuccess() {
					Log.e("item", "上传成功,总共有" + l.size() + "条");
					DataSupport.saveAll(ol);
					// TODO 再次查询并且回填objectId，只有这个方法
					
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					Log.e("item", "上传失败," + arg1);
				}

			});

		}

		return updateList.size();
	}

	private static List<List<BmobObject>> updateObject(Context context, List<BmobObject> bmobObjectList) {
		List<List<BmobObject>> updateList = new ArrayList<>();
		int count = bmobObjectList.size();
		int alreadyCount = 0;
		if (count > 50) {// 先分为每50条一次，生成一个list
			int times = count / 50;
			for (int i = 0; i < times; i++) {

				List<BmobObject> subList = bmobObjectList.subList(i * 50, i * 50 + 50);
				alreadyCount += 50;
				updateList.add(subList);
			}
		}
		// 若还有剩下的，作为一个list
		if (count > 0) {
			List<BmobObject> subList = bmobObjectList.subList(alreadyCount, count);
			updateList.add(subList);
		}

		for (int i = 0; i < updateList.size(); i++) {
			final List<BmobObject> l = updateList.get(i);
			new BmobObject().updateBatch(context, l, new UpdateListener() {

				@Override
				public void onSuccess() {
					Log.e("item", "更新成功,总共有" + l.size() + "条");

				}

				@Override
				public void onFailure(int arg0, String arg1) {
					Log.e("item", "更新失败," + arg1);

				}
			});
		}

		return updateList;
	}

	/**
	 * 更新服务器上已有条目
	 * 
	 * @param context
	 */
	public static void updateOldData(final Context context) {
		TimeStamp timeStamp = DataService.getInstance().getTimeStamp();
		final List<Item> itemList = DataService.getInstance().queryNewItems(timeStamp.getLastUploadTime(), false);
		List<String> ids = new ArrayList<>();
		for (Item c : itemList) {
			ids.add(String.valueOf(c.getId()));
		}
		String idstring = ids.toString().replace("[", "").replace("]", "");
		String bql = "select * from Item where id in (" + idstring + ")";

		BmobQuery<ItemB> query = new BmobQuery<>("ItemB");
		query.setSQL(bql);
		query.doSQLQuery(context, new SQLQueryListener<ItemB>() {

			private SparseArray<String> sMap;

			@Override
			public void done(BmobQueryResult<ItemB> bList, BmobException arg1) {
				List<BmobObject> l = new ArrayList<>();
				sMap = new SparseArray<>();
				for (ItemB itemB : bList.getResults()) {
					sMap.put(itemB.getId(), itemB.getObjectId());
				}

				for (Item item : itemList) {
					ItemB itemB = new ItemB(item);
					itemB.setObjectId(sMap.get(item.getId()));
					l.add(itemB);
				}
				updateObject(context, l);

			}
		});
	}

	private static class DownloadAsyncTask extends AsyncTask<Context, Void, String> {
		
		boolean docSuccFlag = false;
		boolean itemSuccFlag = false;

		@Override
		protected String doInBackground(final Context... params) {
			Context context = params[0];
			try {
				// 删除表的顺序
				// material(无关联)->DocumentItemDetial(持有Document和Item的实例)
				// ->Item(关联表DocItemDetial已删,自己也被删了大部分)->Document(关联表DocItemDetial已删，自己也被删了大部分,持有Customer实例)
				// ->Customer(关联表已删)
				// 下载写入数据库的顺序正好相反
				DataSupport.deleteAll(Material.class);
				DataSupport.deleteAll(DocumentItemDetail.class);
				DataSupport.deleteAll(Item.class);
				DataSupport.deleteAll(Document.class);
				DataSupport.deleteAll(Customer.class);

				downloadMaterial(context);
				downloadCustomer(context);
				downloadItem(context);
				return "succ";

			} catch (Exception e) {
				return "fail";
			}

		}

		private void downloadMaterial(final Context... params) {
			// 下载material表
			BmobQuery<MaterialB> query = new BmobQuery<MaterialB>("Material");
			query.setLimit(Integer.MAX_VALUE);

			query.findObjects(params[0], new FindCallback() {

				@Override
				public void onFailure(int arg0, String arg1) {
					Log.e("error", arg1);
				}

				@Override
				public void onSuccess(JSONArray ja) {
					if (ja != null) {
						int length = ja.length();
						List<Material> materialList = new ArrayList<>();
						Material newMaterial = null;
						try {
							for (int i = 0; i < length; i++) {
								JSONObject jo = ja.getJSONObject(i);
								newMaterial = new Material();
								newMaterial.setObjId(jo.getInt("id"));
								newMaterial.setObjectId(jo.getString("objectId"));
								newMaterial.setIsNew(0);
								JSONObject dateJo = jo.getJSONObject("lastEditTime");
								newMaterial.setLastEditTime(
										DateUtils.getDateFormat19().parse(dateJo.getString("iso")));
								newMaterial.setName(jo.getString("name"));
								newMaterial.setPrice(jo.getInt("price"));
								materialList.add(newMaterial);
							}
							logItemCount();
							Log.e("download", "material下载成功，总共= " + length);
							DataService.getInstance().saveMaterials(materialList);
							logItemCount();
						} catch (JSONException e) {
							e.printStackTrace();
							Log.e("error", "json error = " + e.getMessage());
						} catch (ParseException e) {
							e.printStackTrace();
							Log.e("error", "sdf parse error = " + e.getMessage());
						}
					}

				}
			});
		}

		private void downloadItem(final Context context) {
			// 下载Item表
			BmobQuery<BmobObject> query3 = new BmobQuery<>("Item");
			query3.setLimit(Integer.MAX_VALUE);

			query3.findObjects(context, new FindCallback() {

				@Override
				public void onFailure(int arg0, String arg1) {
					Log.e("error", arg1);
				}

				@Override
				public void onSuccess(JSONArray ja) {
					if (ja != null) {

						int length = ja.length();
						List<Item> itemList = new ArrayList<>();
						Item newItem = null;
						try {
							for (int i = 0; i < length; i++) {
								JSONObject jo = ja.getJSONObject(i);
								newItem = new Item();
								newItem.setObjId(jo.getInt("id"));
								newItem.setObjectId(jo.getString("objectId"));
								newItem.setIsNew(0);
								JSONObject dateJo = jo.getJSONObject("lastEditTime");
								newItem.setLastEditTime(DateUtils.getDateFormat19().parse(dateJo.getString("iso")));
								if (jo.has("material"))
									newItem.setMaterial(jo.getString("material"));
								if (jo.has("lossRatio"))
									newItem.setLossRatio(jo.getString("lossRatio"));
								if (jo.has("name"))
									newItem.setName(jo.getString("name"));
								if (jo.has("price"))
									newItem.setPrice(jo.getInt("price"));
								if (jo.has("unit"))
									newItem.setUnit(jo.getString("unit"));
								if (jo.has("weight"))
									newItem.setWeight(jo.getInt("weight"));
								itemList.add(newItem);
							}
							logItemCount();
							Log.e("download", "item下载成功，总共= " + length);
							DataService.getInstance().saveItems(itemList);
							logItemCount();
							itemSuccFlag = true;
							if(itemSuccFlag&&docSuccFlag) {
								// 必须等待item,doc下载添加完毕，才能关联item
								downloadDetail(context);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Log.e("error", "json error = " + e.getMessage());
						} catch (ParseException e) {
							e.printStackTrace();
							Log.e("error", "sdf parse error = " + e.getMessage());
						}
					}

				}
			});
		}

		private void downloadCustomer(final Context context) {
			// 下载Customer
			BmobQuery<BmobObject> query4 = new BmobQuery<>("Customer");
			query4.setLimit(Integer.MAX_VALUE);

			query4.findObjects(context, new FindCallback() {

				@Override
				public void onFailure(int arg0, String arg1) {
					Log.e("error", arg1);
				}

				@Override
				public void onSuccess(JSONArray ja) {
					if (ja != null) {

						int length = ja.length();
						List<Customer> customerList = new ArrayList<>();
						Customer newCustomer = null;
						try {
							for (int i = 0; i < length; i++) {
								JSONObject jo = ja.getJSONObject(i);
								newCustomer = new Customer();
								newCustomer.setObjId(jo.getInt("id"));
								newCustomer.setObjectId(jo.getString("objectId"));
								newCustomer.setIsNew(0);
								JSONObject dateJo = jo.getJSONObject("lastEditTime");
								newCustomer.setLastEditTime(
										DateUtils.getDateFormat19().parse(dateJo.getString("iso")));
								if (jo.has("name"))
									newCustomer.setName(jo.getString("name"));
								if (jo.has("qq"))
									newCustomer.setQq(jo.getString("qq"));
								if (jo.has("email"))
									newCustomer.setEmail(jo.getString("email"));
								customerList.add(newCustomer);
							}
							Log.e("download", "customer下载成功，总共= " + length);
							DataService.getInstance().saveCustomers(customerList);
							downloadDocument(context);
						} catch (JSONException e) {
							e.printStackTrace();
							Log.e("error", "json error = " + e.getMessage());
						} catch (ParseException e) {
							e.printStackTrace();
							Log.e("error", "sdf parse error = " + e.getMessage());
						}
					}

				}
			});
		}

		/**
		 * 必须等待item,document下载完毕
		 * 
		 * @param params
		 */
		private void downloadDetail(Context... params) {
			// 下载docmentItemDetial表
			BmobQuery<BmobObject> query2 = new BmobQuery<>("DocumentItemDetail");
			query2.setLimit(Integer.MAX_VALUE);

			query2.findObjects(params[0], new FindCallback() {

				@Override
				public void onFailure(int arg0, String arg1) {
					Log.e("error", arg1);
				}

				@Override
				public void onSuccess(JSONArray ja) {
					if (ja != null) {

						List<Item> allItems = DataService.getInstance().queryAllItems();
						SparseArray<Item> itemMap = new SparseArray<>();
						for (Item item : allItems) {
							itemMap.put(item.getObjId(), item);
						}

						List<Document> allDocs = DataService.getInstance().queryAllDocuments(false);
						SparseArray<Document> docMap = new SparseArray<>();
						for (Document doc : allDocs) {
							docMap.put(doc.getObjId(), doc);
						}

						int length = ja.length();
						List<DocumentItemDetail> detailList = new ArrayList<>();
						DocumentItemDetail newDetail = null;
						try {
							for (int i = 0; i < length; i++) {
								JSONObject jo = ja.getJSONObject(i);
								newDetail = new DocumentItemDetail();
								newDetail.setObjId(jo.getInt("id"));
								newDetail.setObjectId(jo.getString("objectId"));
								newDetail.setIsNew(0);
								JSONObject dateJo = jo.getJSONObject("lastEditTime");
								newDetail.setLastEditTime(DateUtils.getDateFormat19().parse(dateJo.getString("iso")));
								if (jo.has("allText"))
									newDetail.setAllText(jo.getString("allText"));
								if (jo.has("resultText"))
									newDetail.setResultText(jo.getString("resultText"));
								if (jo.has("countingText"))
									newDetail.setCountingText(jo.getString("countingText"));
								if (jo.has("itemId")) {
									newDetail.setItem(itemMap.get(jo.getInt("itemId")));
									newDetail.setItemId(jo.getInt("itemId"));
								}
								if (jo.has("documentId")) {
									newDetail.setDocument(docMap.get(jo.getInt("documentId")));
									newDetail.setDocumentId(jo.getInt("documentId"));
								}
								if (jo.has("totalNumber"))
									newDetail.setTotalNumber(jo.getInt("totalNumber"));
								detailList.add(newDetail);
							}
							logItemCount();
							Log.e("download", "detail下载成功，总共= " + length);
							DataService.getInstance().saveDetails(detailList);
							logItemCount();
						} catch (JSONException e) {
							e.printStackTrace();
							Log.e("error", "json error = " + e.getMessage());
						} catch (ParseException e) {
							e.printStackTrace();
							Log.e("error", "sdf parse error = " + e.getMessage());
						}
					}

				}
			});
		}

		/**
		 * 必须等待customer下载完毕
		 * 
		 * @param params
		 */
		private void downloadDocument(final Context context) {
			// 下载document表
			BmobQuery<BmobObject> query2 = new BmobQuery<>("Document");
			query2.setLimit(Integer.MAX_VALUE);

			query2.findObjects(context, new FindCallback() {

				@Override
				public void onFailure(int arg0, String arg1) {
					Log.e("error", arg1);
				}

				@Override
				public void onSuccess(JSONArray ja) {
					if (ja != null) {

						List<Customer> allCustomers = DataService.getInstance().queryAllCustomers();
						SparseArray<Customer> customerMap = new SparseArray<>();
						for (Customer c : allCustomers) {
							customerMap.put(c.getObjId(), c);
						}

						int length = ja.length();
						List<Document> docList = new ArrayList<>();
						Document newDoc = null;
						try {
							for (int i = 0; i < length; i++) {
								JSONObject jo = ja.getJSONObject(i);
								newDoc = new Document();
								newDoc.setObjId(jo.getInt("id"));
								newDoc.setObjectId(jo.getString("objectId"));
								newDoc.setIsNew(0);
								JSONObject dateJo = jo.getJSONObject("lastEditTime");
								newDoc.setLastEditTime(DateUtils.getDateFormat19().parse(dateJo.getString("iso")));
								if (jo.has("beginMonth"))
									newDoc.setBeginMonth(jo.getInt("beginMonth"));
								if (jo.has("beginYear"))
									newDoc.setBeginYear(jo.getInt("beginYear"));
								if (jo.has("endMonth"))
									newDoc.setEndMonth(jo.getInt("endMonth"));
								if (jo.has("endYear"))
									newDoc.setEndYear(jo.getInt("endYear"));
								if (jo.has("timeText"))
									newDoc.setTimeText(jo.getString("timeText"));
								if (jo.has("title"))
									newDoc.setTitle(jo.getString("title"));
								if (jo.has("isCompleted"))
									newDoc.setCompleted(jo.getBoolean("isCompleted"));
								if (jo.has("isPrinted"))
									newDoc.setPrinted(jo.getBoolean("isPrinted"));
								if (jo.has("customerId")) {
									newDoc.setReceiver(customerMap.get(jo.getInt("customerId")));
									newDoc.setReceiverId(jo.getInt("customerId"));
								}

								docList.add(newDoc);
							}
							logItemCount();
							Log.e("download", "document下载成功，总共= " + length);
							DataService.getInstance().saveDocuments(docList);
							logItemCount();
							docSuccFlag = true;
							if(itemSuccFlag&&docSuccFlag) {
								// 必须等待item,doc下载添加完毕，才能关联item
								downloadDetail(context);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Log.e("error", "json error = " + e.getMessage());
						} catch (ParseException e) {
							e.printStackTrace();
							Log.e("error", "sdf parse error = " + e.getMessage());
						}
					}

				}
			});
		}

	}

	private static void test(List<? extends BmobObject> list) throws ParseException {
		if (list != null && list.size() > 0) {
			BmobObject object = list.get(0);
			Class<? extends BmobObject> class1 = object.getClass();
			if (class1 == ItemB.class) {
				List<Item> newList = new ArrayList<>();
				Item item = null;
				for (BmobObject i : list) {
					item = new Item((ItemB) i);
					newList.add(item);
				}
			} else if (class1 == MaterialB.class) {
				List<Material> newList = new ArrayList<>();
				Material item = null;
				for (BmobObject i : list) {
					item = new Material((MaterialB) i);
					newList.add(item);
				}
				// } else if(class1==CustomerB.class) {
				// List<Customer> newList = new ArrayList<>();
				// Customer item = null;
				// for(BmobObject i : list) {
				// item = new Customer((CustomerB)i);
				// newList.add(item);
				// }
				// } else if(class1==ItemB.class) {
				// List<Item> newList = new ArrayList<>();
				// Item item = null;
				// for(BmobObject i : list) {
				// item = new Item((ItemB)i);
				// newList.add(item);
				// }
				// } else if(class1==ItemB.class) {
				// List<Item> newList = new ArrayList<>();
				// Item item = null;
				// for(BmobObject i : list) {
				// item = new Item((ItemB)i);
				// newList.add(item);
				// }
			}
		}

	}

	public static void logItemCount() {
		Log.e("item count", "" + DataSupport.count(Item.class));
	}

}
