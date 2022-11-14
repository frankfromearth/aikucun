package com.aikucun.akapp.utils;

/**
 * Created by jinlibin on 2017/6/9.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aikucun.akapp.api.HttpConfig;

public class MySqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "akucun";//数据库名称
    private static final int SCHEMA_VERSION = 1;//版本号,则是升级之后的,升级方法请看onUpgrade方法里面的判断

    public MySqliteHelper(Context context) {//构造函数,接收上下文作为参数,直接调用的父类的构造函数
        super(context, DATABASE_NAME + (HttpConfig.isOnline ? "o3.db" : "d3.db"), null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//创建的是一个午餐订餐的列表,id,菜名,地址等等
        db.execSQL("CREATE TABLE product (_id String PRIMARY KEY, productId TEXT, " + "" +
                "pinpai TEXT, desc TEXT, json TEXT, xuhao INTEGER,lastxuhao INTEGER, time INTEGER, liveId TEXT, " +
                "corpId TEXT, quehuo INTEGER, forward INTEGER, follow INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion == 1 && newVersion == 3) {//升级判断,如果再升级就要再加两个判断,从1到3,从2到3
//            db.execSQL("ALTER TABLE product ADD liveId TEXT;");
//            db.execSQL("ALTER TABLE product ADD corpId TEXT;");
//            db.execSQL("ALTER TABLE product ADD quehuo INTEGER;");
//
//            // follow is version 3 add
//            db.execSQL("ALTER TABLE product ADD forward INTEGER;");
//            db.execSQL("ALTER TABLE product ADD follow INTEGER;");
//        }
//
//        if (oldVersion == 2 && newVersion == 3) {//升级判断,如果再升级就要再加两个判断,从2到3
//            db.execSQL("ALTER TABLE product ADD forward INTEGER;");
//            db.execSQL("ALTER TABLE product ADD follow INTEGER;");
//        }
    }

    public Cursor getAll(String where, String orderBy) {//返回表中的数据,where是调用时候传进来的搜索内容,orderby是设置中传进来的列表排序类型
        StringBuilder buf = new StringBuilder("SELECT _id, productId, pinpai, desc, json, xuhao,lastxuhao, " +
                "" + "time, liveId, corpId, quehuo ,forward, follow " + "FROM" + " product");

        if (where != null) {
            buf.append(" WHERE ");
            buf.append(where);
        }

        if (orderBy != null) {
            buf.append(" ORDER BY ");
            buf.append(orderBy);
        }

        return (getReadableDatabase().rawQuery(buf.toString(), null));
    }

    public Cursor getAllColumns(String distinct) {
        StringBuilder buf = new StringBuilder("SELECT  DISTINCT " + distinct + " FROM " + " product");
        return (getReadableDatabase().rawQuery(buf.toString(), null));
    }


    public Cursor getById(String id) {//根据点击事件获取id,查询数据库
        String[] args = {id};

        return (getReadableDatabase().rawQuery("SELECT _id, productId, pinpai, desc, json, " +
                "xuhao,lastxuhao," + " time, liveId, corpId, quehuo, forward,follow " + "FROM product WHERE _ID=?", args));
    }

    public Cursor getByProductId(String pid) {
        String[] args = {pid};

        return (getReadableDatabase().rawQuery("SELECT _id, productId, pinpai, desc, json, " +
                        "xuhao,lastxuhao," + " time, liveId, corpId, quehuo,forward,follow " + "FROM product WHERE productId=?",
                args));
    }

    public int count() {
        String sql = "select count(*) from product";
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        int catCount = 0;
        if (c.moveToFirst()) {
            catCount = c.getInt(0);
        }
        return catCount;
    }

    public void deleteAll() {
        String[] args = {String.valueOf("1")};
        getWritableDatabase().delete("product", "_id != ?", args);
    }


    public void deleteOldData(long time) {
        String[] args = {String.valueOf(time)};
        getWritableDatabase().delete("product", "time < ?", args);
    }


    public void deleteLiveId(String liveId) {
        if (StringUtils.isEmpty(liveId))
            return;

        String[] args = {liveId};
        getWritableDatabase().delete("product", "liveId = ?", args);
    }

    public void deleteProduct(String productId) {
        String[] args = {productId};
        getWritableDatabase().delete("product", "productId = ?", args);
    }

    public void insert(String productId, String pinpai, String desc, String json, Integer xuhao, Integer lastxuhao,
                       Long time, String liveId, String corpId, Integer quehuo, Integer forward, Integer follow) {
        ContentValues cv = new ContentValues();
        cv.put("_id", productId);
        cv.put("productId", productId);
        cv.put("pinpai", pinpai);
        cv.put("desc", desc);
        cv.put("json", json);
        cv.put("xuhao", xuhao);
        cv.put("lastxuhao", lastxuhao);
        cv.put("time", time);


        cv.put("liveId", liveId);
        cv.put("corpId", corpId);
        cv.put("quehuo", quehuo);

        cv.put("forward", forward);
        cv.put("follow", follow);

        getWritableDatabase().insert("product", "productId", cv);
    }

    public void update(String productId, String pinpai, String desc, String json, Integer xuhao, Integer lastxuhao,
                       Long time, String liveId, String corpId, Integer quehuo, Integer forward, Integer follow) {
        ContentValues cv = new ContentValues();
        String[] args = {productId};

        cv.put("productId", productId);
        cv.put("pinpai", pinpai);
        cv.put("desc", desc);
        cv.put("json", json);
        cv.put("xuhao", xuhao);
        cv.put("lastxuhao", lastxuhao);
        cv.put("time", time);

        cv.put("liveId", liveId);
        cv.put("corpId", corpId);
        cv.put("quehuo", quehuo);

        cv.put("forward", forward);
        cv.put("follow", follow);

        getWritableDatabase().update("product", cv, "_ID=?", args);
    }

    /**
     * _id String PRIMARY KEY, productId TEXT, " + "" +
     "pinpai TEXT, desc TEXT, json TEXT, xuhao INTEGER,lastxuhao INTEGER, time INTEGER, liveId TEXT, " +
     "corpId TEXT, quehuo INTEGER, forward INTEGER, follow INTEGER
     * @param c
     * @return
     */
    public String getProductId(Cursor c) {
        return (c.getString(c.getColumnIndex("productId")));
    }

    public String getPinpai(Cursor c) {
        return (c.getString(c.getColumnIndex("pinpai")));
    }

    public String getDesc(Cursor c) {
        return (c.getString(c.getColumnIndex("desc")));
    }

    public String getJson(Cursor c) {
        return (c.getString(c.getColumnIndex("json")));
    }

    public String getXuhao(Cursor c) {
        return (c.getString(c.getColumnIndex("xuhao")));
    }

    public String getLastXuhao(Cursor c) {
        return (c.getString(c.getColumnIndex("lastxuhao")));
    }

    public String getTime(Cursor c) {

        return (c.getString(c.getColumnIndex("time")));
    }

    public String getLiveId(Cursor c) {
        return (c.getString(c.getColumnIndex("liveId")));
    }

    public String getCorpId(Cursor c) {
        return (c.getString(c.getColumnIndex("corpId")));
    }

    public String getQuehuo(Cursor c) {
        return (c.getString(c.getColumnIndex("quehuo")));
    }

    public String getForward(Cursor c) {
        return (c.getString(c.getColumnIndex("forward")));
    }

    public String getFollow(Cursor c) {
        return (c.getString(c.getColumnIndex("follow")));
    }

}


