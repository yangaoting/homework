package com.homework.service.impl;

import com.homework.entity.Comment;
import com.homework.mapper.CommentMapper;
import com.homework.service.CommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
