package com.m3pro.groundflipbebatch.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import com.m3pro.groundflipbebatch.entity.redis.RankingDetail;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingRedisRepository {
	private static final String RANKING_KEY = "current_pixel_ranking";
	private static final int RANKING_START_INDEX = 0;
	private static final int RANKING_END_INDEX = -1;
	private final RedisTemplate<String, String> redisTemplate;
	private ZSetOperations<String, String> zSetOperations;

	@PostConstruct
	void init() {
		zSetOperations = redisTemplate.opsForZSet();
	}

	public List<RankingDetail> getRankingsWithCurrentPixelCount() {
		Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(RANKING_KEY,
			RANKING_START_INDEX, RANKING_END_INDEX);
		if (typedTuples == null) {
			return new ArrayList<>();
		}

		List<RankingDetail> rankingDetails = new ArrayList<>();
		long ranking = 1;
		for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
			rankingDetails.add(RankingDetail.from(typedTuple, ranking++));
		}
		return rankingDetails;
	}

	public void resetAllScoresToZero() {
		Set<String> userIds = zSetOperations.range(RANKING_KEY, RANKING_START_INDEX, -1); // Get all members
		if (userIds != null) {
			for (String userId : userIds) {
				zSetOperations.add(RANKING_KEY, userId, 0); // Update the score to 0
			}
		}
	}
}
