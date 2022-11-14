package com.aikucun.akapp.api.entity;

import java.util.List;

/**
 * Created by ak123 on 2018/1/19.
 * 邀请信息
 */

public class InviteInfo {

    private int memberCount;
    private long todoReward;
    private long rewardTotal;
    private List<InviteMember> todo;

    private RuleInfo rule;

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public long getTodoReward() {
        return todoReward;
    }

    public void setTodoReward(long todoReward) {
        this.todoReward = todoReward;
    }

    public long getRewardTotal() {
        return rewardTotal;
    }

    public void setRewardTotal(long rewardTotal) {
        this.rewardTotal = rewardTotal;
    }

    public List<InviteMember> getTodo() {
        return todo;
    }

    public void setTodo(List<InviteMember> todo) {
        this.todo = todo;
    }

    public RuleInfo getRule()
    {
        return rule;
    }

    public void setRule(RuleInfo rule)
    {
        this.rule = rule;
    }
}
