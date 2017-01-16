package bankcomm.demo.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import bankcomm.demo.dao.Do.DemoBeen;


public interface DemoMapper {

	List<DemoBeen> query() ;	
	
}
