package com.aikucun.akapp.activity;

import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.fragment.SearchFragment;
import com.aikucun.akapp.utils.InputMethodUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by jarry on 2017/6/11.
 */

public class SearchActivity extends PopActivity implements TextView.OnEditorActionListener,View.OnClickListener, SearchFragment.onLoadMoreListener {

    @BindView(R.id.search_edit)
    EditText searchEdit;

    @BindView(R.id.search_cancel_btn)
    TextView searchCancel;


    private SearchFragment fragment;

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_search;
    }

    @Override
    public void initView()
    {
        fragment = new SearchFragment();
        fragment.setListener(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.realtabcontent, fragment, "");
        fragmentTransaction.commitAllowingStateLoss();

        searchEdit.setOnEditorActionListener(this);

        searchCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodUtils.hideInputKeyboard(SearchActivity.this, searchEdit);
                finish();
            }
        });
        configWindow(null);
    }

    @Override
    public void initData()
    {
        onInitialZhuanFa();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        String keyword = searchEdit.getText().toString();

        if (actionId == EditorInfo.IME_ACTION_SEARCH)
        {
            InputMethodUtils.hideInputKeyboard(SearchActivity.this, searchEdit);
            fragment.setKeyword(keyword);
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppConfig.MessageEvent event) {

        if (event.messageId.equalsIgnoreCase(AppConfig.SEACH_KEY_WORDS)) {
            String keyword = (String)event.content;
            searchEdit.setText(keyword);
            searchEdit.setSelection(keyword.length());
            InputMethodUtils.hideInputKeyboard(SearchActivity.this, searchEdit);
        }
    }

    @Override
    public void onLoadMore(boolean hasData) {
//        tips_rl.setVisibility(hasData?View.GONE:View.VISIBLE);
    }
}
