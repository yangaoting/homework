package com.homework.service.impl;

import cn.hutool.core.map.MapUtil;
import com.homework.entity.Post;
import com.homework.mapper.PostMapper;
import com.homework.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class PostServiceImpl extends BaseServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public void join(Map<String, Object> map, String filed) {
        Map<String,Object> joincolumns = new HashMap<>();

        if(MapUtil.isEmpty(map) || map.get(filed) == null){
            return;
        }

        String linkfiledvalue = map.get(filed).toString();
        Post post = this.getById(linkfiledvalue);
        if (post == null) return ;

        joincolumns.put("id",post.getId());
        joincolumns.put("title",post.getTitle());
        joincolumns.put("created",post.getCreated());

        map.put("post",joincolumns);

    }
}
