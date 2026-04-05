package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.DictionaryWord;
import com.fank.f1k2.business.dao.DictionaryWordMapper;
import com.fank.f1k2.business.service.IDictionaryWordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class DictionaryWordServiceImpl extends ServiceImpl<DictionaryWordMapper, DictionaryWord> implements IDictionaryWordService {

    /**
     * 分页获取单词词典详情表
     *
     * @param page      分页对象
     * @param queryFrom 单词词典详情表
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<DictionaryWord> page, DictionaryWord queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
