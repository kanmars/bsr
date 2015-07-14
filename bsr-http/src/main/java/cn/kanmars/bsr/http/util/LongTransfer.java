package cn.kanmars.bsr.http.util;

import java.util.Date;

public class LongTransfer {
	/**char 62 ,
	 * 用来处理62进制
	 */
	private static char[] info = {
		'0','1','2','3','4','5','6','7','8','9',
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
	};
	
	private static int jz_ = 62;
	
	public static void main(String[] args) {
 		System.out.println(new Date(100,11,31));
		//long long的最大值：9223372036854775807
		//long long的最小值：-9223372036854775808
//		System.out.println(getStrFrom(9223372036854775807l));//hezMo7
//		System.out.println(getLongFrom("aZl8N0y58M7"));
		
		for(int i=0;i<10;i++){
			long date = new Date(100,11,31).getTime();//获取1900+300 = 2200年的时间
			date = date/1000;
			String str = getStrFrom(date);
			long result = getLongFrom(str);
			System.out.print(date+"    ");
			System.out.print(str+"    ");
			System.out.print(result+"    ");
			System.out.println("  "+(date==result?"true":"false"));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static String getStrFrom(long l){
		StringBuffer sb = new StringBuffer();
		if(l==0)return "0";
		int jz = jz_;
		while(l/jz >= 0){
			if(l==0){
				//sb.insert(0, info[0]);
				break;
			}
			char c = info[(int)(l%jz)];
			//System.out.println((int)(l%62));
			sb.insert(0, c);
			l=l/jz;
		}
		return sb.toString();
	}
	public static long getLongFrom(String str){
		long result = 0;
		int jz = jz_;
		int strlength = str.length();
		char[] cc = new char[strlength];
		str.getChars(0, strlength, cc, 0);
		for(int i=0,j=strlength;i<j;i++ ){
			int num = -1;
			char c = cc[i];
			if('0'<=c&&c<='9'){
				num = c-'0';
			}
			if('a'<=c&&c<='z'){
				num = c-'a'+10;
			}
			if('A'<=c&&c<='Z'){
				num = c-'A'+36;
			}
			//System.out.println(num);
			result = result*jz + num;
		}
		return result;
	}
}
