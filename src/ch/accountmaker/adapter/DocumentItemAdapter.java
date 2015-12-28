package ch.accountmaker.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TextView;

import android.content.Context;
import android.hardware.camera2.TotalCaptureResult;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView.OnEditorActionListener;
import at.markushi.ui.CircleButton;
import ch.accountmaker.R;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.DocumentItemDetail;
import ch.accountmaker.model.Item;
import ch.accountmaker.model.Material;
import ch.accountmaker.utils.StringUtils;

public class DocumentItemAdapter extends BaseAdapter implements OnEditorActionListener, OnClickListener {

	Context mContext;
	FragmentManager mFragmentManager;
	private Spinner spMaterial;
	private RadioButton rb2;
	private RadioButton rb1;
	private EditText etName;
	private EditText etPrice;
	private EditText etWeigth;
	private EditText etUnit;
	
	private TextView tvItemInfo;
	private EditText etDetailInput;
	
	private Dialog.Builder builder;
	private List<String> materialNames;
	private ArrayAdapter<String> mMaterialAdapter;
	private List<Material> materialList;
	List<DocumentItemDetail> mList = new ArrayList<>();
	private Item clickedItem;

	public DocumentItemAdapter(Context c, FragmentManager fm) {
		mContext = c;
		initMaterialAdapter();
		mFragmentManager = fm;
	}

	private void initMaterialAdapter() {
		materialList = DataService.getInstance().queryAllMaterials();
		materialNames = new ArrayList<>();
		for (Material m : materialList) {
			materialNames.add(m.getName());
		}
		mMaterialAdapter = new ArrayAdapter<>(mContext, R.layout.simple_spinner_item, materialNames);
	}

	public void setData(List<DocumentItemDetail> list) {
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mList != null && mList.size() > position + 1) {
			return mList.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.document_detail_item, null);
			holder.tvNo = (TextView) convertView.findViewById(R.id.doc_item_no);
			holder.etName = (TextView) convertView.findViewById(R.id.doc_item_name);
			holder.etName.setOnClickListener(this);
			holder.etContent = (EditText) convertView.findViewById(R.id.doc_item_content);
			holder.etResult = (EditText) convertView.findViewById(R.id.doc_item_result);
			holder.btnCalculate = (FloatingActionButton) convertView.findViewById(R.id.doc_item_calculate);
			holder.btnCalculate.setLineMorphingState(1, true);
			holder.btnCalculate.setOnClickListener(this);
			holder.btnCalculate.setTag(holder);
			
			holder.btnAdd = (FloatingActionButton) convertView.findViewById(R.id.doc_item_add);
			holder.btnAdd.setLineMorphingState(0, true);
			holder.btnAdd.setOnClickListener(this);
			holder.btnAdd.setTag(holder);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DocumentItemDetail did = null;
		if (mList != null && (did = mList.get(position)) != null) {
			holder.tvNo.setText((position + 1) + ".");
			holder.etContent.setText(did.getCountingText());
			holder.etContent.setImeOptions(EditorInfo.IME_ACTION_DONE);
			holder.etContent.setOnEditorActionListener(this);

			holder.etContent.getmInputView().setTag(holder.etContent);
			holder.etResult.getmInputView().setTag(holder.etResult);
			holder.etResult.setText(StringUtils.toVisualString(did.getResultText()));
			holder.detail = did;
			holder.etContent.setTag(holder);
			holder.etName.setTag(holder);
			holder.etResult.setTag(holder);
			holder.tvNo.setTag(did);

			Item item = did.getItem();
			if (item != null) {
				holder.etName.setText(item.getName() + "(" + item.getMaterial() + "):");
			} else {
				holder.etName.setText("");
			}
		}

		return convertView;
	}

	static class ViewHolder {
		TextView tvNo;
		TextView etName;
		FloatingActionButton btnCalculate;
		FloatingActionButton btnAdd;
		EditText etContent;
		EditText etResult;

		DocumentItemDetail detail;

		public DocumentItemDetail getDetail() {
			return detail;
		}

	}

	@Override
	public boolean onEditorAction(android.widget.TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			View et = (View) v.getTag();
			switch (et.getId()) {
			case R.id.doc_item_content:
				EditText etContent = (EditText) et;

				ViewHolder holder = (ViewHolder) etContent.getTag();
				EditText etResult = holder.etResult;
				if (etResult != null) {
					int count = 0;
					String all = v.getText().toString();
					int index = -1;
					if ((index = all.indexOf("=")) >= 0) {
						all = all.substring(0, index);
					}

					String[] temps = all.split(" ");
					for (String str : temps) {
						int n = StringUtils.getInt(str.trim());
						count += n;
					}
					DocumentItemDetail did = (DocumentItemDetail) holder.tvNo.getTag();
					Item item = did.getItem();
					if (did != null && (item = did.getItem()) != null) {// 没有产品记录的情况下，不自动执行计算

						double weight = item.getWeight();
						double totalWeight;

						totalWeight = count * weight;
						String result = all + "=" + count + "*" + weight + "=" + (totalWeight / 1000);
						v.setText(result);

						double money = item.getPrice() * count / 100;
						String moneyResult = count + "×" + item.getPrice() + "=" + money + "元";
						etResult.setText(moneyResult);
					} else {// 没有产品记录，要保存记录 TODO
						String result = all + "=" + count;
						v.setText(result);
						etResult.setText(count + "×");
					}
				}
				break;
			case R.id.doc_item_result:

				break;

			default:
				break;
			}

		}

		return false;
	}
	

	@Override
	public void onClick(View v) {
		ViewHolder holder = (ViewHolder) v.getTag();
		switch (v.getId()) {
		case R.id.doc_item_name:
			if (holder != null) {
				DocumentItemDetail did = holder.detail;
				if (did != null) {
					showModifyDialog(v);
				}
			}
			break;
		case R.id.doc_item_calculate:
			if(holder!=null) {
				calculate(holder);
			}
			break;
			
		case R.id.doc_item_add:
			if (holder != null) {
				DocumentItemDetail did = holder.detail;
				if (did != null) {
					showAddDialog(v);
				}
			}
			break;
			
		default:
			break;
		}
	}
	
	private void showAddDialog(View view) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		if (holder != null && holder.getDetail() != null && holder.getDetail().getItem() != null) {
			final DocumentItemDetail detail = holder.getDetail();
			clickedItem = detail.getItem();
			
			
			builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

				@Override
				protected void onBuildDone(Dialog dialog) {
					dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					tvItemInfo = (TextView) dialog.findViewById(R.id.item_info);
					etDetailInput = (EditText) dialog.findViewById(R.id.item_add_content);
					if (clickedItem != null) {
						String info = clickedItem.getName()+"("+clickedItem.getMaterial()+")\n单价:"+clickedItem.getPrice()+"分\t重量:"+clickedItem.getWeight()+"克";
						tvItemInfo.setText(info);
					}
				}

				@Override
				public void onPositiveActionClicked(DialogFragment fragment) {
					super.onPositiveActionClicked(fragment);
					
					String original = holder.etContent.getText().toString();
					int index = -1;
					if ((index = original.indexOf("=")) >= 0) {
						original = original.substring(0, index);
					}
					
					String input = etDetailInput.getText().toString();
					original = original + " "+ input;
					
					holder.etContent.setText(original);
					
					
					if (clickedItem != null) {
						calculate(holder);
					}
					// TODO 重新计算
					clickedItem = null;
				}

				@Override
				public void onNegativeActionClicked(DialogFragment fragment) {
					super.onNegativeActionClicked(fragment);
					clickedItem = null;
				}

			};

			builder.title("添加发货明细").positiveAction("完成").negativeAction("取消").contentView(R.layout.detail_add_dialog);
			DialogFragment fragment = DialogFragment.newInstance(builder);
			fragment.show(mFragmentManager, null);
			
			
		}
	}
	

	private void showModifyDialog(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder != null && holder.getDetail() != null && holder.getDetail().getItem() != null) {
			final DocumentItemDetail detail = holder.getDetail();
			clickedItem = detail.getItem();

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
					super.onPositiveActionClicked(fragment);
					if (clickedItem != null) {
					} else {
						clickedItem = new Item();
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
					clickedItem.setLastEditTime(new Date());
					clickedItem = DataService.getInstance().saveItem(clickedItem);

					int index = mList.indexOf(detail);
					detail.setItem(clickedItem);
					mList.set(index, detail);
					notifyDataSetChanged();
					// TODO 重新计算
				}

				@Override
				public void onNegativeActionClicked(DialogFragment fragment) {
					super.onNegativeActionClicked(fragment);
					clickedItem = null;
				}

			};

			builder.title("修改产品信息").positiveAction("完成").negativeAction("取消").contentView(R.layout.item_modify_dialog);
			DialogFragment fragment = DialogFragment.newInstance(builder);
			fragment.show(mFragmentManager, null);
		}
	}

	public void calculate(ViewHolder holder) {
		if(holder!=null) {
			DocumentItemDetail did = holder.getDetail();
			EditText etContent = holder.etContent;
			EditText etResult = holder.etResult;
			String result = "";
			String moneyResult = "";
			int count = 0;
			String all = etContent.getText().toString();
			int index = -1;
			if ((index = all.indexOf("=")) >= 0) {
				all = all.substring(0, index);
			}

			String[] temps = all.split(" ");
			for (String str : temps) {
				int n = StringUtils.getInt(str.trim());
				count += n;
			}
			Item item = did.getItem();
			if (did != null && (item = did.getItem()) != null) {// 没有产品记录的情况下，不自动执行计算

				double weight = item.getWeight();
				double totalWeight;

				totalWeight = count * weight;
				result = all + "=" + count + "*" + weight + "=" + (totalWeight / 1000);
				etContent.setText(result);
				double money = item.getPrice() * count / 100;
				moneyResult = count + "×" + item.getPrice() + "=" + money + "元";
				etResult.setText(moneyResult);
				did.setCountingText(result);
				did.setResultText(moneyResult);
			} else {// 没有产品记录，要保存记录 TODO
				result = all + "=" + count;
				etContent.setText(result);
				etResult.setText(count + "×");
			}
			did.setCountingText(result);
			did.setResultText(moneyResult);
			did.setTotalNumber(count);
			did.setLastEditTime(new Date());
			
			did = DataService.getInstance().saveDetail(did);
		}
	}

}
