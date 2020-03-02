package com.jp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jp.entity.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>  {
	List<User> findByCreatedIsNull();
	
	Optional<User> findOneByUsername(String username);
	User findByUsername(String username);
}