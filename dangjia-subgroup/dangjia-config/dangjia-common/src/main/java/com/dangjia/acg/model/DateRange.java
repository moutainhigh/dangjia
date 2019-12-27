package com.dangjia.acg.model;

import lombok.Data;

import java.util.Date;

@Data
public class DateRange {
	private Date start;
	private Date end;

	public DateRange(Date start, Date end) {
		this.start = start;
		this.end = end;
	}
}
	

