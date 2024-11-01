package com.m3pro.groundflipbebatch.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.entity.CommunityRankingHistory;
import com.m3pro.groundflipbebatch.entity.RankingHistory;
import com.m3pro.groundflipbebatch.entity.redis.RankingDetail;
import com.m3pro.groundflipbebatch.repository.CommunityRankingHistoryRepository;
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
	private final CommunityRankingHistoryRepository communityRankingHistoryRepository;

	@Transactional
	public void transferRankingToDatabase() {
		List<RankingDetail> newRankingDetails = userRankingRedisRepository.getRankingsWithCurrentPixelCount();

		Map<Long, RankingHistory> existingRankingHistoryMap = getRankingHistoriesOfThisWeekAsMap();

		List<RankingHistory> updatedRankingHistoryList = new ArrayList<>();

		int existedUser = 0;
		int newUser = 0;
		for (RankingDetail rankingDetail : newRankingDetails) {
			RankingHistory existingRankingHistory = existingRankingHistoryMap.get(rankingDetail.getId());

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

	@Transactional
	public void transferCommunityRankingToDatabase() {
		List<RankingDetail> newRankingDetails = communityRankingRedisRepository.getRankingsWithCurrentPixelCount();

		Map<Long, CommunityRankingHistory> existingRankingHistoryMap = getCommunityRankingHistoriesOfThisWeekAsMap();

		List<CommunityRankingHistory> updatedRankingHistoryList = new ArrayList<>();

		int existedCommunity = 0;
		int newCommunity = 0;
		for (RankingDetail rankingDetail : newRankingDetails) {
			CommunityRankingHistory existingRankingHistory = existingRankingHistoryMap.get(rankingDetail.getId());

			if (existingRankingHistory != null) {
				existingRankingHistory.update(rankingDetail.getCurrentPixelCount(), rankingDetail.getRanking());
				updatedRankingHistoryList.add(existingRankingHistory);
				existedCommunity++;
			} else {
				CommunityRankingHistory newRankingHistory = CommunityRankingHistory.of(rankingDetail);
				updatedRankingHistoryList.add(newRankingHistory);
				newCommunity++;
			}
		}

		communityRankingHistoryRepository.saveAll(updatedRankingHistoryList);
		log.info("[transferCommunityRankingToDatabase] 기존 그룹 {}명, 새로운 그룹 {}명",
			existedCommunity,
			newCommunity
		);
	}

	private Map<Long, CommunityRankingHistory> getCommunityRankingHistoriesOfThisWeekAsMap() {
		LocalDateTime now = LocalDateTime.now();
		Integer currentYear = now.getYear();
		int currentWeek = DateUtils.getWeekOfDate(now.toLocalDate());

		// 월요일 자정 체크
		if (now.getDayOfWeek() == DayOfWeek.MONDAY && now.getHour() == 0) {
			currentWeek -= 1;
		}

		return communityRankingHistoryRepository.findAllByYearAndWeek(currentYear, currentWeek).stream()
			.collect(Collectors.toMap(CommunityRankingHistory::getCommunityId, Function.identity()));
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
