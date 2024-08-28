package com.m3pro.groundflipbebatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PushRequest {
	private String title;

	private String body;

	private String secretKey;
}
