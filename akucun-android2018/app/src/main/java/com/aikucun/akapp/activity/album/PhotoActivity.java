package com.aikucun.akapp.activity.album;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.MToaster;
import com.qiyunxin.android.http.utils.Utils;
import com.qyx.android.weight.choosemorepic.PhotoAibum;
import com.qyx.android.weight.choosemorepic.PhotoItem;
import com.qyx.android.weight.utils.QyxWeightDensityUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 相册
 *
 * @author SL
 */
public class PhotoActivity extends BaseActivity implements
        PhotoAibumAdapter.OnItemClickListener2, PhotoAdapter.onItemSelectListener {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.photo_gridview)
    GridView gv;
    @BindView(R.id.choose_dir)
    TextView choose_dir;
    @BindView(R.id.btn_sure)
    TextView btn_sure;
    @BindView(R.id.preview_tv)
    TextView preview_tv;

    private PhotoAibum aibum = new PhotoAibum();
    private PhotoAdapter adapter;
    private int chooseNum = 0;
    private int selected_count;
    private LayoutInflater inflater;
    //    private ArrayList<PhotoItem> gl_arr = new ArrayList<PhotoItem>();
    private ArrayList<PhotoItem> select_gl_arr = new ArrayList<PhotoItem>();
    private List<PhotoAibum> aibumList;
    private Dialog dialog;
    private PhotoAibumAdapter aibumAdapter;
    private int screenHeight;
    private int max_select_count;

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, // 名称
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.LONGITUDE, // 日期
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.BUCKET_ID, // 目录id
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 目录名称
            MediaStore.MediaColumns.DATE_MODIFIED, MediaStore.MediaColumns.SIZE

    };

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.album);
        //获取图片
        aibumList = getPhotoAlbum();
        max_select_count = getIntent().getIntExtra("max_select_count", 1);
        screenHeight = QyxWeightDensityUtils.getScreenHeight(this);
        if (aibumList != null && aibumList.size() > 0) {
            aibum = aibumList.get(0);
        }
        selected_count = getIntent().getIntExtra("selected_count", 0);
        chooseNum = selected_count;
        int count = aibum.getBitList().size();
        for (int i = 0; i < count; i++) {
            if (aibum.getBitList().get(i).isSelect()) {
                chooseNum++;
            }
        }
        adapter = new PhotoAdapter(this, aibum, null);
        gv.setAdapter(adapter);
        adapter.setOnItemSelectListener(this);
        if (!TextUtils.isEmpty(aibum.getName())
                && !TextUtils.isEmpty(aibum.getCount())) {
            choose_dir.setText(aibum.getName() + " (" + aibum.getCount() + ")");
        }
    }

    @Override
    public void initData() {
        inflater = LayoutInflater.from(PhotoActivity.this);
        View view = inflater.inflate(R.layout.list_dir, null);
        ListView dir_list = view.findViewById(R.id.id_list_dir);
        if (aibumAdapter == null) {
            aibumAdapter = new PhotoAibumAdapter(aibumList, this);
            aibumAdapter.setOnItemClickListener(this);
        } else {
            aibumAdapter.setData(aibumList);
        }
        dir_list.setAdapter(aibumAdapter);

        dialog = new Dialog(this, R.style.pop_dialog);
        dialog.setContentView(view);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCanceledOnTouchOutside(true);
        // dialog.show();
        win.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                screenHeight / 2);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.y = Utils.dip2px(this, 48);
        win.setAttributes(lp);
        initListenter();
    }

    @Override
    public void onItemClickListener(PhotoAibum aibum) {
        dialog.dismiss();
        this.aibum = aibum;
        choose_dir.setText(aibum.getName() + " (" + aibum.getCount() + ")");
        if (adapter != null) {
            adapter.RecycleMaskImageView(true);
        }
        adapter.setData(aibum);
        gv.postDelayed(new Runnable() {
            @Override
            public void run() {
                gv.setSelection(0);
            }
        }, 200);
    }

    @Override
    public void onItemSelect(int position, PhotoItem photoItem, PhotoGridItem gridItem) {
        if (photoItem.isSelect()) {
            //该图片已被选中
//            gl_arr.remove(gridItem);
            chooseNum--;
            select_gl_arr.remove(photoItem);
            init();
            //取消选中
            photoItem.setSelect(false);
            if (gridItem != null) {
                gridItem.setChecked(false);
            }
        } else {
            if (chooseNum == max_select_count) {
                MToaster.showShort(PhotoActivity.this, MessageFormat.format(getResources()
                        .getString(R.string.most_num), max_select_count), MToaster.NO_IMG);
                return;
            }
            photoItem.setSelect(true);
//            gl_arr.add(photoItem);
            select_gl_arr.add(photoItem);
            chooseNum++;
            init();
            if (gridItem != null) {
                gridItem.setChecked(true);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onImgClick(PhotoItem photoItem) {
        String bigpath = "";
        if (!TextUtils.isEmpty(photoItem.getPath())) {
            bigpath = photoItem.getPath();
        } else {
            return;
        }
        ArrayList<String> imgs = new ArrayList<String>();
        imgs.add(bigpath);
        Bundle bundle = new Bundle();
        bundle.putString("content", bigpath);
        bundle.putStringArrayList("imgList", imgs);
        ActivityUtils.startActivity(PhotoActivity.this, PictureShowActivity.class, bundle, -1);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photoalbum_gridview;
    }

    /**
     * 事件处理
     */
    private void initListenter() {

        gv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        if (adapter != null) {
                            adapter.SetIsScrolling(true);
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if (adapter != null) {
                            adapter.SetIsScrolling(false);
                        }
                        int startindex = gv.getFirstVisiblePosition();
                        int endindex = gv.getLastVisiblePosition();
                        for (int currentpostion = startindex; currentpostion <= endindex; currentpostion++) {
                            View convertView = gv.getChildAt(currentpostion
                                    - startindex);
                            if (convertView != null
                                    && convertView instanceof PhotoGridItem) {
                                PhotoGridItem item = (PhotoGridItem) convertView;
                                if (item.mImageView.getVisibility() == View.VISIBLE) {
                                    item.mImageView.StopScroll();
                                }
                            }
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        if (adapter != null) {
                            adapter.SetIsScrolling(false);
                        }
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseNum > 0) {
                    Intent data = new Intent();
                    data.putExtra("gl_arr", select_gl_arr);
                    setResult(RESULT_OK, data);
                    finish();
                }

            }
        });
        preview_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseNum > 0) {
                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                            (v.getMeasuredWidth(), v.getMeasuredHeight());
                    List<String> photoUrls = new ArrayList<>();
                    for (PhotoItem mPhotoItem : select_gl_arr) {
                        //显示大图
                        photoUrls.add(mPhotoItem.getPath());
                    }
                    ImagePagerActivity.startImagePagerActivity((PhotoActivity.this), photoUrls,0, imageSize);
                }
            }
        });
        choose_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                } else {
                    dialog.show();
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private List<PhotoAibum> getPhotoAlbum() {
        PhotoAibum all = new PhotoAibum();
        List<PhotoAibum> aibumList = new ArrayList<>();
        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, "",
                MediaStore.MediaColumns.DATE_ADDED + " DESC");
        Map<String, PhotoAibum> countMap = new HashMap<String, PhotoAibum>();
        PhotoAibum pa = null;
        int i = 0;
        String first_id = "";
        if (cursor == null) {
            return aibumList;
        }
        while (cursor.moveToNext()) {
            long size = cursor.getLong(7);
            if (size <= 0) {
                continue;
            }
            String path = cursor.getString(1);
            String id = cursor.getString(3);
            String dir_id = cursor.getString(4);
            String dir = cursor.getString(5);
            long datemodified = cursor.getLong(6);

            if (i == 0) {
                first_id = id;
            }
            // Log.e("info", "id===" + id + "==dir_id==" + dir_id + "==dir=="
            // + dir + "==path=" + path);
            PhotoItem photoItem = new PhotoItem(Integer.valueOf(id), path,
                    datemodified);
            all.addBit(photoItem);
            if (!countMap.containsKey(dir_id)) {
                pa = new PhotoAibum();
                pa.setName(dir);
                pa.setBitmap(Integer.parseInt(id));
                pa.setCount("1");
                pa.getBitList().add(photoItem);
                countMap.put(dir_id, pa);
            } else {
                pa = countMap.get(dir_id);
                pa.setCount(String.valueOf(Integer.parseInt(pa.getCount()) + 1));
                pa.getBitList().add(photoItem);
            }

            i++;
        }
        cursor.close();
        Iterable<String> it = countMap.keySet();

        for (String key : it) {
            aibumList.add(countMap.get(key));
        }

        all.setName(getResources().getString(R.string.all_photo));
        all.setCount(all.getBitList().size() + "");
        if (!first_id.equals("")) {
            all.setBitmap(Integer.parseInt(first_id));
        }
        aibumList.add(0, all);
        return aibumList;
    }

    private void init() {
        if (chooseNum == 0) {
            btn_sure.setText(R.string.complete);
            preview_tv.setText(R.string.preview);
            btn_sure.setTextColor(getResources().getColor(R.color.lightgray));
        } else {
            preview_tv.setText(getResources().getString(R.string.preview) + "("
                    + select_gl_arr.size() + "/"
                    + (max_select_count - selected_count) + ")");
            btn_sure.setText(getResources().getString(R.string.complete) + "("
                    + select_gl_arr.size() + "/"
                    + (max_select_count - selected_count) + ")");
            btn_sure.setTextColor(getResources().getColor(R.color.text_link));
        }
//        if (isSeclect) {
//            btn_sure.setText(getResources().getString(R.string.complete) + "("
//                    + select_gl_arr.size() + "/"
//                    + (max_select_count - selected_count) + ")");
//        } else {
//            btn_sure.setText(getResources().getString(R.string.complete) + "("
//                    + select_gl_arr.size() + "/"
//                    + (max_select_count - selected_count) + ")");
//        }
    }

}
