package com.m3pro.groundflipbebatch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.m3pro.groundflipbebatch.entity.FcmToken;
import com.m3pro.groundflipbebatch.enums.PushKind;
import com.m3pro.groundflipbebatch.enums.PushTarget;
import com.m3pro.groundflipbebatch.repository.FcmTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {
	private final FirebaseMessaging firebaseMessaging;
	private final FcmTokenRepository fcmTokenRepository;

	public void sendNotificationToAllUsers(String title, String body, PushTarget target, PushKind kind) {
		List<FcmToken> fcmTokens = findFcmTokens(target, kind);

		sendNotificationToUsers(title, body, fcmTokens);
	}

	private List<FcmToken> findFcmTokens(PushTarget target, PushKind kind) {
		List<FcmToken> fcmTokens;
		if (kind == PushKind.SERVICE) {
			if (target == PushTarget.ALL) {
				fcmTokens = fcmTokenRepository.findAllTokensForServiceNotifications();
			} else if (target == PushTarget.ANDROID) {
				fcmTokens = fcmTokenRepository.findAllAndroidTokensForServiceNotification();
			} else {
				fcmTokens = fcmTokenRepository.findAllIOSTokensForServiceNotification();
			}
		} else {
			if (target == PushTarget.ALL) {
				fcmTokens = fcmTokenRepository.findAllTokensForMarketingNotifications();
			} else if (target == PushTarget.ANDROID) {
				fcmTokens = fcmTokenRepository.findAllAndroidTokensForMarketingNotification();
			} else {
				fcmTokens = fcmTokenRepository.findAllIOSTokensForMarketingNotification();
			}
		}

		return fcmTokens;
	}

	private void sendNotificationToUsers(String title, String body, List<FcmToken> fcmTokens) {
		List<Long> failedTokensId = new ArrayList<>();

		fcmTokens.forEach(fcmToken -> {
			try {
				log.info(fcmToken.getId().toString());
				sendMessage(title, body, fcmToken.getToken());
			} catch(FirebaseMessagingException e) {
				if (isInvalidTokenError(e)) {
					failedTokensId.add(fcmToken.getId());
					log.warn("Failed to send notification to [{}]: {}", fcmToken.getUser().getId(), fcmToken.getToken());
				}
			}
		});

		if (!failedTokensId.isEmpty()) {
			fcmTokenRepository.deleteAllById(failedTokensId);
		}
	}

	public void sendNotificationToAndroidUsers(String title, String body) {
		List<FcmToken> fcmTokens = fcmTokenRepository.findAllAndroidTokensForStepNotification();
		List<Long> failedTokensId = new ArrayList<>();

		fcmTokens.forEach(fcmToken -> {
			try {
				log.warn("success send notification to user [{}]: tokenId = {}", fcmToken.getUser().getId(), fcmToken.getId());
				sendMessage(title, body, fcmToken.getToken());
			} catch(FirebaseMessagingException e) {
				if (isInvalidTokenError(e)) {
					failedTokensId.add(fcmToken.getId());
					log.warn("Failed to send notification to [{}]: {}", fcmToken.getUser().getId(), fcmToken.getToken());
				}
			}
		});

		if (!failedTokensId.isEmpty()) {
			fcmTokenRepository.deleteAllById(failedTokensId);
		}
	}

	public void sendMessage(String title, String body, String fcmToken) throws FirebaseMessagingException {
		Message message = createMessage(title, body, fcmToken);
		firebaseMessaging.send(message);
	}

	private Message createMessage(String title, String body, String fcmToken) {
		Notification notification = createNotification(title, body);
		return Message.builder()
			.setToken(fcmToken)
			.setNotification(notification)
			.setApnsConfig(getApnsConfig())
			.build();
	}

	private static Notification createNotification(String title, String body) {
		return Notification.builder()
			.setTitle(title)
			.setBody(body)
			.build();
	}

	private ApnsConfig getApnsConfig() {
		return ApnsConfig.builder()
			.setAps(Aps.builder()
				.setSound("default")
				.build()
			).build();
	}

	private boolean isInvalidTokenError(FirebaseMessagingException e) {
		MessagingErrorCode errorCode = e.getMessagingErrorCode();
		return errorCode.equals(MessagingErrorCode.UNREGISTERED) || errorCode.equals(MessagingErrorCode.INVALID_ARGUMENT);
	}
}
