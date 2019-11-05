package com.example.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author sean
 * @since 2019-08-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VmUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键自增
     */
    private Long userId;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 登录用户名，唯一索引
     */
    private String userName;

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

    private String wechatOpenid;

    /**
     * 启用标记
     */
    private String enableFlag;

    /**
     * 删除flag
     */
    private String delFlg;

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
    private Date updateTime;

}
