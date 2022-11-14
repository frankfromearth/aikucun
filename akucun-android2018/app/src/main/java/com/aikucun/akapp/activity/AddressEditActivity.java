package com.aikucun.akapp.activity;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.api.entity.Address;
import com.aikucun.akapp.api.manager.DeliverApiManager;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.AddressPopWindow;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by jarry on 2017/6/11.
 */

public class AddressEditActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.address_option_city)
    View cityOptionView;

    @BindView(R.id.address_name_edit)
    EditText nameEdit;

    @BindView(R.id.address_mobile_edit)
    EditText mobileEdit;

    @BindView(R.id.address_addr_edit)
    EditText addressEdit;

    @BindView(R.id.address_title_city)
    TextView cityText;

    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.btn_tip)
    ImageButton btnTip;

    @BindView(R.id.rl_tip)
    RelativeLayout rlTip;

    @BindView(R.id.default_rr)
    RelativeLayout default_rr;

    @BindView(R.id.switchBtn)
    SwitchCompat switchBtn;

    protected AdOrder mAdOrder;


    private String name, shoujihao;
    private String sheng = "", shi = "", qu = "";
    private String detailAddr;

    private String addid;
    private boolean isDefaultFlag;

    private boolean isAddingAddress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_address;
    }

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.editor_address);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();

        Address address = (Address) intent.getSerializableExtra("address");
        AdOrder adOrder = (AdOrder) intent.getSerializableExtra("adOrder");
        if (address != null) {
            isAddingAddress = false;
            name = address.getShoujianren();
            shoujihao = address.getDianhua();
            sheng = address.getSheng();
            shi = address.getShi();
            qu = address.getQu();
            detailAddr = address.getDetailaddr();
            isDefaultFlag = address.getDefaultflag() == 1;

            addid = address.getAddrid();

            nameEdit.setText(name);
            mobileEdit.setText(shoujihao);
            addressEdit.setText(detailAddr);
            switchBtn.setChecked(isDefaultFlag);

            checkSheng();
        } else if (null != adOrder) {
            this.mAdOrder = adOrder;

            isAddingAddress = false;
            name = mAdOrder.getShouhuoren();
            shoujihao = mAdOrder.getLianxidianhua();

            String[] addr = mAdOrder.getShouhuodizhi().split(" ");
            List<String> addrList = java.util.Arrays.asList(addr);

            if (addr.length == 3) {
                addrList.add(0, addr[0]);
            }
            if (addrList.size() >= 4) {
                sheng = addrList.get(0);
                shi = addrList.get(1);
                qu = addrList.get(2);
                detailAddr = addrList.get(3);
            }

            nameEdit.setText(name);
            mobileEdit.setText(shoujihao);
            addressEdit.setText(detailAddr);
            default_rr.setVisibility(View.GONE);
            checkSheng();

        } else {
            isAddingAddress = true;
            mTitleText.setText(R.string.add_addr);
        }
        {
//            boolean didShowTip = AppContext.get("didShowAddressTip",false);
//            rlTip.setVisibility(didShowTip?View.GONE:View.VISIBLE);
        }
    }

    private void checkSheng() {
        if (sheng.length() > 0) {
            if (TextUtils.equals(sheng, shi)) {
                cityText.setText(sheng + "　" + qu);
            } else {
                cityText.setText(sheng + "　" + shi + "　" + qu);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameEdit.requestFocus();
    }

    @Override
    @OnClick({R.id.address_option_city, R.id.save_button, R.id.btn_tip})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.address_option_city: {
                hideInputKeyboard();

                AddressPopWindow addressPopWindow = new AddressPopWindow(this, sheng, shi, qu);
                addressPopWindow.showAtLocation(cityOptionView, Gravity.BOTTOM, 0, 0);
                addressPopWindow.setAddresskListener(new AddressPopWindow.OnAddressCListener() {
                    @Override
                    public void onClick(String province, String city, String area) {
                        sheng = province;
                        shi = city;
                        qu = area;

                        if (TextUtils.equals(sheng, shi)) {
                            cityText.setText(sheng + "　" + qu);
                        } else {
                            cityText.setText(sheng + "　" + shi + "　" + qu);
                        }
                    }
                });
            }
            break;

            case R.id.save_button: {
                hideInputKeyboard();

                name = nameEdit.getText().toString();
                shoujihao = mobileEdit.getText().toString();
                detailAddr = addressEdit.getText().toString();

                updateAddress();
            }
            break;

            case R.id.btn_tip: {
                AppContext.set("didShowAddressTip", true);
                rlTip.setVisibility(View.GONE);
            }
            break;

            default:
                break;
        }
    }

    private void hideInputKeyboard() {
        if (nameEdit.isFocused()) {
            InputMethodUtils.hideInputKeyboard(this, nameEdit);
        } else if (mobileEdit.isFocused()) {
            InputMethodUtils.hideInputKeyboard(this, mobileEdit);
        } else if (addressEdit.isFocused()) {
            InputMethodUtils.hideInputKeyboard(this, addressEdit);
        }
    }

    private void updateAddress() {
        if (name.length() == 0) {
            showMessage(R.string.input_consignee_name);
            return;
        }
        if (!StringUtils.isMobile(shoujihao)) {
            showMessage(R.string.input_right_mobile);
            return;
        }
        if (sheng.length() == 0 || shi.length() == 0 || qu.length() == 0) {
            showMessage(R.string.choose_city);
            return;
        }
        if (detailAddr.length() == 0) {
            showMessage(R.string.input_detail_address);
            return;
        }

        showProgress("");
        isDefaultFlag = switchBtn.isChecked();
        if (isAddingAddress) {
            UsersApiManager.userAddAddress(this, name, shoujihao, sheng, shi, qu, detailAddr, isDefaultFlag ? 1 : 0, new
                    ResultCallback() {
                        @Override
                        public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                            super.onApiSuccess(apiResponse, call, jsonResponse);
                            //
                            Address address = new Address();
                            address.setShoujianren(name);
                            address.setDianhua(shoujihao);
                            address.setSheng(sheng);
                            address.setShi(shi);
                            address.setQu(qu);
                            address.setDefaultflag(isDefaultFlag ? 1 : 0);
                            address.setDetailaddr(detailAddr);
                            if (isDefaultFlag) {
                                AddressUtils.setDefaultAddress(address);
                            }
                            cancelProgress();
                            setResult(100, null);
                            finish();
                        }
                    });
        } else {

            if (this.mAdOrder != null) {

                DeliverApiManager.modifyOrderAddress(this, name, shoujihao, sheng, shi, qu, detailAddr, mAdOrder.getAdorderid(), new ResultCallback() {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(apiResponse, call, jsonResponse);
                        cancelProgress();
                        setResult(100, null);
                        finish();
                    }
                });
                return;
            }
            UsersApiManager.userEditAddress(this, name, shoujihao, sheng, shi, qu, detailAddr, addid, isDefaultFlag ? 1 : 0, new ResultCallback() {
                @Override
                public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                    super.onApiSuccess(apiResponse, call, jsonResponse);
                    //
                    Address address = new Address();
                    address.setAddrid(addid);
                    address.setShoujianren(name);
                    address.setDianhua(shoujihao);
                    address.setSheng(sheng);
                    address.setShi(shi);
                    address.setQu(qu);
                    address.setDefaultflag(isDefaultFlag ? 1 : 0);
                    address.setDetailaddr(detailAddr);
                    if (isDefaultFlag) {
                        AddressUtils.setDefaultAddress(address);
                    }
                    Address selectedAddress = AddressUtils.getDefaultAddress();
                    if (selectedAddress != null && address.getAddrid().equals(selectedAddress.getAddrid())) {
                        AddressUtils.setSelectedAddress(address);
                    }
                    cancelProgress();
                    setResult(100, null);
                    finish();
                }
            });
        }


    }
}
