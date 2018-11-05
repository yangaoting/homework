package com.homework.service.impl;

import com.homework.entity.Category;
import com.homework.mapper.CategoryMapper;
import com.homework.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yan'gaoting
 * @since 2018-11-01
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
