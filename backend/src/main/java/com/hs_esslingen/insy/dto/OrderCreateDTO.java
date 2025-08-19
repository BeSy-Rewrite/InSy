package com.hs_esslingen.insy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDTO {

    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("order_created_date")
    private LocalDateTime orderCreatedDate;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("cost_center")
    @JsonAlias("cost_center_name")
    private String costCenter;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("order_quote_price")
    private BigDecimal orderQuotePrice;
    private List<ItemCreateDTO> items;
}
