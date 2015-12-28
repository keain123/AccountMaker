package ch.accountmaker.adapter;

import java.util.List;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.accountmaker.model.Document;

public class MyAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
	
	private int mHeaderResId;

    private LayoutInflater mInflater;

    private int mItemResId;

    private List<Document> mItems;

    public MyAdapter(Context context, List<Document> items, int headerResId,
            int itemResId) {
        init(context, items, headerResId, itemResId);
    }
    
    private void init(Context context, List<Document> items, int headerResId, int itemResId) {
        this.mItems = items;
        this.mHeaderResId = headerResId;
        this.mItemResId = itemResId;
        mInflater = LayoutInflater.from(context);
    }
    
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Document getItem(int position) {
		// TODO Auto-generated method stub
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mItemResId, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Document item = getItem(position);
        if(item!=null&&item.getReceiver()!=null&&item.getReceiver().getName()!=null) {
        	holder.textView.setText(item.getTimeText());
        }

        return convertView;
	}

	@Override
    public long getHeaderId(int position) {
        Document item = getItem(position);
        if(item.getReceiver()!=null) {
        	return item.getReceiverId();
        } else {
        	return 0;
        }
    }

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mHeaderResId, parent, false);
            holder = new HeaderViewHolder();
            holder.headerTextView = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder)convertView.getTag();
        }

        Document item = getItem(position);

        if(item!=null&&item.getReceiver()!=null&&item.getReceiver().getName()!=null) {
        	holder.headerTextView.setText(item.getReceiver().getName());
        }
        

        return convertView;
	}
	
	
	protected class HeaderViewHolder {
        public TextView headerTextView;
    }

    protected class ViewHolder {
        public TextView textView;
    }

}
