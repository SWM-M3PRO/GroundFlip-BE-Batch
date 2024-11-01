package com.m3pro.groundflipbebatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.m3pro.groundflipbebatch.entity.UserAchievement;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {


	@Query("""
		SELECT ua FROM UserAchievement ua
			JOIN FETCH ua.achievement a
			WHERE ua.userId = :userId AND a.categoryId = :categoryId
			ORDER BY ua.obtainedAt DESC
		""")
	List<UserAchievement> findAllByUserIdAndCategoryId(Long userId, Long categoryId);
}
