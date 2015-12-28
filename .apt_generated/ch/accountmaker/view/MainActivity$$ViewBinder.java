// Generated code from Butter Knife. Do not modify!
package ch.accountmaker.view;

import android.content.res.Resources;
import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends ch.accountmaker.view.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361931, "field 'mSnackBar'");
    target.mSnackBar = finder.castView(view, 2131361931, "field 'mSnackBar'");
    view = finder.findRequiredView(source, 2131361930, "field 'vp'");
    target.vp = finder.castView(view, 2131361930, "field 'vp'");
    view = finder.findRequiredView(source, 2131361922, "field 'searchButton'");
    target.searchButton = finder.castView(view, 2131361922, "field 'searchButton'");
    view = finder.findRequiredView(source, 2131361921, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131361921, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131361924, "field 'searchItemLayout'");
    target.searchItemLayout = finder.castView(view, 2131361924, "field 'searchItemLayout'");
    view = finder.findRequiredView(source, 2131361927, "field 'searchCustomer'");
    target.searchCustomer = finder.castView(view, 2131361927, "field 'searchCustomer'");
    view = finder.findRequiredView(source, 2131361925, "field 'searchItem'");
    target.searchItem = finder.castView(view, 2131361925, "field 'searchItem'");
    view = finder.findRequiredView(source, 2131361929, "field 'searchMonth'");
    target.searchMonth = finder.castView(view, 2131361929, "field 'searchMonth'");
    view = finder.findRequiredView(source, 2131361926, "field 'searchDocLayout'");
    target.searchDocLayout = finder.castView(view, 2131361926, "field 'searchDocLayout'");
    view = finder.findRequiredView(source, 2131361932, "field 'fl_drawer'");
    target.fl_drawer = finder.castView(view, 2131361932, "field 'fl_drawer'");
    view = finder.findRequiredView(source, 2131361920, "field 'dl_navigator'");
    target.dl_navigator = finder.castView(view, 2131361920, "field 'dl_navigator'");
    view = finder.findRequiredView(source, 2131361923, "field 'tpi'");
    target.tpi = finder.castView(view, 2131361923, "field 'tpi'");
    view = finder.findRequiredView(source, 2131361928, "field 'searchYear'");
    target.searchYear = finder.castView(view, 2131361928, "field 'searchYear'");
    view = finder.findRequiredView(source, 2131361933, "field 'lv_drawer'");
    target.lv_drawer = finder.castView(view, 2131361933, "field 'lv_drawer'");
    Resources res = finder.getContext(source).getResources();
    target.tabDocCategory = res.getString(2131427358);
    target.itemModify = res.getString(2131427360);
    target.tabDocCategoryGv = res.getString(2131427359);
  }

  @Override public void unbind(T target) {
    target.mSnackBar = null;
    target.vp = null;
    target.searchButton = null;
    target.mToolbar = null;
    target.searchItemLayout = null;
    target.searchCustomer = null;
    target.searchItem = null;
    target.searchMonth = null;
    target.searchDocLayout = null;
    target.fl_drawer = null;
    target.dl_navigator = null;
    target.tpi = null;
    target.searchYear = null;
    target.lv_drawer = null;
  }
}
