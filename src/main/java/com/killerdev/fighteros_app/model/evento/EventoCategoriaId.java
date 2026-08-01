package com.killerdev.fighteros_app.model.evento;

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
public class EventoCategoriaId implements Serializable {

    @Column(name = "evento_id")
    private UUID eventoId;

    @Column(name = "categoria_id")
    private UUID categoriaId;
}
