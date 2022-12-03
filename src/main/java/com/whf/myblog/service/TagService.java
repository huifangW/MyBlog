package com.whf.myblog.service;

import com.whf.myblog.po.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    Tag saveTag(Tag tag);
    Tag getTag(Long id);
    Tag getByName(String string);
    Page<Tag> listTag(Pageable pageable);
    List<Tag> listTagTop(Integer size);
    List<Tag> listTag();
    List<Tag> listTag(String ids);
    Tag updateTag(Long id, Tag tag);
    void deleteTag(Long id);
}
