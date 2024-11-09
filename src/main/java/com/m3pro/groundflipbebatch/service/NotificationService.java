package com.m3pro.groundflipbebatch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.entity.Achievement;
import com.m3pro.groundflipbebatch.entity.Notification;
import com.m3pro.groundflipbebatch.entity.User;
import com.m3pro.groundflipbebatch.entity.UserNotification;
import com.m3pro.groundflipbebatch.enums.NotificationCategory;
import com.m3pro.groundflipbebatch.enums.PushKind;
import com.m3pro.groundflipbebatch.enums.PushTarget;
import com.m3pro.groundflipbebatch.repository.NotificationRepository;
import com.m3pro.groundflipbebatch.repository.UserNotificationRepository;
import com.m3pro.groundflipbebatch.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final UserNotificationRepository userNotificationRepository;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final FcmService fcmService;
	private final AnnouncementService announcementService;


	@Transactional
	public void createAchievementNotification(Long userId, Achievement achievement) {
		Notification notification = notificationRepository.save(Notification.builder()
			.title(achievement.getName() + " 획득!")
			.category(NotificationCategory.ACHIEVEMENT.getCategoryName())
			.categoryId(NotificationCategory.ACHIEVEMENT.getCategoryId())
			.contentId(achievement.getId())
			.build()
		);
		userNotificationRepository.save(UserNotification.builder()
			.userId(userId)
			.notification(notification)
			.build());
		fcmService.sendNotificationToUser(achievement.getName() + " 획득!", achievement.getName() + " 획득하였습니다!", userId);
	}

	@Transactional
	public void createAnnouncementNotification(String title, String contents, String message) {
		Long announcementId = announcementService.createAnnouncement(title, contents);
		Notification notification = notificationRepository.save(Notification.builder()
			.title(title)
			.category(NotificationCategory.ANNOUNCEMENT.getCategoryName())
			.categoryId(NotificationCategory.ANNOUNCEMENT.getCategoryId())
			.contentId(announcementId)
			.build()
		);
		createNotificationToAllUsers(notification);

		fcmService.sendNotificationToAllUsers(title, message, PushTarget.ALL, PushKind.SERVICE);
	}

	private void createNotificationToAllUsers(Notification notification) {
		int page = 0;
		int pageSize = 500;

		Pageable pageable = PageRequest.of(page, pageSize);
		Page<User> userPage;
		do {
			userPage = userRepository.findAll(pageable);
			List<UserNotification> userNotifications = userPage.getContent().stream()
				.map(user -> UserNotification.builder()
					.notification(notification)
					.userId(user.getId())
					.build())
				.collect(Collectors.toList());

			userNotificationRepository.saveAll(userNotifications);
			userNotificationRepository.flush(); // 캐시 초기화

			pageable = pageable.next(); // 다음 페이지로 이동
		} while (userPage.hasNext());
	}
}
