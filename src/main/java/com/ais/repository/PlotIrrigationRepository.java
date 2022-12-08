package com.ais.repository;

import com.ais.model.Plot;
import com.ais.model.PlotIrrigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlotIrrigationRepository extends JpaRepository<PlotIrrigation, String> {

    PlotIrrigation findByPlot_Id(String plotId);

    @Query("SELECT pi FROM PlotIrrigation pi WHERE pi.status = com.ais.model.IrrigationStatus.IDLE AND pi.nextIrrigationTime <= :currentTime")
    List<PlotIrrigation> findAllPlotIrrigationToCreateSlots(@Param("currentTime") Long currentTime);
}
