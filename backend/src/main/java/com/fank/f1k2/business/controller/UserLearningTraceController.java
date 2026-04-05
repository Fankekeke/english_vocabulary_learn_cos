package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.business.service.UserLearningPredictService;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.UserLearningTrace;
import com.fank.f1k2.business.service.IUserLearningTraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

/**
 * 学习足迹与行为记录 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/user-learning-trace")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserLearningTraceController {

    private final IUserLearningTraceService bulletinInfoService;

    private final UserLearningPredictService predictService;

    /**
     * 分页获取学习足迹与行为记录
     *
     * @param page      分页对象
     * @param queryFrom 学习足迹与行为记录
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<UserLearningTrace> page, UserLearningTrace queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 统计用户学习足迹与行为记录
     *
     * @param userId 用户ID
     * @return 统计结果
     */
    @GetMapping("/statistics")
    public R statisticsUserLearningTrace(Integer userId) {
        return R.ok(bulletinInfoService.statisticsUserLearningTrace(userId));
    }

    /**
     * 查询学习足迹与行为记录详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询学习足迹与行为记录列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增学习足迹与行为记录
     *
     * @param addFrom 学习足迹与行为记录对象
     * @return 结果
     */
    @PostMapping
    public R save(UserLearningTrace addFrom) {
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改学习足迹与行为记录
     *
     * @param editFrom 学习足迹与行为记录对象
     * @return 结果
     */
    @PutMapping
    public R edit(UserLearningTrace editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除学习足迹与行为记录
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

    /**
     * 基于 DL4J 预测用户下一步学习内容
     *
     * @param userId 用户 ID
     * @return 预测结果
     */
    @GetMapping("/predict/{userId}")
    public R predictUserLearning(@PathVariable("userId") Integer userId) {
        if (userId == null) {
            return R.error("用户 ID 不能为空");
        }

        // 获取用户历史学习记录
        List<UserLearningTrace> history = bulletinInfoService.list(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserLearningTrace>()
                        .eq("user_id", userId)
                        .orderByDesc("created_at")
                        .last("LIMIT 50")
        );

        if (history == null || history.isEmpty()) {
            return R.error("该用户暂无学习记录，无法进行预测");
        }

        // 调用预测服务
        Map<String, Object> prediction = predictService.predictNextLearningTopic(history);

        if (!(Boolean) prediction.get("success")) {
            return R.error(prediction.get("message").toString());
        }

        return R.ok(prediction);
    }

    /**
     * 批量训练预测模型 (管理员功能)
     *
     * @return 训练结果
     */
    @PostMapping("/train-model")
    public R trainPredictModel() {
        try {
            // 获取所有用户的学习记录用于训练
            List<UserLearningTrace> allRecords = bulletinInfoService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserLearningTrace>()
                            .orderByDesc("created_at")
                            .last("LIMIT 1000")
            );

            if (allRecords == null || allRecords.isEmpty()) {
                return R.error("暂无足够数据进行模型训练");
            }

            // 按用户分组
            Map<Integer, List<UserLearningTrace>> groupedByUser = allRecords.stream()
                    .collect(java.util.stream.Collectors.groupingBy(UserLearningTrace::getUserId));

            // 训练模型
            predictService.trainModel(new java.util.ArrayList<>(groupedByUser.values()));

            return R.ok("模型训练完成，共使用 " + groupedByUser.size() + " 个用户的学习记录");
        } catch (Exception e) {
            return R.error("模型训练失败：" + e.getMessage());
        }
    }

}
