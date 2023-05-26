package com.example.springboothibernateinit.service.impl;

import com.example.springboothibernateinit.common.ErrorCode;
import com.example.springboothibernateinit.constant.UserConstant;
import com.example.springboothibernateinit.dao.UserDao;
import com.example.springboothibernateinit.exception.BusinessException;
import com.example.springboothibernateinit.model.entity.User;
import com.example.springboothibernateinit.model.enums.UserRoleEnum;
import com.example.springboothibernateinit.model.vo.LoginUserVO;
import com.example.springboothibernateinit.model.vo.UserVO;
import com.example.springboothibernateinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	/**
	 * salt
	 */
	private static final String SALT = "jaying";

	@Override
	public long userRegister(String userAccount, String userPassword, String checkPassword) {
		// 1. Parameter check
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "params can not be null");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "account too short");
		}
		if (userPassword.length() < 8 || checkPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "password too short");
		}
		// check password and checkPassword
		if (!userPassword.equals(checkPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "Two passwords are not the same");
		}
		synchronized (userAccount.intern()) {
			// get user by userAccount
			User queryUser = userDao.findByUserAccount(userAccount);
			if (queryUser != null) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "userAccount already exists");
			}
			// 2. encrypt password
			String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
			// 3. save user
			User user = new User();
			user.setUserAccount(userAccount);
			user.setUserPassword(encryptPassword);
			User createUser = userDao.save(user);
			return createUser.getId();
		}
	}

	@Override
	public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
		// 1. Parameter check
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "params can not be empty");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "account incorrect");
		}
		if (userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "password incorrect");
		}

		// 2. encrypt password
		String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

		// check user exists or not
		User queryUser = userDao.findByUserAccount(userAccount);
		if (queryUser == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "user not exists");
		}
		if(!encryptPassword.equals(queryUser.getUserPassword())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "password incorrect");
		}

		// 3. record user login info
		request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, queryUser);

		return this.getLoginUserVO(queryUser);
	}

	@Override
	public User getLoginUser(HttpServletRequest request) {
		// make sure user has login
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User currentUser = (User) userObj;
		if (currentUser == null || currentUser.getId() == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		// get user info from db
		long userId = currentUser.getId();
		currentUser = userDao.findById(userId);
		if (currentUser == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		return currentUser;
	}


	@Override
	public User getLoginUserPermitNull(HttpServletRequest request) {
		// make sure user has login
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User currentUser = (User) userObj;
		if (currentUser == null || currentUser.getId() == null) {
			return null;
		}
		// check user exists or not from db
		long userId = currentUser.getId();
		return userDao.findById(userId);
	}

	@Override
	public boolean isAdmin(HttpServletRequest request) {
		// only admin can do this
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User user = (User) userObj;
		return isAdmin(user);
	}

	@Override
	public boolean isAdmin(User user) {
		return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
	}

	@Override
	public boolean userLogout(HttpServletRequest request) {
		if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "user not login");
		}
		// remove user info from session
		request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
		return true;
	}

	@Override
	public LoginUserVO getLoginUserVO(User user) {
		if (user == null) {
			return null;
		}
		LoginUserVO loginUserVO = new LoginUserVO();
		BeanUtils.copyProperties(user, loginUserVO);
		return loginUserVO;
	}

	@Override
	public UserVO getUserVO(User user) {
		if (user == null) {
			return null;
		}
		UserVO userVO = new UserVO();
		BeanUtils.copyProperties(user, userVO);
		return userVO;
	}

	@Override
	public boolean save(User user) {
		if (user == null) {
			return false;
		}
		userDao.save(user);
		return true;
	}

	@Override
	public User getById(long id) {
		User user = userDao.findById(id);
		if (user == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "user not exists");
		}
		return user;
	}

	@Override
	public boolean removeById(long id) {
		User user = userDao.findById(id);
		if(user == null) {
			return false;
		}
		userDao.deleteById(id);
		return true;
	}

	@Override
	public List<UserVO> getUserVO(List<User> userList) {
		if (userList == null || userList.isEmpty()) {
			return new ArrayList<>();
		}
		return userList.stream().map(this::getUserVO).collect(Collectors.toList());
	}

}
