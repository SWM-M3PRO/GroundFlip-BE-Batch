package com.m3pro.groundflipbebatch.service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.m3pro.groundflipbebatch.entity.RankingHistory;
import com.m3pro.groundflipbebatch.entity.UserAchievement;
import com.m3pro.groundflipbebatch.enums.AchievementCategoryId;
import com.m3pro.groundflipbebatch.enums.Ranking;
import com.m3pro.groundflipbebatch.repository.AchievementRepository;
import com.m3pro.groundflipbebatch.repository.RankingHistoryRepository;
import com.m3pro.groundflipbebatch.repository.UserAchievementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {
	private final RankingHistoryRepository rankingHistoryRepository;
	private final UserAchievementRepository userAchievementRepository;
	private final AchievementRepository achievementRepository;
	@Transactional
	public void updateRankingAchievement(Integer year, Integer week) {
		List<RankingHistory> rankingHistories = rankingHistoryRepository.findAllByYearAndWeek(year, week);

		rankingHistories.forEach(rankingHistory -> {
			Long userId = rankingHistory.getUserId();
			Long ranking = rankingHistory.getRanking();

			List<Long> achievementIds = getRankingAchievementId(ranking);
			List<UserAchievement> userAchievements = userAchievementRepository
				.findAllByUserIdAndCategoryId(userId, AchievementCategoryId.RANKER.getCategoryId());

			updateRankingAchievements(achievementIds, userAchievements, userId);
		});
	}

	private void updateRankingAchievements(List<Long> achievementIds, List<UserAchievement> userAchievements, Long userId) {
		for (Long achievementId : achievementIds) {
			boolean alreadyHasAchievement = userAchievements.stream()
				.anyMatch(ua -> ua.getAchievement().getId().equals(achievementId));

			if (!alreadyHasAchievement) {
				UserAchievement newAchievement = UserAchievement.builder()
					.userId(userId)
					.achievement(achievementRepository.getReferenceById(achievementId))
					.currentValue(1)
					.obtainedAt(LocalDateTime.now())
					.isRewardReceived(false)
					.build();
				userAchievementRepository.save(newAchievement);
			}
		}
	}

	private static List<Long> getRankingAchievementId(Long ranking) {
		return EnumSet.allOf(Ranking.class).stream()
			.filter(r -> ranking <= r.getRanking())
			.map(Ranking::getAchievementId)
			.toList();
	}
}
