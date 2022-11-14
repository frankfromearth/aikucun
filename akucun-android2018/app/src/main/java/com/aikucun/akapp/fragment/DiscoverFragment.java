package com.aikucun.akapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.video.VideoPlayActivity;
import com.aikucun.akapp.adapter.DiscoverAdatper;
import com.aikucun.akapp.adapter.viewholder.discover.DiscoverBaseViewHolder;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.DiscoverListCallback;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.Discover;
import com.aikucun.akapp.api.entity.Reply;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.DiscoverApiManager;
import com.aikucun.akapp.base.BaseFragment;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.AlyVideoUploadUtil;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SystemShareUtils;
import com.aikucun.akapp.widget.CommentPopWindow;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * Created by ak123 on 2017/11/16.
 * 发现模块
 */

public class DiscoverFragment extends BaseFragment implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    private DiscoverAdatper discoverAdatper;
    private int page = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    public void initView(View view) {
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.initView(view);
        discoverAdatper = new DiscoverAdatper(getActivity(), iReplayListener, iPlayVideoListener);
        discoverAdatper.setMore(R.layout.view_load_more, this);
        discoverAdatper.setNoMore(R.layout.view_nomore);

    }

    @Override
    public void initData() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(discoverAdatper);
        recyclerView.setRefreshing(true);
        page = 1;
        requestDiscoverList(page);
    }


    @Override
    public void onLoadMore() {
        recyclerView.setRefreshing(false);
        page++;
        requestDiscoverList(page);
    }

    @Override
    public void onRefresh() {
        recyclerView.setRefreshing(true);
        page = 1;
        requestDiscoverList(page);
    }

    /* 获取发现列表数据 */
    private void requestDiscoverList(final int page) {
        DiscoverApiManager.getListData(getActivity(), page, 20, new DiscoverListCallback() {
            @Override
            public void onApiSuccess(List<Discover> discovers, Call call, ApiResponse jsonResponse) {
                recyclerView.setRefreshing(false);
                if (discovers != null) {
                    if (page == 1) {
                        //获取数据成功隐藏发现新消息
                        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_HIDE_DISCOVER_NEW_MSG));
                        discoverAdatper.clear();
                    }
                    discoverAdatper.addAll(discovers);
                }
                super.onApiSuccess(discovers, call, jsonResponse);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });

    }

    //视频播放
    DiscoverBaseViewHolder.IPlayVideoListener iPlayVideoListener = new DiscoverBaseViewHolder.IPlayVideoListener() {
        @Override
        public void onPlayVideo(String videoId) {
            getUploadVideoToken(videoId);
        }
    };

    DiscoverBaseViewHolder.IReplayListener iReplayListener = new DiscoverBaseViewHolder.IReplayListener() {
        @Override
        public void onReplay(Discover discover, View view) {
            // TODO: 2017/11/22 评论
            sendReplay(view, discover);
        }

        @Override
        public void onForward(Discover discover) {
            if (discover != null) {
                if (!TextUtils.isEmpty(discover.imagesUrl)) {
                    // TODO: 2017/11/22 分享图片
                    SystemShareUtils.shareImgs(getActivity(), discover.title + "\n" + discover.content, Discover.getImageUrls(discover));
                } else {
                    // TODO: 2017/11/22 分享纯文本
                    SystemShareUtils.shareText(getActivity(), discover.title + "\n" + discover.content);
//                    SystemShareUtils.shareImgs(getActivity(),discover.title+"\n"+discover.content,Discover.getImageUrls(discover));
                }
            }

        }
    };

    protected void sendReplay(View view, final Discover discover) {

        CommentPopWindow popWindow = new CommentPopWindow(getContext());
        popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        popWindow.setObject(discover);
        popWindow.setListener(new CommentPopWindow.OnCommentEventListener() {
            @Override
            public void onSendComment(final Object object, final String content) {
                DiscoverApiManager.replayDiscover(getActivity(), content, discover.id, new JsonDataCallback() {
                    @Override
                    public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(jsonObject, call, jsonResponse);
                        MToaster.showShort(getActivity(), R.string.replay_success, MToaster.IMG_INFO);
                        Reply reply = new Reply();
                        UserInfo userInfo = AppContext.getInstance().getUserInfo();
                        reply.adddate = jsonObject.getString("adddate");
                        reply.content = content;
                        reply.nickname = userInfo.getName();
                        reply.userid = AppContext.getInstance().getUserId();
                        Discover mDiscover = (Discover) object;
                        mDiscover.comments.add(reply);
                        discoverAdatper.notifyDataSetChanged();
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        MToaster.showShort(getActivity(), message, MToaster.IMG_INFO);
                    }
                });
            }
        });
    }


    /**
     * 获取上传视频token
     * @param videoId
     */
    private void getUploadVideoToken(final String videoId) {
        AlyVideoUploadUtil.getVideoToken(getActivity(), new AlyVideoUploadUtil.IGetVideoTokenListener() {
            @Override
            public void onResult(String akId, String akSecret, String token, String exTime) {
                if (!TextUtils.isEmpty(akId) && !TextUtils.isEmpty(akSecret) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(exTime)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("akId",akId);
                    bundle.putString("vid",videoId);
                    bundle.putString("akSecret",akSecret);
                    bundle.putString("scuToken",token);
//                    bundle.putString("securityToken",token);
                    ActivityUtils.startActivity(getActivity(), VideoPlayActivity.class,bundle);
                }
            }
        });
    }

    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_DISCOVER_LIST_REFRESH)) {
            recyclerView.setRefreshing(true);
            page = 1;
            requestDiscoverList(page);
        }
    }
}
