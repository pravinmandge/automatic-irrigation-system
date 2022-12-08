package com.ais.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name= "slot")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Slot {

    @Id
    private String id;

    @Column( name= "water_needed")
    private Integer waterNeeded;

    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.PENDING;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plot_id")
    @JsonProperty("plot")
    private Plot plot;

    @Column( name= "retry_count")
    private Integer retryCount;
    @Column( name= "last_retry_time")
    private Long lastRetryTime;
    @Column( name= "duration_in_min")
    private Integer durationInMin;
    @Column( name= "interval_in_min")
    private Integer intervalInMin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return Objects.equals(id, slot.id) && Objects.equals(waterNeeded, slot.waterNeeded)
                && Objects.equals(retryCount, slot.retryCount) && Objects.equals(lastRetryTime, slot.lastRetryTime)
                && Objects.equals(durationInMin, slot.durationInMin) && Objects.equals(intervalInMin, slot.intervalInMin)
                && status == slot.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, waterNeeded, retryCount, lastRetryTime, durationInMin, intervalInMin, status);
    }

}
