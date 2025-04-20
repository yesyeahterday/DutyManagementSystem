package com.luke.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.entity.Position;
import com.luke.service.PositionService;
import com.luke.mapper.PositionMapper;
import org.springframework.stereotype.Service;

/**
* @author 23992
* @description 针对表【position】的数据库操作Service实现
* @createDate 2025-02-27 16:03:09
*/
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position>
    implements PositionService{

}




