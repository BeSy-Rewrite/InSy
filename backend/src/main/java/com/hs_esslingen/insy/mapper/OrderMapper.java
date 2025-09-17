package com.hs_esslingen.insy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.hs_esslingen.insy.dto.OrderResponseDTO;
import com.hs_esslingen.insy.model.CostCenter;
import com.hs_esslingen.insy.model.Order;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { ArticleMapper.class })
public interface OrderMapper {

    @Mapping(target = "articles", source = "articles")
    @Mapping(target = "costCenter", source = "costCenter", qualifiedByName = "mapCostCenterFromName")
    Order toEntity(OrderResponseDTO dto);

    @Mapping(target = "articles", source = "articles")
    @Mapping(target = "costCenter", source = "costCenter.description")
    OrderResponseDTO toDto(Order entity);

    @Named("mapCostCenterFromName")
    default CostCenter mapCostCenter(String costCenterName) {
        if (costCenterName == null)
            return null;
        return new CostCenter(costCenterName);
    }
}
