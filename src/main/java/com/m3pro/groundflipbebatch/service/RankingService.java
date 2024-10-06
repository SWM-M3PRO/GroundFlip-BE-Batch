package com.m3pro.groundflipbebatch.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.entity.RankingHistory;
import com.m3pro.groundflipbebatch.entity.redis.RankingDetail;
import com.m3pro.groundflipbebatch.repository.CommunityRankingRedisRepository;
import com.m3pro.groundflipbebatch.repository.RankingHistoryRepository;
import com.m3pro.groundflipbebatch.repository.UserRankingRedisRepository;
import com.m3pro.groundflipbebatch.util.DateUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingService {
	private final UserRankingRedisRepository userRankingRedisRepository;

	private final CommunityRankingRedisRepository communityRankingRedisRepository;

	private final RankingHistoryRepository rankingHistoryRepository;

	@Transactional
	public void transferRankingToDatabase() {
		List<RankingDetail> newRankingDetails = userRankingRedisRepository.getRankingsWithCurrentPixelCount();

		Map<Long, RankingHistory> existingRankingHistoryMap = getRankingHistoriesOfThisWeekAsMap();

		List<RankingHistory> updatedRankingHistoryList = new ArrayList<>();

		int existedUser = 0;
		int newUser = 0;
		for (RankingDetail rankingDetail : newRankingDetails) {
			RankingHistory existingRankingHistory = existingRankingHistoryMap.get(rankingDetail.getUserId());

			if (existingRankingHistory != null) {
				existingRankingHistory.update(rankingDetail.getCurrentPixelCount(), rankingDetail.getRanking());
				updatedRankingHistoryList.add(existingRankingHistory);
				existedUser++;
			} else {
				RankingHistory newRankingHistory = RankingHistory.of(rankingDetail);
				updatedRankingHistoryList.add(newRankingHistory);
				newUser++;
			}
		}

		rankingHistoryRepository.saveAll(updatedRankingHistoryList);
		log.info("[transferRankingToDatabase] 기존 유저 {}명, 새로운 유저 {}명",
			existedUser,
			newUser
		);
	}

	private Map<Long, RankingHistory> getRankingHistoriesOfThisWeekAsMap() {
		LocalDateTime now = LocalDateTime.now();
		Integer currentYear = now.getYear();
		int currentWeek = DateUtils.getWeekOfDate(now.toLocalDate());

		// 월요일 자정 체크
		if (now.getDayOfWeek() == DayOfWeek.MONDAY && now.getHour() == 0) {
			currentWeek -= 1;
		}

		return rankingHistoryRepository.findAllByYearAndWeek(currentYear, currentWeek).stream()
			.collect(Collectors.toMap(RankingHistory::getUserId, Function.identity()));
	}

	public void resetUserRanking() {
		userRankingRedisRepository.resetAllScoresToZero();
	}

	public void resetCommunityRanking() {
		communityRankingRedisRepository.resetAllScoresToZero();
	}

}
