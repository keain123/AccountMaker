package ch.accountmaker.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Spinner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import ch.accountmaker.R;
import ch.accountmaker.adapter.ItemModifyAdapter;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.Item;
import ch.accountmaker.model.Material;
import ch.accountmaker.utils.StringUtils;

public class ItemModifyFragment extends Fragment implements OnClickListener, OnItemClickListener {
	/**
	 * 下拉刷新
	 */
	final static int REFRESH_TYPE_PULL_DOWN = 1;
	/**
	 * 上拉加载更多
	 */
	final static int REFRESH_TYPE_PULL_UP = 2;
	
	final int pageSize = 30;
	int currentTotal = 0;
	LayoutInflater mInflater;

	PullToRefreshListView lvItems;
	private Spinner spMaterial;
	private RadioButton rb2;
	private RadioButton rb1;
	private EditText etName;
	private EditText etPrice;
	private EditText etWeigth;
	private EditText etUnit;
	ItemModifyAdapter mAdapter;

	ArrayAdapter<String> mMaterialAdapter;

	DataService mDataService;

	private Dialog.Builder builder;

	private Item clickedItem;

	private List<String> materialNames;

	private List<Material> materialList;

	public static ItemModifyFragment newInstance() {
		ItemModifyFragment fragment = new ItemModifyFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mDataService = DataService.getInstance();
		View mainView = inflater.inflate(R.layout.fragment_item_modify, null);
		initViews(mainView);
		initMaterialAdapter();
		return mainView;
	}

	private void initMaterialAdapter() {
		materialList = DataService.getInstance().queryAllMaterials();
		materialNames = new ArrayList<>();
		for (Material m : materialList) {
			materialNames.add(m.getName());
		}
		mMaterialAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, materialNames);
	}

	private void showModifyDialog(View view) {
		ItemModifyAdapter.ViewHolder holder = (ItemModifyAdapter.ViewHolder) view.getTag();
		if (holder != null && holder.getItem() != null) {
			clickedItem = holder.getItem();
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
				if (clickedItem != null) {
					etName.setText(clickedItem.getName());
					etWeigth.setText(clickedItem.getWeight() + "");
					etUnit.setText(clickedItem.getUnit());
					etPrice.setText(clickedItem.getPrice() + "");
					String material = clickedItem.getMaterial();
					for (int i = 0; i < materialNames.size(); i++) {
						if (material.equals(materialNames.get(i))) {
							spMaterial.setSelection(i);
						}
					}
					String lossRatio = clickedItem.getLossRatio();
					if (lossRatio.equals(rb1.getText().toString())) {
						rb1.setChecked(true);
					} else if (lossRatio.equals(rb2.getText().toString())) {
						rb2.setChecked(true);
					}
				}
			}

			@Override
			public void onPositiveActionClicked(DialogFragment fragment) {
				
				if (clickedItem != null) {
				} else {
					clickedItem = new Item();
					currentTotal+=1;
				}
				if (etName != null)
					clickedItem.setName(etName.getText().toString());
				if (etWeigth != null)
					clickedItem.setWeight(StringUtils.getInt(etWeigth.getText().toString()));
				if (etUnit != null)
					clickedItem.setUnit(etUnit.getText().toString());
				if (etPrice != null)
					clickedItem.setPrice(StringUtils.getInt(etPrice.getText().toString()));
				if (spMaterial != null)
					clickedItem.setMaterial((String) spMaterial.getSelectedItem());
				if (rb1 != null && rb2 != null) {
					if (rb1.isChecked()) {
						clickedItem.setLossRatio(rb1.getText().toString());
					} else {
						clickedItem.setLossRatio(rb2.getText().toString());
					}
				}
				boolean isExist = mDataService.isItemAlreadyExist(clickedItem.getName(), clickedItem.getMaterial(), clickedItem.getId());
				if(isExist) {
					Toast.makeText(getActivity(), "相同的产品已存在", Toast.LENGTH_SHORT).show();
				} else {
					super.onPositiveActionClicked(fragment);
					clickedItem.setLastEditTime(new Date());
					clickedItem  =DataService.getInstance().saveItem(clickedItem);
					refreshData();
					
				}
				
			}

			@Override
			public void onNegativeActionClicked(DialogFragment fragment) {
				super.onNegativeActionClicked(fragment);
				clickedItem = null;
			}

		};

		builder.title("修改产品信息").positiveAction("完成").negativeAction("取消").contentView(R.layout.item_modify_dialog);
		DialogFragment fragment = DialogFragment.newInstance(builder);
		fragment.show(getFragmentManager(), null);

	}

	private void initViews(View mainView) {
		lvItems = (PullToRefreshListView) mainView.findViewById(R.id.items);
//		lvItems.setOnRefreshListener(new OnRefreshListener<ListView>() {
//			@Override
//			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
//						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//
//				// Update the LastUpdatedLabel
//				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//
//				// Do work to refresh the list here.
//				new GetDataTask().execute();
//			}
//			
//		});
		lvItems.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new GetDataTask().execute(REFRESH_TYPE_PULL_DOWN);	
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				new GetDataTask().execute(REFRESH_TYPE_PULL_UP);	
			}
		});
		mAdapter = new ItemModifyAdapter(getActivity());
		ListView actualListView = lvItems.getRefreshableView();
		View footerView = mInflater.inflate(R.layout.document_item_footer_add, null);
		footerView.setOnClickListener(this);
		actualListView.addFooterView(footerView);
		View headerView = mInflater.inflate(R.layout.items_item, null);
		actualListView.addHeaderView(headerView);

		actualListView.setAdapter(mAdapter);
		initData();
		lvItems.setOnItemClickListener(this);

	}

	private void refreshData() {
		List<Item> findAll = mDataService.queryPageItems(Math.max(currentTotal,pageSize),0);
		mAdapter.setData(findAll);
		mAdapter.notifyDataSetChanged();
	}
	private void initData() {
		List<Item> findAll = mDataService.queryPageItems(pageSize,0);
		mAdapter.setData(findAll);
		mAdapter.notifyDataSetChanged();
		currentTotal+=findAll.size();
	}

	private class GetDataTask extends AsyncTask<Integer, Void, List<Item>> {

		int currentType;
		
		@Override
		protected List<Item> doInBackground(Integer... params) {
			List<Item> items = null;
			currentType = params[0];
			if(currentType==REFRESH_TYPE_PULL_DOWN) {
				items = mDataService.queryPageItems(currentTotal,0);
			} else if(currentType==REFRESH_TYPE_PULL_UP) {
				
				items = mDataService.queryPageItems(pageSize, currentTotal);
				currentTotal+=items.size();
			}
			return items;
		}

		@Override
		protected void onPostExecute(List<Item> items) {
			if(items!=null) {
				if(currentType==REFRESH_TYPE_PULL_DOWN) {
					mAdapter.setData(items);
				} else if(currentType==REFRESH_TYPE_PULL_UP) {
					
					mAdapter.addAll(items);
				}
				
			} else {
				Toast.makeText(getActivity(), "没有更多记录", Toast.LENGTH_SHORT).show();
			}
			mAdapter.notifyDataSetChanged();
			lvItems.onRefreshComplete();

			super.onPostExecute(items);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.doc_item_footer_add:
			showModifyDialog(v);
//			for(int i=0;i<100;i++) {
//				Item item = new Item();
//				item.setLossRatio("100/98");
//				item.setMaterial("明");
//				item.setName("#"+i+"针");
//				item.setUnit("支");
//				item.setPrice(i);
//				item.setWeight(i);
//				item.save();
//			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		showModifyDialog(view);

	}

}
