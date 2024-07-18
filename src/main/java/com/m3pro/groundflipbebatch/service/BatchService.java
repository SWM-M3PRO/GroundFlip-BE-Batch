package com.m3pro.groundflipbebatch.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.entity.RankingHistory;
import com.m3pro.groundflipbebatch.entity.redis.Ranking;
import com.m3pro.groundflipbebatch.repository.RankingHistoryRepository;
import com.m3pro.groundflipbebatch.repository.RankingRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {
	private final RankingRedisRepository rankingRedisRepository;

	private final RankingHistoryRepository rankingHistoryRepository;

	@Scheduled(cron = "1 0 0 * * *")
	public void transferRankingToDatabase() {
		List<RankingHistory> rankingHistories = rankingRedisRepository.getRankingsWithCurrentPixelCount().stream()
			.map(RankingHistory::of)
			.toList();

		rankingHistoryRepository.saveAll(rankingHistories);


	}

}
