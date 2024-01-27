package com.example.testfunding.repository;

import com.example.testfunding.entity.Funding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingRepository extends JpaRepository<Funding,Long> {
}
