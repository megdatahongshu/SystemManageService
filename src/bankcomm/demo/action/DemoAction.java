package bankcomm.demo.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bankcomm.demo.dao.Do.DemoBeen;
import bankcomm.demo.service.DemoService;
import bankcomm.util.ServiceInterceptor;
import bankcomm.util.data.Table;
import bankcomm.util.data.Transform;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/bankcomm/demo/action")
public class DemoAction {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoAction.class);

	@Autowired
	public DemoService demoService;

	@Autowired
	public ServiceInterceptor serviceInterceptor;
	
	@RequestMapping("queryData")
	@ResponseBody
	public JSONObject queryData(HttpServletRequest req) throws Exception{
		JSONObject js = new JSONObject();
//		
//		List<TaskflowlogDo> list = demoService.queryData();
//		Table t = Transform.createTableByArrayList(list);
//		t.setIDColumn("id");
//		t.setTotal(30);
//		logger.debug(""+Transform.tableToJson(t));
//		js.put("data",Transform.tableToJson(t));
//		
		
		System.out.println("woshiyige haoren");
		List<DemoBeen> userList=demoService.query();
		System.out.println("++++++++++++++++++++++"+userList.get(0).getUser_Name_Cn()+"1231213213221213"+userList.get(0).getUser_Id());
		Table t = Transform.createTableByArrayList(userList);
		js.put("data",Transform.tableToJson(t));
		return js;
	}
	

}
