package com.whf.myblog.web;

import com.whf.myblog.po.Blog;
import com.whf.myblog.po.Comment;
import com.whf.myblog.po.User;
import com.whf.myblog.service.BlogService;
import com.whf.myblog.service.BlogServiceImpl;
import com.whf.myblog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        model.addAttribute("comments", commentService.listComment(blogId));
        return "blog :: commentList";
    }

    @PostMapping("/comments/save")
    public String save(Comment comment, HttpSession session) {
        Long blogId = comment.getBlog().getId();
        Blog blog = blogService.getBlog(blogId);
        comment.setBlog(blog);
        User user = (User) session.getAttribute("user");
        if(user != null) {
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        }
        comment.setAvatar(avatar);
        commentService.saveComment(comment);
        return "redirect:/comments/" + blogId;
    }
}
