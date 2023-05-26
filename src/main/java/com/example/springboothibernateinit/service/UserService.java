package com.example.springboothibernateinit.service;

import com.example.springboothibernateinit.model.entity.User;
import com.example.springboothibernateinit.model.vo.LoginUserVO;
import com.example.springboothibernateinit.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {

	/**
	 * User Register
	 *
	 * @param userAccount
	 * @param userPassword
	 * @param checkPassword
	 * @return new user id
	 */
	long userRegister(String userAccount, String userPassword, String checkPassword);

	/**
	 * user login
	 *
	 * @param userAccount
	 * @param userPassword
	 * @param request
	 * @return get sensitive user info after
	 */
	LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

	/**
	 * get current login user
	 *
	 * @param request
	 * @return
	 */
	User getLoginUser(HttpServletRequest request);

	/**
	 * 获取当前登录用户（允许未登录）
	 *
	 * @param request
	 * @return
	 */
	User getLoginUserPermitNull(HttpServletRequest request);

	/**
	 * 是否为管理员
	 *
	 * @param request
	 * @return
	 */
	boolean isAdmin(HttpServletRequest request);

	/**
	 * 是否为管理员
	 *
	 * @param user
	 * @return
	 */
	boolean isAdmin(User user);

	/**
	 * user logout
	 *
	 * @param request
	 * @return
	 */
	boolean userLogout(HttpServletRequest request);

	/**
	 * get sensitive login user info
	 *
	 * @return
	 */
	LoginUserVO getLoginUserVO(User user);

	/**
	 * get sensitive user info
	 *
	 * @param user
	 * @return
	 */
	UserVO getUserVO(User user);

/**
	 * save user
	 *
	 * @param user
	 * @return
	 */
	boolean save(User user);

	/**
	 * get user by id
	 *
	 * @param id
	 * @return
	 */
	User getById(long id);

	/**
	 * delete user by id
	 *
	 * @param id
	 * @return
	 */
	boolean removeById(long id);

	/**
	 * get sensitive user info list
	 *
	 * @param userList
	 * @return
	 */
	List<UserVO> getUserVO(List<User> userList);

}
