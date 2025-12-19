package com.example.expenseshare.repository;

import com.example.expenseshare.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}