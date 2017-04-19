package io.github.izzyleung.zhihudailypurify.ui.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * 作者:  qiang on 2017/4/19 23:16
 * 邮箱:  anworkmail_q@126.com
 * 作用:
 */

public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutRes(), container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            onFragmentVisible();
        } else if (!isVisibleToUser && isResumed()) {
            onFragmentNotVisible();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            onFragmentVisible();
        } else {
            onFragmentNotVisible();
        }
    }

    protected abstract @LayoutRes int getLayoutRes();

    protected void onFragmentVisible() {
    }

    protected void onFragmentNotVisible() {
    }
}
