package com.whf.myblog.web;

import com.whf.myblog.po.Blog;
import com.whf.myblog.po.Tag;
import com.whf.myblog.service.BlogService;
import com.whf.myblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagShowController {

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;

    @GetMapping("tags/{id}")
    public String tagShow(@PageableDefault(size = 8, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                          @PathVariable Long id,
                          Model model) {

        List<Tag> tags = tagService.listTagTop(10000);
        if(id == -1) {
            id = tags.get(0).getId();
        }
        Page<Blog> blogs = blogService.listBlog(id, pageable);

        model.addAttribute("tags", tags);
        model.addAttribute("pageBlog", blogs);
        model.addAttribute("activeTagId", id);
        return "tags";
    }
}
