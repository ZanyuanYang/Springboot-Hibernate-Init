package com.example.springboothibernateinit.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author <a href="https://github.com/zanyuanyang">Zanyuan Yang</a>
 */

@Table(name = "user")
@Entity
@Data
public class User implements Serializable {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户账号
     */
    @Column(name = "userAccount")
    private String userAccount;

    /**
     * 用户密码
     */
    @Column(name = "userPassword")
    private String userPassword;

    /**
     * 开放平台id
     */
    @Column(name = "unionId")
    private String unionId;

    /**
     * 公众号openId
     */
    @Column(name = "mpOpenId")
    private String mpOpenId;

    /**
     * 用户昵称
     */
    @Column(name = "userName")
    private String userName;

    /**
     * 用户头像
     */
    @Column(name = "userAvatar")
    private String userAvatar;

    /**
     * 用户简介
     */
    @Column(name = "userProfile")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @Column(name = "userRole", insertable = false)
    private String userRole;

    /**
     * 创建时间
     */
    @Column(name = "createTime")
    @CreationTimestamp
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "updateTime")
    @UpdateTimestamp
    private Date updateTime;

    /**
     * 是否删除
     */
    @Column(name = "isDelete")
    private int isDelete;

    private static final long serialVersionUID = 1L;
}