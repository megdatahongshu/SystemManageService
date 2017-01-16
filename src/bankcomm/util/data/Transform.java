package bankcomm.util.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;



/**
 * 将bean转换为Table，用于X5data数据接收
 * @author dreamkei
 *
 */
@Service("utilTransform")
public class Transform {

	private static final Logger logger = LoggerFactory.getLogger(Transform.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	private static final SimpleDateFormat TIME_FORMAT2 = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	private static final String USER_DATA = "userdata";
	private static final String TABLE_NAME = "tableName";
	private static final String ID_COLUMN_NAME = "idColumnName";
	private static final String ID_COLUMN_TYPE = "idColumnType";
	private static final String COLUMN_NAMES = "relationAlias";
	private static final String COLUMN_TYPES = "relationTypes";

	private static final String TABLE_TOTAL = "sys.count";
	private static final String TABLE_OFFSET = "sys.offset";

	private static final String ROWS = "rows";
	private static final String ROW_STATE = "recordState";
	private static final String ROW_ID = "id";
	private static final String ROW_VALUE = "value";
	private static final String ROW_OLD_VALUE = "originalValue";
	private static final String ROW_VALUE_CHANGED = "changed";
	private static final Integer ROW_VALUE_ISCHANGED = 1;
	
	
	/**
	 * 通过java反射生成Table
	 * @param list List<Class>
	 * @return
	 * @throws SQLException
	 */
	public static Table createTableByArrayList(List list) throws Exception {
		Map<String, DataType> tableColumns = new LinkedHashMap<String, DataType>();
		if(list == null || list.size() == 0){
			return new Table(tableColumns);
		}
		Object obj = list.get(0);
		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs) {
			//忽略static成员和final成员  
            if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {  
                continue;  
            } 
			f.setAccessible(true);//Class类中的成员变量为private,故必须进行此操作
//			logger.debug(f.getName().toUpperCase()+"--------"+f.getType().getName()+"-----"+f.get(obj));
			DataType type = DataType.parsePropertyType(f.getType().getName());
			tableColumns.put(f.getName(), type);
		}

		Table table = new Table(tableColumns);
		
		loadRowsFromArrayList(table,list);
		return table;
	}
	
	/**
	 * 通过java反射生成Table
	 * @param list List<Class>
	 * @return
	 * @throws SQLException
	 */
	public static Table createTableByObject(Object obj) throws Exception {
		Map<String, DataType> tableColumns = new LinkedHashMap<String, DataType>();
		if(obj == null){
			return new Table(tableColumns);
		}
		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs) {
			//忽略static成员和final成员  
            if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {  
                continue;  
            } 
			f.setAccessible(true);//Class类中的成员变量为private,故必须进行此操作
//			logger.debug(f.getName().toUpperCase()+"--------"+f.getType().getName()+"-----"+f.get(obj));
			DataType type = DataType.parsePropertyType(f.getType().getName());
			tableColumns.put(f.getName(), type);
		}
		Table table = new Table(tableColumns);
		loadRowsFromObject(table,obj);
		return table;
	}
	
	/**
	 * 转换ResultSet行数据到Table
	 * @param table
	 * @param rs ResultSet
	 * @param count 行数，如果为null或小于零，则转换所有行
	 * @throws SQLException
	 */
	
	private static void loadRowsFromArrayList(Table table, List list) throws Exception {
		for (Object object : list) {
			table.appendRow(arrayListToRow(table,object,RowState.NONE));
		}
	}
	
	/**
	 * 转换ResultSet行数据到Table
	 * @param table
	 * @param rs ResultSet
	 * @param count 行数，如果为null或小于零，则转换所有行
	 * @throws SQLException
	 */
	private static void loadRowsFromObject(Table table, Object obj) throws Exception {
		// 构造一个列名的映射，用于忽略列名大小写敏感
		Map<String, String> columnNameMap = new HashMap<String, String>();
		for (String column : table.getColumnNames()) {
			columnNameMap.put(column.toUpperCase(), column);
		}
		Map<String, DataType> columns = new HashMap<String, DataType>();
		Map<String, ColumnValue> values = new HashMap<String, ColumnValue>();
		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs) {
			//忽略static成员和final成员  
            if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {  
                continue;  
            } 
			f.setAccessible(true);//Class类中的成员变量为private,故必须进行此操作
			String name = f.getName();
			if (columnNameMap.containsKey(name.toUpperCase())) {
				name = columnNameMap.get(name.toUpperCase());
				DataType type = table.getColumnType(name);
				columns.put(name, type);
			}
			values.put(name, new ColumnValue(f.get(obj)));
		}
		Row row = new Row(values, RowState.NONE);
		table.appendRow(row);
	}
	
	private static Row arrayListToRow(Table table, Object objData, RowState state) throws Exception {
		Map<String, ColumnValue> values = new HashMap<String, ColumnValue>();
		Field[] fs = objData.getClass().getDeclaredFields();
		for (Field f : fs) {
			//忽略static成员和final成员  
            if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {  
                continue;  
            } 
			f.setAccessible(true);//Class类中的成员变量为private,故必须进行此操作
			String name = f.getName();
//			logger.debug(f.getName().toUpperCase()+"--------"+f.getType().getName()+"-----"+f.get(objData));
			values.put(name, new ColumnValue(f.get(objData)));
		}
		
//		for (String name : columns.keySet()) {
//			DataType type = columns.get(name);
//			Object value = null;
//			switch(type) {
//			case STRING:
//				value = rs.getString(name);
//				break;
//			case INTEGER:
//				value = rs.getObject(name) == null ? null : rs.getInt(name);
//				break;
//			case LONG:
//				value = rs.getObject(name) == null ? null : rs.getLong(name);
//				break;
//			case FLOAT:
//				value = rs.getObject(name) == null ? null : rs.getFloat(name);
//				break;
//			case DOUBLE:
//				value = rs.getObject(name) == null ? null : rs.getDouble(name);
//				break;
//			case DECIMAL:
//				value = rs.getBigDecimal(name);
//				break;
//			case BOOLEAN:
//				value = rs.getBoolean(name);
//				break;
//			case DATE:
//				value = rs.getDate(name);
//				break;
//			case TIME:
//				value = rs.getTime(name);
//				break;
//			case DATETIME:
//				value = rs.getTimestamp(name);
//				break;
//			default:
//				value = rs.getObject(name);
//			}
//			values.put(name, new ColumnValue(value));
//		}
		return new Row(values, state);
	};
	
	
	/**
	 * 将Table转换为.w中data可以加载的JSON数据结构
	 * @param table
	 * @return
	 */
	public static JSONObject tableToJson(Table table) {
		return tableToJson(table, null);
	}
	
	
	public static JSONObject tableToTreeJson(Table table, String parentColumn) {
		if (parentColumn == null || table.getIDColumn() == null) {
			throw new RuntimeException("转换树形结构必须指定Table的idColumn属性和parentColumn参数");
		}
		return tableToJson(table, parentColumn);
	}
	
	
	private static JSONObject tableToJson(Table table, String parentColumn) {
		JSONObject jsTable = new JSONObject();
		jsTable.put("@type", "table");

		JSONObject jsUserData = new JSONObject();
		jsUserData.put(TABLE_NAME, table.getTableName());
		jsUserData.put(TABLE_TOTAL, table.getTotal());
		jsUserData.put(TABLE_OFFSET, table.getOffset());
		
		String idColumn = table.getIDColumn();
		if (idColumn != null) {
			jsUserData.put(ID_COLUMN_NAME, idColumn);
			jsUserData.put(ID_COLUMN_TYPE, table.getColumnType(idColumn).toString());
		}
		jsUserData.put(COLUMN_NAMES, arrayJoin(table.getColumnNames().toArray(), "%s", ","));
		jsUserData.put(COLUMN_TYPES, arrayJoin(table.getColumnTypes().toArray(), "%s", ","));
		jsTable.put(USER_DATA, jsUserData);

		JSONArray jsRows = new JSONArray();
		if (parentColumn == null) {
			for (Row row : table.getRows()) {
				JSONObject jsRow = rowToJson(table, row);
				jsRows.add(jsRow);
			}
		} else {
			Map<Object, JSONObject> parentMap = new HashMap<Object, JSONObject>();
			Map<Object, List<JSONObject>> childrenMap = new HashMap<Object, List<JSONObject>>();
			for (Row row : table.getRows()) {
				JSONObject jsRow = rowToJson(table, row); 
				
				Object idValue = row.getValue(idColumn);
				Object parentValue = row.getValue(parentColumn);
				
				// find parent
				if (parentMap.containsKey(parentValue)) {
					JSONObject parentRow = parentMap.get(parentValue);
					addJsonRowChild(parentRow, jsRow);
				} else {
					jsRows.add(jsRow);
				}
				// find children
				if (childrenMap.containsKey(idValue)) {
					List<JSONObject> children = childrenMap.get(idValue);
					for (JSONObject childRow : children) {
						addJsonRowChild(jsRow, childRow);
						jsRows.remove(childRow);
					}
				}
				parentMap.put(idValue, jsRow);
				if (parentValue != null) {
					if (!childrenMap.containsKey(parentValue)) {
						childrenMap.put(parentValue, new ArrayList<JSONObject>());
					}
					childrenMap.get(parentValue).add(jsRow);
				}
			}
		}
		jsTable.put(ROWS, jsRows);

		return jsTable;
	}
	
	
	/**
	 * 将一个数组连接为格式化字符串
	 * @param objects
	 * @param format 对数组元素的格式化，例如：'%s'为每个数组元素增加单引号、(%s = ?)将每个数组元素格式化为括号中等于问号
	 * @param separator 分隔符，例如：,、OR、AND
	 * @return
	 */
	private static String arrayJoin(Object[] objects, String format, String separator) {
		StringBuffer buf = new StringBuffer();
		for (Object o : objects) {
			if (buf.length() > 0) {
				buf.append(separator);
			}
			buf.append(String.format(format, o.toString()));
		}
		return buf.toString();
	}
	
	private static JSONObject rowToJson(Table table, Row row) {
		JSONObject jsRow = new JSONObject();

		JSONObject jsUserData = new JSONObject();
		jsUserData.put(ROW_STATE, row.getState().toString());
		
		String idColumn = table.getIDColumn();
		if (idColumn != null) {
			jsUserData.put(ROW_ID, columnValueToJson(row.getColumnValue(idColumn), table.getColumnType(idColumn)));
		}

		jsRow.put(USER_DATA, jsUserData);

		for (String name : table.getColumnNames()) {
			jsRow.put(name, columnValueToJson(row.getColumnValue(name), table.getColumnType(name)));
		}

		return jsRow;
	}
	
	private static void addJsonRowChild(JSONObject parent, JSONObject child) {
		if (!parent.containsKey(ROWS)) {
			parent.put(ROWS, new JSONArray());
		}
		parent.getJSONArray(ROWS).add(child);
	}
	
	private static JSONObject columnValueToJson(ColumnValue value, DataType type) {
		JSONObject jsColumnValue = new JSONObject();
		if (value != null) {
			jsColumnValue.put(ROW_VALUE, valueToJson(value.getValue(), type));
			if (value.isChanged()) {
				jsColumnValue.put(ROW_OLD_VALUE, valueToJson(value.getOldValue(), type));
				jsColumnValue.put(ROW_VALUE_CHANGED, ROW_VALUE_ISCHANGED);
			}
		}
		return jsColumnValue;
	}
	
	private static Object valueToJson(Object value, DataType type) {
		if (value == null) {
			return null;
		}
		switch (type) {
		case STRING:
		case INTEGER:
		case LONG:
		case FLOAT:
		case DOUBLE:
		case BOOLEAN:
			break;
		case DECIMAL:
			return value.toString();
		case DATE:
			return DATE_FORMAT.format(value);
		case TIME:
			return TIME_FORMAT.format(value);
		case DATETIME:
			return DATETIME_FORMAT.format(value);
		case OBJECT:	
		case LIST:
			return value.toString();
		}
		return value;
	}
	
	
	/**
	 * 将.w中data.toJson()的数据转换为Table
	 * @param json
	 * @return
	 * @throws ParseException
	 */
	public static Table jsonToTable(String json) throws ParseException {
		return jsonToTable((JSONObject) JSONObject.parse(json));
	}
	
	/**
	 * 将.w中data.toJson()的数据转换为Table
	 * @param json
	 * @return
	 * @throws ParseException
	 */
	public static Table jsonToTable(JSONObject json) throws ParseException {
		JSONObject jsUserData = json.getJSONObject(USER_DATA);

		Map<String, DataType> columns = new LinkedHashMap<String, DataType>();

		String idColumn = jsUserData.getString(ID_COLUMN_NAME);
		String idColumnType = jsUserData.getString(ID_COLUMN_TYPE);
		String tableName = jsUserData.getString(TABLE_NAME);
		if (idColumn != null && idColumnType != null) {
			columns.put(idColumn, DataType.parse(idColumnType));
		}

		String[] names = jsUserData.getString(COLUMN_NAMES).split(",");
		String[] types = jsUserData.getString(COLUMN_TYPES).split(",");
		for (int i = 0, len = names.length; i < len; i++) {
			columns.put(names[i], DataType.parse(types[i]));
		}

		Table table = new Table(columns);

		table.setTableName(tableName);
		if (idColumn != null) {
			table.setIDColumn(idColumn);
		}
		if (jsUserData.containsKey(TABLE_TOTAL)) {
			table.setTotal(jsUserData.getInteger(TABLE_TOTAL));
		}
		if (jsUserData.containsKey(TABLE_OFFSET)) {
			table.setOffset(jsUserData.getInteger(TABLE_OFFSET));
		}

		JSONArray jsRows = json.getJSONArray(ROWS);
		List<Row> rows = new ArrayList<Row>();
		for (int i = 0, len = jsRows.size(); i < len; i++) {
			JSONObject jsRow = jsRows.getJSONObject(i);
			Row row = jsonToRow(table, jsRow);
			rows.add(row);
		}
		table.appendRows(rows);

		return table;
	}
	
	private static Row jsonToRow(Table table, JSONObject jsRow) throws ParseException {
		Map<String, ColumnValue> values = new HashMap<String, ColumnValue>();
		RowState state = RowState.NONE;

		JSONObject jsUserData = jsRow.getJSONObject(USER_DATA);
		if (jsUserData != null) {
			state = RowState.parse(jsUserData.getString(ROW_STATE));
			if (jsUserData.containsKey(ROW_ID)) {
				String idColumn = table.getIDColumn();
				ColumnValue value = jsonToColumnValue(jsUserData.getJSONObject(ROW_ID), table.getColumnType(idColumn));
				values.put(idColumn, value);
			}
		}
		for (String name : table.getColumnNames()) {
			if (jsRow.containsKey(name)) {
				ColumnValue value = jsonToColumnValue(jsRow.getJSONObject(name), table.getColumnType(name));
				values.put(name, value);
			}
		}

		return new Row(values, state);
	}
	
	private static ColumnValue jsonToColumnValue(JSONObject jsColumnValue, DataType type) throws ParseException {
		Object value = jsonToValue(jsColumnValue.get(ROW_VALUE), type);
		Object oldValue = jsonToValue(jsColumnValue.get(ROW_OLD_VALUE), type);
		Integer changed = jsColumnValue.getInteger(ROW_VALUE_CHANGED);
		return new ColumnValue(value, oldValue, ROW_VALUE_ISCHANGED.equals(changed));
	}
	
	private static Object jsonToValue(Object jsValue, DataType type) throws ParseException {
		if (jsValue == null) {
			return null;
		}
		switch (type) {
		case STRING:
			break;
		case INTEGER:
			return new Integer(jsValue.toString());
		case LONG:
			return new Long(jsValue.toString());
		case FLOAT:
			return new Float(jsValue.toString());
		case DOUBLE:
			return new Double(jsValue.toString());
		case DECIMAL:
			return new BigDecimal(jsValue.toString());
		case BOOLEAN:
			return new Boolean(jsValue.toString());
		case DATE:
			return new java.sql.Date(DATE_FORMAT.parse(jsValue.toString()).getTime());
		case TIME:
			String tv = jsValue.toString();
			return new java.sql.Time((tv.length()>8?TIME_FORMAT.parse(tv):TIME_FORMAT2.parse(tv)).getTime());
		case DATETIME:
			return new java.sql.Timestamp(DATETIME_FORMAT.parse(jsValue.toString()).getTime());
		case OBJECT:	
		case LIST:
			return !StringUtils.isEmpty(jsValue.toString())?JSONObject.parse(jsValue.toString()):null;
		}
		return jsValue;
	}
	
	/**
	 * 将row转换为bean对象
	 * @param t
	 * @param bean
	 * @return
	 */
	public static Object tableRowToObjectBean(Table t, Object bean) throws Exception{
		if(t == null){
			return bean;
		}
		if(t.getRows().size() == 0){
			return bean;
		}
		Row r = t.getRows().get(0);
		Object obj = bean.getClass().newInstance();//创建新实例
		for (Field f : bean.getClass().getDeclaredFields()) {
			//如果是final成员就跳过  
			if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {  
                continue;  
            } 
            f.setAccessible(true);  
            //设置实例成员变量内容  
            f.set(obj, r.getValue(f.getName()));
		}
		return obj;
	}
	
	/**
	 * 将rows转换为List<bean>对象
	 * @param t
	 * @param bean
	 * @return
	 */
	public static List tableRowToListBean(Table t, Object bean) throws Exception{
		if(t == null){
			return new ArrayList<Object>();
		}
		if(t.getRows().size() == 0){
			return new ArrayList<Object>();
		}
		List<Object> list = new ArrayList<Object>();
		List<Row> r = t.getRows();
		for (Row row : r) {
			Object obj = bean.getClass().newInstance();//创建新实例
			for (Field f : bean.getClass().getDeclaredFields()) {
				//忽略static成员和final成员   
				if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {  
	                continue;  
	            } 
				//Class类中的成员变量为private,故必须进行此操作
	            f.setAccessible(true);  
	            //设置实例成员变量内容  
	            f.set(obj, row.getValue(f.getName()));
			}	
			list.add(obj);
		}
		return list;
	}
}
