//package com.aikucun.akapp.activity;
//
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.aikucun.akapp.AppContext;
//import com.aikucun.akapp.R;
//import com.aikucun.akapp.base.BaseActivity;
//import com.aikucun.akapp.utils.InputMethodUtils;
//import com.aikucun.akapp.widget.AddressPopWindow;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//
///**
// * Created by jarry on 2017/6/11.
// */
//
//public class AddressActivity extends BaseActivity
//{
//    @BindView(R.id.toolbar)
//    Toolbar mToolBar;
//
//    @BindView(R.id.address_option_city)
//    View cityOptionView;
//
//    @BindView(R.id.address_name_edit)
//    EditText nameEdit;
//
//    @BindView(R.id.address_mobile_edit)
//    EditText mobileEdit;
//
//    @BindView(R.id.address_addr_edit)
//    EditText addressEdit;
//
//    @BindView(R.id.address_title_city)
//    TextView cityText;
//
//    @BindView(R.id.save_button)
//    Button saveButton;
//
//    @BindView(R.id.btn_tip)
//    ImageButton btnTip;
//
//    @BindView(R.id.rl_tip)
//    RelativeLayout rlTip;
//
//
//    private String name, shoujihao;
//    private String sheng, shi, qu;
//    private String detailAddr;
//
//    @Override
//    protected int getLayoutId()
//    {
//        return R.layout.activity_address;
//    }
//
//    @Override
//    public void initView()
//    {
//        mToolBar.setTitle("");
//        setSupportActionBar(mToolBar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mTitleText = (TextView) findViewById(R.id.tv_title);
//        mTitleText.setText(R.string.editor_address);
//    }
//
//    @Override
//    public void initData()
//    {
//        // FIXME: 2018/1/4
////        Address address = AppContext.getInstance().getUserInfo().getDefaultAddr();
////        if (address != null)
////        {
////            name = address.getShoujianren();
////            shoujihao = address.getDianhua();
////            sheng = address.getSheng();
////            shi = address.getShi();
////            qu = address.getQu();
////            detailAddr = address.getDetailaddr();
////
////            nameEdit.setText(name);
////            mobileEdit.setText(shoujihao);
////            addressEdit.setText(detailAddr);
////
////            if (sheng.length() > 0)
////            {
////                if (TextUtils.equals(sheng, shi))
////                {
////                    cityText.setText(sheng + "　" + qu);
////                }
////                else
////                {
////                    cityText.setText(sheng + "　" + shi + "　" + qu);
////                }
////            }
////        }
//        {
//            boolean didShowTip = AppContext.get("didShowAddressTip",false);
//            rlTip.setVisibility(didShowTip?View.GONE:View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        nameEdit.requestFocus();
//    }
//
//    @Override
//    @OnClick({R.id.address_option_city, R.id.save_button, R.id.btn_tip})
//    public void onClick(View v)
//    {
//        int id = v.getId();
//        switch (id)
//        {
//            case R.id.address_option_city:
//            {
//                hideInputKeyboard();
//
//                AddressPopWindow addressPopWindow = new AddressPopWindow(this, sheng, shi, qu);
//                addressPopWindow.showAtLocation(cityOptionView, Gravity.BOTTOM, 0, 0);
//                addressPopWindow.setAddresskListener(new AddressPopWindow.OnAddressCListener()
//                {
//                    @Override
//                    public void onClick(String province, String city, String area)
//                    {
//                        sheng = province;
//                        shi = city;
//                        qu = area;
//
//                        if (TextUtils.equals(sheng, shi))
//                        {
//                            cityText.setText(sheng + "　" + qu);
//                        }
//                        else
//                        {
//                            cityText.setText(sheng + "　" + shi + "　" + qu);
//                        }
//                    }
//                });
//            }
//            break;
//
//            case R.id.save_button:
//            {
//                hideInputKeyboard();
//
//                name = nameEdit.getText().toString();
//                shoujihao = mobileEdit.getText().toString();
//                detailAddr = addressEdit.getText().toString();
//
//                updateAddress();
//            }
//            break;
//
//            case R.id.btn_tip: {
//                AppContext.set("didShowAddressTip",true);
//                rlTip.setVisibility(View.GONE);
//            }
//            break;
//
//            default:
//                break;
//        }
//    }
//
//    private void hideInputKeyboard()
//    {
//        if (nameEdit.isFocused())
//        {
//            InputMethodUtils.hideInputKeyboard(this, nameEdit);
//        }
//        else if (mobileEdit.isFocused())
//        {
//            InputMethodUtils.hideInputKeyboard(this, mobileEdit);
//        }
//        else if (addressEdit.isFocused())
//        {
//            InputMethodUtils.hideInputKeyboard(this, addressEdit);
//        }
//    }
//
//    private void updateAddress()
//    {
//        if (name.length() == 0)
//        {
//            showMessage(R.string.input_consignee_name);
//            return;
//        }
//        if (shoujihao.length() == 0)
//        {
//            showMessage(R.string.input_consignee_mobile);
//            return;
//        }
//        if (sheng.length() == 0 || shi.length() == 0 || qu.length() == 0)
//        {
//            showMessage(R.string.choose_city);
//            return;
//        }
//        if (detailAddr.length() == 0)
//        {
//            showMessage(R.string.input_detail_address);
//            return;
//        }
//
//        showProgress("");
//
////        UsersApiManager.userAddAddress(this, name, shoujihao, sheng, shi, qu, detailAddr, new
////                ResultCallback()
////        {
////            @Override
////            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse)
////            {
////                super.onApiSuccess(apiResponse, call, jsonResponse);
////                //
////                Address address = new Address();
////                address.setShoujianren(name);
////                address.setDianhua(shoujihao);
////                address.setSheng(sheng);
////                address.setShi(shi);
////                address.setQu(qu);
////                address.setDetailaddr(detailAddr);
////                AppContext.getInstance().getUserInfo().setAddr(address);
////
////                cancelProgress();
////                finish();
////            }
////        });
//    }
//}
