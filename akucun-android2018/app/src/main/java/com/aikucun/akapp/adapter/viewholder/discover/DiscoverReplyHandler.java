package com.aikucun.akapp.adapter.viewholder.discover;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Reply;


/**
 * @author SL
 * @function 评论、赞列表管理
 */
public class DiscoverReplyHandler {

//    public Context mContext;

//    public DiscoverReplyHandler(Context _Context) {
//        mContext = _Context;
//    }

//    public void setPraiseView(ArrayList<Praise> praise, LinearLayout praise_ll,
//                              TextView praise_tv) {
//        if (praise != null && praise.size() > 0) {
//            praise_ll.setVisibility(View.VISIBLE);
//            String s = "";
//            for (int i = 0; i < praise.size(); i++) {
//                if (i == praise.size() - 1) {
//                    s += praise.get(i).liker_nick.trim();
//                } else {
//                    s += praise.get(i).liker_nick.trim() + ",";
//                }
//            }
//            s = Utils.ToDBC(s);
//            praise_tv.setText(s);
//        } else {
//            praise_ll.setVisibility(View.GONE);
//        }
//    }

    public static View getReplyTextView(Context mContext, int dyPos, EditText _editText, Reply r,
                                        int deleteReplyIndx, LinearLayout faLin,
                                        ITextLinkListener mLinkListener) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reply,
                new RelativeLayout(mContext), false);
        TextView reply_all_tv = view.findViewById(R.id.reply_all_tv);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        String reply = mContext.getResources()
                .getString(R.string.dynamic_reply);
        String nickname = "";
        //被回复人的名字
        String pnickname = "";
        //被回复人的id
        String pcustId = r.replyuser;
        if (TextUtils.isEmpty(nickname)) nickname = r.nickname.trim();
        if (TextUtils.isEmpty(pnickname)) pnickname = r.replyUsername.trim();
        // 暂时无回复某人的回复
        if (TextUtils.isEmpty(r.replyid)) {
            ssb.append(nickname + "：");
        } else {
            ssb.append( nickname+ reply + pnickname+"：");
        }

      //  ssb.append(nickname + ": ");

        TextLinkListener tllf = new TextLinkListener(mContext, dyPos, deleteReplyIndx,
                _editText, 0, r, faLin, "", "", mLinkListener);

        TextLinkListener tllr = new TextLinkListener(mContext, dyPos, deleteReplyIndx,
                _editText, 0, r, faLin, pcustId, pnickname, mLinkListener);
        ssb.setSpan(tllf, 0, nickname.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        ssb.append(r.content);
//
//        ssb.append(ExpressionUtil.getExpressionString(mContext, r.content
//                .trim().replace(" ", "")));

        TextLinkListener tlll = new TextLinkListener(mContext, dyPos, deleteReplyIndx,
                _editText, 2, r, faLin, "", "", mLinkListener);

        if (!TextUtils.isEmpty(pcustId)) {
            ssb.setSpan(new ForegroundColorSpan(Color.BLACK),
                    nickname.length(), nickname.length() + reply.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            ssb.setSpan(
                    tllr,
                    nickname.length() + reply.length(),
                    nickname.length() + pnickname.length() + reply.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            //绘制回复人和被回复人的颜色，需要绘制：时pnickname.length() + 1
            ssb.setSpan(tlll, nickname.length() + reply.length()
                            + pnickname.length(), ssb.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        } else {
            //绘制回复人的颜色，需要绘制：时nickname.length() + 1
            ssb.setSpan(tlll, nickname.length(), ssb.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        reply_all_tv.setMovementMethod(LinkMovementMethod.getInstance());
        reply_all_tv.setText(ssb);
        return view;

    }

    public interface ITextLinkListener {
        void onTextLink(int dypos, int status, String openId,
                        String pOpenId, String pNickName, int deleteReplyIndx, LinearLayout linearLayout);
    }

    ;

    public static class TextLinkListener extends ClickableSpan {
        private int type;
        private Reply r;
        private int dyPos;
        private String source_id;
        private String nick_name;
        private EditText editText;
        private ITextLinkListener mLinkListener;
        private int deleteReplyIndx = 0;
        private LinearLayout linearLayout;
        private Context mContext;

        public TextLinkListener(Context context, int _dyPos, int _deleteReplyIndx,
                                EditText _editText, int type, Reply reply,
                                LinearLayout _linearLayout, String source_open_id, String source_nick_name,
                                ITextLinkListener _LinkListener) {
            this.mContext = context;
            this.type = type;
            this.dyPos = _dyPos;
            this.r = reply;
            this.source_id = source_open_id;
            this.nick_name = source_nick_name;
            this.mLinkListener = _LinkListener;
            this.editText = _editText;
            this.deleteReplyIndx = _deleteReplyIndx;
            this.linearLayout = _linearLayout;
        }

        @Override
        public void onClick(View widget) {
            switch (type) {
                case 0:
                    if (TextUtils.isEmpty(source_id)) {
                        // TODO: 2017/11/22 个人资料
//                        ActivityHandle.startUserDetailActivity((Activity) mContext,
//                                r.openId);
                    } else {
                        // TODO: 2017/11/22 我的资料
//                        ActivityHandle.startUserDetailActivity((Activity) mContext,
//                                r.pOpenId + "");
                    }

                    break;
                case 1:
                    break;
                case 2:
//                    String runame = QYXApplication.config.getFriendsRemarkName(r.openId);
//                    if (TextUtils.isEmpty(runame)) runame = r.nickName;
                    String runame = r.nickname;
                    final String rid = r.userid;
//                    final String rid = r.replyid;
                    // TODO: 2017/11/22 如果是自己发的评论提示是否删除
                    if (r.userid.equals(AppContext.getInstance().getUserId())) {
//                        MyDialogUtils.showNormalDialog(mContext, R.string.prompt, R.string.delete_dynamic_dialog, new MyDialogUtils.IDialogListenter() {
//                            @Override
//                            public void onClick() {
//                                mLinkListener.onTextLink(dyPos, 0, rid,
//                                        source_id, nick_name, deleteReplyIndx,
//                                        linearLayout);
//                            }
//                        },null);
//                        MyDialog.Builder alertDialog = new MyDialog.Builder(
//                                mContext);
//                        alertDialog.setTitle(R.string.dialog_title);
//                        alertDialog.setMessage(mContext.getResources().getString(
//                                R.string.delete_dynamic_dialog));
//                        alertDialog.setPositiveButton(R.string.sure,
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        dialog.dismiss();
//                                        mLinkListener.onTextLink(dyPos, 0, rid,
//                                                source_id, nick_name, deleteReplyIndx,
//                                                linearLayout);
//                                    }
//                                });
//
//                        alertDialog
//                                .setNegativeButton(
//                                        R.string.cancel,
//                                        new DialogInterface.OnClickListener() {
//                                            public void onClick(
//                                                    DialogInterface dialog,
//                                                    int which) {
//                                                dialog.dismiss();
//                                            }
//                                        });
//                        alertDialog.create().show();

                    } else {
                        // TODO: 2017/11/22
                        //别人发的评论弹框显示回复某人的评论
//                        editText.setHint(mContext.getResources()
//                                .getString(R.string.dynamic_reply).trim()
//                                + runame.trim() + ":");
//                        mLinkListener.onTextLink(dyPos, 1, rid, r.userid + "", r.nickname,
//                                deleteReplyIndx, linearLayout);
                    }

                    break;
                default:
                    break;
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (type == 0) {
                ds.setColor(mContext.getResources().getColor(
                        R.color.reply_name_color));
            } else {
                ds.setColor(mContext.getResources().getColor(
                        R.color.black));
            }
            ds.setTextSize(mContext.getResources().getDimension(
                    R.dimen.space_14));
            ds.setUnderlineText(false);
        }
    }
}
