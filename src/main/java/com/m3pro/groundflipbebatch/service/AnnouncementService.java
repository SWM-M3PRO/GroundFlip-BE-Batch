package com.m3pro.groundflipbebatch.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.m3pro.groundflipbebatch.entity.Announcement;
import com.m3pro.groundflipbebatch.repository.AnnouncementRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService {
	private final AnnouncementRepository announcementRepository;

	@Transactional
	public Long createAnnouncement(String title, String content) {
		Announcement announcement = announcementRepository.saveAndFlush(Announcement.builder()
			.title(title)
			.content(content)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.viewCount(0L)
			.build());
		return announcement.getId();
	}
}
