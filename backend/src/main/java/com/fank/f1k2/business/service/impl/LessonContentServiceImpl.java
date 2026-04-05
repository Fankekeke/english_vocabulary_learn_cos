package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.LessonContent;
import com.fank.f1k2.business.dao.LessonContentMapper;
import com.fank.f1k2.business.service.ILessonContentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class LessonContentServiceImpl extends ServiceImpl<LessonContentMapper, LessonContent> implements ILessonContentService {

    /**
     * 分页获取英语课文内容表
     *
     * @param page      分页对象
     * @param queryFrom 英语课文内容表
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<LessonContent> page, LessonContent queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
