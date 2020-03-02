package com.jp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jp.entity.Roles;
import com.jp.entity.User;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long>  {
	Roles findByRole(String role);
	Optional<Roles> findOneByRole(String role);
}