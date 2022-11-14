package com.aikucun.akapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.AddressListAdapter;
import com.aikucun.akapp.api.callback.AddressListCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.Address;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.AddressComparator;
import com.aikucun.akapp.utils.AddressUtils;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.aikucun.akapp.adapter.AddressListAdapter.ADDRESS_EVENT_CHOOSE;
import static com.aikucun.akapp.adapter.AddressListAdapter.ADDRESS_EVENT_DEFAULT;
import static com.aikucun.akapp.adapter.AddressListAdapter.ADDRESS_EVENT_DELETE;
import static com.aikucun.akapp.adapter.AddressListAdapter.ADDRESS_EVENT_EDIT;
import static com.lzy.okgo.OkGo.getContext;

/**
 * Created by jarry on 2017/6/11.
 */

public class AddressListActivity extends BaseActivity implements AddressListAdapter.OnItemEventListener {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.buyBtn)
    TextView buyBtn;
    @BindView(R.id.bottomLayout)
    RelativeLayout bottomLayout;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @BindView(R.id.btn_right)
    TextView btn_right;


    private boolean isChoose = false;
    private AddressListAdapter addressListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_address_list;
    }

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.address_manager);
        btn_right.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);

        addressListAdapter = new AddressListAdapter(getContext());
        addressListAdapter.setOnItemEventListener(this);
        recyclerView.setAdapter(addressListAdapter);
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestAddreses();
                    }
                }, 600);
            }
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddressListActivity.this,AddressEditActivity.class),12);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            this.isChoose = intent.getBooleanExtra("isChoose",false);
            addressListAdapter.isChooose = this.isChoose;
            if (this.isChoose) {
                mTitleText.setText(R.string.choose_address);
            }
        }
    }

    @Override
    public void initData() {

        recyclerView.setRefreshing(true);
        requestAddreses();
    }

    private void requestAddreses() {

        UsersApiManager.userAddressList(this,new AddressListCallback(){
            @Override
            public void onApiSuccess(List<Address> addresses, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(addresses, call, jsonResponse);
                recyclerView.setRefreshing(false);
                UserInfo userInfo = AppContext.getInstance().getUserInfo();
                Collections.sort(addresses,new AddressComparator());
                // FIXME: 2018/1/4
                AddressUtils.setAddresses(addresses);
//                userInfo.setAddrList(addresses);
                addressListAdapter.clear();
                addressListAdapter.addAll(addresses);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);

            }
        });
    }

    private void requestDelAddreses(final Address address, final int position) {

        UsersApiManager.userDeleteAddress(this,address.getAddrid(),new JsonDataCallback(){
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                Address selectedAddress = AddressUtils.getSelectedAddress();
                if(address.getAddrid().equalsIgnoreCase(selectedAddress.getAddrid())){
                    AddressUtils.setSelectedAddress(AddressUtils.getDefaultAddress());
                }
                addressListAdapter.remove(position);
                addressListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

    private void requestDefaultAddreses(Address address, int position) {

        UsersApiManager.userDefaultAddress(this,address.getAddrid(),new JsonDataCallback(){
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                requestAddreses();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

    @Override
    @OnClick({R.id.buyBtn})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buyBtn: {
            }
            break;
            default:
                break;
        }
    }
            @Override
            public void onEvent(int event, final Address address, final int position) {
                switch (event) {
                    case ADDRESS_EVENT_DELETE: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getResources().getString(R.string.delete_address_dialog_content)).setNegativeButton(getResources().getString(R.string.cancel), null).setPositiveButton
                                (getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestDelAddreses(address, position);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                    }
                    break;
                    case ADDRESS_EVENT_DEFAULT: {


                        UsersApiManager.userDefaultAddress(this,address.getAddrid(),new JsonDataCallback() {
                            @Override
                            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                                super.onApiSuccess(jsonObject, call, jsonResponse);
                                requestAddreses();
                            }

                            @Override
                            public void onApiFailed(String message, int code) {
                                super.onApiFailed(message, code);
                            }
                        });
                    }
                    break;
                    case ADDRESS_EVENT_EDIT: {

                        Intent intent = new Intent(this,AddressEditActivity.class);
                        intent.putExtra("address",address);
                        startActivityForResult(intent, 12);
                    }
                    break;
                    case ADDRESS_EVENT_CHOOSE: {

                        Intent intent = new Intent();
                        intent.putExtra("address",address);
                        UserInfo userInfo = AppContext.getInstance().getUserInfo();
                        // FIXME: 2018/1/4
                        AddressUtils.setSelectedAddress(address);
//                        userInfo.setSelectAddr(address);
                        setResult(100,intent);
                        finish();
                    }
                    break;
                }
            }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (12 == requestCode) {
            requestAddreses();
        }
    }
}
