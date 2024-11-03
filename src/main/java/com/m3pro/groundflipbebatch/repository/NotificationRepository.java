package com.m3pro.groundflipbebatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m3pro.groundflipbebatch.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
