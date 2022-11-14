package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.TeamMembers;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by ak123 on 2018/1/19.
 * 团队成员信息
 */

public class TeamMembersCallBack extends ApiBaseCallback<List<TeamMembers>> {
    @Override
    public List<TeamMembers> parseResponse(ApiResponse responseData) throws Exception {
        String dataList = responseData.getJsonObject().getString("items");
        List<TeamMembers> list = JSON.parseArray(dataList, TeamMembers.class);
        return list;
    }
}