package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 单词词典详情表
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DictionaryWord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 单词原文
     */
    private String word;
    private String tag;

    /**
     * 英式音标 (对应 phonetic0)
     */
    private String phoneticUk;

    /**
     * 美式音标 (对应 phonetic1)
     */
    private String phoneticUs;

    /**
     * 释义数组: [{pos, cn}]
     */
    private String trans;

    /**
     * 例句数组: [{c, cn}]
     */
    private String sentences;

    /**
     * 短语数组: [{c, cn}]
     */
    private String phrases;

    /**
     * 同义词/近义词扩展
     */
    private String synos;

    /**
     * 同根词/派生词信息
     */
    private String relWords;

    /**
     * 词源信息
     */
    private String etymology;

    /**
     * 入库时间
     */
    private String createdAt;

    /**
     * 更新时间
     */
    private String updatedAt;

    /**
     * 0.未学习 1.已学习
     */
    @TableField(exist = false)
    private String checkFlag;

}
