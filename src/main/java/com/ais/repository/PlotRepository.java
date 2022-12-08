package com.ais.repository;

import com.ais.model.Plot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlotRepository extends JpaRepository<Plot, String> {
}
