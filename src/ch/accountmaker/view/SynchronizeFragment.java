package ch.accountmaker.view;

import android.R.color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import ch.accountmaker.R;

public class SynchronizeFragment extends Fragment{
	
		
	public static SynchronizeFragment newInstance(){
		SynchronizeFragment fragment = new SynchronizeFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_start_page, null);
//		FrameLayout f = (FrameLayout) v.findViewById(R.id.frame);
//		ImageView iv = new ImageView(getActivity());
//		iv.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		iv.setBackgroundColor(color.black);
//		f.addView(iv);
//		ImageView iv2 = new ImageView(getActivity());
//		iv2.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		iv2.setBackgroundColor(color.white);
//		f.addView(iv2, new FrameLayout.LayoutParams(300, 300));
		return v;
	}


}
