package ch.accountmaker.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.rey.material.app.ToolbarManager;
import com.rey.material.widget.CheckedTextView;
import com.rey.material.widget.CustomViewPager;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ImageButton;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TabPageIndicator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.net.wifi.WpsInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import ch.accountmaker.R;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.Customer;
import ch.accountmaker.model.Document;
import ch.accountmaker.model.DocumentItemDetail;
import ch.accountmaker.model.Item;
import ch.accountmaker.model.Material;
import ch.accountmaker.model.TimeStamp;
import ch.accountmaker.model.eventbus.ChangedTab;
import ch.accountmaker.model.eventbus.DocumentNewEntity;
import ch.accountmaker.model.eventbus.Msg;
import ch.accountmaker.utils.SynchronizeInstance;
import ch.accountmaker.widget.DocumentNew;
import cn.bmob.v3.Bmob;
import de.greenrobot.event.EventBus;

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("InflateParams")
public class MainActivity extends ActionBarActivity
		implements ToolbarManager.OnToolbarGroupChangedListener, OnClickListener {
	@Bind(R.id.main_dl)
	DrawerLayout dl_navigator;
	@Bind(R.id.main_fl_drawer)
	FrameLayout fl_drawer;
	@Bind(R.id.main_lv_drawer)
	ListView lv_drawer;
	@Bind(R.id.main_vp)
	CustomViewPager vp;
	@Bind(R.id.main_tpi)
	TabPageIndicator tpi;
	@Bind(R.id.search)
	ImageButton searchButton;

	private DrawerAdapter mDrawerAdapter;
	private PagerAdapter mPagerAdapter;
	@Bind(R.id.main_toolbar)
	Toolbar mToolbar;
	private ToolbarManager mToolbarManager;
	@Bind(R.id.main_sn)
	SnackBar mSnackBar;
	@Bind(R.id.item_search)
	EditText searchItem;
	@Bind(R.id.doc_search_customer)
	Spinner searchCustomer;
	@Bind(R.id.doc_search_year)
	EditText searchYear;
	@Bind(R.id.doc_search_month)
	EditText searchMonth;
	@Bind(R.id.doc_search_layout)
	LinearLayout searchDocLayout;
	@Bind(R.id.item_search_layout)
	LinearLayout searchItemLayout;


	private List<String> mItems = new ArrayList<>();

	@BindString(R.string.document_category)
	String tabDocCategory;
	@BindString(R.string.document_category_grid)
	String tabDocCategoryGv;
	@BindString(R.string.item_modify)
	String itemModify;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EventBus.getDefault().register(this);
		ButterKnife.bind(this);
		Bmob.initialize(getApplicationContext(), "3e8abc19113b47ad6fb46764be91d54b");
		mToolbar.setTitle("");
		mToolbarManager = new ToolbarManager(this, mToolbar, 0, R.style.ToolbarRippleStyle, R.anim.abc_fade_in,
				R.anim.abc_fade_out);
		mToolbarManager.setNavigationManager(new ToolbarManager.BaseNavigationManager(R.style.NavigationDrawerDrawable,
				this, mToolbar, dl_navigator) {
			@Override
			public void onNavigationClick() {
				if (mToolbarManager.getCurrentGroup() != 0)
					mToolbarManager.setCurrentGroup(0);
				else
					dl_navigator.openDrawer(Gravity.START);
			}

			@Override
			public boolean isBackState() {
				return super.isBackState() || mToolbarManager.getCurrentGroup() != 0;
			}

			@Override
			protected boolean shouldSyncDrawerSlidingProgress() {
				return super.shouldSyncDrawerSlidingProgress() && mToolbarManager.getCurrentGroup() == 0;
			}

		});
		mToolbarManager.registerOnToolbarGroupChangedListener(this);

		mDrawerAdapter = new DrawerAdapter();
		lv_drawer.setAdapter(mDrawerAdapter);
		mItems.add("首页");
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mItems);
		vp.setAdapter(mPagerAdapter);
		tpi.setViewPager(vp);
//		tpi.setOnLongClickListener(new View.OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				if(v instanceof CheckedTextView) {
//					CheckedTextView ctv = (CheckedTextView) v;
//					String tab = ctv.getText().toString();
//					if(mPagerAdapter.mTabs.contains(tab)){
//						int index = mPagerAdapter.mTabs.indexOf(tab);
//						mPagerAdapter.mTabs.remove(index);
//						mPagerAdapter.mFragments.remove(index);
//						mPagerAdapter.notifyDataSetChanged();
////						Toast.makeText(getApplicationContext(), "是否关闭 "+ctv.getText(), Toast.LENGTH_SHORT).show();
//					}
//					
//				}
//				return false;
//			}
//		});
		tpi.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				vp.setCurrentItem(position);
				mSnackBar.dismiss();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}

		});
		

		vp.setCurrentItem(0);

		searchButton.setOnClickListener(this);

		
		
	}

	private void updateToNewAll() {
		//TODO
		
		Item item = new Item();
		item.setIsNew(1);
		item.updateAll();
		
		Customer c = new Customer();
		c.setIsNew(1);
		c.updateAll();
		
		Document doc = new Document();
		doc.setIsNew(1);
		doc.updateAll();
		
		Material m = new Material();
		m.setIsNew(1);
		m.updateAll();
		
		DocumentItemDetail d = new DocumentItemDetail();
		d.setIsNew(1);
		d.updateAll();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mToolbarManager.createMenu(R.menu.menu_main);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mToolbarManager.onPrepareMenu();
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.tb_done:
		case R.id.tb_done_all:
			mToolbarManager.setCurrentGroup(0);
			break;
		}
		return true;
	}

	@Override
	public void onToolbarGroupChanged(int oldGroupId, int groupId) {
		mToolbarManager.notifyNavigationStateChanged();
	}

	public SnackBar getSnackBar() {
		return mSnackBar;
	}

	class DrawerAdapter extends BaseAdapter implements View.OnClickListener {

		List<String> mList;

		public DrawerAdapter() {
			mList = new ArrayList<>();
			mList.add("添加");
			mList.add("打开");
			mList.add("打开文档列表");
			mList.add("编辑产品信息");
			mList.add("同步");
			mList.add("下载全部信息");
//			mList.add("全部标记为新");
//			mList.add("清空doc");
//			mList.add("清空detail");
//			mList.add("清空objId");
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = LayoutInflater.from(MainActivity.this).inflate(R.layout.row_drawer, null);
				v.setOnClickListener(this);
			}

			v.setTag(position);
			String tab = (String) getItem(position);
			((TextView) v).setText(tab);

			return v;
		}

		@Override
		public void onClick(View v) {
			String txt = ((TextView) v).getText().toString();
			switch (txt) {
			case "添加":
				AddNewDocument();
				break;
			case "打开":
				OpenFragmentTab(tabDocCategory, DocumentOpenFragment.newInstance());
				break;
			case "打开文档列表":
				OpenFragmentTab(tabDocCategoryGv, new CustomerDocumentFragment());
				break;
			case "编辑产品信息":
				OpenFragmentTab(itemModify, ItemModifyFragment.newInstance());
				break;
			case "同步":
				SynchronizeInstance.synchronize(getApplicationContext());
				break;
			case "下载全部信息":
				SynchronizeInstance.downloadServerData(MainActivity.this, getSupportFragmentManager());
				break;
			case "全部标记为新":
				updateToNewAll();
				break;
			case "清空doc":
				DataSupport.deleteAll(Document.class);
				break;
			case "清空detail":
				DataSupport.deleteAll(DocumentItemDetail.class);
				break;
			case "清空objId":
				clearAllObjId();
				break;
			default:
				break;
			}
			mPagerAdapter.notifyDataSetChanged();
			dl_navigator.closeDrawer(fl_drawer);
		}

		

		int count = 0;

		

	}
	private void OpenFragmentTab(String tabName, Fragment f) {
		if (!mPagerAdapter.mTabs.contains(tabName)) {
			mPagerAdapter.addPager(tabName, f);
			mPagerAdapter.notifyDataSetChanged();
		}
		int index = mPagerAdapter.mTabs.indexOf(tabName);
		vp.setCurrentItem(index);
	}
	
	private void AddNewDocument() {
		DocumentNew docNewWidget = new DocumentNew(MainActivity.this, getSupportFragmentManager());
		docNewWidget.build(-1, -1);
		docNewWidget.show();
	}

	private static class PagerAdapter extends FragmentStatePagerAdapter {

		List<Fragment> mFragments;
		List<String> mTabs;

		public PagerAdapter(FragmentManager fm, List<String> tabs) {
			super(fm);
			mTabs = tabs;
			mFragments = new ArrayList<>();
			mFragments.add(StartPageFragment.newInstance());

		}

		public void addPager(String tab, Fragment fragment) {
			mTabs.add(tab);
			mFragments.add(fragment);
			setFragment(tab, fragment);

		}

		private void setFragment(String tab, Fragment f) {
			for (int i = 0; i < mTabs.size(); i++)
				if (mTabs.get(i).equals(tab)) {
					mFragments.set(i, f);
					break;
				}
		}

		@Override
		public Fragment getItem(int position) {

			return mFragments.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTabs.get(position).toString();
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}
		
		
	}

	public void onEventMainThread(Document doc) {
		openDocument(doc);
	}

	public void onEventMainThread(DocumentNewEntity docNew) {
		saveNewDocuemnt(docNew);
	}

	private void saveNewDocuemnt(DocumentNewEntity docNew) {
		// 保存新文档
		Document doc = new Document();
		doc.setBeginMonth(docNew.getMonth() + 1);
		doc.setBeginYear(docNew.getYear());
		doc.setReceiver(docNew.getCustomer());
		doc.setTimeText(docNew.getYear() + "." + (docNew.getMonth() + 1));
		doc.setCompleted(false);
		doc.setTitle((docNew.getCustomer() != null ? docNew.getCustomer().getName() : "\t") + "\t" + doc.getTimeText());
		doc.setPrinted(false);
		doc.setLastEditTime(new Date());
		doc.setIsNew(1);
		
		doc = DataService.getInstance().saveDocument(doc);
		// 打开该文档
		openDocument(doc);
		// 刷新 文档列表 页面
		refreshDocumentCategory();
	}

	public void onEventMainThread(ChangedTab changedTab) {
		// 修改标题栏
		for (int i = 0; i < mPagerAdapter.mTabs.size(); i++) {
			String title = mPagerAdapter.mTabs.get(i);
			if (changedTab.getOldTitle().equals(title)) {
				mPagerAdapter.mTabs.set(i, changedTab.getNewTitle());
				mPagerAdapter.notifyDataSetChanged();
			}
		}

	}

	private void openDocument(Document doc) {
		DocumentFragment f = new DocumentFragment(doc);
		String tab = doc.getTitle();
		if (!mPagerAdapter.mTabs.contains(tab)) {
			mPagerAdapter.addPager(tab, f);
			mPagerAdapter.notifyDataSetChanged();
		}
		int index = mPagerAdapter.mTabs.indexOf(tab);
		vp.setCurrentItem(index);
		mPagerAdapter.notifyDataSetChanged();
	}

	/**
	 * 如果文档列表页面存在，则刷新页面
	 */
	public void refreshDocumentCategory() {
		if (mPagerAdapter.mTabs.contains(tabDocCategory)) {
			int index = mPagerAdapter.mTabs.indexOf("文档列表");
			Fragment fragment = mPagerAdapter.mFragments.get(index);
			if (fragment instanceof DocumentOpenFragment) {
				((DocumentOpenFragment) fragment).refreshData();
			}
		}
	}

	public void onEventMainThread(Msg msg) {
		if (msg.getMsg().equals("refresh_category")) {
			refreshDocumentCategory();
		} else if(msg.getMsg().equals(getResources().getString(R.string.open_doc))) {
			OpenFragmentTab(tabDocCategoryGv, new CustomerDocumentFragment());
		} else if(msg.getMsg().equals(getResources().getString(R.string.add_doc))) {
			AddNewDocument();
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:
			View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_layout, null);
			PopupWindow pop = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			pop.showAsDropDown(v);
			break;
		default:
			break;
		}
	}
	
	public void clearAllObjId() {
		Item item = new Item();
		item.setObjId(0);
		item.updateAll();
		
		Customer c = new Customer();
		c.setObjId(0);
		c.updateAll();
		
		Document doc = new Document();
		doc.setObjId(0);
		doc.updateAll();
		
		Material m = new Material();
		m.setObjId(0);
		m.updateAll();
		
		DocumentItemDetail d = new DocumentItemDetail();
		d.setObjId(0);
		d.updateAll();
	}
	
}
