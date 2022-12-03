package com.whf.myblog.service;

import com.whf.myblog.dao.BlogRepository;
import com.whf.myblog.po.Blog;
import com.whf.myblog.exception.NotFoundException;
import com.whf.myblog.util.MyBeanUtils;
import com.whf.myblog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        blog.setViews(0);
        blog.setCreateAt(new Date());
        blog.setUpdateAt(new Date());
        return blogRepository.save(blog);
    }


    @Transactional
    @Override
    public Blog getBlog(Long id) {
        //spring boot 2以上没有findOne()方法
        //经查询，用以下方式可以代替
        try {
            blogRepository.updateViews(id);
            return blogRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFoundException(String.format("blog by id %s is not found.",id));
        }
    }

    @Override
    public Blog getAndConvert(Long id) {
        try {
            return blogRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFoundException(String.format("blog by id %s is not found.",id));
        }
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blogQ) {
        return blogRepository.findAll(
//            new Specification<Blog>() {
//                @Override
//                public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
//                    List<Predicate> predicates = new ArrayList<>();
//                    if(!"".equals(blogQ.getTitle()) && blogQ.getTitle() != null) {
//                        predicates.add(cb.like(root.get("title"), "%"+blogQ.getTitle()+"%"));
//                    }
//                    if(blogQ.getTypeId() != null) {
//                        predicates.add(cb.equal(root.get("id"), blogQ.getTypeId()));
//                    }
//                    if(blogQ.isRecommend()) {
//                        predicates.add(cb.equal(root.get("recommend"), blogQ.isRecommend()));
//                    }
//                    cq.where(predicates.toArray(new Predicate[predicates.size()]));
//                    return null;
//                }
//            },
            // 箭头函数写法(lambda表达式)
            (Specification<Blog>) (root, cq, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if(!"".equals(blogQ.getTitle()) && blogQ.getTitle() != null) {
                    predicates.add(cb.like(root.get("title"), "%"+blogQ.getTitle()+"%"));
                }
                if(blogQ.getTypeId() != null) {
                    predicates.add(cb.equal(root.get("type"), blogQ.getTypeId()));
                }
                if(blogQ.isRecommend()) {
                    predicates.add(cb.equal(root.get("recommend"), blogQ.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            },
            pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Join join = root.join("tags");
                return criteriaBuilder.equal(join.get("id"), tagId);
            }
        }, pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery("%"+query+"%", pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Pageable pageable = PageRequest.of(0, size, Sort.Direction.DESC, "createAt");
        return blogRepository.recommendBlogsTop(pageable);
    }

    @Transactional
    @Override
    public Blog updateBlog(Blog blog) {
        try {
            Blog b = blogRepository.findById(blog.getId()).get();
            BeanUtils.copyProperties(blog, b, MyBeanUtils.getNullPropertyNames(blog));
            return blogRepository.save(b);
        } catch (Exception e) {
            throw new NotFoundException(String.format("blog by id %s is not found.", blog.getId()));
        }
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYears();
        Map<String, List<Blog>> archives = new HashMap<>();
        for (String year : years){
            List<Blog> yearBlogs = blogRepository.findByYear(year);
            archives.put(year, yearBlogs);
        };
        return archives;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }
}
