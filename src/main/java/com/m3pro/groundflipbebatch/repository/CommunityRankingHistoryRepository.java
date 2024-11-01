package com.m3pro.groundflipbebatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.m3pro.groundflipbebatch.entity.CommunityRankingHistory;
import com.m3pro.groundflipbebatch.entity.RankingHistory;

public interface CommunityRankingHistoryRepository extends JpaRepository<CommunityRankingHistory, Long> {
	List<CommunityRankingHistory> findAllByYearAndWeek(Integer year, Integer week);
}