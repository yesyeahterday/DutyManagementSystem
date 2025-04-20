package com.luke.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.entity.Message;
import com.luke.service.MessageService;
import com.luke.mapper.MessageMapper;
import org.springframework.stereotype.Service;

/**
* @author 23992
* @description 针对表【message】的数据库操作Service实现
* @createDate 2025-02-27 16:03:09
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

}




