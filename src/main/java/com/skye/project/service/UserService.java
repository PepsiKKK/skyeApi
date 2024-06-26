package com.skye.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.skye.common.model.entity.User;
import com.skye.project.model.dto.user.UserAddRequest;
import com.skye.project.model.dto.user.UserLoginRequest;
import com.skye.project.model.dto.user.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author yupi
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param  userRegisterRequest   请求参数
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest  请求参数
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 添加用户
     * @param userAddRequest
     * @param request
     * @return
     */
    User addUser(UserAddRequest userAddRequest, HttpServletRequest request);

}
