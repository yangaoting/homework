package com.homework.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homework.service.BaseService;

import java.util.List;
import java.util.Map;

public class BaseServiceImpl<M extends BaseMapper<T>,T> extends ServiceImpl<M,T> implements BaseService<T> {
    @Override
    public void join(Map<String, Object> stringObjectMap, String filed) {

    }

    @Override
    public void join(List<Map<String, Object>> datas, String filed) {
        datas.forEach(map ->{
            this.join(map,filed);
        });
    }

    @Override
    public void join(IPage<Map<String, Object>> pageData, String filed) {
        List<Map<String, Object>> records = pageData.getRecords();
        this.join(records,filed);
    }
}
