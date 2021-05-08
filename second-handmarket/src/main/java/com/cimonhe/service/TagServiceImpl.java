package com.cimonhe.service;

import com.cimonhe.mapper.TagMapper;
import com.cimonhe.pojo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService{

    @Autowired
    TagMapper tagMapper;

    @Override
    public String queryTagNameById(int tagId) {
        return tagMapper.queryTagNameById(tagId);
    }

    @Override
    public List<Tag> queryAllTag() {
        return tagMapper.queryAllTagByIsChecked(true);
    }

    @Override
    public List<Tag> queryAllPendingTag() {
        return tagMapper.queryAllTagByIsChecked(false);
    }

    @Override
    public int upTag(Tag tag) {
        tag.setChecked(false);
        return tagMapper.addTag(tag);
    }

    @Override
    public int checkTagPass(int tagId) {
        return tagMapper.upTagIsChecked(tagId);
    }

    @Override
    public int deleteTagById(int tagId) {
        return tagMapper.deleteTagById(tagId);
    }

}
