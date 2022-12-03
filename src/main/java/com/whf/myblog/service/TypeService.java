package com.whf.myblog.service;

import com.whf.myblog.po.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeService {
    Type saveType(Type type);
    Type getType(Long id);
    Type getByName(String string);
    Page<Type> listType(Pageable pageable);
    List<Type> listTypeTop(Integer size);
    List<Type> listType();
    Type updateType(Long id, Type type);
    void deleteType(Long id);
}
