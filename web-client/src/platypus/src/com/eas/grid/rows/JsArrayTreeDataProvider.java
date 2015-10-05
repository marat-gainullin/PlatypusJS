package com.eas.grid.rows;

import com.eas.grid.processing.TreeDataProvider;
import com.google.gwt.core.client.JavaScriptObject;

public class JsArrayTreeDataProvider extends TreeDataProvider<JavaScriptObject> implements JsDataContainer {

	public JsArrayTreeDataProvider(String aParentField, String aChildrenField, Runnable aOnResize) {
		super(new JsTree(aParentField, aChildrenField), aOnResize);
	}

	@Override
	public JavaScriptObject getData() {
		return ((JsTree)tree).getData();
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		((JsTree)tree).setData(aValue);
	}
}
