// Generated code from Butter Knife. Do not modify!
package io.github.izzyleung.zhihudailypurify.ui.activity;

import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import io.github.izzyleung.zhihudailypurify.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding extends BaseActivity_ViewBinding {
  private MainActivity target;

  private View view2131558509;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(final MainActivity target, View source) {
    super(target, source);

    this.target = target;

    View view;
    target.mTabs = Utils.findRequiredViewAsType(source, R.id.main_pager_tabs, "field 'mTabs'", TabLayout.class);
    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.main_pager, "field 'mViewPager'", ViewPager.class);
    view = Utils.findRequiredView(source, R.id.fab_pick_date, "method 'onFloatingButtonClick'");
    view2131558509 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onFloatingButtonClick();
      }
    });
  }

  @Override
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTabs = null;
    target.mViewPager = null;

    view2131558509.setOnClickListener(null);
    view2131558509 = null;

    super.unbind();
  }
}
