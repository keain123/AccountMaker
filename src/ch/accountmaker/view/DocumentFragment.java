package ch.accountmaker.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.NoScrollListView;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Spinner.OnItemClickListener;
import com.rey.material.widget.TextView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Toast;
import ch.accountmaker.R;
import ch.accountmaker.adapter.DocumentItemAdapter;
import ch.accountmaker.adapter.ItemNameSpinnerAdapter;
import ch.accountmaker.adapter.ItemNameSpinnerAdapter.SetItemInfoCallback;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.Customer;
import ch.accountmaker.model.Document;
import ch.accountmaker.model.DocumentItemDetail;
import ch.accountmaker.model.Item;
import ch.accountmaker.model.Material;
import ch.accountmaker.model.eventbus.ChangedTab;
import ch.accountmaker.model.eventbus.Msg;
import ch.accountmaker.model.eventbus.YearMonth;
import ch.accountmaker.utils.StringUtils;
import ch.accountmaker.widget.MonthYearPicker;
import de.greenrobot.event.EventBus;

@SuppressLint("InflateParams")
public class DocumentFragment extends Fragment implements OnClickListener {

	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	
	private static String ADD_NEW_CUSTOMER = "";

	LayoutInflater mInflater;
	private Spinner spCustomer;
	private TextView tvBeginTime;
	private TextView tvEndTime;
	private TextView tvTimeSperator;
	private NoScrollListView lvItems;

	DocumentItemAdapter mAdapter;
	
	private List<DocumentItemDetail> mList;
	private List<Customer> cList;
	private List<String> cStringList;
	private ArrayAdapter<String> mCustomerAdapter;
	private List<String> materialNames;
	private List<Material> materialList;
	private ArrayAdapter<String> mMaterialAdapter;
	private Dialog.Builder builder;
	private Spinner spMaterial;
	private RadioButton rb2;
	private RadioButton rb1;
	private EditText etName;
	private EditText etPrice;
	private EditText etWeigth;
	private EditText etUnit;
	private EditText etDetailInput;
	private EditText.InternalAutoCompleteTextView innerEditText;

	private Document mDocument;

	public static DocumentFragment newInstance(Document doc) {
		DocumentFragment fragment = new DocumentFragment(doc);
		return fragment;
	}

	public DocumentFragment(Document doc) {
		mDocument = doc;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		ADD_NEW_CUSTOMER = getString(R.string.add_new_customer);
		View mainView = inflater.inflate(R.layout.fragment_document, null);
		initViews(mainView);
		initMaterialAdapter();
		EventBus.getDefault().register(this);
		return mainView;
	}

	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	};

	private void initViews(View mainView) {
		
		mPullRefreshScrollView = (PullToRefreshScrollView) mainView.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.mHeaderLayout.setRefreshingLabel("下拉新增条目");
		mPullRefreshScrollView.mHeaderLayout.setPullLabel("下拉新增条目");
		mPullRefreshScrollView.mHeaderLayout.setReleaseLabel("松开新增条目");
		mPullRefreshScrollView.mFooterLayout.setRefreshingLabel("上拉新增条目");
		mPullRefreshScrollView.mFooterLayout.setPullLabel("上拉新增条目");
		mPullRefreshScrollView.mFooterLayout.setReleaseLabel("松开新增条目");
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				mPullRefreshScrollView.onRefreshComplete();
				showNewDetailDialog();
			}
		});
		
		spCustomer = (Spinner) mainView.findViewById(R.id.customer);
		tvBeginTime = (TextView) mainView.findViewById(R.id.begin_time);
		tvEndTime = (TextView) mainView.findViewById(R.id.end_time);
		tvTimeSperator = (TextView) mainView.findViewById(R.id.time_sperator);
		lvItems = (NoScrollListView) mainView.findViewById(R.id.items);

		// footer
//		View footerView = mInflater.inflate(R.layout.document_item_footer_add, null);
//		footerView.setOnClickListener(this);
//		lvItems.addFooterView(footerView);
		mAdapter = new DocumentItemAdapter(getActivity(), getFragmentManager());
		lvItems.setAdapter(mAdapter);

		// 客户名选择spinner
		cList = DataService.getInstance().queryAllCustomers();
		cStringList = new ArrayList<>();
		cStringList.add("");
		cStringList.add(ADD_NEW_CUSTOMER);

		for (Customer c : cList) {
			cStringList.add(c.getName() == null ? "" : c.getName());
		}
		mCustomerAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, cStringList);
		spCustomer.setAdapter(mCustomerAdapter);
		spCustomer.setOnItemClickListener(itemClickListener);

		test();
		initData();
	}

	private void test() {
		// Customer c1 = new Customer();
		// c1.setName("lizhi");
		// c1.setEmail("123@123.com");
		// c1.setQq("123");
		// c1.save();
		// Customer c2 = new Customer();
		// c2.setName("dano");
		// c2.setEmail("123@123.com");
		// c2.setQq("123");
		// c2.save();
		// try {
		//// mList = new ArrayList<>();
		//// Material m = new Material();
		//// m.setName("明");
		//// m.setPrice(1.9);
		//// Material m2 = new Material();
		//// m2.setName("明苯");
		//// m2.setPrice(2.9);
		//// Material m3 = new Material();
		//// m3.setName("苯");
		//// m3.setPrice(5.9);
		//// Item item = new Item();
		//// item.setName("#31跳日盘");
		//// item.setMaterial(m.getName());
		//// item.setLossRatio("1.02");
		//// item.setPrice(30);
		//// item.setUnit("个");
		//// item.setWeight(35);
		//// Item item2 = new Item();
		//// item2.setName("#32跳日盘");
		//// item2.setMaterial(m.getName());
		//// item2.setLossRatio("1.02");
		//// item2.setPrice(39);
		//// item2.setUnit("个");
		//// item2.setWeight(80);
		//// Item item3 = new Item();
		//// item3.setName("#33跳日盘");
		//// item3.setMaterial(m3.getName());
		//// item3.setLossRatio("100/98");
		//// item3.setPrice(20);
		//// item3.setUnit("个");
		//// item3.setWeight(35);
		//// Item item4 = new Item();
		//// item4.setName("#34跳日盘");
		//// item4.setMaterial(m2.getName());
		//// item4.setLossRatio("100/98");
		//// item4.setPrice(10);
		//// item4.setUnit("个");
		//// item4.setWeight(31);
		////
		//// Customer customer = new Customer();
		//// customer.setEmail("12345@qq.com");
		//// customer.setQq("12345");
		//// customer.setName("XX");
		// Item item = DataSupport.findFirst(Item.class);
		// Customer customer = DataSupport.findFirst(Customer.class);
		//
		// Document doc = new Document();
		// doc.setTitle("XX 2011.9-2012.3");
		// doc.setCompleted(false);
		// doc.setPrinted(false);
		// doc.setTimeText("2011.9-2012.3");
		// doc.setReceiver(customer);
		// doc.saveThrows();
		// DocumentItemDetail d = new DocumentItemDetail();
		// d.setItem(item);
		// d.setCountingText("100 200 300 400");
		// d.setDocument(doc);
		// d.setTotalNumber(1000);
		//// m.saveThrows();
		//// m2.saveThrows();
		//// m3.saveThrows();
		//// item.saveThrows();
		//// item2.saveThrows();
		//// item3.saveThrows();
		//// item4.saveThrows();
		//// customer.saveThrows();
		// d.saveThrows();
		//// mList.add(d);
		//// mAdapter.setData(mList);
		//// mAdapter.notifyDataSetChanged();
		// } catch (Exception e) {
		// Log.e("", e.getMessage());
		// }

		// List<Document> queryAllDocuments =
		// mDataService.queryAllDocuments(true);
		// mDocument = queryAllDocuments.get(0);
	}

	private void initData() {
		for (Customer c : cList) {
			if (c.getId() == mDocument.getReceiver().getId()) {
				String name = c.getName();
				int index = cStringList.indexOf(name);
				spCustomer.setSelection(index);
			}
		}
		String beginTime = "";
		String endTime = "";
		if (mDocument.getBeginYear() > 0 && mDocument.getBeginMonth() > 0) {
			beginTime = mDocument.getBeginYear() + "." + mDocument.getBeginMonth();
			tvBeginTime.setText(beginTime);
		}
		if (mDocument.getEndYear() > 0 && mDocument.getEndMonth() > 0) {
			endTime = mDocument.getEndYear() + "." + mDocument.getEndMonth();
			tvEndTime.setText(endTime);
			tvTimeSperator.setVisibility(View.VISIBLE);
		} else {
			tvTimeSperator.setVisibility(View.GONE);
		}

		tvBeginTime.setOnClickListener(this);
		tvEndTime.setOnClickListener(this);

		mList = mDocument.getDocumentDetailList();
		mAdapter.setData(mList);
		mAdapter.notifyDataSetChanged();
	}

	private void initMaterialAdapter() {
		materialList = DataService.getInstance().queryAllMaterials();
		materialNames = new ArrayList<>();
		for (Material m : materialList) {
			materialNames.add(m.getName());
		}
		mMaterialAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, materialNames);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.doc_item_footer_add:
			showNewDetailDialog();
			break;
		case R.id.begin_time:
			String beginTime = ((TextView) v).getText().toString();
			String[] s = beginTime.split(".");
			int month = -1;
			int year = -1;
			if (s.length == 2) {
				month = StringUtils.getInt(s[0]);
				year = StringUtils.getInt(s[1]);
			}
			MonthYearPicker myp = new MonthYearPicker(getFragmentManager(), YearMonth.BEGIN, mDocument.getId());
			myp.build(month, year);
			myp.show();
			break;
		case R.id.end_time:
			String endTime = ((TextView) v).getText().toString();
			String[] s2 = endTime.split(".");
			int month2 = -1;
			int year2 = -1;
			if (s2.length == 2) {
				month2 = StringUtils.getInt(s2[0]);
				year2 = StringUtils.getInt(s2[1]);
			}
			MonthYearPicker myp2 = new MonthYearPicker(getFragmentManager(), YearMonth.END, mDocument.getId());
			myp2.build(month2, year2);
			myp2.show();
			break;
		default:
			break;
		}
	}

	private void showNewDetailDialog() {

		final TextWatcher mTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 3) {
					List<Item> l = DataSupport.where("name like ?", "%" + s.toString() + "%").find(Item.class);
					if (innerEditText != null) {
						final ItemNameSpinnerAdapter adapter = new ItemNameSpinnerAdapter(getActivity());
						adapter.setCallback(new SetItemInfoCallback() {

							@Override
							public void setItemInfo(Item item) {
								if (etName != null)
									etName.setText(item.getName());
								if (etWeigth != null)
									etWeigth.setText(item.getWeight() + "");
								if (etUnit != null)
									etUnit.setText(item.getUnit());
								if (etPrice != null)
									etPrice.setText(item.getPrice() + "");
								if (spMaterial != null && materialNames != null) {
									String material = item.getMaterial();
									for (int i = 0; i < materialNames.size(); i++) {
										if (material.equals(materialNames.get(i))) {
											spMaterial.setSelection(i);
										}
									}

								}

								if (rb1 != null && rb2 != null) {
									String lossRatio = item.getLossRatio();
									if (lossRatio.equals(rb1.getText().toString())) {
										rb1.setChecked(true);
									} else if (lossRatio.equals(rb2.getText().toString())) {
										rb2.setChecked(true);
									}
								}
								adapter.clear();
							}

							@Override
							public void dismiss() {
								etName.dismissDropDown();
							}

						});
						adapter.clear();
						adapter.addAll(l);
						innerEditText.setAdapter(adapter);
					}
				}
			}
		};

		builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

			@Override
			protected void onBuildDone(Dialog dialog) {
				dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				etName = ((EditText) dialog.findViewById(R.id.item_modify_name));
				etWeigth = ((EditText) dialog.findViewById(R.id.item_modify_weight));
				etUnit = ((EditText) dialog.findViewById(R.id.item_modify_unit));
				etPrice = ((EditText) dialog.findViewById(R.id.item_modify_price));
				spMaterial = (Spinner) dialog.findViewById(R.id.item_modify_material);
				spMaterial.setAdapter(mMaterialAdapter);
				rb1 = (RadioButton) dialog.findViewById(R.id.item_modify_loss_1);
				rb2 = (RadioButton) dialog.findViewById(R.id.item_modify_loss_2);
				rb1.setOnCheckedChangeListener(listener);
				rb2.setOnCheckedChangeListener(listener);
				etDetailInput = (EditText) dialog.findViewById(R.id.item_add_content);
				etName.addTextChangedListener(mTextWatcher);
				innerEditText = ((EditText.InternalAutoCompleteTextView) etName.getmInputView());
			}

			@Override
			public void onPositiveActionClicked(DialogFragment fragment) {
				super.onPositiveActionClicked(fragment);
				if (etName != null && spMaterial != null) {
					String name = etName.getText().toString();
					String material = (String) spMaterial.getSelectedItem();
					String content = etDetailInput.getText().toString();
					Item findItem = DataService.getInstance().findItemByNameAndMaterial(name, material);
					if (findItem == null) {
						// item表中找不到，则要先保存item
						Item newItem = new Item();
						if (etName != null)
							newItem.setName(etName.getText().toString());
						if (etWeigth != null)
							newItem.setWeight(StringUtils.getInt(etWeigth.getText().toString()));
						if (etUnit != null)
							newItem.setUnit(etUnit.getText().toString());
						if (etPrice != null)
							newItem.setPrice(StringUtils.getInt(etPrice.getText().toString()));
						if (spMaterial != null)
							newItem.setMaterial((String) spMaterial.getSelectedItem());
						if (rb1 != null && rb2 != null) {
							if (rb1.isChecked()) {
								newItem.setLossRatio(rb1.getText().toString());
							} else {
								newItem.setLossRatio(rb2.getText().toString());
							}
						}
						boolean isExist = DataService.getInstance().isItemAlreadyExist(newItem.getName(),
								newItem.getMaterial(), -1);
						if (isExist) {
							Toast.makeText(getActivity(), "相同的产品已存在", Toast.LENGTH_SHORT).show();
						} else {
							super.onPositiveActionClicked(fragment);
							newItem.setLastEditTime(new Date());
							findItem = DataService.getInstance().saveItem(newItem);
						}
					}

					// 保存detail
					boolean isExist = false;
					for (DocumentItemDetail detail : mList) {
						if (detail.getItem().getId() == findItem.getId()) {
							// 现有的记录中已经有该item的记录，则添加
							int count = 0;

							String all = detail.getCountingText();
							int index = -1;
							if ((index = all.indexOf("=")) >= 0) {
								all = all.substring(0, index);
							}
							all = all + " " + content;

							String[] temps = all.split(" ");
							for (String str : temps) {
								int n = StringUtils.getInt(str.trim());
								count += n;
							}

							double weight = findItem.getWeight();
							double totalWeight;

							totalWeight = count * weight;
							String result = all + "=" + count + "*" + weight + "=" + (totalWeight / 1000);
							detail.setCountingText(result);
							detail.setTotalNumber(count);
							double money = findItem.getPrice() * count / 100;
							String moneyResult = count + "×" + findItem.getPrice() + "=" + money + "元";
							detail.setResultText(moneyResult);
							detail.setLastEditTime(new Date());
							DataService.getInstance().saveDetail(detail);
							isExist = true;
						}
					}
					if (!isExist) {
						// 添加一条detail
						int count = 0;
						DocumentItemDetail newDetail = new DocumentItemDetail();
						newDetail.setItem(findItem);
						newDetail.setDocument(mDocument);

						int index = -1;
						if ((index = content.indexOf("=")) >= 0) {
							content = content.substring(0, index);
						}
						String[] temps = content.split(" ");
						for (String str : temps) {
							int n = StringUtils.getInt(str.trim());
							count += n;
						}
						double weight = findItem.getWeight();
						double totalWeight;

						totalWeight = count * weight;
						String result = content + "=" + count + "*" + weight + "=" + (totalWeight / 1000);
						newDetail.setCountingText(result);
						newDetail.setTotalNumber(count);
						double money = findItem.getPrice() * count / 100;
						String moneyResult = count + "×" + findItem.getPrice() + "=" + money + "元";
						newDetail.setResultText(moneyResult);
						newDetail.setLastEditTime(new Date());
						newDetail.setIsNew(1);
						newDetail = DataService.getInstance().saveDetail(newDetail);
						mList.add(newDetail);

					}
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onNegativeActionClicked(DialogFragment fragment) {
				super.onNegativeActionClicked(fragment);
			}

		};

		builder.title("添加发货明细").positiveAction("完成").negativeAction("取消").contentView(R.layout.detail_new_dialog);
		DialogFragment fragment = DialogFragment.newInstance(builder);
		fragment.show(getFragmentManager(), null);

	}

	final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				rb1.setChecked(rb1 == buttonView);
				rb2.setChecked(rb2 == buttonView);
			}

		}

	};


	public void onEventMainThread(YearMonth ym) {
		if (ym.getDocId() == mDocument.getId()) {
			String oldTitle = "";
			String newTitle = "";
			String timeText = "";
			if (ym.getType() == YearMonth.BEGIN) {
				tvBeginTime.setText(ym.getYear() + "." + (ym.getMonth() + 1));
				mDocument.setBeginYear(ym.getYear());
				mDocument.setBeginMonth(ym.getMonth() + 1);
				if (mDocument.getEndYear() <= 0) {
					// 没有结束时间
					timeText = mDocument.getBeginYear() + "." + mDocument.getBeginMonth();
					newTitle = mDocument.getReceiver().getName() + "\t" + timeText;
				} else {
					timeText = mDocument.getBeginYear() + "." + mDocument.getBeginMonth() + "-" + mDocument.getEndYear()
							+ "." + mDocument.getEndMonth();
					newTitle = mDocument.getReceiver().getName() + "\t" + timeText;

				}
			} else if (ym.getType() == YearMonth.END) {
				tvEndTime.setText(ym.getYear() + "." + (ym.getMonth() + 1));
				mDocument.setEndYear(ym.getYear());
				mDocument.setEndMonth(ym.getMonth() + 1);
				timeText = mDocument.getBeginYear() + "." + mDocument.getBeginMonth() + "-" + mDocument.getEndYear()
						+ "." + mDocument.getEndMonth();
				newTitle = mDocument.getReceiver().getName() + "\t" + timeText;
			}
			oldTitle = mDocument.getTitle();
			mDocument.setTimeText(timeText);
			mDocument.setTitle(newTitle);
			EventBus.getDefault().post(new Msg("refresh_category"));
			EventBus.getDefault().post(new ChangedTab(oldTitle, newTitle));
			mDocument.setLastEditTime(new Date());
			
			mDocument = DataService.getInstance().saveDocument(mDocument);
		}
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {
		private EditText etCustomerName;
		private EditText etCustomerQQ;
		private EditText etCustomerEmail;
		private String newName;
		@Override
		public boolean onItemClick(Spinner parent, View view, int position, long id) {
			newName = cStringList.get(position);

			if (newName.equals(ADD_NEW_CUSTOMER)) {
				// 添加新客户
				SimpleDialog.Builder builder;
				builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

					@Override
					protected void onBuildDone(Dialog dialog) {
						super.onBuildDone(dialog);
						etCustomerName = (EditText) dialog.findViewById(R.id.customer_name);
						etCustomerQQ = (EditText) dialog.findViewById(R.id.customer_qq);
						etCustomerEmail = (EditText) dialog.findViewById(R.id.customer_email);
					}

					@Override
					public void onPositiveActionClicked(DialogFragment fragment) {
						if (etCustomerName != null) {
							String name = etCustomerName.getText().toString();
							Customer c = new Customer();
							c.setName(name);
							c.setQq(etCustomerQQ.getText().toString());
							c.setEmail(etCustomerEmail.getText().toString());
							c.setLastEditTime(new Date());
							c = DataService.getInstance().saveCustomer(c);
							cList.add(c);
							cStringList.add(name);
							mAdapter.notifyDataSetChanged();
							int index = cStringList.indexOf(name);
							spCustomer.setSelection(index);
							newName = name;
							
							
							String oldTitle = mDocument.getTitle();
							String newTitle = newName+"\t"+mDocument.getTimeText();
							mDocument.setReceiver(c);
							mDocument.setTitle(newTitle);
							mDocument.setLastEditTime(new Date());
							mDocument = DataService.getInstance().saveDocument(mDocument);
							
							EventBus.getDefault().post(new Msg("refresh_category"));
							EventBus.getDefault().post(new ChangedTab(oldTitle, newTitle));
							
						}
						super.onPositiveActionClicked(fragment);

					}

					@Override
					public void onNegativeActionClicked(DialogFragment fragment) {
						super.onNegativeActionClicked(fragment);
					}
				};

				builder.title("新建客户").positiveAction("确定").negativeAction("取消").contentView(R.layout.customer_dialog);
				DialogFragment fragment = DialogFragment.newInstance(builder);
				fragment.show(getFragmentManager(), null);
			} else {
				String oldTitle = mDocument.getTitle();
				String newTitle = newName+"\t"+mDocument.getTimeText();
				for (Customer c : cList) {
					if (c.getName().equals(newName)) {
						mDocument.setReceiver(c);
						mDocument.setTitle(newTitle);
						mDocument.setLastEditTime(new Date());
						mDocument = DataService.getInstance().saveDocument(mDocument);
					}
				}
				
				EventBus.getDefault().post(new Msg("refresh_category"));
				EventBus.getDefault().post(new ChangedTab(oldTitle, newTitle));
			}
			
			
			
			return true;
		}

	};
}
