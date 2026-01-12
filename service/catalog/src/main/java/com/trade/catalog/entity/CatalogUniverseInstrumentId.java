package com.trade.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CatalogUniverseInstrumentId implements Serializable {
    private static final long serialVersionUID = 6555109051194031151L;

    @Size(max = 36)
    @NotNull
    @Column(name = "universe_id", nullable = false, length = 36)
    private String universeId;

    @Size(max = 36)
    @NotNull
    @Column(name = "instrument_id", nullable = false, length = 36)
    private String instrumentId;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CatalogUniverseInstrumentId entity = (CatalogUniverseInstrumentId) o;
        return Objects.equals(this.instrumentId, entity.instrumentId) &&
                Objects.equals(this.universeId, entity.universeId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(instrumentId, universeId);
    }

}