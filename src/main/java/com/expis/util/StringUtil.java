package com.expis.util;


import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/**
 * [UTIL]문자열 관련 Util Class
 */
public class StringUtil {

	/**
	 * 문자열을 널인지 검사하여 빈값으로 대체
	 */
	public static String checkNull(String str) {
		return str != null && !str.equals("") ? str : "";
	}

	public static String checkNull(String str, String reg) {
		return str != null && !str.equals("") ? str : reg;
	}


	/**
	 * 문자열 중 특정 문자열을 다른 문자열로 대체
	 */
	public static String replace(String original, String find, String replace) {

		String returnStr = new String("");

		if (original == null || find == null || replace == null || original.length() < 1 || find.length() < 1) {
			return original;
		}

		int index = -1;
		int fromIndex = 0;
		StringBuffer sb = new StringBuffer();
		int tempIndex;

		while ((tempIndex = original.indexOf(find, fromIndex)) >= 0) {
			index = tempIndex;
			sb.append(original.substring(fromIndex, index)).append(replace);
			fromIndex = index + find.length();
		}

		if (sb.length() < 1) {
			returnStr = original;
		} else {
			sb.append(original.substring(index + find.length()));
			returnStr = sb.toString();
		}

		return returnStr;
	}


	/**
	 * 자릿수에 맞게 공백문자를 제로문자(0)로 대체
	 * space2zero("1",3)   ===>    "1" -> "001"
	 */
	public static String space2zero(String str, int maxLen) {

		String result = "";

		int leftZeroCnt = maxLen - str.length();
		for (int i=0 ; i<leftZeroCnt ; i++) {
			result += "0";
		}
		result += str;

		return result;
	}

	public static String space2zero(int val, int maxLen) {
		return space2zero(val+"", maxLen);
	}


	/**
	 * 전체 파일 경로에서 파일명(확장자포함) 만 추출
	 */
	public static String getFileName(String filePath) {

		if (filePath == null || filePath.equals("")) {
			return "";
		}

		String fileName = "";
		String fileSlash = "/";
		filePath = replace(filePath, "\\", fileSlash);

		int idx = filePath.lastIndexOf(fileSlash);
		if (idx > -1 && idx < filePath.length()) {
			fileName = filePath.substring(idx+1, filePath.length());
		}

		fileName = StringUtil.checkNull(fileName);

		return fileName;
	}


	/**
	 * 파일 확장자 명 추출
	 */
	public static String getExtName(String fileName) {

		if (fileName == null || fileName.equals("")) {
			return "";
		}

		String ext = "";
		String[] arr = fileName.split(".");

		if (arr.length > 1) {
			ext = arr[(arr.length-1)];
		}

		return ext;
	}


	/**
	 * 파일 종류별(확장자) 업로드 가능 여부 판별
	 */
	public static boolean isUploadFile(String fileName) {

		boolean bResult = false;

		if (fileName == null || fileName.equals("")) {
			return true;
		}

		String extok = "|JSP|ASP|PHP|SQL|INC|BAT|SH|";
		String ext = getExtName(fileName);
		if (extok.indexOf("|"+ext+"|") > -1) {
			bResult = false;
		} else {
			bResult = true;
		}

		return bResult;
	}

	/**
	 * 스트링을 구분자(Delimeter)로 분리하여 스트링 배열로 리턴한다.
	 * 사용법 : String[] strList = ExpisLib.splitStr("TA, TB", "," );
	 */
	public static String[] splitStr(String paStr, String delim) {
		String[] rtStr = null;

		try {
			StringTokenizer st = new StringTokenizer(paStr, delim);
			String[] arrStr = new String[st.countTokens()];

			for (int i=0; i<arrStr.length; i++) {
				arrStr[i] = st.nextToken().trim();
			}
			rtStr = arrStr;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtStr;
	}

	/**
	 * 문자열이 숫자인지 검사하여 리턴
	 */
	public static boolean isCheckNumber(String str)
	{
		boolean rtBl = true;

		for(int i=0; i<str.length(); i++){
			if(str.charAt(i) >= '0' && str.charAt(i) <= '9'){
			}else{
				rtBl = false;
			}
		}

		return rtBl;
	}

	/**
	 * 문자열을 숫자(정수)로 변환
	 */
	public static int getInt(String str) {

		int rt = 0;
		if (str == null || str.equals("")) { return 0; }

		try {
			rt = Integer.parseInt(str);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return rt;
	}

	/**
	 * 경로 등의 문자열에서 마지막위치에 슬래쉬(/)가 있으면 제거
	 */
	public static String getRemoveSlash(String str) {

		if (str.lastIndexOf("/") == str.length()-1) {
			str = str.substring(0, str.length()-1);
		}
		return str;
	}

	public static String formattedDate(String paDate, String formatStr) {

		SimpleDateFormat toSdf = new SimpleDateFormat(formatStr);
		return toSdf.format(paDate);
	}
}
