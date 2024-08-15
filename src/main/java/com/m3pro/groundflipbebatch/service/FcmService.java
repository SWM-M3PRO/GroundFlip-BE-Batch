package com.m3pro.groundflipbebatch.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmService {
	private final FirebaseMessaging firebaseMessaging;

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
}
