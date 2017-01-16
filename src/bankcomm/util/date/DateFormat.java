package bankcomm.util.date;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateFormat extends SimpleDateFormat{
	/**
	 * 中国日期格式基类
	 * 时区默认为北京时间
	 */
	private static final long serialVersionUID = -3069856269190275981L;

	public static SimpleDateFormat  initFormat(String format){
		SimpleDateFormat f=new SimpleDateFormat(format);
		f.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));//设置时区为上海时区
		return f;
	}

}
