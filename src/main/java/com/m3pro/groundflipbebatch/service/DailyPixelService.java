package com.m3pro.groundflipbebatch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.dto.DailyPixelResponse;
import com.m3pro.groundflipbebatch.entity.DailyPixel;
import com.m3pro.groundflipbebatch.entity.User;
import com.m3pro.groundflipbebatch.repository.DailyPixelRepository;
import com.m3pro.groundflipbebatch.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyPixelService {
	private static final Logger log = LoggerFactory.getLogger(DailyPixelService.class);
	private final DailyPixelRepository dailyPixelRepository;
	private final UserRepository userRepository;

	@Transactional
	@Scheduled(cron = "0 1 0 * * ?")
	public void saveDailyPixelCount() {

		List<DailyPixelResponse> dailyPixelResponseList = dailyPixelRepository.findDailyPixelByDate(LocalDate.now());

		List<DailyPixel> dailyPixelList = new ArrayList<DailyPixel>();

		for (DailyPixelResponse dailyPixelResponse : dailyPixelResponseList) {
			User user = userRepository.getReferenceById(dailyPixelResponse.getUserId());

			DailyPixel dailyPixel = DailyPixel.builder()
				.user(user)
				.dailyPixelCount(dailyPixelResponse.getDailyPixelCount())
				.createdAt(LocalDateTime.now().minusDays(1))
				.build();

			dailyPixelList.add(dailyPixel);
		}

		dailyPixelRepository.saveAll(dailyPixelList);
		log.info("daily pixel save complete");
	}
}
