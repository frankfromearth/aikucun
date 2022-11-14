package com.aikucun.akapp.adapter.viewholder;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Address;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.utils.AddressUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;


/**
 * Created by micker on 2017/7/16.
 */

public class AddressViewHolder extends BaseViewHolder<Address> {

    TextView addressTextName;
    TextView addressTextMobile;
    TextView addressTextAddress;
    TextView address_default_tv;
    ImageView imageView;
    ImageView img_check;
    TextView address_text_default;
    RelativeLayout addressEdit;

    public boolean isChoose;

    public View address_del_btn;
    public View address_default_btn;
    public View address_edit_btn;

    public AddressViewHolder(ViewGroup parent) {
        super(parent, R.layout.layout_address_item);

        addressTextName = $(R.id.address_text_name);
        addressTextMobile = $(R.id.address_text_mobile);
        addressTextAddress = $(R.id.address_text_address);
        addressEdit = $(R.id.address_edit);
        address_default_btn = $(R.id.address_default_btn);
        address_del_btn = $(R.id.address_del_btn);
        address_edit_btn = $(R.id.address_edit_btn);
        imageView = $(R.id.image);
        address_default_tv = $(R.id.address_default_tv);
        img_check = $(R.id.img_check);
        address_text_default = $(R.id.address_text_default);
    }

    @Override
    public void setData(Address data) {

        addressTextMobile.setText(data.getDianhua());
        addressTextName.setText(data.getShoujianren());
        addressTextAddress.setText(data.getDetailaddr());

        if (data.getDefaultflag() == 1) {
            imageView.setImageResource(R.drawable.right);
            address_default_tv.setText("默认地址");
            address_del_btn.setEnabled(false);
            address_del_btn.setAlpha(0.5f);
            address_text_default.setVisibility(View.VISIBLE);


        } else {
            imageView.setImageResource(R.drawable.circle);
            address_default_tv.setText("设为默认");
            address_del_btn.setEnabled(true);
            address_del_btn.setAlpha(1);
            address_text_default.setVisibility(View.GONE);
        }

        if (isChoose) {
            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            // FIXME: 2018/1/4
           // Address selAddress = userInfo.getSelectAddr();
            Address selAddress = AddressUtils.getSelectedAddress();
            boolean isDef = false;
            if (selAddress != null && selAddress.getAddrid().equalsIgnoreCase(data.getAddrid())) isDef = true;
            //boolean isDef = (selAddress.getAddrid().equalsIgnoreCase(data.getAddrid()));
            Resources resource = (Resources) getContext().getResources();
            ColorStateList csl = (ColorStateList) resource.getColorStateList(isDef?R.color.color_accent:R.color.black);

            addressTextMobile.setTextColor(csl);
            addressTextName.setTextColor(csl);
            addressTextAddress.setTextColor(csl);
            img_check.setVisibility(isDef?View.VISIBLE:View.GONE);
        }
    }
}
