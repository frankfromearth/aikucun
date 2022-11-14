package com.aikucun.akapp.api.response;

import com.aikucun.akapp.api.entity.Comment;

import java.util.List;

/**
 * Created by jarry on 2017/6/10.
 */

public class SynCommentsResp
{
    public List<Comment> comments;

    public boolean hasmore;

    public long lastupdate;

}
