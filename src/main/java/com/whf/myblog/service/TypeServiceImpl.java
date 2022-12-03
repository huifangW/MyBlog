package com.whf.myblog.service;

import com.whf.myblog.dao.TypeRepository;
import com.whf.myblog.po.Type;
import com.whf.myblog.exception.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeRepository typeRepository;

    @Transactional
    @Override
    public Type saveType(Type type) {
        return typeRepository.save(type);
    }

    @Transactional
    @Override
    public Type getType(Long id) {
        //spring boot 2以上没有findOne()方法
        //经查询，用以下方式可以代替
        try {
            return typeRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFoundException(String.format("type by id %s is not found.",id));
        }
    }

    @Transactional
    @Override
    public Type getByName(String name) {
        return typeRepository.getTypeByName(name);
    }

    @Override
    public Page<Type> listType(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Override
    public List<Type> listType() {
        return typeRepository.findAll();
    }

    @Override
    public List<Type> listTypeTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "blogs.size");
        Pageable pageable = PageRequest.of(0, size, sort);
        return typeRepository.findTop(pageable);
    }

    @Transactional
    @Override
    public Type updateType(Long id, Type type) {
        try {
            Type t = typeRepository.findById(id).get();
            BeanUtils.copyProperties(type, t);
            return typeRepository.save(t);
        } catch (Exception e) {
            throw new NotFoundException(String.format("type by id %s is not found.",id));
        }
    }

    @Transactional
    @Override
    public void deleteType(Long id) {
        typeRepository.deleteById(id);
    }
}
