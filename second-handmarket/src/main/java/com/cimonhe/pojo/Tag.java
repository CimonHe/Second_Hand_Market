package com.cimonhe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    int id;
    String tagName;
    boolean isChecked;

    public Tag(String tagName) {
        this.tagName = tagName;
    }
}
