package ch.accountmaker.adapter;

import java.util.ArrayList;
import java.util.List;

import com.rey.material.widget.TextView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import ch.accountmaker.R;
import ch.accountmaker.model.Item;

public class ItemNameSpinnerAdapter extends BaseAdapter implements Filterable, OnClickListener{
	private ArrayFilter mFilter;  
	ArrayList<Item> mList;
	ArrayList<Item> mUnfilteredData;
	Context mContext;
	LayoutInflater mLayoutInflater;
	SetItemInfoCallback mCallback;
	public ItemNameSpinnerAdapter(Context c) {
		mContext = c;
		mList = new ArrayList<>();
		mLayoutInflater = LayoutInflater.from(c);
	}

	public void setList(ArrayList<Item> l) {
		mList = l;
	}

	public void add(Item i) {
		if (mList != null) {
			mList.add(i);
		}
	}

	public void addAll(List<Item> l) {
		if (mList != null) {
			mList.addAll(l);
		}
	}

	public void clear() {
		if (mList != null) {
			mList.clear();
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		convertView = mLayoutInflater.inflate(R.layout.items_name_spinner_item, null);
		holder.tvItemName = (TextView) convertView.findViewById(R.id.item_name_sp);

		Item item = (Item) getItem(position);
		if (mList != null && item != null) {
			holder.tvItemName.setText(item.getName()+"("+item.getMaterial()+")");
			holder.tvItemName.setOnClickListener(this);
			holder.tvItemName.setTag(item);
			convertView.setTag(holder);
			holder.item = item;
		}
		return convertView;
	}
	
	public void setCallback(SetItemInfoCallback callback) {
		mCallback = callback;
	}

	public static class ViewHolder {
		TextView tvItemName;
		Item item;
		public Item getItem() {
			return item;
		}
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {  
            mFilter = new ArrayFilter();  
        }  
        return mFilter;  
	}
	
	
	private class ArrayFilter extends Filter {

		@Override  
        protected FilterResults performFiltering(CharSequence prefix) {  
            FilterResults results = new FilterResults();  
  
            if (mUnfilteredData == null) {  
                mUnfilteredData = new ArrayList<Item>(mList);  
            }  
  
            if (prefix == null || prefix.length() == 0) {  
                ArrayList<Item> list = mUnfilteredData;  
                results.values = list;  
                results.count = list.size();  
            } else {  
                String prefixString = prefix.toString().toLowerCase();  
  
                ArrayList<Item> unfilteredValues = mUnfilteredData;  
                int count = unfilteredValues.size();  
  
                ArrayList<Item> newValues = new ArrayList<Item>(count);  
  
                for (int i = 0; i < count; i++) {  
                    Item item = unfilteredValues.get(i);  
                    if (item != null) {  
                          
                        if(item.getName()!=null && item.getName().startsWith(prefixString)){  
                              
                            newValues.add(item);  
                        }  
                    }  
                }  
  
                results.values = newValues;  
                results.count = newValues.size();  
            }  
  
            return results;  
        }  
  
        @Override  
        protected void publishResults(CharSequence constraint,  
                FilterResults results) {  
             //noinspection unchecked  
            mList = (ArrayList<Item>) results.values;  
            if (results.count > 0) {  
                notifyDataSetChanged();  
            } else {  
                notifyDataSetInvalidated();  
            }  
        }  
		
	}
	
	public interface SetItemInfoCallback {
		void setItemInfo(Item item);
		void dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_name_sp:
			Item item = (Item) v.getTag();
			if(item!=null&&mCallback!=null) {
				mCallback.setItemInfo(item);
			}
			// TODO dismiss
			mCallback.dismiss();
			break;

		default:
			break;
		}
	}

}