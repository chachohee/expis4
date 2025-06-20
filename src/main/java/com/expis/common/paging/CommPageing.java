package com.expis.common.paging;

import lombok.Data;

/**
 * [공통]페이징 관련 클래스
 *
 * @FileName			: CommPaging.java
 * @Author				: NAM. D.S
 * @Date				: 2016.01.28
 */

@Data
public class CommPageing {

	int totalPage;
	int startPage;
	int endPage;
	int startRow;
	int endRow;
	int lastPage;
	String resultString;

	public CommPageing(PageDTO page, int recordSize, int blockSize, String method) {
		//nowPage 현재페이지
		//totalCount 총 결과값 갯수
		//recordSize 한페이지에 보여줄 row갯수
		//blockSize [1][2] <--이거 갯수
		
		int lastPage = 0;
		
		
		// 총블럭 수 구하기
		this.totalPage = (int)Math.ceil((double)page.getTotalCount() / (double)recordSize);
		
		if(this.totalPage < page.getNowPage()) {
			this.lastPage = this.totalPage;
			page.setNowPage(this.totalPage);
		} else  {
			this.lastPage = page.getNowPage();
		}
		
		//결과값을 위한 범위값 구하기
		this.startRow = (int) (page.getNowPage() - 1) * recordSize + 1;
		this.endRow = (int) this.startRow + recordSize - 1;
				
		
		//[1][2] <-- 이거 시작과 끝 구하기
		this.startPage = (int)((page.getNowPage() -1) / blockSize) * blockSize + 1;
		this.endPage = (int)this.startPage + blockSize - 1;

		//마지막 페이지 번호 보정
		if(this.endPage > this.totalPage) {
			this.endPage = totalPage;
		}
		
		if(totalPage == 0) {
			totalPage = 1;
		}
		
		StringBuilder result = new StringBuilder();
		
		//이전버튼 생성
		if(page.getNowPage() > 1) {
			result.append("<a href='javascript:" + method +"(");
			result.append("1");
			result.append(")' class='pre_end' title='FIRST'>");
			result.append("FIRST");
			result.append("</a>");
			
			result.append("<a href='javascript:" + method + "(");
			result.append(page.getNowPage()-1);
			result.append(")' class='pre' title='PREV'>");
			result.append("PREV");
			result.append("</a>");
		} else {
			result.append("<a href='javascript:void(0);' title='FIRST' class='pre_end preEnNon'>");
			result.append("FIRST");
			result.append("</a>");
			
			result.append("<a href='javascript:void(0);' title='PREV' class='pre preNon'>");
			result.append("PREV");
			result.append("</a>");
		}
		
		if(endPage == 0) {
			result.append("<strong>1</strong>");
		} else {
			for(int i=startPage; i<=endPage; i++) {
				if(page.getNowPage() == i) {
					result.append("<strong>");
					result.append(i);
					result.append("</strong>");
				} else {
					result.append("<a href='javascript:" + method + "(");
					result.append(i);
					result.append(")' title='" + i + "'> ");
					result.append(i);
					result.append(" </a>");
				}
			}
		}
		
		if(page.getNowPage() < totalPage && page.getTotalCount() != 0) {
			result.append("<a href='javascript:" + method + "(");
			result.append(page.getNowPage() + 1);
			result.append(")' class='next' title='NEXT' >");
			result.append("NEXT");
			result.append("</a>");

			//끝 페이지
			result.append("<a href='javascript:" + method + "(");
			result.append(totalPage);
			result.append(")' class='next_end' title='LAST'>");
			result.append("LAST");
			result.append("</a>");
		} else {
			result.append("<a href='javascript:void(0);' class='next nextNon' title='NEXT'>");
			result.append("NEXT");
			result.append("</a>");

			//끝 페이지
			result.append("<a href='javascript:void(0);' class='next_end nextEnNon' title='LAST'>");
			result.append("LAST");
			result.append("</a>");
		}
		
		resultString = result.toString();
		
	}

	public String resultString() {
		return resultString;
	}
	
}
