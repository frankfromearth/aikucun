package com.aikucun.akapp.activity.aftersale;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.storage.YiFaAdOrderManager;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleLoufaBufa;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleLoufaTuikuan;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASalePending;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleRejected;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleSubmit;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleTuihuoBufa;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleTuihuoPending;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleTuihuoTuikuan;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusCancel;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusFahuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusInit;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusPending;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusQuehuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusQuehuoDone;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusTuihuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusTuikuan;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusTuikuanDone;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusWeifahuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusYifahuo;

/**
 * Created by ak123 on 2018/1/23.
 */

public class ApplyAfterSaleProductViewHolder extends BaseViewHolder<CartProduct> {

    public TextView contentText;
    public TextView skuText;
    public TextView amountText;
    public TextView statusText;
    public TextView barcode_tv;
    public ImageView productImage;
    //????????????
    public TextView apply_after_sale_btn;

    public TextView remarkText;
    public Button remarkBtn;

    public String adOrderId;


    public ApplyAfterSaleProductViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_apply_after_sale_product);

        contentText = $(R.id.contentTv);
        skuText = $(R.id.skuTv);
        amountText = $(R.id.amountTv);
        statusText = $(R.id.order_product_status);
        productImage = $(R.id.productImage);
        remarkBtn = $(R.id.remarkBtn);
        remarkText = $(R.id.txt_remark);
        barcode_tv = $(R.id.barcode_tv);
        apply_after_sale_btn = $(R.id.apply_after_sale_btn);
    }

    @Override
    public void setData(CartProduct data) {
        contentText.setText(data.getDesc());
        skuText.setText(data.getSku().getChima() + "  x1");
        amountText.setText("????????????" + StringUtils.getPriceString(data.getJiesuanjia()));

        remarkText.setText("??? ??????" + data.getRemark());
        if (!TextUtils.isEmpty(data.getRemark())) {
            remarkBtn.setVisibility(View.GONE);
            remarkText.setVisibility(View.VISIBLE);
        } else {
            remarkBtn.setVisibility(View.VISIBLE);
            remarkText.setVisibility(View.GONE);
        }
        int statusValue = data.getShangpinzhuangtai();
        String status = statusTextValue(statusValue);
        barcode_tv.setText("?????? " + data.getSku().getBarcode());
        Glide.with(getContext()).load(data.getImageUrl()).diskCacheStrategy(DiskCacheStrategy
                .ALL).placeholder(R.color.color_bg_image).into(productImage);
        statusText.setText(status);

        if (statusValue == ProductStatusYifahuo) {
            apply_after_sale_btn.setVisibility(View.VISIBLE);
            statusText.setVisibility(View.GONE);
        } else {
            apply_after_sale_btn.setVisibility(View.GONE);
            statusText.setVisibility(View.VISIBLE);
        }

        //????????????????????????
        {
            String uid = RSAUtils.md5String(adOrderId + data.getCartproductid());
            boolean isPeihuo = YiFaAdOrderManager.getInstance().checkYiFaHuoIsPeihuo(uid);
            int color = getContext().getResources().getColor(R.color.black);

            if (isPeihuo) {
                color = getContext().getResources().getColor(R.color.color_accent);
            }

            contentText.setTextColor(color);
            skuText.setTextColor(color);
            amountText.setTextColor(color);
            remarkText.setTextColor(color);

        }


        /*
        if (statusValue == ProductStatusInit)
        {
            productButton.setVisibility(View.VISIBLE);
            productButton.setText("?????????");
            cancelButton.setVisibility(View.GONE);
        }
        else if (statusValue == ProductStatusWeifahuo)
        {
            productButton.setVisibility(View.VISIBLE);
            productButton.setText("?????????");
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText("??? ???");

            //??????????????????????????????

        }
        else if (statusValue == ProductStatusYifahuo || statusValue == ProductStatusFahuo)
        {
            //?????????????????????
            productButton.setVisibility(View.VISIBLE);
//            productButton.setText("????????????");
            productButton.setText("????????????");

            cancelButton.setVisibility(View.GONE);
//            cancelButton.setText("??? ???");//??????

            String uid = RSAUtils.md5String(adOrderId + data.getCartproductid());
            boolean isPeihuo =  YiFaAdOrderManager.getInstance().checkYiFaHuoIsPeihuo(uid);
            int color = getContext().getResources().getColor(R.color.black);

            if (isPeihuo) {
                color = getContext().getResources().getColor(R.color.color_accent);
                productButton.setVisibility(View.GONE);
                statusText.setVisibility(View.VISIBLE);
                statusText.setText("???????????????");
            }

            contentText.setTextColor(color);
            skuText.setTextColor(color);
            amountText.setTextColor(color);
            remarkText.setTextColor(color);
            statusText.setTextColor(color);
        }
        else
        {
            productButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }
*/
    }

    private String statusTextValue(int status) {
        String statusText = "";
        switch (status) {
            case ProductStatusInit: {
                statusText = "?????????";
            }
            break;
            case ProductStatusWeifahuo: {
                statusText = "?????????";
            }
            break;
            case ProductStatusYifahuo: {
                statusText = "?????????";
            }
            break;
            case ProductStatusFahuo: {
                statusText = "?????? ?????????";
            }
            break;
            case ProductStatusCancel: {
                statusText = "??????????????????????????????";
            }
            break;
            case ProductStatusQuehuo: {
                statusText = "???????????? ?????????";
            }
            break;
            case ProductStatusTuihuo: {
                statusText = "?????? ?????????";
            }
            break;
            case ProductStatusTuikuan: {
                statusText = "???????????? ?????????";
            }
            break;
            case ProductStatusPending: {
                statusText = "???????????? ?????????";
            }
            break;
            case ProductStatusTuikuanDone:
                statusText = "???????????? ?????????";
                break;
            case ProductStatusQuehuoDone:
                statusText = "???????????? ?????????";
                break;
            case ProductASaleSubmit:
                statusText = "?????? ????????? (?????????)";
                break;
            case ProductASaleRejected:
                statusText = "?????? ???????????????";
                break;
            case ProductASalePending:
                statusText = "?????? ??????????????????????????????)";
                break;
            case ProductASaleLoufaBufa:
                statusText = "?????? ???????????????";
                break;
            case ProductASaleLoufaTuikuan:
                statusText = "?????? ???????????????";
                break;
            case ProductASaleTuihuoBufa:
                statusText = " ?????? ???????????????";
                break;
            case ProductASaleTuihuoTuikuan:
                statusText = " ?????? ???????????????";
                break;
            case ProductASaleTuihuoPending:
                statusText = " ?????? ???????????????";
                break;
            default:
                break;
        }
        return statusText;
    }
}
