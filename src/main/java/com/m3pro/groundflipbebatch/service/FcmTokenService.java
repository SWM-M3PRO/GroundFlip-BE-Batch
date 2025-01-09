package com.m3pro.groundflipbebatch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.entity.FcmToken;
import com.m3pro.groundflipbebatch.enums.PushKind;
import com.m3pro.groundflipbebatch.enums.PushTarget;
import com.m3pro.groundflipbebatch.repository.FcmTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmTokenService {
	private final FcmTokenRepository fcmTokenRepository;

	public List<FcmToken> findFcmTokens(PushTarget target, PushKind kind) {
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

	@Transactional
	public void removeAllTokens(List<String> tokens) {
		fcmTokenRepository.deleteByTokenIn(tokens);
	}
}
