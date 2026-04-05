package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 英语课文内容表
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LessonContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 英文标题
     */
    private String title;

    /**
     * 标题中文翻译
     */
    private String titleTranslate;

    /**
     * 英文正文内容
     */
    private String text;

    /**
     * 正文中文翻译
     */
    private String textTranslate;

    /**
     * 生词列表 
     */
    private String newWords;

    /**
     * 音频文件路径
     */
    private String audioSrc;

    /**
     * 文本同步时间轴
     */
    private String lrcPosition;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 更新时间
     */
    private String updatedAt;


}
