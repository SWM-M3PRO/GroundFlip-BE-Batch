package com.m3pro.groundflipbebatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m3pro.groundflipbebatch.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
