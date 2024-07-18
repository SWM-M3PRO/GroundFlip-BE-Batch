package com.m3pro.groundflipbebatch.entity;

import java.time.LocalDate;

import com.m3pro.groundflipbebatch.entity.redis.RankingDetail;
import com.m3pro.groundflipbebatch.util.DateUtils;

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
public class RankingHistory extends BaseTimeEntity {
	@Id
	@Column(name = "ranking_history_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	Long userId;

	Long ranking;

	Long currentPixelCount;

	Integer year;

	Integer week;

	public static RankingHistory of(RankingDetail rankingDetail) {
		int year = LocalDate.now().getYear();
		int week = DateUtils.getWeekOfDate(LocalDate.now());
		return RankingHistory.builder()
			.userId(rankingDetail.getUserId())
			.ranking(rankingDetail.getRanking())
			.currentPixelCount(rankingDetail.getCurrentPixelCount())
			.year(year)
			.week(week)
			.build();
	}

	public void update(Long currentPixelCount, Long ranking) {
		this.currentPixelCount = currentPixelCount;
		this.ranking = ranking;
	}
}
