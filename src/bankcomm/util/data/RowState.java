package bankcomm.util.data;

import org.apache.commons.lang.StringUtils;

public enum RowState {
	NONE, NEW, EDIT, DELETE;
	
	public static RowState parse(String state) {
		return StringUtils.isEmpty(state) ? RowState.NONE : RowState.valueOf(state.toUpperCase());
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

}
