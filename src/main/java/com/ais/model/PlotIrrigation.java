package com.ais.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "plot_irrigation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlotIrrigation {

	@Id
	private String id;

	@OneToOne
	@JoinColumn(name = "plot_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private Plot plot;

	@Column(name = "duration_in_min")
	private Integer durationInMin;

	@Column(name = "interval_in_min")
	private Integer intervalInMin;

	@Column(name = "water_needed_in_mm")
	private Integer waterNeededInMm;

	@Column(name = "next_irrigation_time")
	private Long nextIrrigationTime;

	@Enumerated(EnumType.STRING)
	private IrrigationStatus status = IrrigationStatus.IDLE;

	@Column(name = "created_time")
	private Long createdTime;

	@Column(name = "updated_time")
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
		final PlotIrrigation plotIrrigationEntity = (PlotIrrigation) obj;
		return plotIrrigationEntity.id == this.id;
	}

}