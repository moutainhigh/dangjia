package com.dangjia.acg.dto.activity;

import lombok.Data;

import java.util.List;

@Data
public class RedPageResult {
	
	private String businessOrderNumber;
	private List<ActivityRedPackRecordDTO> redPacetResultList;
	private List<ActivityRedPackRecordDTO> redPacetNotList;

}
