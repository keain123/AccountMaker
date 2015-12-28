package ch.accountmaker.view;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import ch.accountmaker.R;
import ch.accountmaker.adapter.DocumentAdapter;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.Document;
import ch.accountmaker.widget.DocumentNew;

@SuppressLint("InflateParams")
public class DocumentOpenFragment extends Fragment implements OnClickListener, OnItemClickListener {
	/**
	 * 下拉刷新
	 */
	final static int REFRESH_TYPE_PULL_DOWN = 1;
	/**
	 * 上拉加载更多
	 */
	final static int REFRESH_TYPE_PULL_UP = 2;

	final int pageSize = 20;
	int currentTotal = 0;
	LayoutInflater mInflater;

	PullToRefreshListView lvItems;

	DocumentAdapter mAdapter;

	DataService mDataService;

	List<Document> mList = new ArrayList<>();
	private ListView mActualListView;

	public static DocumentOpenFragment newInstance() {
		DocumentOpenFragment fragment = new DocumentOpenFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mDataService = DataService.getInstance();
		View mainView = inflater.inflate(R.layout.fragment_open_document, null);
		initViews(mainView);
		initData();

		return mainView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	boolean hasSearchView;
	CardView headerSearchView;
	int headerSearchViewHeight = 300;

	private void initViews(View mainView) {
		lvItems = (PullToRefreshListView) mainView.findViewById(R.id.items);
		lvItems.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new GetDataTask().execute(REFRESH_TYPE_PULL_DOWN);
				hasSearchView = !hasSearchView;
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				new GetDataTask().execute(REFRESH_TYPE_PULL_UP);
			}
		});
		mAdapter = new DocumentAdapter(getActivity());
		mActualListView = lvItems.getRefreshableView();
		View footerView = mInflater.inflate(R.layout.document_item_footer_add, null);
		footerView.setOnClickListener(this);
		mActualListView.addFooterView(footerView);

		mActualListView.setAdapter(mAdapter);

//		headerSearchView = (CardView) mInflater.inflate(R.layout.document_search_view, null);
//		headerSearchView = (CardView) mainView.findViewById(R.id.cardview);
//		int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//
//		int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//
//		headerSearchView.measure(width, height);
//
//		int measuredHeight = headerSearchView.getMeasuredHeight();
//
//		headerSearchViewHeight = measuredHeight;
		
		
	}

	public void refreshData() {
		mList = mDataService.queryPageDocuments(Math.max(currentTotal, pageSize), 0);
		mAdapter.setData(mList);
		mAdapter.notifyDataSetChanged();
	}

	private void initData() {
		mList = mDataService.queryPageDocuments(pageSize, 0);
		mAdapter.setData(mList);
		mAdapter.notifyDataSetChanged();
		currentTotal += mList.size();
	}

	private class GetDataTask extends AsyncTask<Integer, Void, List<Document>> {

		int currentType;

		@Override
		protected List<Document> doInBackground(Integer... params) {
			List<Document> docs = null;
			currentType = params[0];
			if (currentType == REFRESH_TYPE_PULL_DOWN) {
				docs = mDataService.queryPageDocuments(Math.max(currentTotal, pageSize), 0);
			} else if (currentType == REFRESH_TYPE_PULL_UP) {

				docs = mDataService.queryPageDocuments(pageSize, currentTotal);
				currentTotal += docs.size();
			}
			return docs;
		}

		@Override
		protected void onPostExecute(List<Document> docs) {
			if (docs != null) {
				if (currentType == REFRESH_TYPE_PULL_DOWN) {
					mAdapter.setData(docs);
				} else if (currentType == REFRESH_TYPE_PULL_UP) {
					mAdapter.addAll(docs);
				}

			} else {
				Toast.makeText(getActivity(), "没有更多记录", Toast.LENGTH_SHORT).show();
			}
			mAdapter.notifyDataSetChanged();
			lvItems.onRefreshComplete();

			super.onPostExecute(docs);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.doc_item_footer_add:
			DocumentNew docNewWidget = new DocumentNew(getActivity(), getFragmentManager());
			docNewWidget.build(-1, -1);
			docNewWidget.show();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

}
