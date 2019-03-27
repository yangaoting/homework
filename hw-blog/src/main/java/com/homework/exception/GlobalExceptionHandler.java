package com.homework.exception;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 定义全局异常处理
 * @ControllerAdvice表示定义全局控制器异常处理
 * @ExceptionHandler表示针对性异常处理，可对每种异常针对性处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request,Exception e){
        log.error("---------->捕获全局异常",e);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception",e);
        mav.addObject("url",request.getRequestURL());
        mav.setViewName("error");

        return mav;
    }
    @ExceptionHandler(value = MyException.class)
    @ResponseBody
    public R jsonErrorHandler(HttpServletRequest request, MyException e){
        return R.failed(e.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ModelAndView assertExceptionHandler(HttpServletRequest req,IllegalArgumentException e){
        log.error("----------------->捕捉Assert异常");

        ModelAndView mav = new ModelAndView();

        mav.addObject("msg",e.getMessage());
        mav.addObject("url",req.getRequestURL());
        mav.setViewName("error/500");

        return mav;
    }
}
