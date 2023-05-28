package com.example.springboothibernateinit.controller;

import com.example.springboothibernateinit.annotation.AuthCheck;
import com.example.springboothibernateinit.common.BaseResponse;
import com.example.springboothibernateinit.common.DeleteRequest;
import com.example.springboothibernateinit.common.ErrorCode;
import com.example.springboothibernateinit.common.ResultUtils;
import com.example.springboothibernateinit.constant.UserConstant;
import com.example.springboothibernateinit.exception.BusinessException;
import com.example.springboothibernateinit.exception.ThrowUtils;
import com.example.springboothibernateinit.model.dto.user.UserAddRequest;
import com.example.springboothibernateinit.model.dto.user.UserLoginRequest;
import com.example.springboothibernateinit.model.dto.user.UserQueryRequest;
import com.example.springboothibernateinit.model.dto.user.UserRegisterRequest;
import com.example.springboothibernateinit.model.entity.User;
import com.example.springboothibernateinit.model.vo.LoginUserVO;
import com.example.springboothibernateinit.model.vo.UserVO;
import com.example.springboothibernateinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Resource
	private UserService userService;

	/**
	 * User Register
	 *
	 * @param userRegisterRequest
	 * @return
	 */
	@PostMapping("/register")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		if (userRegisterRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			return null;
		}
		long result = userService.userRegister(userAccount, userPassword, checkPassword);
		return ResultUtils.success(result);
	}

	/**
	 * User login
	 *
	 * @param userLoginRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/login")
	public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
		if (userLoginRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
		return ResultUtils.success(loginUserVO);
	}

	/**
	 * user logout
	 *
	 * @param request
	 * @return
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
		if (request == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = userService.userLogout(request);
		return ResultUtils.success(result);
	}

	/**
	 * get login user
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/get/login")
	public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
		User user = userService.getLoginUser(request);
		return ResultUtils.success(userService.getLoginUserVO(user));
	}

	/**
	 * create user(admin only)
	 *
	 * @param userAddRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
		if (userAddRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = new User();
		BeanUtils.copyProperties(userAddRequest, user);
		boolean result = userService.save(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(user.getId());
	}

	/**
	 * delete user(admin only)
	 *
	 * @param deleteRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean b = userService.removeById(deleteRequest.getId());
		return ResultUtils.success(b);
	}

	/**
	 * get user by id（admin only）
	 *
	 * @param id
	 * @param request
	 * @return
	 */
	@GetMapping("/get")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
		if (id <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		return ResultUtils.success(user);
	}

	/**
	 * 根据 id 获取包装类
	 *
	 * @param id
	 * @param request
	 * @return
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
		BaseResponse<User> response = getUserById(id, request);
		User user = response.getData();
		return ResultUtils.success(userService.getUserVO(user));
	}

	// get user list by page
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<List<User>> getUserListByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
		int current = (int) userQueryRequest.getCurrent();
		int size = (int) userQueryRequest.getPageSize();
		List<User> userList = userService.getUserListByPage(current, size);
		return ResultUtils.success(userList);
	}

}
