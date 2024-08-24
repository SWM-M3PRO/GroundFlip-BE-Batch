package com.m3pro.groundflipbebatch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Device {
	IOS("iOS"),
	ANDROID("Android");

	private final String device;
}
