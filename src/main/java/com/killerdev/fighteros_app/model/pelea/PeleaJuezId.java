package com.killerdev.fighteros_app.model.pelea;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PeleaJuezId implements Serializable {

    @Column(name = "pelea_id")
    private UUID peleaId;

    @Column(name = "juez_id")
    private UUID juezId;
}
