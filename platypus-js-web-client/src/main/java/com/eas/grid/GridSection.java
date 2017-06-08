package com.eas.grid;

import com.eas.bound.JsArrayList;
import com.eas.core.XElement;
import com.eas.grid.columns.Column;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableColElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.KeyCodes;
import java.util.ArrayList;

/**
 *
 * @author mg
 * @param <T>
 */
public class GridSection<T> {

    public static final String JS_ROW_NAME = "js-row";

    private TableElement table = Document.get().createTableElement();
    private TableColElement colgroup = Document.get().createColGroupElement();
    private Element keyboardSelectedElement;
    protected String dynamicCellClassName;
    protected String dynamicOddRowsClassName;
    protected String dynamicEvenRowsClassName;
    protected String dynamicHeaderRowClassName;
    private boolean draggableRows;
    private int keyboardSelectedRow = -1;
    private int keyboardSelectedColumn = -1;
    private int rowsPerPage = 30; // Only for PageDown or PageUp keys handling
    private JsArrayList data; // Already sorted
    private int viewStart; // Inclusive
    private int viewEnd; // Exclusive
    private List<Column> columns = new ArrayList<>();

    public GridSection(String aDynamicCellClassName, String aDynamicOddRowsClassName, String aDynamicEvenRowsClassName, String aDynamicHeaderRowClassName) {
        super();
        this.dynamicCellClassName = aDynamicCellClassName;
        this.dynamicOddRowsClassName = aDynamicOddRowsClassName;
        this.dynamicEvenRowsClassName = aDynamicEvenRowsClassName;
        this.dynamicHeaderRowClassName = aDynamicHeaderRowClassName;
        TableSectionElement thead = table.getTHead();
        thead.appendChild(colgroup);

        table.getStyle().setProperty("borderCollapse", "collapse");
        table.<XElement>cast().addEventListener(BrowserEvents.KEYDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                int oldRow = GridSection.this.keyboardSelectedRow;
                int oldColumn = GridSection.this.keyboardSelectedColumn;
                int keyCode = event.getKeyCode();
                if (keyCode == KeyCodes.KEY_LEFT) {
                } else if (keyCode == KeyCodes.KEY_RIGHT) {
                } else if (keyCode == KeyCodes.KEY_UP) {
                } else if (keyCode == KeyCodes.KEY_DOWN) {
                } else if (keyCode == KeyCodes.KEY_PAGEUP) {
                } else if (keyCode == KeyCodes.KEY_PAGEDOWN) {
                } else if (keyCode == KeyCodes.KEY_HOME) {
                } else if (keyCode == KeyCodes.KEY_END) {
                }
            }
        });
    }

    public TableElement getElement() {
        return table;
    }

    public boolean isDraggableRows() {
        return draggableRows;
    }

    public void setDraggableRows(boolean aValue) {
        if (draggableRows != aValue) {
            draggableRows = aValue;
        }
    }

    public int getKeyboardSelectedColumn() {
        return keyboardSelectedColumn;
    }

    public void setKeyboardSelectedColumn(int keyboardSelectedColumn) {
        this.keyboardSelectedColumn = keyboardSelectedColumn;
    }

    public int getKeyboardSelectedRow() {
        return keyboardSelectedRow;
    }

    public void setKeyboardSelectedRow(int keyboardSelectedRow) {
        this.keyboardSelectedRow = keyboardSelectedRow;
    }

    public void addColumn(Column aColumn) {
        columns.add(aColumn);
        colgroup.appendChild(aColumn.getElement());
        redraw();
    }

    public void addColumn(int index, Column aColumn) {
        if (index >= 0 && index <= columns.size()) { // It is all about insertBefore
            if (index < columns.size()) { // It is all about insertBefore
                columns.add(index, aColumn);
                Element col = (Element) colgroup.getChild(index);
                colgroup.insertBefore(aColumn.getElement(), col);
            } else {
                addColumn(aColumn);
            }
            redraw();
        }
    }

    public Column removeColumn(int index) {
        if (index >= 0 && index < columns.size()) {
            Column removed = columns.remove(index);
            removed.getElement().removeFromParent();
            removed.getColumnRule().removeFromParent();
            redraw();
            return removed;
        } else {
            return null;
        }
    }

    public int getColumnCount() {
        return columns.size();
    }

    public Column getColumn(int index) {
        return index >= 0 && index < columns.size() ? columns.get(index) : null;
    }

    public int getColumnIndex(Column column) {
        return columns.indexOf(column);
    }

    public int getRowsCount() {
        return table.getTBodies().getItem(0).getRows().getLength();
    }

    public TableCellElement getViewCell(int aRow, int aCol) {
        NodeList<TableRowElement> viewRows = table.getTBodies().getItem(0).getRows();
        if (aRow - viewStart >= 0 && aRow - viewStart < viewRows.getLength()) {
            TableRowElement viewRow = viewRows.getItem(aRow);
            NodeList<TableCellElement> cells = viewRow.getCells();
            if (aCol >= 0 && aCol < cells.getLength()) {
                return cells.getItem(aCol);
            }
        }
        return null;
    }

    public void focusCell(int aRow, int aCol) {
        setKeyboardSelectedColumn(aCol);
        setKeyboardSelectedRow(aRow);
        TableCellElement cell = getViewCell(aRow, aCol);
        cell.focus();
    }

    public Element getKeyboardSelectedElement() {
        return keyboardSelectedElement;
    }

    public void redraw() {
        redrawHeaders();
        redrawBody();
        redrawFooters();
    }

    public void redrawBody() {
        TableSectionElement tbody = table.getTBodies().getItem(0);
        tbody.removeFromParent();
        table.appendChild(Document.get().createTBodyElement());
        drawBody();
    }

    public void drawBody() {
        TableSectionElement tbody = table.getTBodies().getItem(0);
        for (int i = viewStart; i < viewEnd; i++) {
            JavaScriptObject dataRow = data.get(i);
            TableRowElement viewRow = Document.get().createTRElement();
            if ((i + 1) % 2 == 0) {
                viewRow.addClassName(dynamicEvenRowsClassName);
            } else {
                viewRow.addClassName(dynamicOddRowsClassName);
            }
            viewRow.addClassName("selected-row");
            viewRow.setPropertyJSO(JS_ROW_NAME, dataRow);
            tbody.appendChild(viewRow);
            for (int c = 0; c < columns.size(); c++) {
                Column column = columns.get(c);
                TableCellElement viewCell = Document.get().createTDElement();
                // TODO: Check alignment of the cell
                viewCell.addClassName(dynamicCellClassName);
                viewRow.appendChild(viewCell);
                column.render(i, dataRow, viewCell);
            }
        }
    }

    public void redrawHeaders() {
        TableSectionElement tbody = table.getTHead();
        tbody.removeFromParent();
        table.appendChild(Document.get().createTHeadElement());
        drawHeaders();
    }

    public void drawHeaders() {
    }

    public void redrawFooters() {
        TableSectionElement tbody = table.getTFoot();
        tbody.removeFromParent();
        table.appendChild(Document.get().createTFootElement());
        drawFooters();
    }

    public void drawFooters() {
    }

    public void redrawRow(int index) {
        TableSectionElement tbody = table.getTBodies().getItem(0);
        if (index - viewStart >= 0 && index - viewStart < tbody.getRows().getLength()) {
            TableRowElement viewRow = (TableRowElement) tbody.getRows().getItem(index - viewStart);
            JavaScriptObject dataRow = data.get(index);
            for (int c = 0; c < columns.size(); c++) {
                Column column = columns.get(c);
                TableCellElement viewCell = (TableCellElement) viewRow.getCells().getItem(c);
                column.render(index, dataRow, viewCell);
            }
        } // if the row is not rendered then we have to do nothing
    }

    public void redrawColumn(int aIndex) {
        if (aIndex >= 0 && aIndex < columns.size()) {
            Column column = columns.get(aIndex);
            TableSectionElement tbody = table.getTBodies().getItem(0);
            for (int i = viewStart; i < viewEnd; i++) {
                JavaScriptObject dataRow = data.get(i);
                TableRowElement viewRow = (TableRowElement) tbody.getRows().getItem(i - viewStart);
                TableCellElement viewCell = (TableCellElement) viewRow.getCells().getItem(aIndex);
                while (viewCell.getFirstChildElement() != null) {
                    viewCell.getFirstChildElement().removeFromParent();
                }
                column.render(i, dataRow, viewCell);
            }
        }
    }
}
