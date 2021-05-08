package com.cimonhe.mapper;

import com.cimonhe.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TagMapper {

    public String queryTagNameById(int tagId);

    public List<Tag> queryAllTagByIsChecked(boolean isChecked);

    public int addTag(Tag tag);

    public int upTagIsChecked(int id);

    public int deleteTagById(int id);
}
