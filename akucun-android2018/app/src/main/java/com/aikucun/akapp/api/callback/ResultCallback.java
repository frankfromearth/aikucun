package com.aikucun.akapp.api.callback;

import android.app.Activity;

import com.aikucun.akapp.base.BaseActivity;

/**
 * Created by jarry on 2017/6/9.
 */
public class ResultCallback extends ApiBaseCallback<ApiResponse>
{

    private Activity activity;

    public ResultCallback() {
        super();
    }

    public ResultCallback(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onApiFailed(String message, int code)
    {
        if (activity != null)
        {
            if (activity instanceof BaseActivity)
                ((BaseActivity)activity).handleApiFailed(message, code);
        } else {
            super.onApiFailed(message,code);
        }
    }


    @Override
    public ApiResponse parseResponse(ApiResponse responseData) throws Exception
    {
        return responseData;
    }
}
