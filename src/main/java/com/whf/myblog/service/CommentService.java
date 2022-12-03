package com.whf.myblog.service;

import com.whf.myblog.po.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> listComment(Long blogId);
    Comment saveComment(Comment comment);
}
