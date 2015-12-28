package ch.accountmaker.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;
import org.litepal.util.Const.LitePal;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.NoScrollListView;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Spinner.OnItemClickListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnHeaderClickListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnHeaderLongClickListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Toast;
import ch.accountmaker.R;
import ch.accountmaker.adapter.DocumentItemAdapter;
import ch.accountmaker.adapter.ItemNameSpinnerAdapter;
import ch.accountmaker.adapter.ItemNameSpinnerAdapter.SetItemInfoCallback;
import ch.accountmaker.adapter.MyAdapter;
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
public class CustomerDocumentFragment extends Fragment implements OnClickListener, android.widget.AdapterView.OnItemClickListener, OnHeaderClickListener, OnHeaderLongClickListener {


	LayoutInflater mInflater;

	GridView mGridView;

	MyAdapter mAdapter;

	private List<Document> mDocList;

	public CustomerDocumentFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		View mainView = inflater.inflate(R.layout.fragment_customer_doc, null);
		initViews(mainView);
		return mainView;
	}

	public void onDestroyView() {
		super.onDestroyView();
	};

	private void initViews(View mainView) {
		mGridView = (GridView)mainView.findViewById(R.id.document_grid_view);
        mGridView.setOnItemClickListener(this);
        
        
        mDocList = DataService.getInstance().queryAllDocuments(true);

        mAdapter = new MyAdapter(getActivity().getApplicationContext(), mDocList, R.layout.sticky_header, R.layout.sticky_item);
        
        mGridView.setAdapter(mAdapter);



        ((StickyGridHeadersGridView)mGridView).setOnHeaderClickListener(this);
        ((StickyGridHeadersGridView)mGridView).setOnHeaderLongClickListener(this);

        setHasOptionsMenu(true);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	@Override
	public boolean onHeaderLongClick(AdapterView<?> parent, View view, long id) {
		return false;
	}

	@Override
	public void onHeaderClick(AdapterView<?> parent, View view, long id) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Document doc = mAdapter.getItem(position);
		EventBus.getDefault().post(doc);		
	}


}
