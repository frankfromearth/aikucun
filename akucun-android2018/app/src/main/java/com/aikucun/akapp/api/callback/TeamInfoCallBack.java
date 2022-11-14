package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.RuleInfo;
import com.aikucun.akapp.api.entity.TeamInfo;
import com.alibaba.fastjson.JSON;

/**
 * Created by ak123 on 2018/1/19.
 * 团队信息返回
 */

public class TeamInfoCallBack extends ApiBaseCallback<TeamInfo> {
    @Override
    public TeamInfo parseResponse(ApiResponse responseData) throws Exception {
        TeamInfo mTeamInfo = JSON.parseObject(responseData.getJsonObject().toString(), TeamInfo.class);
        if (mTeamInfo.getRule() == null) {
            RuleInfo rule = new RuleInfo();
            rule.setRuleUrl("");
            rule.setVideoUrl("");
            mTeamInfo.setRule(rule);
        }
        return mTeamInfo;
    }
}
