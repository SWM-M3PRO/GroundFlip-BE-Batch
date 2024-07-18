package com.m3pro.groundflipbebatch.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
public class BatchService {
	private final RankingService rankingService;

	@Scheduled(cron = "0 0 * * * *")
	public void transferRankingToDatabaseOnEveryMidnight() {
		rankingService.transferRankingToDatabase();
	}


	@Scheduled(cron = "0 0 0 * * MON")
	public void resetRankingOnEveryMonday() {
		rankingService.resetRanking();
	}
}
