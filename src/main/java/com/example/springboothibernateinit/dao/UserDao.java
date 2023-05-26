package com.example.springboothibernateinit.dao;

import com.example.springboothibernateinit.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author jayingyoung
 */
public interface UserDao extends JpaRepository<User, Long> {
	User findById(long id);
	User findByUserAccount(String userAccount);
}
