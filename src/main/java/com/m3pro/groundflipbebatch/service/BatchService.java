package com.m3pro.groundflipbebatch.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {
	private final RankingService rankingService;

	@Scheduled(cron = "0 0 * * * *")
	public void transferRankingToDatabaseOnEveryMidnight() {
		log.info("[transferRankingToDatabaseOnEveryMidnight] 랭킹 정보를 DB로 이관 시작");
		rankingService.transferRankingToDatabase();
		log.info("[transferRankingToDatabaseOnEveryMidnight] 랭킹 정보를 DB로 이관 완료");
	}


	@Scheduled(cron = "0 0 0 * * MON")
	public void resetRankingOnEveryMonday() {
		log.info("[resetRankingOnEveryMonday] 레디스의 모든 픽셀 갯수를 0으로 초기화 시작");
		rankingService.resetRanking();
		log.info("[resetRankingOnEveryMonday] 레디스의 모든 픽셀 갯수를 0으로 초기화 완료");

	}
}
