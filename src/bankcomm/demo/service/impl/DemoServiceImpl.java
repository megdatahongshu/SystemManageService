package bankcomm.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bankcomm.demo.dao.DemoDao;
import bankcomm.demo.dao.Do.DemoBeen;
import bankcomm.demo.service.DemoService;

@Service
public class DemoServiceImpl implements DemoService{

	@Autowired
	public DemoDao dao;
	
	@Override
	public List<DemoBeen> query() {
		System.out.println("zheshi serverice");
		return dao.query();
	}
	
	
}
