/**
 	Copyright 2014 SuRendra Reddy P.V

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package ch.accountmaker.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Spinner.OnItemClickListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Toast;
import ch.accountmaker.R;
import ch.accountmaker.dao.DataService;
import ch.accountmaker.model.Customer;
import ch.accountmaker.model.eventbus.DocumentNewEntity;
import ch.accountmaker.model.eventbus.YearMonth;
import de.greenrobot.event.EventBus;

/**
 * The Class MonthYearPicker.
 *
 * @author SuRendra Reddy
 */
@SuppressLint("InflateParams")
public class DocumentNew {

	int type = -1;

	int docId;

	private static String ADD_NEW_CUSTOMER = "";

	private static final int MIN_YEAR = 1970;

	private static final int MAX_YEAR = 2099;

	private static final String[] PICKER_DISPLAY_MONTHS_NAMES = new String[] { "一月", "二月", "三月", "四月", "五月", "六月", "七月",
			"八月", "九月", "十月", "十一月", "十二月" };

	// private static final String[] MONTHS = new String[] { "January",
	// "February", "March", "April", "May", "June",
	// "July", "August", "September", "October", "November", "December" };

	private Context mContext;
	private FragmentManager mFragmentManager;
	private SimpleDialog.Builder builder;
	private NumberPicker monthNumberPicker;
	private NumberPicker yearNumberPicker;
	private Spinner spCustomer;

	private List<String> cStringList;

	private int currentYear;

	private int currentMonth;

	private List<Customer> cList;

	private ArrayAdapter<String> mAdapter;

	/**
	 * Instantiates a new month year picker.
	 *
	 * @param activity
	 *            the activity
	 */
	public DocumentNew(Context c, FragmentManager fm) {
		this.mContext = c;
		this.mFragmentManager = fm;
		ADD_NEW_CUSTOMER = c.getString(R.string.add_new_customer);
	}

	/**
	 * Builds the month year alert dialog.
	 *
	 * @param positiveButtonListener
	 *            the positive listener
	 * @param negativeButtonListener
	 *            the negative listener
	 */

	/**
	 * Builds the month year alert dialog.
	 *
	 * @param selectedMonth
	 *            the selected month 0 to 11 (sets current moth if invalid
	 *            value)
	 * @param selectedYear
	 *            the selected year 1970 to 2099 (sets current year if invalid
	 *            value)
	 * @param positiveButtonListener
	 *            the positive listener
	 * @param negativeButtonListener
	 *            the negative listener
	 */
	public void build(int selectedMonth, int selectedYear) {

		initSpinner();

		final Calendar instance = Calendar.getInstance();
		currentMonth = instance.get(Calendar.MONTH);
		currentYear = instance.get(Calendar.YEAR);

		final int finalMonth;
		final int finalYear;

		if (selectedMonth > 11 || selectedMonth < 0) {
			finalMonth = currentMonth;
		} else {
			finalMonth = selectedMonth;
		}

		if (selectedYear < MIN_YEAR || selectedYear > MAX_YEAR || selectedYear == -1) {
			finalYear = currentYear;
		} else {
			finalYear = selectedYear;
		}

		builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

			@Override
			protected void onBuildDone(Dialog dialog) {
				super.onBuildDone(dialog);
				monthNumberPicker = (NumberPicker) dialog.findViewById(R.id.monthNumberPicker);
				monthNumberPicker.setDisplayedValues(PICKER_DISPLAY_MONTHS_NAMES);

				monthNumberPicker.setMinValue(0);
				monthNumberPicker.setMaxValue(PICKER_DISPLAY_MONTHS_NAMES.length - 1);

				yearNumberPicker = (NumberPicker) dialog.findViewById(R.id.yearNumberPicker);
				yearNumberPicker.setMinValue(MIN_YEAR);
				yearNumberPicker.setMaxValue(MAX_YEAR);

				monthNumberPicker.setValue(finalMonth);
				yearNumberPicker.setValue(finalYear);

				monthNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
				yearNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

				spCustomer = (Spinner) dialog.findViewById(R.id.doc_new_sp);
				spCustomer.setAdapter(mAdapter);
				spCustomer.setOnItemClickListener(itemClickListener);
			}

			@Override
			public void onPositiveActionClicked(DialogFragment fragment) {
				DocumentNewEntity newDoc = new DocumentNewEntity(getSelectedYear(), getSelectedMonth());
				if (spCustomer != null) {
					String name = (String) spCustomer.getSelectedItem();
					for (Customer c : cList) {
						if (c.getName().equals(name)) {
							newDoc.setCustomer(c);

						}
					}
					EventBus.getDefault().post(newDoc);
					super.onPositiveActionClicked(fragment);
				} else {

				}

			}

			@Override
			public void onNegativeActionClicked(DialogFragment fragment) {
				super.onNegativeActionClicked(fragment);
			}
		};
		String title = "";
		switch (type) {
		case YearMonth.BEGIN:
			title = "选择开始时间";
			break;
		case YearMonth.END:
			title = "选择结束时间";
			break;
		default:
			break;
		}

		builder.title(title).positiveAction("确定").negativeAction("取消").contentView(R.layout.document_new_dialog);

	}

	public void show() {
		DialogFragment fragment = DialogFragment.newInstance(builder);
		fragment.show(mFragmentManager, null);
	}

	public int getSelectedMonth() {
		return monthNumberPicker.getValue();
	}

	public String getSelectedMonthShortName() {
		return PICKER_DISPLAY_MONTHS_NAMES[monthNumberPicker.getValue()];
	}

	public int getSelectedYear() {
		return yearNumberPicker.getValue();
	}

	public void setMonthValueChangedListener(NumberPicker.OnValueChangeListener valueChangeListener) {
		monthNumberPicker.setOnValueChangedListener(valueChangeListener);
	}

	/**
	 * Sets the year value changed listener.
	 *
	 * @param valueChangeListener
	 *            the new year value changed listener
	 */
	public void setYearValueChangedListener(NumberPicker.OnValueChangeListener valueChangeListener) {
		yearNumberPicker.setOnValueChangedListener(valueChangeListener);
	}

	/**
	 * Sets the month wrap selector wheel.
	 *
	 * @param wrapSelectorWheel
	 *            the new month wrap selector wheel
	 */
	public void setMonthWrapSelectorWheel(boolean wrapSelectorWheel) {
		monthNumberPicker.setWrapSelectorWheel(wrapSelectorWheel);
	}

	/**
	 * Sets the year wrap selector wheel.
	 *
	 * @param wrapSelectorWheel
	 *            the new year wrap selector wheel
	 */
	public void setYearWrapSelectorWheel(boolean wrapSelectorWheel) {
		yearNumberPicker.setWrapSelectorWheel(wrapSelectorWheel);
	}

	private void initSpinner() {
		cList = DataService.getInstance().queryAllCustomers();
		cStringList = new ArrayList<>();
		cStringList.add("");
		cStringList.add(ADD_NEW_CUSTOMER);

		for (Customer c : cList) {
			cStringList.add(c.getName() == null ? "" : c.getName());
		}
		mAdapter = new ArrayAdapter<>(mContext, R.layout.simple_spinner_item, cStringList);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {
		private EditText etCustomerName;
		private EditText etCustomerQQ;
		private EditText etCustomerEmail;

		@Override
		public boolean onItemClick(Spinner parent, View view, int position, long id) {
			String selection = cStringList.get(position);
			if (selection.equals(ADD_NEW_CUSTOMER)) {
				// 添加新客户
				SimpleDialog.Builder builder;
				builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {

					@Override
					protected void onBuildDone(Dialog dialog) {
						super.onBuildDone(dialog);
						etCustomerName = (EditText) dialog.findViewById(R.id.customer_name);
						etCustomerQQ = (EditText) dialog.findViewById(R.id.customer_qq);
						etCustomerEmail = (EditText) dialog.findViewById(R.id.customer_email);
					}

					@Override
					public void onPositiveActionClicked(DialogFragment fragment) {
						if (etCustomerName != null) {
							String name = etCustomerName.getText().toString();
							if (!"".equals(name)) {
								Customer c = new Customer();
								c.setName(name);
								c.setQq(etCustomerQQ.getText().toString());
								c.setEmail(etCustomerEmail.getText().toString());
								c.setLastEditTime(new Date());
								c = DataService.getInstance().saveCustomer(c);
								cList.add(c);
								cStringList.add(name);
								mAdapter.notifyDataSetChanged();
								int index = cStringList.indexOf(name);
								spCustomer.setSelection(index);
								super.onPositiveActionClicked(fragment);
							} else {
								Toast.makeText(mContext, "请输入客户名", Toast.LENGTH_SHORT).show();
							}
						}

					}

					@Override
					public void onNegativeActionClicked(DialogFragment fragment) {
						super.onNegativeActionClicked(fragment);
					}
				};

				builder.title("新建客户").positiveAction("确定").negativeAction("取消").contentView(R.layout.customer_dialog);
				DialogFragment fragment = DialogFragment.newInstance(builder);
				fragment.show(mFragmentManager, null);
			}
			return true;
		}
	};

}
