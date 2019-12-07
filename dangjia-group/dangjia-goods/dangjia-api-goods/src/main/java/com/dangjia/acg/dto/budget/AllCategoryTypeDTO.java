package com.dangjia.acg.dto.budget;

import lombok.Data;

import java.util.List;
@Data
public class AllCategoryTypeDTO {
    private Double totalPrice;
    List<AllCategoryDTO> list;
}
