package com.m3pro.groundflipbebatch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PushTarget {
	ALL("all"),
	IOS("iOS"),
	ANDROID("Android");

	private final String target;
}
