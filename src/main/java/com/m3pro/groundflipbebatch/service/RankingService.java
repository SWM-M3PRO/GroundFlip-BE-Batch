package com.m3pro.groundflipbebatch.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.entity.RankingHistory;
import com.m3pro.groundflipbebatch.entity.redis.RankingDetail;
import com.m3pro.groundflipbebatch.repository.RankingHistoryRepository;
import com.m3pro.groundflipbebatch.repository.RankingRedisRepository;
import com.m3pro.groundflipbebatch.util.DateUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankingService {
	private final RankingRedisRepository rankingRedisRepository;

	private final RankingHistoryRepository rankingHistoryRepository;

	@Transactional
	public void transferRankingToDatabase() {
		List<RankingDetail> newRankingDetails = rankingRedisRepository.getRankingsWithCurrentPixelCount();

		Map<Long, RankingHistory> existingRankingHistoryMap = getRankingHistoriesOfThisWeekAsMap();

		List<RankingHistory> updatedRankingHistoryList = new ArrayList<>();

		int updatedRankingHistory = 0;
		int createdRankingHistory = 0;

		for (RankingDetail rankingDetail : newRankingDetails) {
			RankingHistory existingRankingHistory = existingRankingHistoryMap.get(rankingDetail.getUserId());

			if (existingRankingHistory != null) {
				existingRankingHistory.update(rankingDetail.getCurrentPixelCount(), rankingDetail.getRanking());
				updatedRankingHistoryList.add(existingRankingHistory);
				updatedRankingHistory++;
			} else {
				RankingHistory newRankingHistory = RankingHistory.of(rankingDetail);
				updatedRankingHistoryList.add(newRankingHistory);
				createdRankingHistory++;
			}
		}

		rankingHistoryRepository.saveAll(updatedRankingHistoryList);
	}

	private Map<Long, RankingHistory> getRankingHistoriesOfThisWeekAsMap() {
		Integer currentYear = LocalDate.now().getYear();
		Integer currentWeek = DateUtils.getWeekOfDate(LocalDate.now());

		return rankingHistoryRepository.findAllByYearAndWeek(currentYear, currentWeek).stream()
			.collect(Collectors.toMap(RankingHistory::getUserId, Function.identity()));
	}

	public void resetRanking() {
		rankingRedisRepository.resetAllScoresToZero();
	}

}
