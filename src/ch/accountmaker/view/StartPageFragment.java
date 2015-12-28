package ch.accountmaker.view;

import java.util.List;

import com.rey.material.widget.TextView;

import android.R.color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.FrameLayout.LayoutParams;
import ch.accountmaker.R;
import ch.accountmaker.adapter.DocumentAdapter;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.Document;
import ch.accountmaker.model.eventbus.Msg;
import de.greenrobot.event.EventBus;

public class StartPageFragment extends Fragment implements OnClickListener{
	
	TextView tvAddDocument;
	TextView tvOpenDocument;
	
	ListView lvRecentDocument;
	
	DocumentAdapter mAdapter;
	
		
	public static StartPageFragment newInstance(){
		StartPageFragment fragment = new StartPageFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.fragment_start_page, null);
		initViews(mainView);
		initRecentDocumentList();
		
		return mainView;
	}

	private void initRecentDocumentList() {
		List<Document> recentList = DataService.getInstance().queryRecentDocumentList(10);
		mAdapter.setData(recentList);
		mAdapter.notifyDataSetChanged();
	}

	private void initViews(View mainView) {
		tvAddDocument = (TextView) mainView.findViewById(R.id.add_document);
		tvOpenDocument = (TextView) mainView.findViewById(R.id.open_document);
		tvAddDocument.setOnClickListener(this);
		tvOpenDocument.setOnClickListener(this);
		lvRecentDocument = (ListView) mainView.findViewById(R.id.recent_documents);
		mAdapter = new DocumentAdapter(getActivity());
		lvRecentDocument.setAdapter(mAdapter);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_document:
			EventBus.getDefault().post(new Msg(getResources().getString(R.string.add_doc)));
			break;
		case R.id.open_document:
			EventBus.getDefault().post(new Msg(getResources().getString(R.string.open_doc)));
			break;

		default:
			break;
		}
	}
	
	

}
