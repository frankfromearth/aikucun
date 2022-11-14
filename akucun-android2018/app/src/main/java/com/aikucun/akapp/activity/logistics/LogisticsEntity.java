package com.aikucun.akapp.activity.logistics;

import java.io.Serializable;

/**
 * Created by ak123 on 2017/12/7.
 */

public class LogisticsEntity implements Serializable {
    public String address;
    public String date;
    public int status;

    public LogisticsEntity(String _address,String _date,int _status){
        this.date = _date;
        this.address=_address;
        this.status=_status;
    }

    public LogisticsEntity(){};
}
