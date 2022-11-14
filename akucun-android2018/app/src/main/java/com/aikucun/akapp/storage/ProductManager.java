package com.aikucun.akapp.storage;

import android.database.Cursor;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.api.entity.Comment;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.utils.MySqliteHelper;
import com.aikucun.akapp.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jinlibin on 2017/6/9.
 */

public class ProductManager {
    private static final String PINPAI_NON_IDENTIFIER = "PINPAI_NON_IDENTIFIER";
    public long productUpdate = 0;
    public long commentUpdate = 0;
    public long skuUpdate = 0;

    public int forwardCount;

    private String liveId;

    private static ProductManager _instance;

    private MySqliteHelper mySqliteHelper;

    private HashMap<String, Integer> forwardData;

    public static List<Product> loadProducts(int start, String liveId) {
        return getInstance().getProductsStartWith(start, 20, liveId);
    }

    public static List<Product> searchProductsBy(int start, boolean isQuehuo, String liveId) {
        return getInstance().getProductsQuehuo(start, 20, isQuehuo, liveId);
    }

    public static List<Product> searchProductsBy(String key, int index, String liveId) {
        return getInstance().searchProductsByKey(key, index, liveId);
    }


    public int getForwardIndex() {
        String key = StringUtils.isEmpty(getInstance().liveId) ? ProductManager.PINPAI_NON_IDENTIFIER : getInstance().liveId;
        return AppContext.get(key, 1);
    }

    public void setForwardIndex(int index) {
        String key = StringUtils.isEmpty(getInstance().liveId) ? ProductManager.PINPAI_NON_IDENTIFIER : getInstance().liveId;
        AppContext.set(key, index > 0 ? index : 1);
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_FORWARD_PINPAIID_CHANGED));

    }

    public Product getForwardProduct(int xuehao) {

        Product product = getValidForwardProduct(xuehao);
        if (null != product) {
            setForwardIndex(product.getXuhao());
        }
        return product;
    }

    public Product getValidForwardProduct(int xuhao) {
        Product product = null;
        int index = xuhao;
        if (index == 0) {
            index = ProductManager.getInstance().getForwardIndex();
        }

        Product maxProduct = ProductManager.getInstance().lastProduct(liveId);
        if (null == maxProduct)
            return maxProduct;

        int maxXuhao = maxProduct.getXuhao();

        while (null == product) {
            List<Product> items = ProductManager.getInstance().searchProductByXuhaoAndLive(index, liveId);

            if (index <= maxXuhao) {
                if (null == items || 0 == items.size()) {
                    index++;
                    continue;
                }
            } else {
                return null;
            }
            Product tmpProduct = items.get(0);
            if (!tmpProduct.isQuehuo()) {
                product = tmpProduct;
                break;
            } else {
                index++;
            }
        }
        return product;
    }

    public static void clearAllData() {
        getInstance().clearAllProduct();
    }


    public static ProductManager getInstance() {
        if (_instance == null) {
            _instance = new ProductManager();
        }
        return _instance;
    }

    public void clearAllProduct() {
        mySqliteHelper.deleteAll();

        this.productUpdate = 0;
        this.commentUpdate = 0;
        this.skuUpdate = 0;
    }

    public void init(MySqliteHelper helper) {
        this.mySqliteHelper = helper;

        // 进行数据清除以及处理
        initData();
    }


    public void initData() {
        // 清除旧数据
        deleteOldData();

        // 获取最新一条数据
        List<Product> products = getProductsStartWith(0, 1, null);
        if (products != null && products.size() > 0) {
            this.productUpdate = products.get(0).getShangjiashuzishijian();
        }
        //
        this.skuUpdate = AppContext.get(AppConfig.PREF_KEY_SKU_UPDATE, (long) 0);
        this.commentUpdate = AppContext.get(AppConfig.PREF_KEY_COMMENT_UPDATE, (long) 0);

    }

    public Product findProductById(String pid) {
        ProductDB pdb = getProductById(pid);
        if (null != pdb) {
            return pdb.productModel();
        }
        return null;
    }


    public ProductDB getProductById(String pId) {
        Cursor c = mySqliteHelper.getByProductId(pId);
        if (null == c) {
            return null;
        }

        if (c.moveToFirst()) {
            ProductDB p = build(c);
            c.close();
            return p;
        }
        return null;
    }

    private ProductDB build(Cursor c) {
        ProductDB p = new ProductDB();
        p.productId = mySqliteHelper.getProductId(c);
        p.pinpai = mySqliteHelper.getPinpai(c);
        p.desc = mySqliteHelper.getDesc(c);
        p.json = mySqliteHelper.getJson(c);
        p.xuhao = Integer.valueOf(mySqliteHelper.getXuhao(c));
        p.lastxuhao = Integer.valueOf(mySqliteHelper.getLastXuhao(c));
        p.time = Long.valueOf(mySqliteHelper.getTime(c));

        p.liveId = mySqliteHelper.getLiveId(c);
        p.corpId = mySqliteHelper.getCorpId(c);
        p.quehuo = Integer.valueOf(mySqliteHelper.getQuehuo(c));

        p.forward = Integer.valueOf(mySqliteHelper.getForward(c));
        p.follow = Integer.valueOf(mySqliteHelper.getFollow(c));
        return p;
    }

    public void updateProduct(Product product) {
        ProductDB pdb = ProductDB.initWithModel(product);
        mySqliteHelper.update(pdb.productId, pdb.pinpai, pdb.desc, pdb.json, pdb.xuhao, pdb.lastxuhao, pdb.time, pdb.liveId, pdb.corpId, pdb.quehuo, pdb.forward, pdb.follow);
    }

    public void insertProducts(List<Product> products) {
        List<ProductDB> lst = new LinkedList<ProductDB>();
        for (Product p : products) {
            ProductDB pdb = ProductDB.initWithModel(p);
            lst.add(pdb);
        }

        if (!lst.isEmpty()) {
            saveProductsToDB(lst);
        }
    }

    public List<Product> getProductsQuehuo(int index, int count, boolean isQuehuo, String liveId) {
        int total = mySqliteHelper.count();
        if (index >= total) {
            return null;
        }
        int remain = total - index;
        if (remain < count) {
            count = remain;
        }

        String where = "quehuo = " + (isQuehuo ? 1 : 0);

        if (!StringUtils.isEmpty(liveId)) {
            where += " AND liveId LIKE '" + liveId + "'";
        }

        // 以时间倒排
        String orderby = "time desc LIMIT " + index + "," + count;
        List<Product> lst = new LinkedList<Product>();
        Cursor c = mySqliteHelper.getAll(where, orderby);
        while (c.moveToNext()) {
            ProductDB pdb = build(c);
            Product p = pdb.productModel();
            lst.add(p);
        }
        return lst;
    }

    public Product lastProduct(String liveId) {

        String where = null;
        if (!StringUtils.isEmpty(liveId)) {
            where = " liveId LIKE '" + liveId + "'";
        }

        String orderby = " xuhao desc LIMIT 1 OFFSET 0";
        Cursor c = mySqliteHelper.getAll(where, orderby);
        Product p = null;
        while (c.moveToNext()) {
            ProductDB pdb = build(c);
            p = pdb.productModel();
        }
        return p;
    }

    /**
     * 获取最后一个序号的最大值商品
     *
     * @param liveId
     * @return
     */
    public Product getLastXuHaoProduct(String liveId) {
        String where = null;
        if (!StringUtils.isEmpty(liveId)) {
            where = " liveId LIKE '" + liveId + "'";
        }

        String orderby = " lastxuhao desc LIMIT 1 OFFSET 0";
        Cursor c = mySqliteHelper.getAll(where, orderby);
        Product p = null;
        while (c.moveToNext()) {
            ProductDB pdb = build(c);
            p = pdb.productModel();
        }
        return p;
    }

    public List<Product> getProductsStartWith(int index, int count, String liveId) {
        int total = mySqliteHelper.count();
        if (index >= total) {
            return null;
        }
        int remain = total - index;
        if (remain < count) {
            count = remain;
        }

        String where = null;
        if (null != liveId && !liveId.isEmpty()) {
            // 从中查找相关字段
            where = " liveId LIKE '" + liveId + "'";
        }

        // 以时间倒排
        String orderby = "time desc LIMIT " + index + "," + count;
        List<Product> lst = new LinkedList<Product>();
        Cursor c = mySqliteHelper.getAll(where, orderby);
        while (c.moveToNext()) {
            ProductDB pdb = build(c);
            Product p = pdb.productModel();
            lst.add(p);
        }
        return lst;
    }


    public List<String> getPinpais() {
        List<String> lst = new LinkedList<String>();
        Cursor c = mySqliteHelper.getAllColumns("pinpai");
        while (c.moveToNext()) {
            lst.add(c.getString(0));
        }
        return lst;
    }

    public String getPinpaiString() {
        List<String> pinpais = getPinpais();
        StringBuilder builder = new StringBuilder();
        for (String pinpai : pinpais) {
            builder.append(pinpai);
            builder.append(",");
        }
        if (pinpais.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }


    public List<Product> searchProductsByKey(String key, int index, String liveId) {
        // 从描述中查找相关字段
        String where = " desc LIKE '%" + key + "%'";

        if (!StringUtils.isEmpty(liveId)) {
            where += " AND liveId LIKE '" + liveId + "'";
        }

        // 以时间倒排
        String orderby = "time desc LIMIT " + index + ",20";
        List<Product> lst = new LinkedList<Product>();
        Cursor c = mySqliteHelper.getAll(where, orderby);
        while (c.moveToNext()) {
            ProductDB pdb = build(c);
            Product p = pdb.productModel();
            lst.add(p);
        }
        return lst;
    }

//    public List<Product> searchProductByXuhao(int xuhao)
//    {
//        // 从描述中查找相关字段
//        String where = "xuhao=" + xuhao;
//        // 以时间倒排
//        String orderby = "time desc ";
//        List<Product> lst = new LinkedList<Product>();
//        Cursor c = mySqliteHelper.getAll(where, orderby);
//        while (c.moveToNext())
//        {
//            ProductDB pdb = build(c);
//            Product p = pdb.productModel();
//            lst.add(p);
//        }
//        return lst;
//    }


    public List<Product> searchProductByXuhaoAndLive(int xuhao, String liveId) {
        // 从描述中查找相关字段
        String where = "xuhao = " + xuhao;
        if (!StringUtils.isEmpty(liveId)) {
            where += " AND liveId = '" + liveId + "'";
        }
        // 以时间倒排
        String orderby = " time desc ";
        List<Product> lst = new LinkedList<Product>();
        Cursor c = mySqliteHelper.getAll(where, orderby);
        while (c.moveToNext()) {
            ProductDB pdb = build(c);
            Product p = pdb.productModel();
            lst.add(p);
        }
        return lst;
    }


    private void saveProductsToDB(List<ProductDB> products) {
        if (null == products || products.isEmpty()) {
            return;
        }

        // 进行插入记录
        for (ProductDB p : products) {
            if (p.lastxuhao == 0) p.lastxuhao = p.xuhao;
            if (!isExit(p.productId))
                mySqliteHelper.insert(p.productId, p.pinpai, p.desc, p.json, p.xuhao, p.lastxuhao, p.time, p.liveId, p.corpId, p.quehuo, p.forward, p.follow);
            else mySqliteHelper.update(p.productId,p.pinpai,p.desc,p.json,p.xuhao,p.lastxuhao,p.time,p.liveId,p.corpId,p.quehuo,p.forward,p.follow);
        }

    }

    /***
     * 清除 7天前的数据
     */
    private void deleteOldData() {
        Date date = new Date(new Date().getTime() - 7 * 24 * 3600 * 1000);
        mySqliteHelper.deleteOldData(date.getTime() / 1000);
    }

//    public void removeOverLives() {
//        String overLiveIds = LiveManager.getInstance().getOverLiveIds();
//        if (!StringUtils.isEmpty(overLiveIds)) {
//            String overLiveIdsss[] = overLiveIds.split(",");
//            for (String liveId : overLiveIdsss) {
//                mySqliteHelper.deleteLiveId(liveId);
//            }
//
//            LiveManager.getInstance().setOverPinpaiIds("");
//        }
//    }

    public void insertComments(List<Comment> comments) {
        for (Comment comment : comments) {
            if (comment.getStatus() != 0)
                continue;
            ProductDB prodcutDb = getProductById(comment.getProductid());
            if (null == prodcutDb)
                continue;
            Product product = prodcutDb.productModel();
            if (null == product)
                continue;
            product.addComment(comment);
        }
    }

    public void deleteProduct(String productId) {
        mySqliteHelper.deleteProduct(productId);
    }

    public void deleteLiveData(String liveId) {
        mySqliteHelper.deleteLiveId(liveId);
    }

    public boolean isExit(String pId) {
        Cursor c = mySqliteHelper.getById(pId);
        if (null == c) {
            return false;
        }
        if (c.moveToFirst()) {
            return true;
        } else return false;
    }
}
