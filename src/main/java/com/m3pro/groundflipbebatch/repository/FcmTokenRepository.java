package com.m3pro.groundflipbebatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m3pro.groundflipbebatch.entity.FcmToken;
import com.m3pro.groundflipbebatch.entity.User;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
	Optional<FcmToken> findByUser(User user);

	void deleteByUser(User user);
}
