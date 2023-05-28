package com.example.springboothibernateinit.dao;

import com.example.springboothibernateinit.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author jayingyoung
 */
public interface UserDao extends PagingAndSortingRepository<User, Long> {
	User findById(long id);
	User findByUserAccount(String userAccount);
	Page<User> findAll(Pageable pageable);
}
