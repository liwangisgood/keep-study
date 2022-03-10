package com.kuang.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuang.message.client.CourseClient;
import com.kuang.message.entity.InfoCourse;
import com.kuang.message.mapper.InfoCourseMapper;
import com.kuang.message.service.InfoCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kuang.springcloud.entity.MessageCourseVo;
import com.kuang.springcloud.exceptionhandler.XiaoXiaException;
import com.kuang.springcloud.utils.R;
import com.kuang.springcloud.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Xiaozhang
 * @since 2022-02-11
 */
@Service
@Slf4j
public class InfoCourseServiceImpl extends ServiceImpl<InfoCourseMapper, InfoCourse> implements InfoCourseService {

    @Resource
    private CourseClient courseClient;


    //查询未读消息
    @Override
    public Integer findUserUnreadNumber(String userId) {
        QueryWrapper<InfoCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id" , userId);
        wrapper.eq("is_read" , 0);
        return baseMapper.selectCount(wrapper);
    }

    //查找课程通知数量
    @Override
    public Integer findUserNewsNumber(String userId) {
        QueryWrapper<InfoCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id" , userId);
        return baseMapper.selectCount(wrapper);
    }

    //查找课程通知
    @Override
    public List<String> findUserNewsId(Long current, Long limit, String userId) {
        current = (current - 1) * limit;
        List<MessageCourseVo> userNews = baseMapper.findUserNews(current, limit, userId);
        List<String> courseIdList = new ArrayList<>();
        for(MessageCourseVo messageCourseVo : userNews){
            courseIdList.add(messageCourseVo.getCourseId());
        }
        return courseIdList;
    }

    //让课程通知已读
    @Async
    @Override
    public void setCourseRead(List<String> courseIdList, String userId) {
        log.info("课程通知消息已读");
        if(courseIdList.size() != 0){
            baseMapper.setCourseRead(courseIdList , userId);
        }
    }

    //查找课程通知
    @Async
    @Override
    public Future<Object> findUserNews(List<String> courseIdList) {
        R messageCourseDetaile = courseClient.findMessageCourseDetaile(courseIdList);
        if(!messageCourseDetaile.getSuccess()){
            throw new XiaoXiaException(ResultCode.ERROR , "查询课程通知失败");
        }
        Object o = messageCourseDetaile.getData().get("courseNewsList");
        return new AsyncResult<>(o);
    }
}
