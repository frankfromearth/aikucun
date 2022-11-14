package com.aikucun.akapp.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.DirectBuyActivity;
import com.aikucun.akapp.activity.PopActivity;
import com.aikucun.akapp.activity.ProductForwardSettingActivity;
import com.aikucun.akapp.adapter.TrailerAdapter;
import com.aikucun.akapp.api.callback.ApiBaseCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.ProductsSKUListCallback;
import com.aikucun.akapp.api.entity.Comment;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.CommentsApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.base.BaseFragment;
import com.aikucun.akapp.storage.LiveInfosManager;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.DisplayUtils;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.SystemShareUtils;
import com.aikucun.akapp.widget.CommentPopWindow;
import com.aikucun.akapp.widget.FastForwardPopWindow;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.widget.ShareView;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_BUY;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_COMMENT;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_COMMENT_DELETE;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_COMMENT_REPLY;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_FOLLOW_ACTION;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_FORWARD;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_HIDE_PROGRESS;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_SAVEIMAGE;

/**
 * 预告
 */

public class BaseTrailerFragment extends BaseFragment implements
        RecyclerArrayAdapter.OnLoadMoreListener, TrailerAdapter.OnItemEventListener {

    public TrailerAdapter trailerAdapter;

    protected ProductSKU selectSKU;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @Override
    public void initView(View view) {
        super.initView(view);

        trailerAdapter = new TrailerAdapter(getActivity());
        trailerAdapter.setOnItemEventListener(this);
//        productAdapter.setMore(R.layout.view_load_more, this);
        trailerAdapter.setNoMore(R.layout.view_nomore);


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, DisplayUtils.dip2px(getActivity(), 0.5f), 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(trailerAdapter);
    }


    /* 添加商品到购物车 */
    protected void requestBuyProduct(final Product product, final ProductSKU sku) {
        //
        if (!AppContext.get(AppConfig.UDK_REMARK_DATA, true)) {
            // FIXME: 2018/1/5 
           // new AppGoodsManager(trailerAdapter, product, sku, "", (BaseActivity) getActivity()).requestBuyProduct(sku, null);
            return;
        }
        final View view = View.inflate(getContext(), R.layout.view_buy_check, null);
        final EditText editText = (EditText) view.findViewById(R.id.comment_et);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("确认加入购物车？").setView(view)
                .setNegativeButton("不下单", null);
        builder.setPositiveButton("下单", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String remark = editText.getText().toString();
                // FIXME: 2018/1/5
             //   new AppGoodsManager(trailerAdapter, product, sku, remark, (BaseActivity) getActivity()).requestBuyProduct(sku, null);
            }
        });
        builder.show();
        InputMethodUtils.showInputKeyboard(getContext(), editText);

    }

    protected void sendComment(Product product, String hint) {

        if (!AppContext.getInstance().isVip()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("非会员不支持评论，请先购买会员资格")
                    .setNegativeButton("不，谢谢", null);
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    //getActivity().startActivity(new Intent(getActivity(), MemberActivity.class));
                }
            });
            builder.show();
            return;
        }


        CommentPopWindow popWindow = new CommentPopWindow(getActivity());
        popWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
        popWindow.setObject(product);
        if (!StringUtils.isEmpty(hint)) {
            popWindow.setHint("回复：" + hint);
        }

        popWindow.setListener(new CommentPopWindow.OnCommentEventListener() {
            @Override
            public void onSendComment(final Object object, final String content) {
                final Product mProduct = (Product) object;
                CommentsApiManager.sendComment((Activity) getActivity(), mProduct.getId(), content, new JsonDataCallback() {
                    @Override
                    public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(jsonObject, call, jsonResponse);
                        MToaster.showShort(getActivity(), R.string.replay_success, MToaster.IMG_INFO);

                        UserInfo userInfo = AppContext.getInstance().getUserInfo();
                        Comment comment = new Comment();
                        comment.setId(jsonObject.getString("commentid"));
                        comment.setContent(content);
                        comment.setName(userInfo.getName());
                        comment.setPinglunzheID(AppContext.getInstance().getUserId());
                        mProduct.addComment(comment);
                        trailerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                    }
                });
            }
        });
    }

    protected void deleteComment(final Product product, final Comment comment) {
        if (comment.getPinglunzheID().equalsIgnoreCase(AppContext.getInstance().getUserId())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("删除评论").setNegativeButton(R.string.cancel, null).setPositiveButton
                    (R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            product.removeComment(comment);
                            CommentsApiManager.cancelComment(getActivity(), product.getId(), comment.getId(), new ApiBaseCallback() {
                                @Override
                                public void onApiSuccess(Object o, Call call, ApiResponse jsonResponse) {
                                    super.onApiSuccess(o, call, jsonResponse);
                                }
                            });
                            trailerAdapter.notifyDataSetChanged();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    protected void forwardProduct(Product product, Object object) {
        ((BaseActivity) getActivity()).cancelProgress();
        List<String> imagesPath = (List<String>) object;
        if (getActivity() instanceof PopActivity) {
            ((PopActivity) getActivity()).setShareProduct(product);
        }
        float addMoney = AppContext.get(ProductForwardSettingActivity.FORWARD_FARE_KEY, 0.0f);
        if (product.weixinDesc().indexOf("¥") > -1 && addMoney > 0.0 && product.getProductType() == 0) {
            SystemShareUtils.shareMultipleImage(getActivity(), FastForwardPopWindow.getFaredContent(product.weixinDesc()), imagesPath);
        } else {
            SystemShareUtils.shareMultipleImage(getActivity(), product.weixinDesc(), imagesPath);
        }


        if (null == product.getId() || product.hasNoSku()) {//非商品
            return;
        }
        ProductManager.getInstance().setForwardIndex(product.getXuhao());
        ProductManager.getInstance().setLiveId(product.getLiveid());
    }

    protected void toggleFollowProduct(final Product product, Object object) {
        final int follow = product.getFollow() == 1 ? 0 : 1;

        ProductApiManager.followProduct(getActivity(), product.getId(), follow, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                product.setFollow(follow);
                ProductManager.getInstance().updateProduct(product);
                MToaster.showShort(getActivity(), (1 == follow) ? "已添加关注 ！" : "已取消关注 ！", MToaster.IMG_INFO);
                if (1 == follow) {
                    EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_FOLLOW_STATUS, product));
                } else {
                    EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_UNFOLLOW_STATUS, product));

                    if (isNeedToRemoveUnfollowdProduct()) {
                        trailerAdapter.remove(product);
                    }
                }
                trailerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

    protected void updateSKUS(final List<Product> products, final UpdateSKUListener listener) {

        if (products == null || listener == null || products.size() <= 0) {
            if (listener != null) {
                listener.onUpdateSuccess(products);
            }
            return;
        }

        ArrayList<String> ids = new ArrayList<>();

        for (Product product : products) {
            ids.add(product.getId());
        }

        ProductApiManager.updateSKUProduct(getActivity(), ids, new ProductsSKUListCallback(products) {
            @Override
            public void onApiSuccess(List<ProductSKU> productSKUs, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(productSKUs, call, jsonResponse);
                if (listener != null) {
                    listener.onUpdateSuccess(getResultProducts());
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                if (listener != null) {
                    listener.onUpdateFailed(getResultProducts());
                }
            }
        });
    }

    public interface UpdateSKUListener {
        public void onUpdateSuccess(List<Product> products);

        public void onUpdateFailed(List<Product> products);
    }

    public boolean isNeedToRemoveUnfollowdProduct() {
        return false;
    }

    @Override
    public void onEvent(int event, final Product product, final Object object, int position) {
        switch (event) {
            case PRODUCT_EVENT_SAVEIMAGE: {
                ((BaseActivity) getActivity()).showProgress("");
            }
            break;
            case PRODUCT_EVENT_HIDE_PROGRESS: {
                ((BaseActivity) getActivity()).cancelProgress();
            }
            break;

            case PRODUCT_EVENT_BUY: {
                ProductSKU sku = (ProductSKU) object;
                SCLog.debug("-> Buy : " + sku.getChima());
                selectSKU = sku;

                // LiveInfo liveInfo = LiveManager.getInstance().getLiveById(product.getLiveid());
                LiveInfo liveInfo = LiveInfosManager.getInstance().getLiveInfo(product.getLiveid());
                if (liveInfo != null && liveInfo.getBuymodel() > 0) {
                    // 直购模式
                    Intent intent = new Intent(getActivity(), DirectBuyActivity.class);
                    intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID, product.getId());
                    intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_SKU, sku.getId());
                    startActivityForResult(intent, 0);
//                    overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
//                    getActivity().startActivity(intent);
                    break;
                }

                requestBuyProduct(product, sku);
            }
            break;
            case PRODUCT_EVENT_FORWARD: {
                createShareImage(product, object);
            }
            break;
            case PRODUCT_EVENT_COMMENT: {
                sendComment(product, null);
            }
            break;
            case PRODUCT_EVENT_COMMENT_REPLY: {
//
//                屏蔽回复，
//                final Comment comment = (Comment)object;
//                sendComment(product,comment.getName());
            }
            break;
            case PRODUCT_EVENT_COMMENT_DELETE: {
                final Comment comment = (Comment) object;
                deleteComment(product, comment);
            }
            break;

            case PRODUCT_EVENT_FOLLOW_ACTION: {
                if (product.getFollow() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("取消关注该商品?").setNegativeButton(R.string.cancel, null).setPositiveButton
                            (R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    toggleFollowProduct(product, object);
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                } else {
                    toggleFollowProduct(product, object);
                }
            }
            break;
        }
    }

    @Override
    public void onLoadMore() {

    }

    public void setEmptyText(String text) {
        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.empty_text);
        textView.setText(text);
        recyclerView.setEmptyView(emptyView);
    }

    private void createShareImage(final Product product, final Object object) {

        showProductForwardDialog(product, object);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("商品描述已复制")
//                .setMessage("1、点击'去分享'后若无微信和QQ选项，可在'更多'中打开\n2、商品描述已复制，可以长按'粘贴'")
//                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ((BaseActivity) getActivity()).cancelProgress();
//                    }
//                })
//                .setNegativeButton("普通分享", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        forwardProduct(product, object);
//                    }
//                });
//
//        final ArrayList<String> imagePath = (ArrayList<String>) object;
//        if (((product.getProductType() == 2 || product.getProductType() == 1) && imagePath.size() < 9) || product.getProductType() == 0) {
//            builder.setPositiveButton("合成再分享", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    ShareView shareView = new ShareView(getContext());
//                    shareView.setShareViewInterface(new ShareView.ShareViewInterface() {
//                        @Override
//                        public void afterSaveBitmap(String localPath) {
//
//                            ArrayList<String> imagePaths = new ArrayList<String>();
//                            imagePaths.add(localPath);
//                            forwardProduct(product, imagePaths);
//                        }
//                    });
//                    shareView.setImageUrls(imagePath, product.weixinDesc(), product.getPinpaiid());
//                }
//            });
//        }
//
//
//        AlertDialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();

    }

    /**
     * 显示转发对话框
     *
     * @param product
     * @param object
     */
    private void showProductForwardDialog(final Product product, final Object object) {
        boolean showSingleImgSelected = false, showMoreImgSelected = false;
        final ArrayList<String> imagePath = (ArrayList<String>) object;
        if (imagePath.size() == 9 || imagePath.size() == 0) {
            showSingleImgSelected = false;
            showMoreImgSelected = false;
        } else if (imagePath.size() > 4 && imagePath.size() < 9) {
            showSingleImgSelected = false;
            showMoreImgSelected = true;
        } else {
            showSingleImgSelected = true;
            showMoreImgSelected = true;
        }
        Log.e("商品类型：", "" + product.getProductType());
        final float addMoney = AppContext.get(ProductForwardSettingActivity.FORWARD_FARE_KEY, 0.0f);
        String btnStr = getResources().getString(R.string.forward);
        //商品类型0：普通商品2：活动商品
        if (product.weixinDesc().indexOf("¥") > -1 && addMoney > 0.0 && product.getProductType() == 0) {
            btnStr = getResources().getString(R.string.forward) + "+" + addMoney + "元";
        }
        // 活动转发 不显示选项
        if (product.getProductType() != 0) {
            showSingleImgSelected = false;
        }
        MyDialogUtils.showProductForwardDialog(getContext(), "1、点击'去分享'后若无微信和QQ选项，可在'更多'中打开\n2、商品描述已复制，可以长按'粘贴'", btnStr, showSingleImgSelected, showMoreImgSelected, new MyDialogUtils.IProductForwardListener() {
            @Override
            public void onResult(boolean singleSelected, boolean moreSelected) {
                Log.e("选中类型：", singleSelected + "|" + moreSelected);
                if (!singleSelected && !moreSelected) {
                    forwardProduct(product, object);
                } else {
                    if (singleSelected) {
                        final ArrayList<String> imagePaths = new ArrayList<String>();
                        ShareView shareView = new ShareView(getContext());
                        shareView.setShareViewInterface(new ShareView.ShareViewInterface() {
                            @Override
                            public void afterSaveBitmap(String localPath) {
                                imagePaths.add(localPath);
                                forwardProduct(product, imagePaths);
                            }
                        });
                        if (product.weixinDesc().indexOf("¥") > -1 && addMoney > 0.0 && product.getProductType() == 0) {
                            shareView.setImageUrls(imagePath, FastForwardPopWindow.getFaredContent(product.weixinDesc()), product.getPinpaiid());
                        } else
                            shareView.setImageUrls(imagePath, product.weixinDesc(), product.getPinpaiid());

                    } else {
                        // TODO: 2017/12/8 多文字变图片
                        ShareView shareView = new ShareView(getContext());
                        shareView.setShareViewInterface(new ShareView.ShareViewInterface() {
                            @Override
                            public void afterSaveBitmap(String localPath) {
                                imagePath.add(0, localPath);
                                forwardProduct(product, imagePath);
                            }
                        });
                        if (product.weixinDesc().indexOf("¥") > -1 && addMoney > 0.0 && product.getProductType() == 0) {
                            shareView.setImageUrls(null, FastForwardPopWindow.getFaredContent(product.weixinDesc()), product.getPinpaiid());
                        } else
                            shareView.setImageUrls(null, product.weixinDesc(), product.getPinpaiid());

                    }

                }
            }

            @Override
            public void onCancel() {
                ((BaseActivity) getActivity()).cancelProgress();
            }
        });
    }

}
