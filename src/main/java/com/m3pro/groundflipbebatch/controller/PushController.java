package com.m3pro.groundflipbebatch.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m3pro.groundflipbebatch.dto.PushAnnouncementRequest;
import com.m3pro.groundflipbebatch.dto.PushRequest;
import com.m3pro.groundflipbebatch.service.FcmService;
import com.m3pro.groundflipbebatch.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
public class PushController {
	private final FcmService fcmService;
	private final NotificationService notificationService;
	@Value("${spring.push.secret}")
	private String secretKey;

	@PostMapping("/all")
	public void sendNotificationToAllUsers(@RequestBody PushRequest pushRequest) {
		if (pushRequest.getSecretKey().equals(secretKey)) {
			fcmService.sendNotificationToAllUsers(pushRequest.getTitle(), pushRequest.getBody(), pushRequest.getTarget(), pushRequest.getKind());
		}
	}

	@PostMapping("/announcement")
	public void sendAnnouncementToAllUsers(@RequestBody PushAnnouncementRequest pushRequest) {
		if (pushRequest.getSecretKey().equals(secretKey)) {
			notificationService.createAnnouncementNotification(pushRequest.getTitle(), pushRequest.getContent(),
				pushRequest.getMessage());
		}
	}
}
