package com.fank.f1k2.business.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.entity.WordCheckRecord;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.business.service.IWordCheckRecordService;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.DictionaryWord;
import com.fank.f1k2.business.service.IDictionaryWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 单词词典详情表 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/dictionary-word")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DictionaryWordController {

    private final IDictionaryWordService bulletinInfoService;

    private final IWordCheckRecordService wordCheckRecordService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取单词词典详情表
     *
     * @param page      分页对象
     * @param queryFrom 单词词典详情表
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<DictionaryWord> page, DictionaryWord queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询单词词典详情表详情
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/queryWordCheckRecord")
    public R queryWordCheckRecord(Integer userId) {
        List<DictionaryWord> dictionaryWordList = bulletinInfoService.list();
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        List<WordCheckRecord> wordCheckRecordList = wordCheckRecordService.list(Wrappers.<WordCheckRecord>lambdaQuery().eq(WordCheckRecord::getUserId, userInfo.getId()));

        Map<Integer, WordCheckRecord> wordCheckRecordMap = wordCheckRecordList.stream()
                .collect(java.util.stream.Collectors.toMap(
                        WordCheckRecord::getWordId,
                        wordCheckRecord -> wordCheckRecord,
                        (existing, replacement) -> existing
                ));
        if (CollectionUtil.isEmpty(wordCheckRecordList)) {
            dictionaryWordList.forEach(dictionaryWord -> dictionaryWord.setCheckFlag("0"));
            return R.ok(dictionaryWordList);
        }
        dictionaryWordList.forEach(dictionaryWord -> {
            if (wordCheckRecordMap.containsKey(dictionaryWord.getId())) {
                dictionaryWord.setCheckFlag("1");
            } else {
                dictionaryWord.setCheckFlag("0");
            }
        });
        return R.ok(dictionaryWordList);
    }

    /**
     * 查询单词词典详情表详情
     *
     * @param userId 用户ID
     * @param wordId 单词ID
     * @return 结果
     */
    @GetMapping("/wordCheckRecord")
    public R wordCheckRecord(Integer userId, Integer wordId) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        WordCheckRecord wordCheckRecord = new WordCheckRecord();
        wordCheckRecord.setUserId(userInfo.getId());
        wordCheckRecord.setWordId(wordId);
        wordCheckRecord.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(wordCheckRecordService.save(wordCheckRecord));
    }

    /**
     * 查询单词词典详情表详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * JSON文件导入单词数据
     *
     * @param file JSON文件
     * @return 结果
     */
    @PostMapping("/import")
    public R importWords(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {
        try {
            if (file.isEmpty()) {
                return R.error("上传文件不能为空");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".json")) {
                return R.error("请上传JSON格式的文件");
            }

            String content = new String(file.getBytes(), "UTF-8");
            JSONArray jsonArray = JSON.parseArray(content);

            if (jsonArray == null || jsonArray.isEmpty()) {
                return R.error("JSON数据为空或格式不正确");
            }

            List<DictionaryWord> wordList = new ArrayList<>();
            String currentTime = DateUtil.formatDateTime(new Date());

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                DictionaryWord word = new DictionaryWord();

                word.setWord(jsonObj.getString("word"));
                word.setPhoneticUk(jsonObj.getString("phonetic0"));
                word.setPhoneticUs(jsonObj.getString("phonetic1"));

                Object transObj = jsonObj.get("trans");
                if (transObj != null) {
                    word.setTrans(JSON.toJSONString(transObj));
                }

                Object sentencesObj = jsonObj.get("sentences");
                if (sentencesObj != null) {
                    word.setSentences(JSON.toJSONString(sentencesObj));
                }

                Object phrasesObj = jsonObj.get("phrases");
                if (phrasesObj != null) {
                    word.setPhrases(JSON.toJSONString(phrasesObj));
                }

                Object synosObj = jsonObj.get("synos");
                if (synosObj != null) {
                    word.setSynos(JSON.toJSONString(synosObj));
                }

                Object relWordsObj = jsonObj.get("relWords");
                if (relWordsObj != null) {
                    word.setRelWords(JSON.toJSONString(relWordsObj));
                }

                Object etymologyObj = jsonObj.get("etymology");
                if (etymologyObj != null) {
                    word.setEtymology(JSON.toJSONString(etymologyObj));
                }

                word.setCreatedAt(currentTime);
                word.setUpdatedAt(currentTime);
                word.setTag(tag);
                wordList.add(word);
            }

            boolean result = bulletinInfoService.saveBatch(wordList);
            if (result) {
                return R.ok("成功导入" + wordList.size() + "条单词数据");
            } else {
                return R.error("导入失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 查询单词词典详情表列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增单词词典详情表
     *
     * @param addFrom 单词词典详情表对象
     * @return 结果
     */
    @PostMapping
    public R save(DictionaryWord addFrom) {
        addFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改单词词典详情表
     *
     * @param editFrom 单词词典详情表对象
     * @return 结果
     */
    @PutMapping
    public R edit(DictionaryWord editFrom) {
        editFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除单词词典详情表
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
