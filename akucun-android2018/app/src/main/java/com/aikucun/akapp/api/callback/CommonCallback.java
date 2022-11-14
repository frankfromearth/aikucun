package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.utils.HtmlUtils;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.convert.StringConvert;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 通用字符串返回数据
 * Created by jarry on 16/9/25.
 */
public class CommonCallback extends ApiBaseCallback<String>
{
    @Override
    public String convertSuccess(Response response) throws Exception
    {
        String s = StringConvert.create().convertSuccess(response);
        String body = s.replaceAll("\\r\\n", "").replaceAll("\\t", "");
        response.close();
        return HtmlUtils.filterHtml(body);
    }

    @Override
    public void onSuccess(String s, Call call, Response response)
    {
        SCLog.logi("HTTP - Response :");
        SCLog.log(s);

        onApiSuccess(s, call, null);
    }
}
