package com.homework.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.entity.Category;
import com.homework.entity.Comment;
import com.homework.entity.Post;
import com.homework.entity.UserCollection;
import com.homework.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class PostController extends BaseController {

    @GetMapping("/post/{id}")
    public String index(@PathVariable long id,
                        @RequestParam(defaultValue = "1") Integer current,
                        @RequestParam(defaultValue = "10") Integer size){
        Map<String, Object> post = postService.getMap(new QueryWrapper<Post>().eq("id", id));
        userService.join(post,"user_id");
        categoryService.join(post,"category_id");

        Assert.notNull(post,"该文章已被删除");

        req.setAttribute("post",post);
        req.setAttribute("currentCategoryId",post.get("category_id"));

        Page<Comment> page = new Page<>();
        page.setCurrent(current);
        page.setSize(size);

        IPage<Map<String, Object>> pageData = commentService.pageMaps(page, new QueryWrapper<Comment>()
                .eq("post_id", id)
                .orderByDesc("created"));
        userService.join(pageData,"user_id");
        commentService.join(pageData,"parent_id");

        req.setAttribute("pageData",pageData);

        return "post/index";
    }

    @GetMapping("/category/{id}")
    public String category(@PathVariable Long id,
                           @RequestParam(defaultValue = "1") Integer current,
                           @RequestParam(defaultValue = "10") Integer size){

        Page<Post> page = new Page<>();
        page.setCurrent(current);
        page.setSize(size);

        IPage<Map<String, Object>> pageData = postService.pageMaps(page,
                new QueryWrapper<Post>().eq("category_id", id).orderByDesc("created"));

        userService.join(pageData,"user_id");
        categoryService.join(pageData,"category_id");

        req.setAttribute("pageData",pageData);
        req.setAttribute("currentCategoryId",id);

        return "post/category";
    }

    @GetMapping("/user/post")
    public String getPost(){
        String id = req.getParameter("id");
        Post post = new Post();
        if(!StringUtils.isEmpty(id)){
            post = postService.getById(Long.valueOf(id));
        }
        req.setAttribute("pid",id);
        req.setAttribute("post",post);

        List<Category> categories = categoryService.list(new QueryWrapper<Category>().orderByDesc("order_num"));
        req.setAttribute("categories",categories);

        return "post/add";
    }

    @ResponseBody
    @PostMapping("/user/post")
    public R posttPost(@Valid Post post, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return R.failed(bindingResult.getFieldError().getDefaultMessage());
        }

        if(post.getId() == null){
            post.setUserId(getProfileId());

            post.setCreated(new Date());
            post.setCreated(new Date());
            post.setCommentCount(0);
            post.setEditMode(Constant.EDIT_HTML_MODEL);
            post.setLevel(0);
            post.setRecommend(false);
            post.setVoteUp(0);
            post.setVoteDown(0);
            post.setViewCount(0);
            post.setStatus(Constant.NORMAL_STATUS);
        }else {
            Post tempPost = postService.getById(post.getId());
            if(tempPost.getUserId() != getProfileId()){
                return R.failed("不是自己的贴子");
            }
        }

        postService.saveOrUpdate(post);

        return R.ok(post.getId());
    }

    @ResponseBody
    @Transactional
    @PostMapping("/user/post/delete")
    public R postDelete(Long id){
        Post post = postService.getById(id);
        
        Assert.isTrue(post != null,"该帖子已被删除");

        Long profileId = getProfileId();
        if(post.getUserId() != profileId){
            return R.failed("不是删除非自己的贴子");
        }

        postService.removeById(id);

        //同时删除所有的相关收藏
        userCollectionService.removeByMap(MapUtil.of("post_id",id));

        return R.ok(null);
    }

    /**
     * 判断是否收藏
     * @param postId
     * @return
     */
    @ResponseBody
    @PostMapping("/user/post/collection/find")
    public R collectionFind(String postId){
        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("post_id",postId)
                .eq("user_id",getProfileId()));

        return R.ok(MapUtil.of("collection",count > 0));
    }

    /**
     * 收藏
     * @param postId
     * @return
     */
    @ResponseBody
    @PostMapping("/user/post/collection/add")
    public R collectionAdd(Long postId){
        Post post = postService.getById(postId);

        Assert.isTrue(post != null,"该帖子已被删除");

        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("post_id", postId)
                .eq("user_id", getProfileId()));

        if(count > 0){
            return R.failed("你已经收藏");
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(getProfileId());
        collection.setPostId(postId);
        collection.setPostUserId(post.getUserId());
        collection.setCreated(new Date());
        collection.setModified(new Date());

        userCollectionService.save(collection);

        return R.ok(MapUtil.of("collection",true));
    }

    /**
     * 取消收藏
     * @param postId
     * @return
     */
    @ResponseBody
    @PostMapping("/user/post/collection/remove")
    public R collectionRemove(String postId){
        Post post = postService.getById(postId);

        Assert.isTrue(post != null,"该帖子以删除");

        boolean hasRemove = userCollectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", postId));

        return R.ok(hasRemove);
    }

    @ResponseBody
    @PostMapping("/user/post/comment")
    public R commentAdd(@Valid Comment comment,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return R.failed(bindingResult.getFieldError().getDefaultMessage());
        }

        Post post = postService.getById(comment.getPostId());
        Assert.isTrue(post != null,"该帖子已被删除");

        comment.setUserId(getProfileId());
        comment.setCreated(new Date());
        comment.setModified(new Date());
        comment.setStatus(Constant.NORMAL_STATUS);
        commentService.save(comment);

        //TODO 记录动作

        //TODO 通知作者

        return R.ok(null);
    }

    /**
     * 删除评论
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/user/post/comment/delete")
    public R commentDel(Long id){
        Comment comment = commentService.getById(id);

        Assert.isTrue(comment != null,"该评论已删除");

        if(comment.getUserId() != getProfileId()){
            return R.failed("删除失败");
        }
        boolean hasDel = commentService.removeById(id);

        return R.ok(null);
    }
}
