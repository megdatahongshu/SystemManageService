package bankcomm.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import bankcomm.util.date.DateTool;

public class KeyTool {
	public static String creatUuidKey(){
		return Math.abs(UUID.randomUUID().getLeastSignificantBits())+"";
	}
	public static String creatTimeKey(){
		return DateTool.getFormatDate("yyyyMMddHHmmssSSS", new Date());
	}
	public static String creatVersionNo(){
		return DateTool.getFormatDate("yyyy.MM.dd.HH.mm", new Date());
	}
	public static Long creatLongKey() {
		String key = "";
		String s = getRandomNumber();
		key = new Date().getTime() + s;
		return new Long(key);
	}
	public static String creatKey() {
		String key = "";
		String s = getRandomNumber();
		key = new Date().getTime() + s;
		return key;
	}
	private static String getRandomNumber() {
		int j = 6;
		long[] random = new long[j];
		for (int i = 0; i < j; i++) {
			random[i] = Math.round(Math.floor((Math.random() * 10)));
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < random.length; i++) {
			String temp = Long.toString(random[i]);
			sb.append(temp);
		}
		return sb.toString();
	}
	
	/**
	 * 生成指定位数随机字符
	 * @param length
	 * @return
	 */
	public static synchronized String createRandomWords(int length){
		 int orginalCharCount = 34;
		//"A-Z"(不包括O,I),"0-9"
		 char[] originalChar = new char[]{'8', 'A', '0', 'B', '9', 'C', 'D', 'E', '3', 'F', 'G', 'H',
				'J', 'K', 'L', '4', 'M', '7', 'N', 'P', '2', 'Q', 'R', '6', 'S', 'T', '5', 'U', 'V', 'W', 'X', 'Y',
				'Z', '1'};
		 Random random = new Random(); 
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < length; i++) { 
				sb.append(originalChar[random.nextInt(orginalCharCount)]);
			}
			return sb.toString();
	}
	/**
	 * 生成指定位数,指定数量且不重复随机字符
	 * @param length 随机数字符长度
	 * @param number 随机数总量
	 * @return 随机数列表
	 */
	public static synchronized List createRandomWords(int length,int number){
		//System.out.println("Random create start......");
		//System.out.println("Random length :"+length);
		//System.out.println("Random number:"+number);
		Date d1=new Date();
		List  list=new ArrayList();
		Map tempMap=new HashMap();//用于存放已经生成好的随机数
		String temp="";
		for(int i=0;i<number;i++){
			while(1==1){
				temp=KeyTool.createRandomWords(length);
				//如果不存在重复数据,则放入随机数列表
				if(tempMap.get(temp)==null){
					tempMap.put(temp, temp);
					list.add(temp);
					break;
				}
				else{
					//System.out.println("Random:"+temp+" is exist,now recreate one!");
				}
			}
		}
		Date d2=new Date();
		long time=d2.getTime()-d1.getTime();
		//System.out.println("Random created success, use time:"+time+" ms");
		return list;
	}
	
	public static void main(String[] args) {
		String key=KeyTool.creatUuidKey();
		//System.out.println("key:"+key.length());
	}
	
	

}
