package com.zzg.mybatis.generator.util;

/**
 * Created by Owen on 6/18/16.
 */
public class MyStringUtils {

    /**
     *
     * convert string from slash style to camel style, such as my_course will convert to MyCourse
     *
     * @param str
     * @return
     */
    public static String dbStringToCamelStyle(String str) {
        if (str != null) {
            str = str.toLowerCase();
            StringBuilder sb = new StringBuilder();
            //sb.append(String.valueOf(str.charAt(0)).toUpperCase());
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c != '_') {
                    sb.append(c);
                } else {
                    if (i + 1 < str.length()) {
                        sb.append(String.valueOf(str.charAt(i + 1)).toUpperCase());
                        i++;
                    }
                }
            }
            return sb.toString();
        }
        return null;
    }
    public static String getEnglishWord(String str) {
    	if (str!=null) {
    		 StringBuilder sb = new StringBuilder();
    		 for (int i = 0; i < str.length(); i++) {
                 char c = str.charAt(i);
                 if (((c>='a'&&c<='z')||(c>='A'&&c<='Z'))) {
                	 sb.append(c);
				}
             }
             return sb.toString();
		}
		return null;
    }

	public static void main(String[] args) {
		String string = "你好世界233fss0jjsj";
		String englishWord = getEnglishWord(string);
		System.out.println(englishWord);
	}
}
