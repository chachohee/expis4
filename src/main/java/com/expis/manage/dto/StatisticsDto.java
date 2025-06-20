package com.expis.manage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class StatisticsDto {

	private String userId;
	private String year;
	private String month;
	private String day;
	private String date;
	private String timeVal;
	private String searchType;
	private String selectId;
	private String selectName;
	private String type;
	private String yearMonth;
	private String userName;
	private String toKey;
	private String toName;
	private String tocoId;
	private String tocoName;
	private String connDate;
	private String disconnDate;
	private String connIp;
	private String xyName;
	private int connCount;
	private int userCount;
	
	private String createUserId;
	private Date createDate;
	private String modifyUserId;
	private Date modifyDate;

	@JsonProperty("rNum")
	private int rNum;
	@JsonProperty("tNum")
	private int tNum;
	private int startRow;
	private int endRow;
	private int recordCnt;
	private int nowPage;
	private String dbType;
	
	private String startDate;
	private String endDate;
	
	private String searchCate;
	private String searchValue;
	
	private String dateForm;
	private String yearDate;
	private String monthDate;
	private String dayDate;

}
