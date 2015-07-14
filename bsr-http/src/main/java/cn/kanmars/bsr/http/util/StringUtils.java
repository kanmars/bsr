package cn.kanmars.bsr.http.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

public class StringUtils {
	public static boolean isEmpty(String string) {
		if (string == null || string.trim().length() == 0)
			return true;
		else
			return false;
	}
	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}



	public static String initString(char ch, int length) {
		if (length < 0)
			return "";
		char chars[] = new char[length];
		for (int i = 0; i < length; i++)
			chars[i] = ch;
		return new String(chars);
	}

	public static String strLeftAlign(String str, int length) {
		if (str == null)
			return initString(' ', length);

		int len = str.length();
		if (length < len)
			return str.substring(0, length);
		else if (length == len)
			return str;
		else
			return str + initString(' ', length - len);
	}


	public static String listToString(List strList) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < strList.size(); i++) {
			if (i != 0)
				buf = buf.append(",");
			buf = buf.append((String) strList.get(i));
		}
		return buf.toString();
	}



	public static List stringToList(String str) {
		List list = new ArrayList();
		if (StringUtils.isEmpty(str))
			return list;
		int startPos = 0;
		int endPos = str.indexOf(',', startPos);
		while (endPos >= 0) {
			list.add(str.substring(startPos, endPos));
			startPos = endPos + 1;
			endPos = str.indexOf(',', startPos);
		}
		if (startPos < str.length())
			list.add(str.substring(startPos));
		return list;
	}

	public static boolean compareToStrings(String[] str1, String[] str2) {
		boolean ret = false;
		if (str1==null&&str2==null){
			ret = true;
			return  ret;
		}else if(str1==null||str2==null){
			return  ret;
		}
		int len = str1.length;
		if (len == str2.length) {
			for (int i = 0; i < len; i++) {
				if (str1[i].equalsIgnoreCase(str2[i])) {
					ret = true;
				}else{
					ret = false;
					break;
				}

			}
		}
		return ret;
	}

	public static Object[] concatArray(Object[] arr1, Object[] arr2) {
		if (arr1 == null)
			return arr2;
		if (arr2 == null)
			return arr1;

		List list = new ArrayList(arr1.length + arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			list.add(arr1[i]);
		}

		for (int i = 0; i < arr2.length; i++) {
			list.add(arr2[i]);
		}
		return list.toArray();
	}

	public static int byteToInt(byte[] bytes) {
		int reValue = 0;
		try {
			reValue = new Integer(new String(bytes));
		} catch (NumberFormatException e) {
			throw e;
		}

		return reValue;
	}

	public static byte[] intToByte (int value, int length) {
		byte[] bytes = new byte[length];
		if (value > 0) {
			String valueStr = String.valueOf(value);
			byte[] tmp = valueStr.getBytes();
			for (int i=0; i<length - tmp.length; i++) {
				valueStr = '0' + valueStr;
			}
			bytes = valueStr.getBytes();
		} else {
			bytes = "0000".getBytes();
		}
		return bytes;
	}

	public static String trim(String value) {
		if (null == value) {
			return "";
		}
		return value.trim();
	}

	public static String trimZero(String value) {
		if (null == value) {
			return "0";
		}
		long valueLong = Long.parseLong(value);
		String reStr = String.valueOf(valueLong);
		return reStr;
	}
	
	public static String fillString(String string, char filler, int totalLength, boolean atEnd) {
		if(string.length() >= totalLength){
			return string;
		}
		byte[] bytes = new byte[totalLength];
		int stringlength = string.length();
		if(!atEnd){
			//宸﹁ˉ0
			int c_space = totalLength - stringlength;
			System.arraycopy(string.getBytes(), 0, bytes, c_space, stringlength);
			for(int i = 0;i<c_space;i++){
				bytes[i]= (byte)filler;
			}
		}else{
			//鍙宠ˉ0
			System.arraycopy(string.getBytes(), 0, bytes,0 , stringlength);
			for(int i=stringlength;i<totalLength;i++){
				bytes[i]= (byte)filler;
			}
		}
		
		return new String(bytes);
	}

	
	public static boolean isNumber(String str) {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()){
        	return true;
        } else {
        	return false;
        }
    }

	public static Set<String> getSet(String configName){
		HashSet<String> result = new HashSet<String>();
		String value= configName;
		if(value!=null&&!value.trim().equals("")){
			String sarray[] = value.split(",");
			for(String str:sarray){
				result.add(str);
			}
		}
		return result;
	}

	public static String dateTransFormat( String p_str_format){
		String format = trim(p_str_format);
		format = format.replaceAll("-", "")
				.replaceAll(" ", "")
				.replaceAll(":", "");
		return format ;
	}
	
	public static String buildParam(Map<String,String> p_map){
		StringBuilder params = new StringBuilder("");
		Iterator it = p_map.keySet().iterator();
		while(it.hasNext()){
			String key = (String)it.next();
			String value = (String)p_map.get(key);
			if(!"".equals(key)&&!"".equals(value)){
				params.append("&"+key+"="+value);
			}
		}
		String paramSeq = params.substring(1);
		System.out.println(paramSeq);
		return paramSeq ;
	}

	public static String trimNull(String str) {
		return str == null ? "" : str;
	}
	
	
	
	public static String getRandomFixLenthString(int strLength) {  
        Random rm = new Random();  
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);  
      
        String fixLenthString = String.valueOf(pross);  
      
        return fixLenthString.substring(1, strLength + 1);  
    }  
	
	

	public static void main(String arg[]){
//		String[] str1 = {"1","2","3"};
//		String[] str2 = null;
//		System.out.println(compareToStrings(str1,str2));
//		System.out.println(strLeftAlign(null,3));
//		System.out.println(isNumber("4a"));
		System.out.println("["+fillString("123", ' ', 100000000, false)+"]");
	}
}
