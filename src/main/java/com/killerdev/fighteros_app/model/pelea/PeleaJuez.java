package com.killerdev.fighteros_app.model.pelea;

import com.killerdev.fighteros_app.model.deportivo.Juez;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pelea_jueces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PeleaJuez {

    @EqualsAndHashCode.Include
    @EmbeddedId
    private PeleaJuezId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("peleaId")
    @JoinColumn(name = "pelea_id")
    private Pelea pelea;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("juezId")
    @JoinColumn(name = "juez_id")
    private Juez juez;
}
