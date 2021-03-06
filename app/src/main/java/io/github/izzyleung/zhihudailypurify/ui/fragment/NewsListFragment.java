package io.github.izzyleung.zhihudailypurify.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.github.izzyleung.zhihudailypurify.R;
import io.github.izzyleung.zhihudailypurify.ZhihuDailyPurifyApplication;
import io.github.izzyleung.zhihudailypurify.adapter.NewsAdapter;
import io.github.izzyleung.zhihudailypurify.bean.DailyNews;
import io.github.izzyleung.zhihudailypurify.observable.NewsListFromAccelerateServerObservable;
import io.github.izzyleung.zhihudailypurify.observable.NewsListFromDatabaseObservable;
import io.github.izzyleung.zhihudailypurify.observable.NewsListFromZhihuObservable;
import io.github.izzyleung.zhihudailypurify.support.Constants;
import io.github.izzyleung.zhihudailypurify.task.SaveNewsListTask;
import io.github.izzyleung.zhihudailypurify.ui.activity.BaseActivity;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewsListFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, Observer<List<DailyNews>> {

    @BindView(R.id.news_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<DailyNews> newsList = new ArrayList<>();

    private String date;
    private NewsAdapter mAdapter;

    // Fragment is single in SingleDayNewsActivity
    private boolean isToday;
    private boolean isRefreshed = false;

    public static NewsListFragment newInstance(String date, boolean isFirstPage, boolean isSingle) {
        NewsListFragment fragment = new NewsListFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleKeys.DATE, date);
        bundle.putBoolean(Constants.BundleKeys.IS_FIRST_PAGE, isFirstPage);
        bundle.putBoolean(Constants.BundleKeys.IS_SINGLE, isSingle);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            date = bundle.getString(Constants.BundleKeys.DATE);
            isToday = bundle.getBoolean(Constants.BundleKeys.IS_FIRST_PAGE);

            setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView.setHasFixedSize(!isToday);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mAdapter = new NewsAdapter(newsList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_primary);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        NewsListFromDatabaseObservable.ofDate(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        refreshIf(shouldRefreshOnVisibilityChange(isVisibleToUser));
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_news_list;
    }

    private void refreshIf(boolean prerequisite) {
        if (prerequisite) {
            doRefresh();
        }
    }

    private void doRefresh() {
        getNewsListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private Observable<List<DailyNews>> getNewsListObservable() {
        if (shouldSubscribeToZhihu()) {
            return NewsListFromZhihuObservable.ofDate(date);
        } else {
            return NewsListFromAccelerateServerObservable.ofDate(date);
        }
    }

    private boolean shouldSubscribeToZhihu() {
        return isToday || !shouldUseAccelerateServer();
    }

    private boolean shouldUseAccelerateServer() {
        return ZhihuDailyPurifyApplication.getSharedPreferences()
                .getBoolean(Constants.SharedPreferencesKeys.KEY_SHOULD_USE_ACCELERATE_SERVER, false);
    }

    private boolean shouldAutoRefresh() {
        return ZhihuDailyPurifyApplication.getSharedPreferences()
                .getBoolean(Constants.SharedPreferencesKeys.KEY_SHOULD_AUTO_REFRESH, true);
    }

    private boolean shouldRefreshOnVisibilityChange(boolean isVisibleToUser) {
        return isVisibleToUser && shouldAutoRefresh() && !isRefreshed;
    }

    @Override
    public void onRefresh() {
        doRefresh();
    }

    @Override
    public void onNext(List<DailyNews> newsList) {
        this.newsList = newsList;
    }

    @Override
    public void onError(Throwable e) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (isAdded()) {
            ((BaseActivity) getActivity()).showSnackbar(R.string.network_error);
        }
    }

    @Override
    public void onCompleted() {
        isRefreshed = true;

        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.updateNewsList(newsList);

        new SaveNewsListTask(newsList).execute();
    }
}