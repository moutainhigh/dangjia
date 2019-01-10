package com.dangjia.acg.dto.house;

import lombok.Data;

import java.util.List;

/**
 * 小区按首字母分类
 */
@Data
public class VillageClassifyDTO {
	private String initials;   //首字母
	private List<VillageDTO> villageDTOList;
	
}
