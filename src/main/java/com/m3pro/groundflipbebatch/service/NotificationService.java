package com.m3pro.groundflipbebatch.service;

import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.entity.Achievement;
import com.m3pro.groundflipbebatch.enums.PushKind;
import com.m3pro.groundflipbebatch.enums.PushTarget;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final FcmService fcmService;
	private final NotificationManager notificationManager;

	public void createAchievementNotification(Long userId, Achievement achievement) {
		notificationManager.createAchievementNotification(userId, achievement);
		fcmService.sendNotificationToUser(achievement.getName() + " 획득!", achievement.getName() + " 획득하였습니다!", userId);
	}

	public void createAnnouncementNotification(String title, String contents, String message) {
		notificationManager.createAnnouncementNotification(title, contents);
		fcmService.sendNotificationToAllUsers(title, message, PushTarget.ALL, PushKind.SERVICE);
	}
}
