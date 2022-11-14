
package com.jxccp.ui.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.R;

public class JXLeaveMsgActivity extends Activity implements OnClickListener {

    LinearLayout rootView;

    WebView webView;

    ProgressBar web_pb;

    ImageView backView;

    String url;
    
    String skillId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jx_activity_leave_msg);

        rootView = (LinearLayout)findViewById(R.id.ll_container);
        web_pb = (ProgressBar)findViewById(R.id.web_pb);
        backView = (ImageView)findViewById(R.id.iv_left);
        backView.setOnClickListener(this);
        
        skillId = getIntent().getStringExtra("skillId");

        url = JXImManager.McsUser.getInstance().getLeaveMsgWebUrl(skillId);
        init(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(String address) {
        webView = (WebView)findViewById(R.id.webView);
        // 启用支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 优先使用缓存
//        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        
        webView.getSettings().setDomStorageEnabled(true);
        
        // WebView加载web资源
        webView.loadUrl(address);
        
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    web_pb.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == web_pb.getVisibility()) {
                        web_pb.setVisibility(View.VISIBLE);
                    }
                    web_pb.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
            
            @Override
            public void onCloseWindow(WebView window) {
                JXLog.d("on close window event");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        JXLeaveMsgActivity.this.finish();
                    }
                }, 500);
                super.onCloseWindow(window);
            }
            
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                
                builder.setMessage(message)
                        .setPositiveButton(getString(R.string.jx_confirm), null);
                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;
            }
        });
        
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_left) {
            finish();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //webView资源释放
        if(rootView != null){
            rootView.removeAllViews();
        }
        if(webView != null){
            webView.removeAllViews();
            webView.clearCache(true);
            webView.pauseTimers();
            webView.destroy();
            webView = null;
        }
    }
}
