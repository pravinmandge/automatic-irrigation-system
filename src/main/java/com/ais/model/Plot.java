package com.ais.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.id.UUIDGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name= "plot")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plot {

    @Id
    private String id;

    @Column(name= "name")
    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "plot", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Slot> slots = new LinkedHashSet<>();

    @Column(name= "sensor_url")
    private String sensorUrl;

    @JsonBackReference
    @OneToOne(mappedBy = "plot")
    @NotFound(action = NotFoundAction.IGNORE)
    private PlotIrrigation plotIrrigation;

    public void addSlot(Slot slot){
        slots.add(slot);
        slot.setPlot(this);
    }
}
