package com.whf.myblog.service;

import com.whf.myblog.dao.CommentRepository;
import com.whf.myblog.po.Comment;
import org.hibernate.boot.model.source.spi.Sortable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> listComment(Long blogId) {
        Sort sort = Sort.by("createAt");
        List<Comment> comments = commentRepository.findCommentByBlogIdAndParentCommentNull(blogId, sort);
        return commentsView(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        if (comment.getParentComment().getId() == -1) {
            comment.setParentComment(null);
        } else {
            Comment parentComment = commentRepository.findById(comment.getParentComment().getId()).get();
            comment.setParentComment(parentComment);
        }
        comment.setCreateAt(new Date());
        return commentRepository.save(comment);
    }

    /**
     * 先复制顶级回复(防止数据库数据被更改)，然后将所有的子回复(无论层级)全部取出来放到一个集合里
     * @param comments 顶级回复(parentComment属性为null)
     * @return
     */
    private List<Comment> commentsView(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for(Comment comment : comments) {
            Comment commentCopy = new Comment();
            BeanUtils.copyProperties(comment, commentCopy);
            List<Comment> replayComments = new ArrayList<>();
            commentCopy.setReplayComments(replayCommentsRecursive(commentCopy, replayComments));
            commentsView.add(commentCopy);
        }
        return commentsView;
    }

    /**
     * 遍历获取顶级回复的所有子回复，并将结果集合作为顶级comment的replayComments
     * @param comment 上层回复
     * @param replayComments 用于存放子回复的集合
     * @return
     */
    private List<Comment> replayCommentsRecursive(Comment comment, List<Comment> replayComments) {
        if (comment.getReplayComments().size() > 0) {
            for (Comment replayComment : comment.getReplayComments()) {
                replayComments.add(replayComment);
                replayCommentsRecursive(replayComment, replayComments);
            }
        }
        return replayComments;
    }
}
