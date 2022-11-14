package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * 发货物流信息
 * Created by jarry on 2017/6/7.
 */

public class Logistics implements Serializable
{
    private String fahuoshijian;
    private String lianxidianhua;
    private String shouhuodizhi;
    private String shouhuoren;
    private String wuliugongsi;
    private String wuliuhao;

    public String getFahuoshijian()
    {
        return fahuoshijian;
    }

    public void setFahuoshijian(String fahuoshijian)
    {
        this.fahuoshijian = fahuoshijian;
    }

    public String getLianxidianhua()
    {
        return lianxidianhua;
    }

    public void setLianxidianhua(String lianxidianhua)
    {
        this.lianxidianhua = lianxidianhua;
    }

    public String getShouhuodizhi()
    {
        return shouhuodizhi;
    }

    public void setShouhuodizhi(String shouhuodizhi)
    {
        this.shouhuodizhi = shouhuodizhi;
    }

    public String getShouhuoren()
    {
        return shouhuoren;
    }

    public void setShouhuoren(String shouhuoren)
    {
        this.shouhuoren = shouhuoren;
    }

    public String getWuliugongsi()
    {
        return wuliugongsi;
    }

    public void setWuliugongsi(String wuliugongsi)
    {
        this.wuliugongsi = wuliugongsi;
    }

    public String getWuliuhao()
    {
        return wuliuhao;
    }

    public void setWuliuhao(String wuliuhao)
    {
        this.wuliuhao = wuliuhao;
    }


    public String displayWuliuInfo(){

        String[] wuliuhaoArray = wuliuhao.split(",");
        if (wuliuhaoArray.length <= 1)
        {
            return wuliugongsi + "\n单号：" + wuliuhao;
        }
        StringBuilder result = new StringBuilder("" + wuliugongsi);

        int index = 0;
        for (String wuliu : wuliuhaoArray)
        {
            result.append("\n单号 " + ++index + " : " + wuliu);
        }
        return result.toString();
    }

}
