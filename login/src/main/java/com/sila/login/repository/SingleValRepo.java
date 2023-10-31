package com.sila.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sila.login.model.SingleValues;

@Repository
public interface SingleValRepo extends JpaRepository<SingleValues, String>{
    
}
