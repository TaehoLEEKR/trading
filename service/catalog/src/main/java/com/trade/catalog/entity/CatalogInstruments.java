package com.trade.catalog.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * catalog_instruments
 */
@Data
@Builder
public class CatalogInstruments{
    private String instrumentId;

    private String market;

    private String exchange;

    private String symbol;

    private String name;

    private String currency;

    private Integer isActive;

//    private String createdAt;

    private String updatedAt;
}