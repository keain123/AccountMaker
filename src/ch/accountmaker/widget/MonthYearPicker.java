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

import java.util.Calendar;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.widget.NumberPicker;
import ch.accountmaker.R;
import ch.accountmaker.model.eventbus.YearMonth;
import de.greenrobot.event.EventBus;

/**
 * The Class MonthYearPicker.
 *
 * @author SuRendra Reddy
 */
@SuppressLint("InflateParams")
public class MonthYearPicker {
	
	int type = -1;
	
	int docId;

	private static final int MIN_YEAR = 1970;

	private static final int MAX_YEAR = 2099;

	private static final String[] PICKER_DISPLAY_MONTHS_NAMES = new String[] { "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月",
			"十一月", "十二月" };

//	private static final String[] MONTHS = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September",
//			"October", "November", "December" };

	private FragmentManager mFragmentManager;
	private SimpleDialog.Builder builder;
	private NumberPicker monthNumberPicker;
	private NumberPicker yearNumberPicker;

	/**
	 * Instantiates a new month year picker.
	 *
	 * @param activity
	 *            the activity
	 */
	public MonthYearPicker(FragmentManager fm, int type, int docId) {
		mFragmentManager = fm;
		this.type = type;
		this.docId = docId;
	}

	/**
	 * Builds the month year alert dialog.
	 *
	 * @param positiveButtonListener
	 *            the positive listener
	 * @param negativeButtonListener
	 *            the negative listener
	 */

	private int currentYear;

	private int currentMonth;

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

			}
			
			@Override
			public void onPositiveActionClicked(DialogFragment fragment) {
				super.onPositiveActionClicked(fragment);
				YearMonth yearMonth = new YearMonth(getSelectedYear(), getSelectedMonth(), type);
				yearMonth.setDocId(docId);
				EventBus.getDefault().post(yearMonth);
				
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
		
		builder.title(title)
		.positiveAction("确定")
        .negativeAction("取消")
        .contentView(R.layout.month_year_picker_view);

		
		

	}

	/**
	 * Show month year picker dialog.
	 */
	public void show() {
		DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(mFragmentManager, null);
	}

	/**
	 * Gets the selected month.
	 *
	 * @return the selected month
	 */
	public int getSelectedMonth() {
		return monthNumberPicker.getValue();
	}


	/**
	 * Gets the selected month name.
	 *
	 * @return the selected month short name i.e Jan, Feb ...
	 */
	public String getSelectedMonthShortName() {
		return PICKER_DISPLAY_MONTHS_NAMES[monthNumberPicker.getValue()];
	}

	/**
	 * Gets the selected year.
	 *
	 * @return the selected year
	 */
	public int getSelectedYear() {
		return yearNumberPicker.getValue();
	}

	/**
	 * Gets the current year.
	 *
	 * @return the current year
	 */
	public int getCurrentYear() {
		return currentYear;
	}

	/**
	 * Gets the current month.
	 *
	 * @return the current month
	 */
	public int getCurrentMonth() {
		return currentMonth;
	}

	/**
	 * Sets the month value changed listener.
	 *
	 * @param valueChangeListener
	 *            the new month value changed listener
	 */
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
	
}
