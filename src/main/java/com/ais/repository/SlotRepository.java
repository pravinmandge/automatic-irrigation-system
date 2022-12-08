package com.ais.repository;

import com.ais.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, String> {

    List<Slot> findAllByPlot_Id(String plotId);

}
