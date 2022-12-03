package com.whf.myblog.dao;

import com.whf.myblog.po.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    Page<Tag> findAll(Pageable pageable);
    Tag getTagByName(String string);

    @Query("select t from Tag t")
    List<Tag> findTop(Pageable pageable);
}
