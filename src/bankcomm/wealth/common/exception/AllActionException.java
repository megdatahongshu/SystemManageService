package bankcomm.wealth.common.exception;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * AOP异常处理
 * @author jiangyzh
 *
 */
@Aspect
public class AllActionException {
	private static Logger log = LoggerFactory.getLogger(AllActionException.class);
	
	
	/**
	 * 拦截Action
	 * @param jp
	 * @param context
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* bankcomm..*.action..*(..))") 
	public Object around(ProceedingJoinPoint jp, HttpServletRequest req)
			throws Throwable {
		log.info("-----------------进入Action异常监听AllActionException---------");
		Object result = new Object();
		try {
			result = jp.proceed();
			return result;
		}catch (Exception e) {
//			String dt = DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss");
//			AppUser user = new AppUser();
//			if(context.getUser() != null){
//				user = context.getUser();
//			}
//			StringWriter sw = new StringWriter();
//			sw.write("系统ERROR异常\n");
//			sw.write("当前操作用户："+user.getUserCode()+user.getUserName()+" ，机构："+user.getOrgCode()+user.getOrgName()+"\n");
//			sw.write("当前操作功能："+context.getProcessId()+"\n");
//			sw.write("当前操作时间："+dt+"\n");
//			sw.write("异常原因："+e.getMessage()+"\n");
//			sw.write("请求信息："+context.getDataMap()+"\n");
//			sw.write("异常详情：\n");
//			e.printStackTrace(new PrintWriter(sw,true));
//			log.error(sw.toString());
//			Response response = new Response();
//			response.setSuccess(false);
//			response.setMessageType("3");
//			response.setMessage("系統異常，請下載錯誤信息并聯繫管理員！當前操作時間："+dt);
//			response.setErrorMessage(sw.toString());
//			context.setData("response", response);
			return new Object();
		}
	}
}
