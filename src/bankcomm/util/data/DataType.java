package bankcomm.util.data;

import java.sql.Types;   

import org.apache.commons.lang.StringUtils;

public enum DataType {
	STRING("String"), INTEGER("Integer"), LONG("Long"), FLOAT("Float"), DOUBLE("Double"), DECIMAL("Decimal"), BOOLEAN("Boolean"), DATE("Date"), TIME("Time"), DATETIME("DateTime"), OBJECT("Object"), LIST("List");
	
	private String value;
	private DataType(String type) {
		this.value = type;
	}
	
	public static DataType parse(String str) {
		return StringUtils.isEmpty(str) ? DataType.STRING : DataType.valueOf(str.toUpperCase()); 
	}

	@Override
	public String toString() {
		return value;
	}
	
	public static DataType parsePropertyType(String columntype) {
		//TODO 类型
		switch (columntype) {
		case "byte":
		case "char":
		case "java.util.String":
		case "java.lang.Object":
		case "java.util.Map":
		case "java.util.List":
			return STRING;
		case "short":
		case "int":
			return INTEGER;
		case "long":
			return LONG;
		case "double":
			return DOUBLE;
		case "java.util.Date":
			return DATETIME;
		default:
			return STRING;
		}
	}
}

