package com.levelup.backend_levelup.repository;

import com.levelup.backend_levelup.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByUserId(Long userId);
}