package com.homework.service.impl;

import com.homework.entity.Post;
import com.homework.mapper.PostMapper;
import com.homework.service.PostService;
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
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

}
