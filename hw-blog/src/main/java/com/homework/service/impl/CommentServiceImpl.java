package com.homework.service.impl;

import com.homework.entity.Comment;
import com.homework.mapper.CommentMapper;
import com.homework.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yan'gaoting
 * @since 2018-11-01
 */
@Service
public class CommentServiceImpl extends BaseServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public void join(Map<String, Object> map, String filed) {
        Map<String,Object> joinColumns = new HashMap<>();

        if(map.get(filed) == null){
            return;
        }

        Comment comment = this.getById(map.get(filed).toString());

        joinColumns.put("id",comment.getId());
        joinColumns.put("content",comment.getContent());
        joinColumns.put("created",comment.getCreated());

        map.put("comment",joinColumns);
    }
}
