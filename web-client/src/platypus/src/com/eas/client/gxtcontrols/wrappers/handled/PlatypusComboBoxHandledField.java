package com.eas.client.gxtcontrols.wrappers.handled;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.ObjectKeyProvider;
import com.eas.client.gxtcontrols.model.ListStorePkFiller;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusComboBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;

public class PlatypusComboBoxHandledField extends PlatypusComboBox {

	protected ModelElementRef modelElement;
	protected ModelElementRef valueRef;
	protected ModelElementRef displayRef;
	protected JavaScriptObject cellFunction;

	protected PlatypusComboLabelHandledProvider labelProvider;
	protected ListStorePkFiller filler;

	public PlatypusComboBoxHandledField() {
		super(new ComboBoxCell<Object>(new ListStore<Object>(new ObjectKeyProvider()), new PlatypusComboLabelHandledProvider()));
		labelProvider = (PlatypusComboLabelHandledProvider) getCell().getLabelProvider();
		labelProvider.setContainer(this);
		setEditable(false);
		setTypeAhead(true);
		setTriggerAction(TriggerAction.ALL);
		filler = new ListStorePkFiller(getCell().getStore());
	}

	public ModelElementRef getModelElement() {
		return modelElement;
	}

	public void setModelElement(ModelElementRef aValue) {
		modelElement = aValue;
		labelProvider.setTargetValueRef(modelElement);
	}

	public ModelElementRef getValueRef() {
		return valueRef;
	}

	public void setValueRef(ModelElementRef aValue) {
		valueRef = aValue;
		labelProvider.setLookupValueRef(aValue);
		filler.setValuesRowsetHost(valueRef != null ? valueRef.entity : null);
	}

	public ModelElementRef getDisplayRef() {
		return displayRef;
	}

	public void setDisplayRef(ModelElementRef aValue) {
		displayRef = aValue;
		labelProvider.setDisplayValueRef(aValue);
		if (displayRef != null)
			filler.ensureRowset(displayRef.entity);
	}

	public JavaScriptObject getCellFunction() {
		return cellFunction;
	}

	public void setCellFunction(JavaScriptObject aValue) {
		if (aValue != cellFunction) {
			cellFunction = aValue;
			labelProvider.setCellFunction(cellFunction);
			cellFunction = aValue;
			redraw();
		}
	}

	@Override
	public void setValue(Object value, boolean fireEvents, boolean redraw) {
		super.setValue(value, fireEvents, redraw);
		if (!redraw && cellFunction != null)
			redraw();
	}

	@Override
	protected void onRedraw() {
		super.onRedraw();
		if (modelElement != null && cellFunction != null && modelElement.entity.getRowset() != null) {
			try {
				JavaScriptObject eventThis = modelElement.entity.getModel().getModule();
				if (getParent() != null && getParent().getParent() instanceof PlatypusAdapterStandaloneField<?>) {
					PlatypusAdapterField<?> adapter = (PlatypusAdapterStandaloneField<?>) getParent().getParent();
					eventThis = adapter.getPublishedField();
				}
				Row currentRow = modelElement.entity.getRowset().getCurrentRow();
				Object currentRowValue = currentRow.getColumnObject(modelElement.getColIndex());
				PublishedCell cellToRender = ControlsUtils.calcStandalonePublishedCell(eventThis, cellFunction, currentRow, labelProvider.getLabel(currentRowValue), modelElement);
				if (cellToRender != null) {
					cellToRender.styleToElement(getInputEl());
				}
			} catch (Exception ex) {
				Logger.getLogger(PlatypusComboBoxHandledField.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

}
