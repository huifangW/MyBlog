package com.whf.myblog.service;

import com.whf.myblog.po.Blog;
import com.whf.myblog.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BlogService {
    Blog saveBlog(Blog blog);
    Blog getBlog(Long id);
    Blog getAndConvert(Long id);
    Page<Blog> listBlog(Pageable pageable, BlogQuery blogQ);
    Page<Blog> listBlog(Long tagId, Pageable pageable);
    Page<Blog> listBlog(Pageable pageable);
    Page<Blog> listBlog(String query, Pageable pageable);
    List<Blog> listRecommendBlogTop(Integer size);
    Map<String, List<Blog>> archiveBlog();
    Blog updateBlog(Blog blog);
    Long countBlog();
    void deleteBlog(Long id);
}
