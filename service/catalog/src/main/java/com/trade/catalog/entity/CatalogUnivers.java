package com.trade.catalog.entity;

import com.trade.common.model.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "catalog_universes", schema = "trading")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogUnivers extends BaseTime {
    @Id
    @Size(max = 36)
    @Column(name = "universe_id", nullable = false, length = 36)
    private String universeId;

    @Size(max = 36)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Size(max = 20)
    @NotNull
    @Column(name = "market", nullable = false, length = 20)
    private String market;

    @Size(max = 120)
    @NotNull
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

}