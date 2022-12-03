package com.whf.myblog.web.admin;

import com.whf.myblog.po.Blog;
import com.whf.myblog.po.User;
import com.whf.myblog.service.BlogService;
import com.whf.myblog.service.TagService;
import com.whf.myblog.service.TypeService;
import com.whf.myblog.util.MyBeanUtils;
import com.whf.myblog.vo.BlogQuery;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 8, sort = "updateAt", direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blogQ, Model model) {
        Page<Blog> blogs = blogService.listBlog(pageable, blogQ);
        model.addAttribute("types", typeService.listType());
        model.addAttribute("blogs", blogs);
        if(blogs.getTotalElements() == 0) {
            model.addAttribute("message", "暂无博客信息");
        }
        return "admin/blogs";
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 8, sort = "updateAt", direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blogQ, Model model) {
        Page<Blog> blogs = blogService.listBlog(pageable, blogQ);
        model.addAttribute("blogs", blogs);
        if(blogs.getTotalElements() == 0) {
            model.addAttribute("message", "抱歉！没有找到您查找的结果");
        }
        return "admin/blogs :: searchResultArea";
    }

    @GetMapping("/blog/new")
    public String newBlog(Model model) {
        model.addAttribute("blog", new Blog());
        setTypeAndTag(model);
        return "/admin/blog-input";
    }

    @GetMapping("/blogs/{id}/edit")
    public String editBlog(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog", blog);
        setTypeAndTag(model);
        return "/admin/blog-input";
    }

    @PostMapping("/blog/save")
    public String save(Blog blog, RedirectAttributes attributes, HttpSession session) {
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
        Blog b;
        if(blog.getId() == null) {
            b = blogService.saveBlog(blog);
        } else {
            b = blogService.updateBlog(blog);
        }

        if(b == null) {
            attributes.addFlashAttribute("message", "操作失败");
        } else {
            attributes.addFlashAttribute("message", "操作成功");
        }
        return "redirect:/admin/blogs";
    }

    @GetMapping("blogs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/blogs";
    }

    private Model setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
        return model;
    }
}
