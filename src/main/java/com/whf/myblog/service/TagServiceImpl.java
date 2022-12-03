package com.whf.myblog.service;

import com.whf.myblog.dao.TagRepository;
import com.whf.myblog.po.Tag;
import com.whf.myblog.exception.NotFoundException;
import com.whf.myblog.po.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    @Transactional
    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag getTag(Long id) {
        //spring boot 2以上没有findOne()方法
        //经查询，用以下方式可以代替
        try {
            return tagRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFoundException(String.format("tag by id %s is not found.",id));
        }
    }

    @Override
    public Tag getByName(String string) {
        return tagRepository.getTagByName(string);
    }

    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public List<Tag> listTagTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "blogs.size");
        Pageable pageable = PageRequest.of(0, size, sort);
        return tagRepository.findTop(pageable);
    }

    @Override
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> listTag(String ids) {
        return tagRepository.findAllById(convertToList(ids));
    }

    private List<Long> convertToList(String ids) {
        List<Long> list = new ArrayList<>();
        if (!"".equals(ids) && ids != null) {
            String[] idarray = ids.split(",");
            for (int i=0; i < idarray.length;i++) {
                list.add(new Long(idarray[i]));
            }
        }
        return list;
    }

    @Override
    public Tag updateTag(Long id, Tag tag) {
        try {
            Tag t = tagRepository.findById(id).get();
            BeanUtils.copyProperties(tag, t);
            return tagRepository.save(t);
        } catch (Exception e) {
            throw new NotFoundException(String.format("tag by id %s is not found.",id));
        }
    }

    @Override
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}
