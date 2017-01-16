package bankcomm.demo.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import bankcomm.demo.dao.DemoDao;
import bankcomm.demo.dao.Do.DemoBeen;
import bankcomm.demo.dao.mapper.DemoMapper;

@Repository
public class DemoDaoImpl implements DemoDao{

	
	@Autowired
	public DemoMapper mapper;

	@Override
	public List<DemoBeen> query() {
		// TODO 自动生成的方法存根
		System.out.println("zheshi dao");
		return mapper.query();
	}
	
	
}
