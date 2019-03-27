package com.homework.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homework.entity.Post;
import com.homework.mapper.PostMapper;
import com.homework.service.PostService;
import com.homework.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private RedisUtil redisUtil;

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

    /**
     * 初始化首页周评排行榜
     */
    @Override
    public void initIndexWeekRank() {
        //缓存最近7天的文章的评论数量
        List<Post> last7DayPosts = list(new QueryWrapper<Post>()
                .ge("created", DateUtil.offsetDay(new Date(), -7).toJdkDate())
                .select("id,title,user_id,comment_count,view_count,created"));

        for(Post post : last7DayPosts){
            String key = "day_rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);

            //设置有效期
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60;

            //缓存文章到set中，评论数量作为排行标准
            redisUtil.zSet(key,post.getId(),post.getCommentCount());
            redisUtil.expire(key,expireTime);

            this.hashCashePostIdAndTitle(post);
        }

        this.zUnionAndStoreLast7DayForLastWeekRank();
    }

    @Override
    public void incrZsetValueAndUnionForLastWeekRank(Long postId) {
        String dayRank = "day_rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);

        //文章评论加一
        redisUtil.zIncrementScore(dayRank,postId,1);

        this.hashCashePostIdAndTitle(this.getById(postId));

        this.zUnionAndStoreLast7DayForLastWeekRank();
    }

    private void hashCashePostIdAndTitle(Post post) {
        boolean isExist = redisUtil.hasKey("rank_post_" + post.getId());

        if(!isExist){
            //设置有效期
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60;

            //缓存文章基本信息
            redisUtil.hset("rank_post_" + post.getId(),"post:id",post.getId(),expireTime);
            redisUtil.hset("rank_post_" + post.getId(),"post:title",post.getTitle(),expireTime);
        }
    }

    @Override
    public void zUnionAndStoreLast7DayForLastWeekRank() {
        String prifix = "day_rank:";

        List<String> keys = new ArrayList<>();
        String key = prifix + DateUtil.format(new Date(),DatePattern.PURE_DATE_FORMAT);

        for(int i = -7; i < 0 ; i++){
            Date date = DateUtil.offsetDay(new Date(), i).toJdkDate();
            keys.add(prifix + DateUtil.format(date,DatePattern.PURE_DATE_FORMAT));
        }

        redisUtil.zUnionAndStore(key,keys,"last_week_rank");
    }
}
