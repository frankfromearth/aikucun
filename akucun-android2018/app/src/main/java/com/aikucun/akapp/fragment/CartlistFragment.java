package com.aikucun.akapp.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.AddressListActivity;
import com.aikucun.akapp.activity.CartRecycleActivity;
import com.aikucun.akapp.activity.MyOrderActivity;
import com.aikucun.akapp.activity.PayOrderActivity;
import com.aikucun.akapp.adapter.CartProductAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.CartProductsCallback;
import com.aikucun.akapp.api.callback.OrderCreateCallback;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.Address;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.entity.PinpaiCart;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.CartApiManager;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.response.OrderCreateResp;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.alibaba.fastjson.JSON;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static android.support.v7.app.AlertDialog.Builder;
import static android.support.v7.app.AlertDialog.OnClickListener;
import static com.aikucun.akapp.adapter.CartProductAdapter.PRODUCT_EVENT_DELETE;
import static com.aikucun.akapp.adapter.CartProductAdapter.PRODUCT_EVENT_ITEM;
import static com.aikucun.akapp.adapter.CartProductAdapter.PRODUCT_EVENT_REMAKR;

/**
 * 购物车列表页
 * Created by jarry on 2017/6/1.
 */
public class CartlistFragment extends BaseProductFragment implements CartProductAdapter.OnItemEventListener {

    @BindView(R.id.totalTv)
    TextView totalTextView;

    @BindView(R.id.buyBtn)
    TextView buyButton;

    @BindView(R.id.btn_tip)
    ImageButton btnTip;

    @BindView(R.id.rl_tip)
    RelativeLayout rlTip;

    private CartProductAdapter cartAdapter;

    private StickyRecyclerHeadersDecoration headersDecoration;


    private int totalCount = 0;
    private String totalAmount;

    private CartHeaderView headerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cart;
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        cartAdapter = new CartProductAdapter(getContext());
        recyclerView.setAdapter(cartAdapter);
        headerView = new CartHeaderView();
        headerView.onBindView(view);

        setEmptyText("购物车为空");


        cartAdapter.setOnItemEventListener(this);
        headersDecoration = new StickyRecyclerHeadersDecoration(cartAdapter);
        recyclerView.addItemDecoration(headersDecoration);

        StickyRecyclerHeadersTouchListener headersTouchListener = new StickyRecyclerHeadersTouchListener(recyclerView.getRecyclerView(), headersDecoration);
        headersTouchListener.setOnHeaderClickListener(new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(View view, int position, long headerId) {

                CartProduct product = cartAdapter.getItem(position);
                invalideProduct(product, true);
            }
        });

        recyclerView.addOnItemTouchListener(headersTouchListener);
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestCartProducts();
                    }
                }, 600);
            }
        });

        {
            boolean didShowTip = AppContext.get("didShowCartTip", false);
            rlTip.setVisibility(didShowTip ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void initData() {
        super.initData();

        setBuyButtonEnabeld(false);
        totalTextView.setText("合计：¥ --");
    }

    @Override
    public void onResume() {
        super.onResume();

        requestCartProducts();
        if (headerView != null) {
            headerView.setAddress();
        }
    }

    @Override
    @OnClick({R.id.buyBtn, R.id.btn_tip})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buyBtn: {
                Address address = AddressUtils.getDefaultAddress();
                if (address == null) {
                    ((BaseActivity) getActivity()).showMessage("请添加收货地址！");
                    return;
                }
                Builder builder = new Builder(getContext());
                String msg = "一共 " + totalCount + " 件  " + "结算金额 " + totalAmount + " 元\n(请确认收货地址 提交后不能修改)";
                builder.setTitle("立即去支付 ?").setMessage(msg).setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.sure, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestCreateOrder();
                                setBuyButtonEnabeld(false);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
            break;
            case R.id.btn_tip: {
                AppContext.set("didShowCartTip", true);
                rlTip.setVisibility(View.GONE);
            }
            break;

            default:
                break;
        }
    }

    @Override
    public void onEvent(int event, final CartProduct product, final int position) {
        switch (event) {
            case PRODUCT_EVENT_DELETE: {
                Builder builder = new Builder(getContext());
                builder.setTitle("确定从购物车中移除商品 ?").setNegativeButton(R.string.cancel, null).setPositiveButton
                        (R.string.sure, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestCancelProduct(product, position);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
            break;
            case PRODUCT_EVENT_REMAKR: {
                MyDialogUtils.showSetRemarkDialog(getContext(), new MyDialogUtils.ISetRemarkLisenter() {
                    @Override
                    public void onBack(String content) {
                        requestRemarkProduct(product, content, position);
                    }
                });
            }
            break;
            case PRODUCT_EVENT_ITEM: {
                invalideProduct(product, false);
            }
            break;
        }
    }

    /**
     * 结算按钮状态更新
     */
    private void setBuyButtonEnabeld(boolean enabled) {
        buyButton.setEnabled(enabled);
        int color = getResources().getColor(R.color.color_accent);
        buyButton.setBackgroundColor(enabled ? color : Color.GRAY);
    }

    /**
     * 订单生成成功，跳转订单支付界面
     */
    private void createOrderSuccess(List<String> orderIds, OrderCreateResp resp) {
        Intent intent = new Intent(getActivity(), PayOrderActivity.class);
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_IDS, JSON.toJSONString(orderIds));
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_AMOUNT, resp.getTotal_shangpinjine());
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_DIKOU, resp.getTotal_dikoujine());
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_YUNFEI, resp.getTotal_yunfeijine());
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TOTAL, resp.getTotal_amount());
        //设置收货地址
        intent.putExtra(AddressUtils.orderReceiptName, AddressUtils.getSelectedAddress().getShoujianren());
        intent.putExtra(AddressUtils.orderAddressPhone, AddressUtils.getSelectedAddress().displayMobile());
        intent.putExtra(AddressUtils.orderAddress, AddressUtils.getSelectedAddress().displayAddress());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
    }

    /**
     * 编辑收货地址信息
     */
    private void onEditAddress() {
        Intent intent = new Intent(getActivity(), AddressListActivity.class);
        intent.putExtra("isChoose", true);
        getActivity().startActivityForResult(intent, 100);
    }

    private void invalideProduct(CartProduct product, Boolean isAll) {


        if (isAll) {
            product.setAllSelected(!product.isAllSelected());
            boolean isall = product.isAllSelected();

            for (int i = 0; i < cartAdapter.getCount(); i++) {
                CartProduct pro = cartAdapter.getItem(i);
                if (pro.getPinpaiHash() == product.getPinpaiHash()) {
                    pro.setSelected(isall);
                    pro.setAllSelected(isall);
                }
            }
        } else {
            product.setSelected(!product.isSelected());
            boolean isall = product.isSelected();

            int count = 0;
            boolean isAllSlected = true;
            for (int i = 0; i < cartAdapter.getCount(); i++) {
                CartProduct pro = cartAdapter.getItem(i);
                if (pro.getPinpaiHash() == product.getPinpaiHash()) {
                    if (!pro.isSelected()) {
                        isAllSlected = false;
                        break;
                    }
                }
            }

            for (int i = 0; i < cartAdapter.getCount(); i++) {
                CartProduct pro = cartAdapter.getItem(i);
                if (pro.getPinpaiHash() == product.getPinpaiHash()) {
                    pro.setAllSelected(isAllSlected);
                }
            }


        }

        headersDecoration.invalidateHeaders();
        cartAdapter.notifyDataSetChanged();
        computeCart();
    }

    private void computeCart() {
        List<CartProduct> products = cartAdapter.getAllData();

        int amount = 0;
        int jianshu = 0;
        for (CartProduct product : products) {
            if (product.isSelected()) {
                amount += product.getJiesuanjia();
                jianshu++;
            }
        }
        totalCount = jianshu;
        totalAmount = StringUtils.getPriceString(amount);
        totalTextView.setText("合计：" + totalAmount + " (" + totalCount + " 件)");
        setBuyButtonEnabeld(totalCount > 0);
        if (totalCount < 1) {
            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_CLEAR_CART));
        } else {
            //AppContext.set("cart_new_msg_count",totalCount);
            AppContext.getInstance().setCartMsgCount(totalCount);
            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_CART));
        }
    }

    public List<CartProduct> setCartsList(List<PinpaiCart> cartList) {
        ArrayList<CartProduct> products = new ArrayList<>();
        for (PinpaiCart cart : cartList) {
            products.addAll(cart.getCartproducts());
        }

        return products;
    }

    /**
     * 请求购物车列表数据
     */
    private void requestCartProducts() {
        recyclerView.setRefreshing(true);

        CartApiManager.getCartProducts(getActivity(), new CartProductsCallback() {
            @Override
            public void onApiSuccess(List<PinpaiCart> pinpaiCarts, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(pinpaiCarts, call, jsonResponse);

                recyclerView.setRefreshing(false);

                List<CartProduct> products = setCartsList(pinpaiCarts);
                Collections.sort(products, new Comparator<CartProduct>() {
                    @Override
                    public int compare(CartProduct o1, CartProduct o2) {

                        return (int) (o1.getPinpaiHash() - (o2.getPinpaiHash()));
                    }
                });
                cartAdapter.clear();
                cartAdapter.addAll(products);
                headersDecoration.invalidateHeaders();
                computeCart();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }

    /**
     * 生成订单
     */
    private void requestCreateOrder() {
        ((BaseActivity) getActivity()).showProgress("");

        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        List<String> products = new ArrayList<>();
        for (CartProduct product : cartAdapter.getAllData()) {
            if (product.isSelected()) {
                products.add(product.getCartproductid());
            }
        }
        Address address = AddressUtils.getSelectedAddress();
        OrderApiManager.createOrder(getActivity(), products, address.getAddrid(), new OrderCreateCallback() {
            @Override
            public void onApiSuccess(OrderCreateResp orderCreateResp, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(orderCreateResp, call, jsonResponse);

                ((BaseActivity) getActivity()).cancelProgress();

                cartAdapter.clear();
                setBuyButtonEnabeld(false);
                totalTextView.setText("合计：¥ --");

                //
                createOrderSuccess(orderCreateResp.getOrderids(), orderCreateResp);
            }

            @Override
            public void onApiFailed(String message, int code) {
                boolean flag = handleOrderNotPay(message, code);
                if (!flag) {
                    super.onApiFailed(message, code);
                }
                ((BaseActivity) getActivity()).cancelProgress();
                setBuyButtonEnabeld((totalCount > 0));
            }
        });
    }

    private boolean handleOrderNotPay(String message, int code) {
        if (60037 == code) {
            //你有未支付的订单，请先支付再下单！
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("未支付订单").setMessage(message).setPositiveButton("支付", new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(getActivity(), MyOrderActivity.class);
                            intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 0);
                            startActivity(intent);

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return true;
        }
        return false;
    }

    private void requestCancelProduct(CartProduct product, final int position) {
        ((BaseActivity) getActivity()).showProgress("");

        ProductApiManager.cancelBuyProduct(getActivity(), product.getCartproductid(), new
                ResultCallback(getActivity()) {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(apiResponse, call, jsonResponse);

                        ((BaseActivity) getActivity()).cancelProgress();

                        cartAdapter.remove(position);
                        cartAdapter.notifyDataSetChanged();
                        computeCart();
                    }
                });
    }

    private void requestRemarkProduct(final CartProduct product, final String remark, final int position) {
        ProductApiManager.remarkProduct(getActivity(), product.getCartproductid(), remark, new ResultCallback(getActivity()) {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(apiResponse, call, jsonResponse);
                product.setRemark(remark);
                cartAdapter.update(product, position);
                cartAdapter.notifyDataSetChanged();
                MToaster.showShort(getActivity(), "成功添加备注", MToaster.IMG_INFO);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {

            headerView.setAddress();
        }
    }

    private void gotoCartRecycle() {
        getActivity().startActivity(new Intent(getActivity(), CartRecycleActivity.class));
    }


    /**
     * 购物车 Header View
     */
    public class CartHeaderView {  //implements RecyclerArrayAdapter.ItemView
        TextView addrNameText;
        TextView addrMobileText;
        TextView addressText;
        TextView userNoTextView;
        TextView address_text_default;
        TextView address_edit_btn_internal;
        TextView header_back_list;

        //        @Override
//        public View onCreateView(ViewGroup parent) {
//            View header = mInflater.inflate(R.layout.header_cart, null);
//            return header;
//        }
//
//        @Override
        public void onBindView(View headerView) {
            addrNameText = headerView.findViewById(R.id.address_text_name);
            addrMobileText = headerView.findViewById(R.id.address_text_mobile);
            addressText = headerView.findViewById(R.id.address_text_address);
            userNoTextView = headerView.findViewById(R.id.header_user_no);
            address_text_default = headerView.findViewById(R.id.address_text_default);
            address_edit_btn_internal = headerView.findViewById(R.id.address_edit_btn_internal);
            header_back_list = headerView.findViewById(R.id.header_back_list);

            header_back_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoCartRecycle();
                }
            });

            View editBtn = headerView.findViewById(R.id.address_edit_btn);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditAddress();
                }
            });
            setAddress();
        }

        public void setAddress() {
            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            if (userNoTextView == null)
                return;
            if (userInfo == null)
                return;
            userNoTextView.setText("代购编号： " + userInfo.getYonghubianhao());
            // FIXME: 2018/1/4
            Address address = AddressUtils.getSelectedAddress();
            if (address == null) {
                address = AddressUtils.getDefaultAddress();
                AddressUtils.setSelectedAddress(address);
            }
            if (address != null) {
                addrNameText.setVisibility(View.VISIBLE);
                addrMobileText.setVisibility(View.VISIBLE);
                addrNameText.setText(address.getShoujianren());
                addrMobileText.setText(address.displayMobile());
                addressText.setText(address.displayAddress());
                addressText.setTextSize(13);
                address_edit_btn_internal.setText("编辑");
                address_text_default.setVisibility(address.getDefaultflag() == 1 ? View.VISIBLE : View.GONE);
            } else {
                addrNameText.setVisibility(View.GONE);
                addrMobileText.setVisibility(View.GONE);
                addressText.setText("请添加收货地址");
                addressText.setTextSize(14);
                address_edit_btn_internal.setText("添加");
            }
        }
    }
}
