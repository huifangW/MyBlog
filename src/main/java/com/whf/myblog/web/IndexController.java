package com.whf.myblog.web;

import com.whf.myblog.po.Blog;
import com.whf.myblog.po.Comment;
import com.whf.myblog.service.BlogService;
import com.whf.myblog.service.CommentService;
import com.whf.myblog.service.TagService;
import com.whf.myblog.service.TypeService;
import com.whf.myblog.util.MarkdownUtils;
import com.whf.myblog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/")
    public String index(@PageableDefault(size = 8, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("pageBlog", blogService.listBlog(pageable));
        model.addAttribute("pageType", typeService.listTypeTop(6));
        model.addAttribute("pageTag", tagService.listTagTop(10));
        return "index";
    }

    @PostMapping("/search")
    public String search(@RequestParam String query,
                         @PageableDefault(size = 8, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable,
                         Model model) {
        Page<Blog> queryResult = blogService.listBlog(query, pageable);
        if(queryResult.getTotalElements() == 0) {
            model.addAttribute("message", "没有找到您想要的信息");
        }
        model.addAttribute("pageQueryBlog", queryResult);
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/blogs/{id}")
    public String showBlog(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlog(id);
        Blog blog_c = new Blog();
        BeanUtils.copyProperties(blog, blog_c);
        blog_c.setContent(MarkdownUtils.markdownToHtmlExtensions(blog_c.getContent()));
        model.addAttribute("comments", commentService.listComment(id));
        model.addAttribute("blog", blog_c);
        return "blog";
    }
}
