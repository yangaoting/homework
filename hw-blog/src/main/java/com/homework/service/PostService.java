package com.homework.service;

import com.homework.entity.Post;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yan'gaoting
 * @since 2018-11-01
 */
public interface PostService extends BaseService<Post> {

    void incrZsetValueAndUnionForLastWeekRank(Long postId);

    void zUnionAndStoreLast7DayForLastWeekRank();

    void initIndexWeekRank();
}
