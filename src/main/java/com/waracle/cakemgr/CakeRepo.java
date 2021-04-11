package com.waracle.cakemgr;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CakeRepo extends JpaRepository<CakeEntity, Long> {
 
    List<CakeEntity> findAll();
    
    CakeEntity save(CakeEntity persisted);
}