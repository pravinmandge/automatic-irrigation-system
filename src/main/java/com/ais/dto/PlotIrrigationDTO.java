package com.ais.dto;

import com.ais.model.IrrigationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlotIrrigationDTO {

	private String id;

	private String plotId;

	private Integer durationInMin;

	private Integer intervalInMin;

	private Integer waterNeededInMm;

	private Long nextIrrigationTime;

	@Enumerated(EnumType.STRING)
	private IrrigationStatus status;

	private Long createdTime;

	private Long updatedTime;

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PlotIrrigationDTO plotIrrigationEntity = (PlotIrrigationDTO) obj;
		return plotIrrigationEntity.id == this.id;
	}

}