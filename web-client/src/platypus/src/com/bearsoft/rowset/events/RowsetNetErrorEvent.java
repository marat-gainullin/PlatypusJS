package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

public class RowsetNetErrorEvent extends RowsetEvent {

	protected String message;

	/**
	 * Rowset error event constructor.
	 * 
	 * @param source
	 *            Rowset the row is inserted to.
	 * @see RowsetEventMoment
	 */
	public RowsetNetErrorEvent(Rowset source, String aMessage) {
		super(source, RowsetEventMoment.AFTER);
		message = aMessage;
	}

	public String getMessage() {
		return message;
	}
}
