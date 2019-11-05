package com.example.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author sean
 * @since 2019-08-14
 */
@Data
public class TableColumnVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Integer id;

    /**
     * 表名称
     */
    @JsonIgnore
    @TableField("table_name")
    private String tableName;

    /**
     * 字段名称
     */
    @JsonIgnore
    @TableField("column_name")
    private String columnName;

    /**
     * 字段值
     */
    private String columnValue;

    /**
     * 值说明
     */
    private String valueDesc;

}
