package com.whf.myblog.dao;

import com.whf.myblog.po.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {
    @Query("select b from Blog b where b.recommend = true")
    List<Blog> recommendBlogsTop(Pageable pageable);

    @Query("select b from Blog b where b.title like ?1 or b.content like ?1")
    Page<Blog> findByQuery(String query,Pageable pageable);

    @Query("select function('date_format',b.updateAt, '%Y') as year from Blog as b group by year order by year desc")
    List<String> findGroupYears();

    @Query("select b from Blog b where function('date_format',b.updateAt, '%Y') = ?1")
    List<Blog> findByYear(String year);

    @Modifying
    @Query("update Blog set views=views+1 where id = ?1")
    int updateViews(Long blogId);
}
