package com.m3pro.groundflipbebatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m3pro.groundflipbebatch.entity.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {}
