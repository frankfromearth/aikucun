package com.aikucun.akapp.activity.album;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.view.CirclePageIndicator;
import com.aikucun.akapp.widget.MyDialog;
import com.aswife.ui.TouchImageView;
import com.aswife.ui.TouchViewPager;
import com.qiyunxin.android.http.cache.HttpStreamCache;
import com.qiyunxin.android.http.listener.OnProgressListener;
import com.qiyunxin.android.http.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sl
 */
public class PictureShowActivity extends Activity {

    private FileUtils fileUtils = new FileUtils();
    private ArrayList<String> imgList = new ArrayList<String>();
    private int INDEX = 0;
    private TouchViewPager mViewPager;
    private View mTransLoading;
    private boolean isClearCache = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.chating_show);
        initView();
    }

    public void initView() {
        mViewPager = (TouchViewPager) findViewById(R.id.viewPager);
        mTransLoading = (View) findViewById(R.id.loading);
        Bundle bundle = getIntent().getExtras();
        String content = bundle.getString("content");
        isClearCache = bundle.getBoolean("isClearCache", true);
        imgList = bundle.getStringArrayList("imgList");
        int length = imgList.size();
        int initIndex = 0;
        for (int i = 0; i < length; i++) {
            if (imgList.get(i).equals(content)) {
                initIndex = i;
                INDEX = i;
                break;
            }
        }

        TouchImageAdapter pagerAdapter = new TouchImageAdapter(imgList);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(initIndex);

        if (imgList.size() > 1) {
            CirclePageIndicator circlePageIndicator = findViewById(R.id.indicator);
            circlePageIndicator.setViewPager(mViewPager);
            circlePageIndicator.setRadius(getResources().getDimension(
                    R.dimen.space_4));
            circlePageIndicator.setStrokeWidth(0);
            circlePageIndicator.setStrokeColor(getResources().getColor(
                    R.color.CLR252525));
            circlePageIndicator.setFillColor(getResources().getColor(
                    R.color.CLR818181));
            circlePageIndicator.setPageColor(getResources().getColor(
                    R.color.CLR252525));
            circlePageIndicator
                    .setOnPageChangeListener(new OnPageChangeListener() {

                        @Override
                        public void onPageSelected(int arg0) {
                            INDEX = arg0;
                        }

                        @Override
                        public void onPageScrolled(int arg0, float arg1,
                                                   int arg2) {
                        }

                        @Override
                        public void onPageScrollStateChanged(int arg0) {
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (isClearCache) {
            Recycle(mViewPager);
        }
        super.onDestroy();
    }

    private void Recycle(ViewGroup vg) {
        if (vg instanceof ViewGroup) {
            int count = vg.getChildCount();
            for (int i = 0; i < count; i++) {
                if (vg.getChildAt(i) instanceof TouchImageView) {
                    TouchImageView fiv = (TouchImageView) vg.getChildAt(i);
                    fiv.Recycle();
                }
            }
        }
    }

    class TouchImageAdapter extends PagerAdapter {
        private List<String> mResources;
        private List<Boolean> mImgLoaded = new ArrayList<Boolean>();

        public TouchImageAdapter(List<String> resources) {
            this.mResources = resources;
            for (int i = 0, size = mResources.size(); i < size; i++) {
                mImgLoaded.add(false);
            }
        }

        private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                int index = (Integer) v.getTag();
                saveToAlbumDialog(mResources.get(index));
                return false;
            }
        };

        private OnClickListener mClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
//				overridePendingTransition(0, R.anim.base_slide_right_out);
            }
        };

        private OnProgressListener mOnProgressListener = new OnProgressListener() {

            @Override
            public void OnSuccess(View v) {
                if (v == null || v.getTag() == null) {
                    return;
                }
                int position = (Integer) v.getTag();
                if (v instanceof TouchImageView) {
                    TouchImageView iv = (TouchImageView) v;
                    iv.setVisibility(View.VISIBLE);
                    if (!iv.mIsDefault) {
                        mImgLoaded.set(position, true);
                        if (INDEX == position) {
                            mTransLoading.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void OnProgress(long total_bytes, long downloaded_bytes) {
            }

            @Override
            public void OnFail(String reason) {
            }
        };

        @Override
        public int getCount() {
            return mResources.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            TouchImageView iv = new TouchImageView(container.getContext());
            iv.setVisibility(View.GONE);
            iv.setTag(position);
            if (INDEX == position && !mImgLoaded.get(position)) {
                mTransLoading.setVisibility(View.VISIBLE);
            }

            iv.setScaleType(ScaleType.FIT_CENTER);
            iv.OnProgress(mOnProgressListener);
            iv.setImageResource(R.drawable.ic_loading);
            iv.SetUrl(mResources.get(position));
            iv.setOnClickListener(mClickListener);
            iv.setOnLongClickListener(mOnLongClickListener);
            container.addView(iv, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof TouchImageView) {
                TouchImageView iv = (TouchImageView) object;
                if (iv != null) {
                    iv.Recycle();
                }
            }
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    public void saveToAlbumDialog(final String picUrl) {
        String[] save_to_phone_text = {getResources().getString(R.string.save_to_phone)};
        final MyDialog.Builder dlg = new MyDialog.Builder(
                PictureShowActivity.this);
        final ListView menus = new ListView(PictureShowActivity.this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        menus.setLayoutParams(params);
        menus.setTag(save_to_phone_text);
        menus.setAdapter(new ArrayAdapter<String>(this, R.layout.custom_contenxt_menu_item,
                save_to_phone_text));
        dlg.setListView(menus);
        dlg.setMessage("");
        dlg.create().show();

        menus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        String path = null;
                        if (picUrl.substring(0, 4).equalsIgnoreCase("http")) {
                            path = HttpStreamCache.getInstance()
                                    .GetFilePath(picUrl);
                        } else {
                            path = picUrl;
                        }
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        if (bitmap != null) {
                            if (fileUtils.saveImageToGallery(bitmap)) {
                                MToaster.showShort(PictureShowActivity.this, R.string.save_success, MToaster.NO_IMG);
                            } else {
                                MToaster.showShort(PictureShowActivity.this, R.string.save_failed, MToaster.NO_IMG);
                            }
                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                        }

                        break;

                    default:
                        break;
                }
                dlg.getDialog().dismiss();
            }

        });
    }

}
