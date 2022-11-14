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

public class MyYFHSqliteHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "akucun_adorder" ;//数据库名称
    private static final String TABLE_NAME = "scan_pei_huo";//表名称
    private static final int SCHEMA_VERSION = 1;//版本号,则是升级之后的,升级方法请看onUpgrade方法里面的判断

    public MyYFHSqliteHelper(Context context)
    {
        super(context, DATABASE_NAME + (HttpConfig.isOnline?"o.db":"d.db"), null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                "("+
                "_id String PRIMARY KEY,"+
                " adorderid TEXT, " +
                " ctimestamp INTEGER, "+
                " adorderjson TEXT, " +
                " productid TEXT, " +
                " cartproductid TEXT, " +
                " barcode TEXT, " +
                " name TEXT, " +
                " remark TEXT, " +
                " isPeiHuo INTEGER);" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    private String selectAllColumns() {
        return "SELECT " +
                "_id, " +
                "adorderid, "+
                "ctimestamp, "+
                "adorderjson, "+
                "productid, "+
                "cartproductid, " +
                "barcode, "+
                "name, "+
                "remark, "+
                "isPeiHuo " +
                "FROM " +
                TABLE_NAME +
                " ";

    }
    public Cursor getAll(String where, String orderBy)
    {
        StringBuilder buf = new StringBuilder(
                selectAllColumns());

        if (where != null)
        {
            buf.append(" WHERE ");
            buf.append(where);
        }

        if (orderBy != null)
        {
            buf.append(" ORDER BY ");
            buf.append(orderBy);
        }

        return (getReadableDatabase().rawQuery(buf.toString(), null));
    }

    public Cursor getAllColumns(String distinct) {
        StringBuilder buf = new StringBuilder("SELECT  DISTINCT " + distinct + " FROM " + TABLE_NAME);
        return (getReadableDatabase().rawQuery(buf.toString(), null));
    }


    public Cursor getById(String id)
    {
        //根据点击事件获取id,查询数据库
        String[] args = {id};

        return (getReadableDatabase().rawQuery(selectAllColumns() + " WHERE _ID=?", args));
    }

    public Cursor getByAdOrderId(String pid)
    {
        String[] args = {pid};

        return (getReadableDatabase().rawQuery(selectAllColumns() + " WHERE adorderid=?", args));
    }

    public Cursor getByBarcode(String barcode)
    {
        String[] args = {barcode};

        return (getReadableDatabase().rawQuery(selectAllColumns() + " WHERE barcode=? ", args));
    }

    public Cursor getByBarcodeWithNoPeihuo(String barcode)
    {
        String[] args = {barcode};

        return (getReadableDatabase().rawQuery(selectAllColumns() + " WHERE barcode=? AND isPeiHuo=0 ", args));
    }


    public Cursor getByOrderBarcode(String adorderId, String barcode)
    {
        String[] args = {adorderId, barcode};
        return (getReadableDatabase().rawQuery(selectAllColumns() + " WHERE adorderid = ? AND barcode=?  AND isPeiHuo=0 ", args));
    }

    public Cursor getByOrderBarcodeWithNoPeihuo(String adorderId,String barcode)
    {
        String[] args = {adorderId, barcode};
        return (getReadableDatabase().rawQuery(selectAllColumns() + " WHERE adorderid = ? AND barcode=?", args));
    }

    public int count()
    {
        String sql = "select count(*) from " + TABLE_NAME;
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        int catCount = 0;
        if (c.moveToFirst())
        {
            catCount = c.getInt(0);
        }
        return catCount;
    }

    public void deleteAll()
    {
        String[] args = {String.valueOf("1")};
        getWritableDatabase().delete(TABLE_NAME, "_id != ?", args);
    }

    public void insert(String uid,
                       String adorderid,
                       Long ctimestamp,
                       String adOrderString,
                       String productid,
                       String cartproductid,
                       String barcode,
                       String name,
                       String remark,
                       int isPeiHuo)
    {
        ContentValues cv = new ContentValues();
        cv.put("_id", uid);
        cv.put("adorderid", adorderid);
        cv.put("ctimestamp", ctimestamp);
        cv.put("adorderjson", adOrderString);
        cv.put("productid", productid);
        cv.put("cartproductid", cartproductid);
        cv.put("barcode", barcode);
        cv.put("name", name);
        cv.put("remark", remark);
        cv.put("isPeiHuo", isPeiHuo);

        long result = getWritableDatabase().insert(TABLE_NAME, "_id", cv);
        if (-1 == result) {
            SCLog.error("插入数据库失败！");
        }
    }

    public void update(String uid,
                       String adorderid,
                       Long ctimestamp,
                       String adOrderString,
                       String productid,
                       String cartproductid,
                       String barcode,
                       String name,
                       String remark,
                       int isPeiHuo)
    {
        ContentValues cv = new ContentValues();
        String[] args = {uid};

        cv.put("adorderid", adorderid);
        cv.put("ctimestamp", ctimestamp);
        cv.put("adorderjson", adOrderString);
        cv.put("productid", productid);
        cv.put("cartproductid", cartproductid);
        cv.put("barcode", barcode);
        cv.put("name", name);
        cv.put("remark", remark);
        cv.put("isPeiHuo", isPeiHuo);

        int result = getWritableDatabase().update(TABLE_NAME, cv, "_ID=?", args);
        if (0  == result) {
            SCLog.error("更新数据库失败！");
        }
    }


    public void updatePeiHuo(String uid, int isPeiHuo ) {

        ContentValues cv = new ContentValues();
        String[] args = {uid};
        cv.put("isPeiHuo", isPeiHuo);
        getWritableDatabase().update(TABLE_NAME, cv, "_ID=?", args);
    }

    public void updateRemark(String uid, String remark) {
        ContentValues cv = new ContentValues();
        String[] args = {uid};
        cv.put("remark", remark);
        int result = getWritableDatabase().update(TABLE_NAME, cv, "_ID=?", args);
        if (0 == result) {
            SCLog.debug("update remark failed!");
        }
    }

    public String getId(Cursor c)
    {
        return (c.getString(0));
    }

    public String getAdorderID(Cursor c)
    {
        return (c.getString(1));
    }
    public String getCtimestamp(Cursor c)
    {
        return (c.getString(2));
    }

    public String getAdOrderString(Cursor c)
    {
        return (c.getString(3));
    }

    public String getProductID(Cursor c)
    {
        return (c.getString(4));
    }

    public String getCartProductID(Cursor c)
    {
        return (c.getString(5));
    }

    public String getBarcode(Cursor c)
    {
        return (c.getString(6));
    }

    public String getName(Cursor c)
    {
        return (c.getString(7));
    }

    public String getRemark(Cursor c) {
        return (c.getString(8));
    }

    public String getPeiHuo(Cursor c)
    {
        return (c.getString(9));
    }

    public void deleteAdOrder(String adorderid) {

        ContentValues cv = new ContentValues();
        String[] args = {adorderid};
        int result = getWritableDatabase().delete(TABLE_NAME, "adorderid=?", args);
        if (0 == result) {
            SCLog.debug("delete adorder failed!");
        }
    }
}


