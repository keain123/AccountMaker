package ch.accountmaker.adapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.litepal.crud.DataSupport;

import com.rey.material.widget.EditText;
import com.rey.material.widget.TextView;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import butterknife.OnClick;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import ch.accountmaker.R;
import ch.accountmaker.model.Item;

public class ItemModifyAdapter extends BaseAdapter {

	List<Item> mList;
	Context mContext;
	LayoutInflater mLayoutInflater;

	public ItemModifyAdapter(Context c) {
		mLayoutInflater = LayoutInflater.from(c);
		mList = new ArrayList<>();
		mContext = c;
	}

	public void setData(List<Item> items) {
		mList = items;
	}
	
	public void addAll(List<Item> items) {
		if(mList!=null) {
			mList.addAll(items);
		} else {
			mList = items;
		}
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public Item getItem(int position) {
		if (mList != null && mList.size() > position) {
			return mList.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		if (mList != null && mList.size() > position) {
			return mList.get(position).getId();
		} else {
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.items_item, null);
			holder = new ViewHolder();
			holder.tvItemNo = (TextView) convertView.findViewById(R.id.item_no);
			holder.tvItemName = (TextView) convertView.findViewById(R.id.item_name);
			holder.tvItemWeight = (TextView) convertView.findViewById(R.id.item_weight);
			holder.tvItemPrice = (TextView) convertView.findViewById(R.id.item_price);

			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mList != null) {
			Item item = getItem(position);
			if (item != null) {
				holder.tvItemNo.setText((position+1)+".");
				holder.tvItemName.setText(item.getName()+"("+item.getMaterial()+")");
				holder.tvItemWeight.setText(String.valueOf(item.getWeight()));
				holder.tvItemPrice.setText(item.getPrice()+"");
				holder.item = item;
			}
		}


			

		return convertView;
	}

	public static class ViewHolder {
		TextView tvItemNo;
		TextView tvItemName;
		TextView tvItemWeight;
		TextView tvItemPrice;
		Item item;
		public Item getItem() {
			return item;
		}
		
		
	}

}
