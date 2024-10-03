package com.m3pro.groundflipbebatch.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m3pro.groundflipbebatch.service.DailyPixelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pixel")
public class DailyPixelController {

	private final DailyPixelService dailyPixelService;

	@PostMapping("/daily")
	public void postDailyPixel() {
		dailyPixelService.saveDailyPixelCount();
	}
}
