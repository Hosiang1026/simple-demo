package com.example.param;

import com.example.annotation.ExcelField;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author sean
 * @since 2019-08-09
 */
@Data
public class UserParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 每页记录数
     */
    private long size = 10;

    /**
     * 当前页数
     */
    private long current = 1;

    /**
     * 用户ID，主键自增
     */
    private Long userId;

    /**
     * 登录用户名，唯一索引
     */
    @ExcelField(title = "用户名", align = 2, sort = 1)
    private String userName;

    /**
     * 用于用户名精确查询
     */
    private String userNameStr;

    /**
     * 密码
     */
    private String password;

    /**
     * 显示用户名
     */
    private String displayName;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户类型：1 后台系统用户 2 APP用户
     */
    private String userType;

    /**
     * 公司编号（二级客户填二级客户自己的公司编号）
     */
    private Integer companyId;

    /**
     * 启用标记
     */
    private String enableFlag = "1";

    /**
     * 删除flag
     */
    private String delFlg = "0";

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime = new Date();

    /**
     * 角色ID<>查询条件</>
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    private String roleName;

}
