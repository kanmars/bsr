package cn.kanmars.bsr.http.util;

public class ByteUtils {
    public static int byteIndexOf(byte[] searched, byte[] find, int start)  
    {  
        boolean matched = false;  
        int end = find.length - 1;  
        int skip = 0;  
        for (int index = start; index <= searched.length - find.length; ++index)  
        {  
            matched = true;  
            if (find[0] != searched[index] || find[end] != searched[index + end]) continue;
            else skip++;  
            if (end > 10)  
                if (find[skip] != searched[index + skip] || find[end - skip] != searched[index + end - skip])  
                    continue;  
                else skip++;  
            for (int subIndex = skip; subIndex < find.length - skip; ++subIndex)  
            {  
                if (find[subIndex] != searched[index + subIndex])  
                {  
                    matched = false;  
                    break;  
                }  
            }  
            if (matched)  
            {  
                return index;  
            }  
        }  
        return -1;  
    }
    
    public static void main(String[] args) {
		byte[] bs = new byte[]{1,2,3,4,5,6,7,8,9,10};
		
		System.out.println(byteIndexOf(bs,new byte[]{1,2},0));
		System.out.println(byteIndexOf(bs,new byte[]{3,4},1));
		System.out.println(byteIndexOf(bs,new byte[]{6,7},1));
		System.out.println(byteIndexOf(bs,new byte[]{9,10},1));
	}
}
