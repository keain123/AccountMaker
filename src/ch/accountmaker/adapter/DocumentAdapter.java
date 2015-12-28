package ch.accountmaker.adapter;

import java.util.ArrayList;
import java.util.List;

import com.rey.material.widget.TextView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ch.accountmaker.R;
import ch.accountmaker.model.Document;
import de.greenrobot.event.EventBus;

public class DocumentAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener {

	Context mContext;
	List<Document> mList = new ArrayList<>();
	LayoutInflater mLayoutInflater;

	public DocumentAdapter(Context c) {
		mContext = c;
		mLayoutInflater = LayoutInflater.from(c);
	}

	public void setData(List<Document> l) {
		mList = l;
	}

	public void addAll(List<Document> docs) {
		if (mList != null) {
			mList.addAll(docs);
		} else {
			mList = docs;
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
	public Document getItem(int position) {
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.document_item, null);
			holder = new ViewHolder();
			holder.tvDocNo = (TextView) convertView.findViewById(R.id.doc_no);
			holder.tvDocTitle = (TextView) convertView.findViewById(R.id.doc_title);
			holder.tvDocTitle.setOnClickListener(this);
			holder.tvDocTitle.setOnLongClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Document doc;
		if ((doc = getItem(position)) != null) {
			holder.tvDocNo.setText(String.valueOf(position + 1)+".");
//			String userName = doc.getReceiver()!=null?doc.getReceiver().getName():"\t";
//			String title = userName + "\t" + doc.getTimeText();
			holder.tvDocTitle.setText(doc.getTitle());
			holder.tvDocTitle.setTag(doc);
		}
		return convertView;
	}

	public static class ViewHolder {
		TextView tvDocNo;
		TextView tvDocTitle;
	}

	@Override
	public void onClick(View v) {
		Document doc = (Document) v.getTag();
		EventBus.getDefault().post(doc);
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}
	
	public interface OpenDocumentCallback {
		void openDocument();
	}

}
