package com.aikucun.akapp.storage;

import android.database.Cursor;

import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.utils.MyYFHSqliteHelper;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by micker on 2017/7/23.
 */

public class YiFaAdOrderManager {
    private static YiFaAdOrderManager _instance;

    private MyYFHSqliteHelper mySqliteHelper;


    public static YiFaAdOrderManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new YiFaAdOrderManager();
        }
        return _instance;
    }

    public static void clearAllData()
    {
        getInstance().clearAllProduct();
    }

    public void clearAllProduct()
    {
        mySqliteHelper.deleteAll();
    }

    public YiFaAdOrderDB getByOrderAndBarCode(String adorderId,String barCode, boolean isPeiHuo) {

        Cursor c = null;
        if (isPeiHuo) {
            c = mySqliteHelper.getByOrderBarcode(adorderId ,barCode);
        } else {
            c = mySqliteHelper.getByOrderBarcodeWithNoPeihuo(adorderId,barCode);
        }
        if (null == c)
        {
            return null;
        }

        if (c.moveToFirst())
        {
            YiFaAdOrderDB p = build(c);
            c.close();
            return p;
        }
        return null;
    }

    public YiFaAdOrderDB getByBarCode(String barCode)
    {
        Cursor c = mySqliteHelper.getByBarcodeWithNoPeihuo(barCode);
        if (null == c)
        {
            return null;
        }

        if (c.moveToFirst())
        {
            YiFaAdOrderDB p = build(c);
            c.close();
            return p;
        }
        return null;
    }

    public boolean checkYiFaHuoIsPeihuo(String adorderId) {
        Cursor c = mySqliteHelper.getById(adorderId);
        if (null == c)
        {
            return false;
        }

        if (c.moveToFirst())
        {
            YiFaAdOrderDB p = build(c);

            return p.getIsPeiHuo() == 1;
        }
        return false;
    }

    public AdOrder getByAdOrderId(String adorderId) {
        Cursor c = mySqliteHelper.getByAdOrderId(adorderId);
        if (null == c)
        {
            return null;
        }

        if (c.moveToFirst())
        {
            YiFaAdOrderDB p = build(c);
            AdOrder order = AdOrder.yy_modelWithJSON(p.getAdOrderString());
            /*
            if (null != order) {
                for (CartProduct pro:order.getProducts()) {
                    SCLog.debug("id : " + pro.getCartproductid() + "   ----->  " + p.getCartproductid());
                    if (pro.getCartproductid().equalsIgnoreCase(p.getCartproductid())) {
                        pro.setRemark(p.getRemark());
                    }
                }
            }
            */
            c.close();
            return order;
        }
        return null;
    }


    private YiFaAdOrderDB build(Cursor c)
    {
        YiFaAdOrderDB p = new YiFaAdOrderDB();
        p.setId(mySqliteHelper.getId(c));
        p.setAdorderid(mySqliteHelper.getAdorderID(c));;
        p.setCtimestamp(Long.valueOf(mySqliteHelper.getCtimestamp(c)));;
        p.setAdOrderString( mySqliteHelper.getAdOrderString(c));;
        p.setProductid( mySqliteHelper.getProductID(c));
        p.setCartproductid(mySqliteHelper.getCartProductID(c));
        p.setBarcode(  mySqliteHelper.getBarcode(c));;
        p.setName( mySqliteHelper.getName(c));;
        p.setRemark(mySqliteHelper.getRemark(c));;
        p.setIsPeiHuo( Integer.valueOf(mySqliteHelper.getPeiHuo(c)));;

        return p;
    }

    public void save(AdOrder order) {

        if (null == order)
            return;
        ArrayList<YiFaAdOrderDB> result = YiFaAdOrderDB.initWithModel(order);

        for (YiFaAdOrderDB p : result)
        {
            String uid = RSAUtils.md5String(order.getAdorderid()+p.getCartproductid());
            Cursor c = mySqliteHelper.getById(uid);
            if (null != c)
            {
                if (!c.moveToFirst()) {
                    c.close();
                    mySqliteHelper.insert(
                            uid,
                            p.getAdorderid(),
                            p.getCtimestamp(),
                            p.getAdOrderString(),
                            p.getProductid(),
                            p.getCartproductid(),
                            p.getBarcode(),
                            p.getName(),
                            p.getRemark(),
                            p.getIsPeiHuo());
                }
            }

        }
    }


    public void peiHuo(YiFaAdOrderDB order) {
        if (null == order)
            return;
        mySqliteHelper.updatePeiHuo(order.getId(),1);
    }

    public void init(MyYFHSqliteHelper myYFHSqliteHelper) {

        this.mySqliteHelper = myYFHSqliteHelper;

    }

    public void updateRemark(String uid, String remark) {

        if (StringUtils.isEmpty(remark) || StringUtils.isEmail(uid))
            return;
        mySqliteHelper.updateRemark(uid, remark);
    }

    public void deleteAdOrder(String adorderid) {
        if (StringUtils.isEmpty(adorderid))
            return;
        mySqliteHelper.deleteAdOrder(adorderid);

    }
}
