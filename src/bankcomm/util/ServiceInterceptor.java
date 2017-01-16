package bankcomm.util;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class ServiceInterceptor {
	
	private static final long serialVersionUID = -5873620616781916663L;
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String MULTIPART_CONTENT_TYPE = "multipart/form-data";	
	private static final String JSON_CONTENT_TYPE = "application/json";
//	private static final String LOG_CONFIG = "/WEB-INF/baas.log.properties";
//	private static final String LOG_HOME_NAME = "JUSTEP_LOG_HOME";
//	private static final String JUSTEP_HOME_NAME = "JUSTEP_HOME";
	
	public JSONObject getParams(HttpServletRequest request) throws Exception {
		String method = request.getMethod();
		if(isRequestMultipart(request) || (!METHOD_GET.equalsIgnoreCase(method)&&!isJson(request))){
			JSONObject params = new JSONObject();
			return params;
		}else if(METHOD_POST.equalsIgnoreCase(method)&&isJson(request)) return getParams(request.getInputStream());
		else{
			JSONObject params = new JSONObject();
			for (Object k : request.getParameterMap().keySet()) {
				String key = (String)k;
				params.put(key, request.getParameter(key));
			}
			return params;
		}
	}
	
	private static boolean isRequestMultipart(HttpServletRequest request) {
		return isRequestMultipart(getRequestContentType(request));
	}
	private static String getRequestContentType(HttpServletRequest request) {
		return request.getContentType();
	}
	private static boolean isRequestMultipart(String type) {
		return null != type && -1 < type.indexOf(MULTIPART_CONTENT_TYPE);
	}
	private static boolean isJson(String type) {
		return null!=type && -1<type.indexOf(JSON_CONTENT_TYPE);
	}
	
	private static boolean isJson(HttpServletRequest request) {
		return isJson(getRequestContentType(request));
	}
	private JSONObject getParams(ServletInputStream inputStream) throws Exception  {
		final int BUFFER_SIZE = 8 * 1024;
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int bLen = 0;
		while ((bLen = inputStream.read(buffer)) > 0) {
			baos.write(buffer, 0, bLen);
		}
		String bodyData = new String(baos.toByteArray(), "UTF-8");
		JSONObject jo = JSONObject.parseObject(bodyData);
		return jo;
	}
	
}
