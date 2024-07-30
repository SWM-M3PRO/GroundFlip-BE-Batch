package com.m3pro.groundflipbebatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m3pro.groundflipbebatch.entity.RankingHistory;

public interface RankingHistoryRepository extends JpaRepository<RankingHistory, Long> {
	List<RankingHistory> findAllByYearAndWeek(Integer year, Integer week);
}
