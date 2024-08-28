package com.m3pro.groundflipbebatch.dto;

import com.m3pro.groundflipbebatch.enums.PushKind;
import com.m3pro.groundflipbebatch.enums.PushTarget;

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

	private PushTarget target;

	private PushKind kind;

	private String secretKey;
}
