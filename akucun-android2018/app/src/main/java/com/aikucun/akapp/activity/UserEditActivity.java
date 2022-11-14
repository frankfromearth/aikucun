package com.aikucun.akapp.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.MToaster;
import com.alibaba.fastjson.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.aikucun.akapp.R.id.save_button;

/**
 * Created by micker on 2017/7/12.
 */

public class UserEditActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.nickname_edit)
    EditText userNameEdit;

    @BindView(save_button)
    Button saveButton;

    private String name;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.update_user_info);
    }

    @Override
    public void initData() {
        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        if (null != userInfo) {
            userNameEdit.setText(userInfo.getName());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_edit;
    }


    @Override
    protected void onResume() {
        super.onResume();
        userNameEdit.requestFocus();
    }

    @Override
    @OnClick({save_button})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case save_button: {
                hideInputKeyboard();

                name = userNameEdit.getText().toString();
                saveAction();
            }
            break;

            default:
                break;
        }
    }

    private void hideInputKeyboard() {
        if (userNameEdit.isFocused()) {
            InputMethodUtils.hideInputKeyboard(this, userNameEdit);
        }
    }

    private void saveAction() {
        if (name.length() == 0) {
            showMessage(getString(R.string.input_nick_name));
            userNameEdit.requestFocus();
            return;
        }

        UsersApiManager.userUpdateInfo(this, name, "", new JsonDataCallback() {

            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                MToaster.showShort(UserEditActivity.this, R.string.update_success, MToaster.IMG_INFO);
                saveButton.setEnabled(true);
                UserInfo userInfo = AppContext.getInstance().getUserInfo();
                userInfo.setName(name);
                setResult(RESULT_OK);
                finish();
            }
        });

    }
}
