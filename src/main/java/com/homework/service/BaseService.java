package com.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface BaseService<T> extends IService<T> {

    void join(Map<String,Object> stringObjectMap,String filed);

    void join(List<Map<String,Object>> datas,String filed);

    void join(IPage<Map<String,Object>> pageData,String filed);
}
