package com.cimonhe;

import com.cimonhe.mapper.TagMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TagTest {

    @Autowired
    TagMapper tagMapper;

    @Test
    public void queryTagNameById(){
        System.out.println(tagMapper.queryTagNameById(1));
    }

}
