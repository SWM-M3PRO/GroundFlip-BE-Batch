package com.m3pro.groundflipbebatch.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Announcement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "announcement_id")
	private Long id;

	private String title;

	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	private Long viewCount;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public void incrementViewCount() {
		this.viewCount++;
	}
}
