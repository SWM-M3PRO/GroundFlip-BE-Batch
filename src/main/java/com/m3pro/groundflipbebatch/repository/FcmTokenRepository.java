package com.m3pro.groundflipbebatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.m3pro.groundflipbebatch.entity.FcmToken;
import com.m3pro.groundflipbebatch.entity.User;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
	Optional<FcmToken> findByUser(User user);

	@Query("""
		SELECT f FROM FcmToken f
		JOIN Permission p ON f.user.id = p.user.id
		WHERE p.serviceNotificationsEnabled = true
		""")
	List<FcmToken> findAllTokensForServiceNotifications();

	void deleteByUser(User user);
}
