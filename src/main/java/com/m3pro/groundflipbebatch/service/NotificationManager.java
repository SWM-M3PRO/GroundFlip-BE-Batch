package com.m3pro.groundflipbebatch.service;

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
import com.m3pro.groundflipbebatch.repository.NotificationRepository;
import com.m3pro.groundflipbebatch.repository.UserNotificationRepository;
import com.m3pro.groundflipbebatch.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationManager {
	private final UserNotificationRepository userNotificationRepository;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final AnnouncementService announcementService;

	@Transactional
	public void createAchievementNotification(Long userId, Achievement achievement) {
		Notification notification = createNotification(achievement.getName() + " 획득!", NotificationCategory.ACHIEVEMENT, achievement.getId());
		userNotificationRepository.save(UserNotification.builder()
			.userId(userId)
			.notification(notification)
			.build());
	}

	@Transactional
	public void createAnnouncementNotification(String title, String contents) {
		Long announcementId = announcementService.createAnnouncement(title, contents);
		Notification notification = createNotification(title, NotificationCategory.ANNOUNCEMENT, announcementId);
		createNotificationToAllUsers(notification);
	}

	private Notification createNotification(String title, NotificationCategory category, Long contentId) {
		return notificationRepository.save(Notification.builder()
			.title(title)
			.category(category.getCategoryName())
			.categoryId(category.getCategoryId())
			.contentId(contentId)
			.build()
		);
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
