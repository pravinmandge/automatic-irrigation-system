package com.ais.dto;

import com.ais.model.SlotStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotDTO {
    private String id;
    @NotNull
    private Integer waterNeeded;
    private SlotStatus status = SlotStatus.PENDING;
    @NotBlank
    private String plotId;
    private Integer retryCount = 0;
    private Long lastRetryTime = System.currentTimeMillis();
    @NotNull
    private Integer durationInMin;
    @NotNull
    private Integer intervalInMin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotDTO slot = (SlotDTO) o;
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
