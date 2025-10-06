package com.example.samplebatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samplebatch.entity.BeforeEntity;

public interface BeforeRepository extends JpaRepository<BeforeEntity, Long> {
}
