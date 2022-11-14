package com.aikucun.akapp.activity.invitation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.DisplayUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.QRCodeUtil;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.SystemShareUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by micker on 2017/7/12.
 */

public class InvActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.invTextView)
    TextView invTextView;
    @BindView(R.id.qr_iv)
    ImageView qr_iv;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private String invicateCode;
    private String filePath;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.my_invitation_code);
    }

    @Override
    public void initData() {
        getQr();
        invicateCode = AppContext.getInstance().getUserInfo().getPreferralcode();
        invTextView.setText(invicateCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_inv_code;
    }


    private void getQr() {

        QRCodeUtil.createQRImg("http://www.aikucun.com/m/download.php?code=" + invicateCode, InvActivity.this, true, qr_iv, new QRCodeUtil.ICreateQRImg() {
            @Override
            public void onFilePath(String _filePath) {
                filePath = _filePath;
            }
        });
    }

    @Override
    @OnClick({R.id.bottomLayout})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bottomLayout: {
                if (!TextUtils.isEmpty(invicateCode)) {
                    shoot(this);
                } else {
                    MToaster.showShort(this, R.string.invita_code_error, MToaster.IMG_ALERT);
                }
            }
            break;
            default:
                break;
        }

    }

    private Bitmap takeScreenShot(Activity activity) {

        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        statusBarHeight += DisplayUtils.dip2px(this, 90);
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight - DisplayUtils.dip2px(this, 45), width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    // 保存到sdcard
    private void savePic(Bitmap b, String url) {
        final String dir = Environment.getExternalStorageDirectory() + "/akucun/";
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        final String saveName = "pic" + RSAUtils.md5String(url) + ".jpg";
        final File file = new File(dir, saveName);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> imagesPath = new ArrayList<>();
        imagesPath.add(file.getAbsolutePath());
        SystemShareUtils.shareMultipleImage(InvActivity.this, getString(R.string.invitation_code), imagesPath);
    }

    public void shoot(Activity activity) {
        savePic(takeScreenShot(activity), System.currentTimeMillis() + "");
    }
}
