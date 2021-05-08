package com.cimonhe.service;

import com.cimonhe.pojo.Tag;

import java.util.List;

public interface TagService {

    public String queryTagNameById(int tagId);

    public List<Tag> queryAllTag();

    public List<Tag> queryAllPendingTag();

    public int upTag(Tag tag);

    public int checkTagPass(int tagId);

    public int deleteTagById(int tagId);
}
