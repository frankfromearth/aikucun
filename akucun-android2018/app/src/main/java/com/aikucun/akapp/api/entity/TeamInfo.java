package com.aikucun.akapp.api.entity;

/**
 * Created by ak123 on 2018/1/19.
 * 团队信息
 */

public class TeamInfo {
    private int level;
    private long monthsTotal;
    private long todoReward;
    private long rewardTotal;
    private String amountArea;

    private RuleInfo rule;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getMonthsTotal() {
        return monthsTotal;
    }

    public void setMonthsTotal(long monthsTotal) {
        this.monthsTotal = monthsTotal;
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

    public String getAmountArea() {
        return amountArea;
    }

    public void setAmountArea(String amountArea) {
        this.amountArea = amountArea;
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
