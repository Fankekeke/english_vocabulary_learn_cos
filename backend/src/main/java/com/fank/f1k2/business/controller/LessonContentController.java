package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.LessonContent;
import com.fank.f1k2.business.service.ILessonContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 英语课文内容表 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/lesson-content")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LessonContentController {

    private final ILessonContentService bulletinInfoService;

    /**
     * 分页获取英语课文内容表
     *
     * @param page      分页对象
     * @param queryFrom 英语课文内容表
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<LessonContent> page, LessonContent queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }


    /**
     * 查询英语课文内容表详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * JSON文件导入课文数据（支持文件上传和直接JSON字符串）
     *
     * @param file JSON文件（可选）
     * @return 结果
     */
    @PostMapping("/import")
    public R importLessons(@RequestParam(value = "file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".json")) {
                return R.error("请上传JSON格式的文件");
            }

            String content = new String(file.getBytes(), "UTF-8");
            JSONArray jsonArray = JSON.parseArray(content);

            if (jsonArray == null || jsonArray.isEmpty()) {
                return R.error("JSON数据为空或格式不正确");
            }

            List<LessonContent> lessonList = new ArrayList<>();
            String currentTime = DateUtil.formatDateTime(new Date());

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                LessonContent lesson = parseJsonToLesson(jsonObj, currentTime);
                lessonList.add(lesson);
            }

            boolean result = bulletinInfoService.saveBatch(lessonList);
            if (result) {
                return R.ok("成功导入" + lessonList.size() + "条课文数据");
            } else {
                return R.error("导入失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 将JSON对象转换为LessonContent实体
     *
     * @param jsonObj JSON对象
     * @param currentTime 当前时间
     * @return LessonContent对象
     */
    private LessonContent parseJsonToLesson(JSONObject jsonObj, String currentTime) {
        LessonContent lesson = new LessonContent();

        lesson.setTitle(jsonObj.getString("title"));
        lesson.setTitleTranslate(jsonObj.getString("titleTranslate"));
        lesson.setText(jsonObj.getString("text"));
        lesson.setTextTranslate(jsonObj.getString("textTranslate"));
        lesson.setAudioSrc(jsonObj.getString("audioSrc"));

        Object newWordsObj = jsonObj.get("newWords");
        if (newWordsObj != null) {
            lesson.setNewWords(JSON.toJSONString(newWordsObj));
        }

        Object lrcPositionObj = jsonObj.get("lrcPosition");
        if (lrcPositionObj != null) {
            lesson.setLrcPosition(JSON.toJSONString(lrcPositionObj));
        }

        lesson.setCreatedAt(currentTime);
        lesson.setUpdatedAt(currentTime);

        return lesson;
    }

    /**
     * 查询英语课文内容表列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增英语课文内容表
     *
     * @param addFrom 英语课文内容表对象
     * @return 结果
     */
    @PostMapping
    public R save(LessonContent addFrom) {
        addFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改英语课文内容表
     *
     * @param editFrom 英语课文内容表对象
     * @return 结果
     */
    @PutMapping
    public R edit(LessonContent editFrom) {
        editFrom.setUpdatedAt(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除英语课文内容表
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
