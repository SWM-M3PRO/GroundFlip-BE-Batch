package com.m3pro.groundflipbebatch.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.enums.PushKind;
import com.m3pro.groundflipbebatch.enums.PushTarget;
import com.m3pro.groundflipbebatch.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {
	private final RankingService rankingService;
	private final FcmService fcmService;
	private final AchievementService achievementService;

	@Scheduled(cron = "0 20 8 * * ?")
	public void sendDailyWalkNotification() {
		String title = "\uD83D\uDC5F 그라운드 플립과 함께 걸을 시간";
		String body = "오늘도 그라운드 플립을 켜고 땅을 점령해요!!";
		fcmService.sendNotificationToAllUsers(title, body, PushTarget.ALL, PushKind.SERVICE);
	}

	// @Scheduled(cron = "0 1 0 * * ?")
	// public void sendStepInitializeNotification() {
	// 	String title = "\uD83D\uDC5F 걸음 수 저장 완료!";
	// 	String body = "오늘의 걸음수가 잘 저장되었어요!!";
	// 	fcmService.sendNotificationToAndroidUsers(title, body);
	// }

	@Scheduled(cron = "0 0 * * * *")
	public void transferRankingToDatabaseOnEveryMidnight() {
		log.info("[transferRankingToDatabaseOnEveryMidnight] 사용자 랭킹 정보를 DB로 이관 시작");
		rankingService.transferRankingToDatabase();
		log.info("[transferRankingToDatabaseOnEveryMidnight] 사용자 랭킹 정보를 DB로 이관 완료");
	}

	@Scheduled(cron = "0 0 * * * *")
	public void transferCommunityRankingToDatabaseOnEveryMidnight() {
		log.info("[transferCommunityRankingToDatabaseOnEveryMidnight] 그룹 랭킹 정보를 DB로 이관 시작");
		rankingService.transferCommunityRankingToDatabase();
		log.info("[transferCommunityRankingToDatabaseOnEveryMidnight] 그룹 랭킹 정보를 DB로 이관 완료");
	}


	@Scheduled(cron = "5 0 0 * * MON")
	public void resetUserRankingOnEveryMonday() {
		log.info("[resetUserRankingOnEveryMonday] 레디스의 모든 사용자 현재 픽셀 갯수를 0으로 초기화 시작");
		rankingService.resetUserRanking();
		log.info("[resetUserRankingOnEveryMonday] 레디스의 모든 사용자 현재 픽셀 갯수를 0으로 초기화 완료");
	}

	@Scheduled(cron = "5 0 0 * * MON")
	public void resetCommunityRankingOnEveryMonday() {
		log.info("[resetCommunityRankingOnEveryMonday] 레디스의 모든 그룹 현재 픽셀 갯수를 0으로 초기화 시작");
		rankingService.resetCommunityRanking();
		log.info("[resetCommunityRankingOnEveryMonday] 레디스의 모든 그룹 현재 픽셀 갯수를 0으로 초기화 완료");
	}

	@Scheduled(cron = "0 10 0 * * MON")
	public void updateRankingAchievementOnEveryMonday() {
		LocalDateTime now = LocalDateTime.now();
		Integer currentYear = now.getYear();
		int currentWeek = DateUtils.getWeekOfDate(now.toLocalDate()) - 1;
		achievementService.updateRankingAchievement(currentYear, currentWeek);
	}
}
