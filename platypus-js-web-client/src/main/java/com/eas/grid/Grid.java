package com.eas.grid;

import com.eas.core.Utils;
import java.util.ArrayList;
import java.util.List;

import com.eas.core.XElement;
import com.eas.grid.columns.CheckServiceColumn;
import com.eas.grid.columns.Column;
import com.eas.grid.columns.RadioServiceColumn;
import com.eas.grid.columns.UsualServiceColumn;
import com.eas.grid.columns.header.HeaderAnalyzer;
import com.eas.grid.columns.header.HeaderSplitter;
import com.eas.grid.columns.header.HeaderNode;
import com.eas.grid.processing.TreeDataProvider;
import com.eas.grid.rows.JsArrayTreeDataProvider;
import com.eas.grid.rows.JsDataContainer;
import com.eas.menu.MenuItemCheckBox;
import com.eas.menu.Menu;
import com.eas.ui.BlurEvent;
import com.eas.ui.FocusEvent;
import com.eas.ui.Focusable;
import com.eas.ui.HasBinding;
import com.eas.ui.HasOnRender;
import com.eas.ui.PublishedColor;
import com.eas.ui.Widget;
import com.eas.ui.XDataTransfer;
import com.eas.ui.events.BlurHandler;
import com.eas.ui.events.FocusHandler;
import com.eas.ui.events.HasBlurHandlers;
import com.eas.ui.events.HasFocusHandlers;
import com.eas.ui.events.HasSelectionHandlers;
import com.eas.ui.events.SelectionEvent;
import com.eas.ui.events.SelectionHandler;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.eas.ui.events.HasKeyDownHandlers;
import com.eas.ui.events.HasKeyUpHandlers;
import com.eas.ui.events.HasKeyPressHandlers;
import com.eas.ui.events.KeyDownEvent;
import com.eas.ui.events.KeyDownHandler;
import com.eas.ui.events.KeyPressEvent;
import com.eas.ui.events.KeyPressHandler;
import com.eas.ui.events.KeyUpEvent;
import com.eas.ui.events.KeyUpHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class Grid extends Widget implements HasSelectionHandlers<JavaScriptObject>, HasSelectionLead, HasOnRender, HasBinding, Focusable, HasFocusHandlers, HasBlurHandlers,
        HasKeyDownHandlers, HasKeyPressHandlers, HasKeyUpHandlers,
        JsDataContainer {

    public static final String RULER_STYLE = "grid-ruler";
    public static final String COLUMN_PHANTOM_STYLE = "grid-column-phantom";
    public static final int MINIMUM_COLUMN_WIDTH = 15;
    //
    protected StyleElement cellsStyleElement = Document.get().createStyleElement();
    protected StyleElement rowsStyleElement = Document.get().createStyleElement();
    protected StyleElement oddRowsStyleElement = Document.get().createStyleElement();
    protected StyleElement evenRowsStyleElement = Document.get().createStyleElement();
    protected StyleElement headerRowsStyleElement = Document.get().createStyleElement();
    protected String dynamicCellClassName = "grid-cell-" + Document.get().createUniqueId();
    protected String dynamicOddRowsClassName = "grid-odd-row-" + Document.get().createUniqueId();
    protected String dynamicEvenRowsClassName = "grid-even-row-" + Document.get().createUniqueId();
    protected String dynamicHeaderRowClassName = "grid-heder-row-" + Document.get().createUniqueId();
    protected Element headerLeftContainer = Document.get().createDivElement();
    protected Section headerLeft = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element headerRightContainer = Document.get().createDivElement();
    protected Section headerRight = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element frozenLeftContainer = Document.get().createDivElement();
    protected Section frozenLeft = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element frozenRightContainer = Document.get().createDivElement();
    protected Section frozenRight = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element scrollableLeftContainer = Document.get().createDivElement();
    protected Section scrollableLeft = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element scrollableRightContainer = Document.get().createDivElement();
    protected Section scrollableRight = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element footerLeftContainer = Document.get().createDivElement();
    protected Section footerLeft = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element footerRightContainer = Document.get().createDivElement();
    protected Section footerRight = new Section(dynamicCellClassName, dynamicOddRowsClassName, dynamicEvenRowsClassName, dynamicHeaderRowClassName);
    protected Element ghostLine = Document.get().createDivElement();
    protected Element ghostColumn;
    protected ColumnDrag targetDraggedColumn;

    protected List<HeaderNode> header = new ArrayList<>();
    //
    protected Element columnsChevron = Document.get().createDivElement();
    //
    private int headerRowsHeight = 30;
    protected int rowsHeight = 30;
    protected boolean showHorizontalLines = true;
    protected boolean showVerticalLines = true;
    protected boolean showOddRowsInOtherColor = true;
    protected PublishedColor gridColor;
    protected PublishedColor oddRowsColor = PublishedColor.create(241, 241, 241, 255);

    private Set<JavaScriptObject> selected = new HashSet<>();
    private JavaScriptObject selectionLead;

    protected int frozenColumns;
    protected int frozenRows;
    protected String parentField;
    protected String childrenField;
    //
    protected JavaScriptObject data; // bounded data. this is not rows source. rows source is data['field' property path]
    protected JavaScriptObject sortedRows; // rows in view. subject of sorting. subject of collapse / expand in tree.
    protected String field;
    protected HandlerRegistration boundToData;
    protected HandlerRegistration boundToCursor;
    protected String cursorProperty = "cursor";
    protected JavaScriptObject onRender;
    protected JavaScriptObject onAfterRender;
    protected JavaScriptObject onExpand;
    protected JavaScriptObject onCollapse;
    // runtime
    protected Widget activeEditor;
    protected HandlerRegistration sortHandlerReg;
    protected HandlerRegistration positionSelectionHandler;
    protected HandlerRegistration onSelectEventSelectionHandler;
    protected boolean editable = true;
    protected boolean deletable = true;
    protected boolean insertable = true;
    protected boolean draggableRows;

    public Grid() {
        super();
        element.setClassName("grid-shell");
        headerLeftContainer.setClassName("grid-section-header-left");
        headerRightContainer.setClassName("grid-section-header-right");
        frozenLeftContainer.setClassName("grid-section-frozen-left");
        frozenRightContainer.setClassName("grid-section-frozen-right");
        scrollableLeftContainer.setClassName("grid-section-body-left");
        scrollableRightContainer.setClassName("grid-section-body-right");
        footerLeftContainer.setClassName("grid-section-footer-left");
        footerRightContainer.setClassName("grid-section-footer-right");
        columnsChevron.addClassName("grid-columns-chevron");
        element.appendChild(cellsStyleElement);
        element.appendChild(rowsStyleElement);
        element.appendChild(oddRowsStyleElement);
        element.appendChild(evenRowsStyleElement);
        element.appendChild(headerLeftContainer);
        element.appendChild(headerRightContainer);
        element.appendChild(frozenLeftContainer);
        element.appendChild(frozenRightContainer);
        element.appendChild(scrollableLeftContainer);
        element.appendChild(scrollableRightContainer);
        element.appendChild(footerLeftContainer);
        element.appendChild(footerRightContainer);
        element.appendChild(columnsChevron);

        scrollableRightContainer.<XElement>cast().addEventListener(BrowserEvents.SCROLL, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                // TODO: Add ajacent sections movement logic
            }
        });

        /*
            @Override
            public void onScroll(ScrollEvent event) {
                int aimLeft = scrollableRightContainer.getElement().getScrollLeft();
                if (isHeaderVisible()) {
                    headerRightContainer.getElement().setScrollLeft(aimLeft);
                    int factLeftDelta0 = aimLeft - headerRightContainer.getElement().getScrollLeft();
                    if (factLeftDelta0 > 0) {
                        headerRightContainer.getElement().getStyle().setRight(factLeftDelta0, Style.Unit.PX);
                    } else {
                        headerRightContainer.getElement().getStyle().clearRight();
                    }
                }
                if (frozenColumns > 0 || frozenRows > 0) {
                    int aimTop = scrollableRightContainer.getElement().getScrollTop();

                    scrollableLeftContainer.getElement().setScrollTop(aimTop);
                    int factTopDelta = aimTop - scrollableLeftContainer.getElement().getScrollTop();
                    if (factTopDelta > 0) {
                        scrollableLeftContainer.getElement().getStyle().setBottom(factTopDelta, Style.Unit.PX);
                    } else {
                        scrollableLeftContainer.getElement().getStyle().setBottom(0, Style.Unit.PX);
                        //scrollableLeftContainer.getElement().getStyle().clearBottom();
                    }
                    frozenRightContainer.getElement().setScrollLeft(aimLeft);
                    int factLeftDelta1 = aimLeft - frozenRightContainer.getElement().getScrollLeft();
                    if (factLeftDelta1 > 0) {
                        frozenRightContainer.getElement().getStyle().setRight(factLeftDelta1, Style.Unit.PX);
                    } else {
                        frozenRightContainer.getElement().getStyle().clearRight();
                    }
                    footerRightContainer.getElement()
                            .setScrollLeft(scrollableRightContainer.getElement().getScrollLeft());
                    int factLeftDelta2 = aimLeft - footerRightContainer.getElement().getScrollLeft();
                    if (factLeftDelta2 > 0) {
                        footerRightContainer.getElement().getStyle().setRight(factLeftDelta2, Style.Unit.PX);
                    } else {
                        footerRightContainer.getElement().getStyle().clearRight();
                    }
                }
            }
        });
         */
        ghostLine.addClassName(RULER_STYLE);
        ghostLine.getStyle().setPosition(Style.Position.ABSOLUTE);
        ghostLine.getStyle().setTop(0, Style.Unit.PX);
        ghostColumn = Document.get().createDivElement();
        ghostColumn.addClassName(COLUMN_PHANTOM_STYLE);
        ghostColumn.getStyle().setPosition(Style.Position.ABSOLUTE);
        ghostColumn.getStyle().setTop(0, Style.Unit.PX);

        element.<XElement>cast().addEventListener(BrowserEvents.DRAGSTART, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (draggableRows) {
                    EventTarget et = event.getEventTarget();
                    Element targetElement = Element.as(et);
                    if ("tr".equalsIgnoreCase(targetElement.getTagName())) {
                        event.stopPropagation();
                        JavaScriptObject dragged = targetElement.getPropertyJSO(Section.JS_ROW_NAME);
                        JavaScriptObject rows = getRows();
                        if (rows != null) {
                            Utils.JsObject dataArray = rows.cast();
                            int dataIndex = dataArray.indexOf(dragged);
                            event.getDataTransfer().setData("text/modelgrid-row",
                                    "{\"gridName\":\"" + name + "\", \"dataIndex\": " + dataIndex + "}");
                        }
                    }
                }
            }

        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGENTER, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null && ColumnDrag.instance.isMove()) {
                    event.preventDefault();
                    event.stopPropagation();
                    ColumnDrag target = findTargetDraggedColumn(event.getEventTarget());
                    if (target != null) {
                        showColumnMoveDecorations(target);
                        event.getDataTransfer().<XDataTransfer>cast().setDropEffect("move");
                    } else {
                        event.getDataTransfer().<XDataTransfer>cast().setDropEffect("none");
                    }
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAG, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null && ColumnDrag.instance.isResize()) {
                    event.stopPropagation();
                }
            }

        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGOVER, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null) {
                    event.preventDefault();
                    event.stopPropagation();
                    if (ColumnDrag.instance.isMove()) {
                        ColumnDrag target = findTargetDraggedColumn(event.getEventTarget());
                        if (target != null) {
                            event.getDataTransfer().<XDataTransfer>cast().setDropEffect("move");
                        } else {
                            hideColumnDecorations();
                            event.getDataTransfer().<XDataTransfer>cast().setDropEffect("none");
                        }
                    } else {
                        Element hostElement = Grid.this.getElement();
                        int clientX = event.getClientX();
                        int hostAbsX = hostElement.getAbsoluteLeft();
                        int hostScrollX = hostElement.getScrollLeft();
                        int docScrollX = hostElement.getOwnerDocument().getScrollLeft();
                        int relativeX = clientX - hostAbsX + hostScrollX + docScrollX;
                        ghostLine.getStyle().setLeft(relativeX, Style.Unit.PX);
                        ghostLine.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
                        if (ghostLine.getParentElement() != hostElement) {
                            hostElement.appendChild(ghostLine);
                        }
                    }
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGLEAVE, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null) {
                    event.stopPropagation();
                    if (ColumnDrag.instance.isMove()) {
                        if (event.getEventTarget() == (JavaScriptObject) Grid.this.getElement()) {
                            hideColumnDecorations();
                        }
                    }
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.DRAGEND, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                if (ColumnDrag.instance != null) {
                    event.stopPropagation();
                    hideColumnDecorations();
                    ColumnDrag.instance = null;
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.DROP, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                ColumnDrag source = ColumnDrag.instance;
                ColumnDrag target = targetDraggedColumn;
                hideColumnDecorations();
                ColumnDrag.instance = null;
                if (source != null) {
                    event.preventDefault();
                    event.stopPropagation();
                    if (source.isMove()) {
                        // target table may be any section in our grid
                        if (target != null) {
                            HeaderView sourceHeader = source.getHeader();
                            HeaderView targetHeader = target.getHeader();
                            moveColumnNode(sourceHeader.getHeaderNode(), targetHeader.getHeaderNode());
                        }
                    } else {
                        HeaderView header = source.getHeader();
                        if (header.isResizable()) {
                            int newWidth = Math.max(
                                    event.getClientX() - source.getDecorationElement().getAbsoluteLeft(),
                                    MINIMUM_COLUMN_WIDTH);
                            if (newWidth >= header.getColumn().getMinWidth() && newWidth <= header.getColumn().getMaxWidth()) {
                                header.getColumn().setWidth(newWidth);
                            }
                        }
                    }
                }
            }
        });

        columnsChevron.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                Menu columnsMenu = new Menu();
                fillColumns(columnsMenu, headerLeft);
                fillColumns(columnsMenu, headerRight);
                columnsMenu.showRelativeTo(columnsChevron);
            }

            private void fillColumns(Menu aTarget, final Section aSection) {
                for (int i = 0; i < aSection.getColumnCount(); i++) {
                    final Column column = aSection.getColumn(i);
                    MenuItemCheckBox miCheck = new MenuItemCheckBox(column.isVisible(),
                            aSection.getColumn(i).getHeader().getText(), true);
                    miCheck.addValueChangeHandler(new ValueChangeHandler() {

                        @Override
                        public void onValueChange(ValueChangeEvent event) {
                            if (Boolean.TRUE.equals(event.getNewValue())) {
                                column.setVisible(true);
                            } else {
                                column.setVisible(false);
                            }
                        }

                    });
                    aTarget.add(miCheck);
                }
            }

        });

        gridColor = PublishedColor.create(211, 211, 211, 255);
        regenerateDynamicHeaderRowsStyles();
        regenerateDynamicRowsStyles();
        regenerateDynamicOddRowsStyles();
        regenerateDynamicCellsStyles();

        element.<XElement>cast().addEventListener(BrowserEvents.KEYUP, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                JavaScriptObject rows = getRows();
                if (rows != null) {
                    if (activeEditor == null) {
                        if (event.getKeyCode() == KeyCodes.KEY_DELETE && deletable) {
                            if (sortedRows.<JsArray>cast().length() > 0) {
                                // calculate some view sugar
                                int lastSelectedViewIndex = -1;
                                for (int i = sortedRows.<JsArray>cast().length() - 1; i >= 0; i--) {
                                    JavaScriptObject element = sortedRows.<JsArray>cast().get(i);
                                    if (isSelected(element)) {
                                        lastSelectedViewIndex = i;
                                        break;
                                    }
                                }
                                // actually delete selected elements
                                int deletedAt = -1;
                                for (int i = rows.<JsArray>cast().length() - 1; i >= 0; i--) {
                                    JavaScriptObject element = rows.<JsArray>cast().get(i);
                                    if (isSelected(element)) {
                                        rows.<Utils.JsObject>cast().splice(i, 1);
                                        deletedAt = i;
                                    }
                                }
                                final int viewIndexToSelect = lastSelectedViewIndex;
                                if (deletedAt > -1) {
                                    // TODO: Check if Invoke.Later is an option
                                    int vIndex = viewIndexToSelect;
                                    if (vIndex >= 0 && sortedRows.<JsArray>cast().length() > 0) {
                                        if (vIndex >= sortedRows.<JsArray>cast().length()) {
                                            vIndex = sortedRows.<JsArray>cast().length() - 1;
                                        }
                                        JavaScriptObject toSelect = sortedRows.<JsArray>cast().get(vIndex);
                                        makeVisible(toSelect, true);
                                    } else {
                                        Grid.this.setFocus(true);
                                    }
                                }
                            }
                        } else if (event.getKeyCode() == KeyCodes.KEY_INSERT && insertable) {
                            int insertAt = -1;
                            JavaScriptObject lead = selectionLead;
                            insertAt = rows.<Utils.JsObject>cast().indexOf(lead);
                            insertAt++;
                            JavaScriptObject oElementClass = rows.<Utils.JsObject>cast().getJs("elementClass");
                            Utils.JsObject elementClass = oElementClass != null ? oElementClass.<Utils.JsObject>cast() : null;
                            final JavaScriptObject inserted = elementClass != null ? elementClass.newObject()
                                    : JavaScriptObject.createObject();
                            rows.<Utils.JsObject>cast().splice(insertAt, 0, inserted);
                            // TODO: Check if Invoke.Later is an option
                            makeVisible(inserted, true);
                        }
                    }
                }
            }

        });
    }

    public JavaScriptObject getRows() {
        return data != null && field != null && !field.isEmpty() ? Utils.getPathData(data, field)
                : data;
    }

    @Override
    public void removedItems(JavaScriptObject anArray) {
    }

    @Override
    public void addedItems(JavaScriptObject anArray) {
    }

    @Override
    public void changedItems(JavaScriptObject anArray) {
    }

    @Override
    public JavaScriptObject getLead() {
        return selectionLead;
    }

    public boolean isSelected(JavaScriptObject item) {
        return selected.contains(item);
    }

    public void select(JavaScriptObject item) {
        selected.add(item);
        selectionLead = item;
        JavaScriptObject rows = getRows();
        rows.<Utils.JsObject>cast().setJs(cursorProperty, selectionLead);
        fireSelected(item);
    }

    public boolean unselect(JavaScriptObject item) {
        if(selectionLead == item){
            selectionLead = null;
        }
        return selected.remove(item);
    }

    public void clearSelection() {
        selected.clear(); // TODO: Think about related event.
        if(selected.contains(selectionLead)){
            selectionLead = null;
        }
    }

    private final Set<SelectionHandler<JavaScriptObject>> selectionHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<JavaScriptObject> handler) {
        selectionHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                selectionHandlers.remove(handler);
            }
        };
    }

    public void fireSelected(JavaScriptObject item) {
        SelectionEvent<JavaScriptObject> event = new SelectionEvent<>(this, item);
        selectionHandlers.forEach(sh -> sh.onSelection(event));
    }

    private Set<FocusHandler> focusHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        focusHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                focusHandlers.remove(handler);
            }

        };
    }

    private void fireFocus() {
        FocusEvent event = new FocusEvent(this);
        for (FocusHandler h : focusHandlers) {
            h.onFocus(event);
        }
    }

    private Set<BlurHandler> blurHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        blurHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                blurHandlers.remove(handler);
            }

        };
    }

    private void fireBlur() {
        BlurEvent event = new BlurEvent(this);
        for (BlurHandler h : blurHandlers) {
            h.onBlur(event);
        }
    }

    private Set<KeyUpHandler> keyUpHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        keyUpHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyUpHandlers.remove(handler);
            }

        };
    }

    private void fireKeyUp(NativeEvent nevent) {
        KeyUpEvent event = new KeyUpEvent(this, nevent);
        for (KeyUpHandler h : keyUpHandlers) {
            h.onKeyUp(event);
        }
    }

    private Set<KeyDownHandler> keyDownHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        keyDownHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyDownHandlers.remove(handler);
            }

        };
    }

    private void fireKeyDown(NativeEvent nevent) {
        KeyDownEvent event = new KeyDownEvent(this, nevent);
        for (KeyDownHandler h : keyDownHandlers) {
            h.onKeyDown(event);
        }
    }

    private Set<KeyPressHandler> keyPressHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        keyPressHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                keyPressHandlers.remove(handler);
            }

        };
    }

    private void fireKeyPress(NativeEvent nevent) {
        KeyPressEvent event = new KeyPressEvent(this, nevent);
        for (KeyPressHandler h : keyPressHandlers) {
            h.onKeyPress(event);
        }
    }

    protected ColumnDrag findTargetDraggedColumn(JavaScriptObject aEventTarget) {
        if (Element.is(aEventTarget)) {
            Section targetSection = null;
            Element targetCell = null;
            Element currentTarget = Element.as(aEventTarget);
            if (COLUMN_PHANTOM_STYLE.equals(currentTarget.getClassName())
                    || RULER_STYLE.equals(currentTarget.getClassName())) {
                return targetDraggedColumn;
            } else {
                while ((targetCell == null || targetSection == null) && currentTarget != null
                        && currentTarget != Grid.this.getElement()) {
                    if (targetCell == null) {
                        if ("td".equalsIgnoreCase(currentTarget.getTagName())
                                || "th".equalsIgnoreCase(currentTarget.getTagName())) {
                            targetCell = currentTarget;
                        }
                    }
                    if (targetSection == null) {
                        if (currentTarget == headerLeft.getElement()) {
                            targetSection = headerLeft;
                        } else if (currentTarget == frozenLeft.getElement()) {
                            targetSection = frozenLeft;
                        } else if (currentTarget == scrollableLeft.getElement()) {
                            targetSection = scrollableLeft;
                        } else if (currentTarget == footerLeft.getElement()) {
                            targetSection = footerLeft;
                        } else if (currentTarget == headerRight.getElement()) {
                            targetSection = headerRight;
                        } else if (currentTarget == frozenRight.getElement()) {
                            targetSection = frozenRight;
                        } else if (currentTarget == scrollableRight.getElement()) {
                            targetSection = scrollableRight;
                        } else if (currentTarget == footerRight.getElement()) {
                            targetSection = footerRight;
                        }
                    }
                    currentTarget = currentTarget.getParentElement();
                }
                if (targetSection != null && targetCell != null) {
                    HeaderView header = (HeaderView) targetCell.getPropertyObject(HeaderView.HEADER_VIEW);
                    if (header != null) {
                        return new ColumnDrag(header, targetCell);
                    } else {
                        return null;
                    }
                }
                return null;
            }
        } else {
            return null;
        }
    }

    protected void hideColumnDecorations() {
        ghostLine.removeFromParent();
        ghostColumn.removeFromParent();
        targetDraggedColumn = null;
    }

    protected void showColumnMoveDecorations(ColumnDrag target) {
        targetDraggedColumn = target;
        Element hostElement = getElement();
        Element thtdElement = target.getDecorationElement();
        int thLeft = thtdElement.getAbsoluteLeft();
        thLeft = thLeft - element.getAbsoluteLeft() + hostElement.getScrollLeft();
        ghostLine.getStyle().setLeft(thLeft, Style.Unit.PX);
        ghostLine.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
        ghostColumn.getStyle().setLeft(thLeft, Style.Unit.PX);
        ghostColumn.getStyle().setWidth(thtdElement.getOffsetWidth(), Style.Unit.PX);
        ghostColumn.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
        if (ghostLine.getParentElement() != hostElement) {
            ghostLine.removeFromParent();
            hostElement.appendChild(ghostLine);
        }
        if (ghostColumn.getParentElement() != hostElement) {
            ghostColumn.removeFromParent();
            hostElement.appendChild(ghostColumn);
        }
    }

    public String getDynamicCellClassName() {
        return dynamicCellClassName;
    }

    public boolean isShowHorizontalLines() {
        return showHorizontalLines;
    }

    public void setShowHorizontalLines(boolean aValue) {
        if (showHorizontalLines != aValue) {
            showHorizontalLines = aValue;
            regenerateDynamicCellsStyles();
        }
    }

    public boolean isShowVerticalLines() {
        return showVerticalLines;
    }

    public void setShowVerticalLines(boolean aValue) {
        if (showVerticalLines != aValue) {
            showVerticalLines = aValue;
            regenerateDynamicCellsStyles();
        }
    }

    protected void regenerateDynamicCellsStyles() {
        cellsStyleElement.setInnerHTML("." + dynamicCellClassName + "{"
                + "border-left-width: " + (showHorizontalLines ? 1 : 0) + "px;"
                + "border-right-width:" + (showVerticalLines ? 1 : 0) + "px;"
                + "border-color: " + (gridColor != null ? gridColor.toStyled() : "auto") + ";");
    }

    protected void regenerateDynamicOddRowsStyles() {
        if (showOddRowsInOtherColor && oddRowsColor != null) {
            oddRowsStyleElement.setInnerHTML(
                    "." + dynamicOddRowsClassName + "{background-color: " + oddRowsColor.toStyled() + "}");
        } else {
            oddRowsStyleElement.setInnerHTML("");
        }
    }

    public PublishedColor getGridColor() {
        return gridColor;
    }

    public void setGridColor(PublishedColor aValue) {
        gridColor = aValue;
        regenerateDynamicCellsStyles();
    }

    public PublishedColor getOddRowsColor() {
        return oddRowsColor;
    }

    public void setOddRowsColor(PublishedColor aValue) {
        oddRowsColor = aValue;
        regenerateDynamicOddRowsStyles();
    }

    public int getRowsHeight() {
        return rowsHeight;
    }

    public void setHeaderRowsHeight(int aValue) {
        if (headerRowsHeight != aValue && aValue >= 10) {
            headerRowsHeight = aValue;
            regenerateDynamicRowsStyles();
        }
    }

    protected void regenerateDynamicRowsStyles() {
        rowsStyleElement.setInnerHTML("." + dynamicCellClassName + "{ height: " + headerRowsHeight + "px;");
    }

    public void setRowsHeight(int aValue) {
        if (rowsHeight != aValue && aValue >= 10) {
            rowsHeight = aValue;
            regenerateDynamicHeaderRowsStyles();
        }
    }

    protected void regenerateDynamicHeaderRowsStyles() {
        headerRowsStyleElement.setInnerHTML("." + dynamicCellClassName + "{ height: " + rowsHeight + "px;");
    }

    public boolean isHeaderVisible() {
        return !Style.Display.NONE.equals(headerLeft.getElement().getStyle().getDisplay())
                && !Style.Display.NONE.equals(headerRight.getElement().getStyle().getDisplay());
    }

    public void setHeaderVisible(boolean aValue) {
        if (aValue) {
            columnsChevron.getStyle().clearDisplay();
            headerLeftContainer.getStyle().clearDisplay();
            headerRightContainer.getStyle().clearDisplay();
        } else {
            columnsChevron.getStyle().setDisplay(Style.Display.NONE);
            headerLeftContainer.getStyle().setDisplay(Style.Display.NONE);
            headerRightContainer.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    public int getFrozenColumns() {
        return frozenColumns;
    }

    public void setFrozenColumns(int aValue) {
        if (aValue >= 0 && frozenColumns != aValue) {
            if (aValue >= 0) {
                frozenColumns = aValue;
                applyColumnsNodes();
            }
        }
    }

    public int getFrozenRows() {
        return frozenRows;
    }

    public void setFrozenRows(int aValue) {
        if (aValue >= 0 && frozenRows != aValue) {
            frozenRows = aValue;
            setupRanges();
        }
    }

    public boolean isShowOddRowsInOtherColor() {
        return showOddRowsInOtherColor;
    }

    public void setShowOddRowsInOtherColor(boolean aValue) {
        if (showOddRowsInOtherColor != aValue) {
            showOddRowsInOtherColor = aValue;
            regenerateDynamicOddRowsStyles();
        }
    }

    public boolean isDraggableRows() {
        return draggableRows;
    }

    public void setDraggableRows(boolean aValue) {
        if (draggableRows != aValue) {
            draggableRows = aValue;
            for (Section section : new Section[]{frozenLeft, frozenRight, scrollableLeft, scrollableRight}) {
                section.setDraggableRows(aValue);
            }
        }
    }

    public Widget getActiveEditor() {
        return activeEditor;
    }

    public void setActiveEditor(Widget aWidget) {
        activeEditor = aWidget;
    }

    public JavaScriptObject getOnRender() {
        return onRender;
    }

    public void setOnRender(JavaScriptObject aValue) {
        onRender = aValue;
    }

    public JavaScriptObject getOnAfterRender() {
        return onAfterRender;
    }

    public void setOnAfterRender(JavaScriptObject aValue) {
        onAfterRender = aValue;
    }

    public JavaScriptObject getOnExpand() {
        return onExpand;
    }

    public void setOnExpand(JavaScriptObject aValue) {
        onExpand = aValue;
    }

    public JavaScriptObject getOnCollapse() {
        return onCollapse;
    }

    public void setOnCollapse(JavaScriptObject aValue) {
        onCollapse = aValue;
    }

    public String getCursorProperty() {
        return cursorProperty;
    }

    public void setCursorProperty(String aValue) {
        if (aValue != null && !cursorProperty.equals(aValue)) {
            unbind();
            cursorProperty = aValue;
            bind();
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean aValue) {
        deletable = aValue;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(boolean aValue) {
        insertable = aValue;
    }

    protected boolean serviceColumnsRedrawQueued;

    protected void enqueueServiceColumnsRedraw() {
        serviceColumnsRedrawQueued = true;
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                if (serviceColumnsRedrawQueued) {
                    serviceColumnsRedrawQueued = false;
                    if (getColumnCount() > 0) {
                        for (int i = 0; i < getColumnCount(); i++) {
                            Column col = getColumn(i);
                            if (col instanceof UsualServiceColumn) {
                                if (i < frozenColumns) {
                                    frozenLeft.redrawColumn(i);
                                    scrollableLeft.redrawColumn(i);
                                } else {
                                    frozenRight.redrawColumn(i - frozenColumns);
                                    scrollableRight.redrawColumn(i - frozenColumns);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    protected Scheduler.ScheduledCommand redrawQueued;

    private void enqueueRedraw() {
        redrawQueued = new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (redrawQueued == this) {
                    redrawQueued = null;
                    redraw();
                }
            }
        };
        Scheduler.get().scheduleDeferred(redrawQueued);
    }

    public boolean expanded(JavaScriptObject anElement) {
        if (getDataProvider() instanceof JsArrayTreeDataProvider) {
            JsArrayTreeDataProvider treeDataProvider = (JsArrayTreeDataProvider) getDataProvider();
            return treeDataProvider.isExpanded(anElement);
        } else {
            return false;
        }
    }

    public void expand(JavaScriptObject anElement) {
        if (getDataProvider() instanceof JsArrayTreeDataProvider) {
            JsArrayTreeDataProvider treeDataProvider = (JsArrayTreeDataProvider) getDataProvider();
            treeDataProvider.expand(anElement);
        }
    }

    public void collapse(JavaScriptObject anElement) {
        if (getDataProvider() instanceof JsArrayTreeDataProvider) {
            JsArrayTreeDataProvider treeDataProvider = (JsArrayTreeDataProvider) getDataProvider();
            treeDataProvider.collapse(anElement);
        }
    }

    public void toggle(JavaScriptObject anElement) {
        if (getDataProvider() instanceof JsArrayTreeDataProvider) {
            JsArrayTreeDataProvider treeDataProvider = (JsArrayTreeDataProvider) getDataProvider();
            if (treeDataProvider.isExpanded(anElement)) {
                treeDataProvider.collapse(anElement);
            } else {
                treeDataProvider.expand(anElement);
            }
        }
    }

    protected void applyRows() {
        unbindCursor();
        if (sortHandlerReg != null) {
            sortHandlerReg.removeHandler();
        }
        Runnable onResize = new Runnable() {
            @Override
            public void run() {
                setupRanges();
                if (dataProvider instanceof IndexOfProvider<?>) {
                    ((IndexOfProvider<?>) dataProvider).rescan();
                }
                sortHandler.setList(dataProvider != null ? dataProvider.getList() : new ArrayList<JavaScriptObject>());
                if (sortList.size() > 0) {
                    sortList.clear();
                    redrawHeaders();
                }
                if (dataProvider == null || dataProvider.getList() == null || dataProvider.getList().isEmpty()) {
                    clearSelection();
                }
            }

        };
        Runnable onChange = new Runnable() {
            @Override
            public void run() {
                Grid.this.redraw();
            }

        };
        Runnable onSort = new Runnable() {
            @Override
            public void run() {
                if (dataProvider instanceof IndexOfProvider<?>) {
                    ((IndexOfProvider<?>) dataProvider).rescan();
                }
            }

        };
        Object oData = data != null && field != null && !field.isEmpty() ? Utils.getPathData(data, field) : data;
        JavaScriptObject jsData = oData instanceof JavaScriptObject ? (JavaScriptObject) oData : null;
        if (jsData != null) {
            if (isTreeConfigured()) {
                JsArrayTreeDataProvider treeDataProvider = new JsArrayTreeDataProvider(parentField, childrenField,
                        onResize);
                setDataProvider(treeDataProvider);
                sortHandler = new TreeMultiSortHandler<>(treeDataProvider, onSort);
                treeDataProvider.addExpandedHandler(new ExpandedHandler<JavaScriptObject>() {
                    @Override
                    public void expanded(JavaScriptObject anElement) {
                        ColumnSortEvent.fire(ModelGrid.this, sortList);
                        if (onExpand != null) {
                            JavaScriptObject jsEvent = EventsPublisher.publishItemEvent(ModelGrid.this.published,
                                    anElement);
                            try {
                                Utils.executeScriptEventVoid(ModelGrid.this.published, onExpand, jsEvent);
                            } catch (Exception e) {
                                Logger.getLogger(EventsExecutor.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                    }

                });
                treeDataProvider.addCollapsedHandler(new CollapsedHandler<JavaScriptObject>() {
                    @Override
                    public void collapsed(JavaScriptObject anElement) {
                        ColumnSortEvent.fire(ModelGrid.this, sortList);
                        if (onCollapse != null) {
                            JavaScriptObject jsEvent = EventsPublisher.publishItemEvent(ModelGrid.this.published,
                                    anElement);
                            try {
                                Utils.executeScriptEventVoid(ModelGrid.this.published, onCollapse, jsEvent);
                            } catch (Exception e) {
                                Logger.getLogger(EventsExecutor.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                    }

                });
            } else {
                setDataProvider(new JsArrayListDataProvider(onResize, onChange, null));
                sortHandler = new ListMultiSortHandler<>(dataProvider.getList(), onSort);
            }
            for (int colIndex = 0; colIndex < getColumnCount(); colIndex++) {
                Column modelCol = getColumn(colIndex);
                sortHandler.setComparator(modelCol, modelCol.getComparator());
            }
            sortHandlerReg = addColumnSortHandler(sortHandler);
            ((JsDataContainer) getDataProvider()).setData(jsData);
            bindCursor();
        } else {
            setDataProvider(null);
        }
    }

    public boolean makeVisible(JavaScriptObject anElement, boolean aNeedToSelect) {
        IndexOfProvider<JavaScriptObject> indexOfProvider = (IndexOfProvider<JavaScriptObject>) dataProvider;
        int index = indexOfProvider.indexOf(anElement);
        if (index > -1) {
            if (index >= 0 && index < frozenRows) {
                TableCellElement leftCell = frozenLeft.getViewCell(index, 0);
                if (leftCell != null) {
                    leftCell.scrollIntoView();
                } else {
                    TableCellElement rightCell = frozenRight.getViewCell(index, 0);
                    if (rightCell != null) {
                        rightCell.scrollIntoView();
                    }
                }
            } else {
                TableCellElement leftCell = scrollableLeft.getViewCell(index, 0);
                if (leftCell != null) {
                    leftCell.scrollIntoView();
                } else {
                    TableCellElement rightCell = scrollableRight.getViewCell(index, 0);
                    if (rightCell != null) {
                        rightCell.scrollIntoView();
                    }
                }
            }
            if (aNeedToSelect) {
                clearSelection();
                select(anElement);
                if (index >= 0 && index < frozenRows) {
                    frozenLeft.setKeyboardSelectedRow(index, true);
                    frozenRight.setKeyboardSelectedRow(index, true);
                } else {
                    scrollableLeft.setKeyboardSelectedRow(index - frozenRows, true);
                    scrollableRight.setKeyboardSelectedRow(index - frozenRows, true);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void rebind() {
        unbind();
        bind();
    }

    protected void bind() {
        if (data != null) {
            applyRows();
            if (field != null && !field.isEmpty()) {
                boundToData = Utils.listenPath(data, field, new Utils.OnChangeHandler() {

                    @Override
                    public void onChange(JavaScriptObject anEvent) {
                        applyRows();
                    }
                });
            }
        } else {
            applyRows();
            selected.clear();
        }
        setupRanges();
    }

    protected void unbind() {
        if (boundToData != null) {
            boundToData.removeHandler();
            boundToData = null;
        }
        unbindCursor();
    }

    protected void bindCursor() {
        boundToCursor = Utils.listenPath(data, cursorProperty, new Utils.OnChangeHandler() {

            @Override
            public void onChange(JavaScriptObject anEvent) {
                enqueueServiceColumnsRedraw();
            }

        });
    }

    protected void unbindCursor() {
        if (boundToCursor != null) {
            boundToCursor.removeHandler();
            boundToCursor = null;
        }
    }

    @Override
    public JavaScriptObject getData() {
        return data;
    }

    @Override
    public void setData(JavaScriptObject aValue) {
        if (data != aValue) {
            unbind();
            data = aValue;
            bind();
        }
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setField(String aValue) {
        if (field == null ? aValue != null : !field.equals(aValue)) {
            unbind();
            field = aValue;
            bind();
        }
    }

    public String getParentField() {
        return parentField;
    }

    public void setParentField(String aValue) {
        if (parentField == null ? aValue != null : !parentField.equals(aValue)) {
            boolean wasTree = isTreeConfigured();
            parentField = aValue;
            boolean isTree = isTreeConfigured();
            if (wasTree != isTree) {
                applyRows();
            }
        }
    }

    public String getChildrenField() {
        return childrenField;
    }

    public void setChildrenField(String aValue) {
        if (childrenField == null ? aValue != null : !childrenField.equals(aValue)) {
            boolean wasTree = isTreeConfigured();
            childrenField = aValue;
            boolean isTree = isTreeConfigured();
            if (wasTree != isTree) {
                applyRows();
            }
        }
    }

    public final boolean isTreeConfigured() {
        return parentField != null && !parentField.isEmpty() && childrenField != null && !childrenField.isEmpty();
    }

    public void setupRanges() {
        frozenLeft.setRange(0, frozenRows);
        frozenRight.setRange(0, frozenRows);
        scrollableLeft.setRange(frozenRows, getDataRowsCount()/* rows.length */);
        scrollableRight.setRange(frozenRows, getDataRowsCount()/* rows.length */);
    }

    private int getDataRowsCount() {
        return 0;
    }

    protected Column treeIndicatorColumn;

    private void checkTreeIndicatorColumn() {
        if (isTreeConfigured()) {
            if (treeIndicatorColumn == null) {
                int treeIndicatorIndex = 0;
                while (treeIndicatorIndex < getColumnCount()) {
                    Column indicatorColumn = getColumn(treeIndicatorIndex);
                    if (indicatorColumn instanceof UsualServiceColumn || indicatorColumn instanceof RadioServiceColumn
                            || indicatorColumn instanceof CheckServiceColumn) {
                        treeIndicatorIndex++;
                    } else if (indicatorColumn instanceof Column) {
                        treeIndicatorColumn = indicatorColumn;
                        break;
                    }
                }
            }
        } else {
            treeIndicatorColumn = null;
        }
    }

    protected void clearColumnsNodes() {
        clearColumnsNodes(true);
    }

    protected void clearColumnsNodes(boolean needRedraw) {
        for (int i = getColumnCount() - 1; i >= 0; i--) {
            Column toDel = getColumn(i);
            Column mCol = (Column) toDel;
            if (mCol == treeIndicatorColumn) {
                treeIndicatorColumn = null;
            }
            mCol.setGrid(null);
        }
        headerLeft.clearColumnsAndHeader(needRedraw);
        headerRight.clearColumnsAndHeader(needRedraw);
    }

    public void applyColumnsNodes() {
        clearColumnsNodes(false);
        List<HeaderNode> leaves = HeaderAnalyzer.toLeaves(header);
        for (HeaderNode leaf : leaves) { // linear list of columner header nodes
            addColumnToSections(leaf.getColumn());
        }
        checkTreeIndicatorColumn();
        headerLeft.setHeaderNodes(HeaderSplitter.split(header, 0, frozenColumns - 1), false);
        headerRight.setHeaderNodes(HeaderSplitter.split(header, frozenColumns, getColumnCount()), false);
        redraw();
    }

    public List<HeaderNode> getHeader() {
        return header;
    }

    public void setHeader(List<HeaderNode> aHeader) {
        if (header != aHeader) {
            header = aHeader;
            applyColumnsNodes();
        }
    }

    public boolean removeColumnNode(HeaderNode aNode) {
        boolean res = header.remove(aNode);
        if (res) {
            aNode.getColumn().setGrid(null);
            if (treeIndicatorColumn == aNode.getColumn()) {
                treeIndicatorColumn = null;
            }
            applyColumnsNodes();
            return true;
        } else {
            return false;
        }
    }

    public void addColumnNode(HeaderNode aNode) {
        header.add(aNode);
        aNode.getColumn().setGrid(this);
        applyColumnsNodes();
    }

    public void insertColumnNode(int aIndex, HeaderNode aNode) {
        header.add(aIndex, aNode);
        aNode.getColumn().setGrid(this);
        applyColumnsNodes();
    }

    public void moveColumnNode(HeaderNode aSubject, HeaderNode aInsertBefore) {
        if (aSubject != null && aInsertBefore != null && aSubject.getParent() == aInsertBefore.getParent()) {
            List<HeaderNode> neighbours = aSubject.getParent() != null
                    ? aSubject.getParent().getChildren() : header;
            neighbours.remove(aSubject);
            int insertAt = neighbours.indexOf(aInsertBefore);
            neighbours.add(insertAt, aSubject);
            applyColumnsNodes();
        }
    }

    private void addColumnToSections(Column aColumn) {
        if (headerLeft.getColumnCount() < frozenColumns) {
            headerLeft.addColumn(aColumn, false);
            frozenLeft.addColumn(aColumn, false);
            scrollableLeft.addColumn(aColumn, false);
            footerLeft.addColumn(aColumn, false);
        } else {
            headerRight.addColumn(aColumn, false);
            frozenRight.addColumn(aColumn, false);
            scrollableRight.addColumn(aColumn, false);
            footerRight.addColumn(aColumn, false);
        }
    }

    public void redrawRow(int index) {
        frozenLeft.redrawRow(index);
        frozenRight.redrawRow(index);
        scrollableLeft.redrawRow(index);
        scrollableRight.redrawRow(index);
    }

    public void redraw() {
        headerRight.redraw();
        frozenLeft.redraw();
        frozenRight.redraw();
        scrollableLeft.redraw();
        scrollableRight.redraw();
        footerLeft.redraw();
        footerRight.redraw();
    }

    public void redrawHeaders() {
        headerLeft.redrawHeaders();
        headerRight.redrawHeaders();
    }

    public void redrawFooters() {
        footerLeft.redrawFooters();
        footerRight.redrawFooters();
    }

    public int getColumnCount() {
        return (headerLeft != null ? headerLeft.getColumnCount() : 0)
                + (headerRight != null ? headerRight.getColumnCount() : 0);
    }

    public Column getColumn(int aIndex) {
        if (aIndex >= 0 && aIndex < getColumnCount()) {
            return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getColumn(aIndex)
                    : headerRight.getColumn(aIndex - headerLeft.getColumnCount());
        } else {
            return null;
        }
    }

    public TableCellElement getViewCell(int aRow, int aCol) {
        Section targetSection;
        if (aRow < frozenRows) {
            if (aCol < frozenColumns) {
                targetSection = frozenLeft;
            } else {
                targetSection = frozenRight;
            }
        } else {
            if (aCol < frozenColumns) {
                targetSection = scrollableLeft;
            } else {
                targetSection = scrollableRight;
            }
        }
        return targetSection.getViewCell(aRow, aCol);
    }

    public void focusViewCell(int aRow, int aCol) {
        Section targetSection;
        if (aRow < frozenRows) {
            if (aCol < frozenColumns) {
                targetSection = frozenLeft;
            } else {
                targetSection = frozenRight;
            }
        } else {
            if (aCol < frozenColumns) {
                targetSection = scrollableLeft;
            } else {
                targetSection = scrollableRight;
            }
        }
        targetSection.focusCell(aRow, aCol);
    }

    public void sort() {
        sort(true);
    }

    public void sort(boolean needRedraw) {
        // var rows = getRows();
        // sortedRows = rows.slice(0, data.length);
        // sortedRows.sort(function(i1, i2){
        //     var res = 0;
        //     var index = 0;
        //     while(res === 0 && index < getColumnsCount()){
        //         var column = getColumn(index++);
        //         if(column.comparator != null){
        //             res = column.comparator(i1, i2);
        //         }
        //     }
        //     return res;
        // });
        // if(needRedraw){
        //     redraw();
        //     fireRowsSorted();
        // }
    }

    public void unsort() {
        unsort(true);
    }

    public void unsort(boolean needRedraw) {
        for (int i = 0; i < getColumnCount(); i++) {
            Column column = getColumn(i);
            column.unsort(false);
        }
        // if(needRedraw){
        //     redraw();
        //     fireRowsSorted();
        // }
    }

    protected int tabIndex;

    protected Element calcFocusedElement() {
        Element focusedEelement = scrollableRight.getKeyboardSelectedElement();
        if (focusedEelement == null) {
            focusedEelement = scrollableLeft.getKeyboardSelectedElement();
        }
        if (focusedEelement == null) {
            focusedEelement = frozenLeft.getKeyboardSelectedElement();
        }
        if (focusedEelement == null) {
            focusedEelement = frozenRight.getKeyboardSelectedElement();
        }
        if (focusedEelement == null) {
            focusedEelement = getElement();
        }
        return focusedEelement;
    }

    @Override
    public int getTabIndex() {
        return tabIndex;
    }

    @Override
    public void setFocus(boolean focused) {
        Element focusedElement = calcFocusedElement();
        focusedElement.setTabIndex(tabIndex);
        if (focused) {
            focusedElement.focus();
        } else {
            focusedElement.blur();
        }
    }

    @Override
    public void setTabIndex(int index) {
        tabIndex = index;
        Element focusedElement = calcFocusedElement();
        focusedElement.setTabIndex(tabIndex);
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(Grid aWidget, JavaScriptObject aPublished)/*-{
        aPublished.select = function(aRow) {
            if (aRow != null && aRow != undefined)
                aWidget.@com.eas.grid.ModelGrid::selectElement(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
            };
        aPublished.unselect = function(aRow) {
            if (aRow != null && aRow != undefined)
                aWidget.@com.eas.grid.ModelGrid::unselectElement(Lcom/google/gwt/core/client/JavaScriptObject;)(aRow);
        };
        aPublished.clearSelection = function() {
            aWidget.@com.eas.grid.ModelGrid::clearSelection()();
        };
        aPublished.find = function() {
            aWidget.@com.eas.grid.ModelGrid::find()();
        };
        aPublished.findSomething = function() {
            aPublished.find();
        };
        aPublished.makeVisible = function(aInstance, needToSelect) {
            var need2Select = arguments.length > 1 ? !!needToSelect : false;
            if (aInstance != null){
            	// We have to use willBeVisible() instead of makeVisible(), because of
            	// asynchronous nature of grid's cells rendering.
            	// Imagine, that someone requested cells re-rendering already.
            	// In such situation, results of makeVisible call is a zombie.
                aWidget.@com.eas.grid.ModelGrid::willBeVisible(Lcom/google/gwt/core/client/JavaScriptObject;Z)(aInstance, need2Select);
            }
        };
          
        aPublished.expanded = function(aInstance) {
            return aWidget.@com.eas.grid.ModelGrid::expanded(Lcom/google/gwt/core/client/JavaScriptObject;)(aInstance);
        };
          
        aPublished.expand = function(aInstance) {
            aWidget.@com.eas.grid.ModelGrid::expand(Lcom/google/gwt/core/client/JavaScriptObject;)(aInstance);
        };
          
        aPublished.collapse = function(aInstance) {
            aWidget.@com.eas.grid.ModelGrid::collapse(Lcom/google/gwt/core/client/JavaScriptObject;)(aInstance);
        };
          
        aPublished.toggle = function(aInstance) {
            aWidget.@com.eas.grid.ModelGrid::toggle(Lcom/google/gwt/core/client/JavaScriptObject;)(aInstance);
        };
          
        aPublished.unsort = function() {
            aWidget.@com.eas.grid.ModelGrid::unsort()();
        };
          
        aPublished.redraw = function() {
            // ModelGrid.redraw() is used as a rebinder in applications.
            // Because applications don't care about grid's dataProviders' internal
            // data structures, such as JsArrayDataProvider and its internal list of elements.
          	// So, redraw grid through rebinding of its data.
            aWidget.@com.eas.grid.ModelGrid::rebind()();
        };
        aPublished.changed = function(aItems){
            if(!$wnd.Array.isArray(aItems))
                aItems = [aItems];
            aWidget.@com.eas.grid.ModelGrid::changedItems(Lcom/google/gwt/core/client/JavaScriptObject;)(aItems);
        };
        aPublished.added = function(aItems){
            if(!$wnd.Array.isArray(aItems))
                aItems = [aItems];
            aWidget.@com.eas.grid.ModelGrid::addedItems(Lcom/google/gwt/core/client/JavaScriptObject;)(aItems);
        };
        aPublished.removed = function(aItems){
            if(!$wnd.Array.isArray(aItems))
                aItems = [aItems];
            aWidget.@com.eas.grid.ModelGrid::removedItems(Lcom/google/gwt/core/client/JavaScriptObject;)(aItems);
        };
          
        aPublished.removeColumnNode = function(aColumnFacade){
            if(aColumnFacade && aColumnFacade.unwrap)
                return aWidget.@com.eas.grid.ModelGrid::removeColumnNode(Lcom/eas/grid/columns/header/HeaderNode;)(aColumnFacade.unwrap());
            else
              return false;
        };
        aPublished.addColumnNode = function(aColumnFacade){
            if(aColumnFacade && aColumnFacade.unwrap)
                aWidget.@com.eas.grid.ModelGrid::addColumnNode(Lcom/eas/grid/columns/header/HeaderNode;)(aColumnFacade.unwrap());
        };
        aPublished.insertColumnNode = function(aIndex, aColumnFacade){
            if(aColumnFacade && aColumnFacade.unwrap)
                aWidget.@com.eas.grid.ModelGrid::insertColumnNode(ILcom/eas/grid/columns/header/HeaderNode;)(aIndex, aColumnFacade.unwrap());
        };
        aPublished.columnNodes = function(){
            var headerRoots = aWidget.@com.eas.grid.ModelGrid::getHeader()();
            var rootsCount = headerRoots.@java.util.List::size()();
            var res = [];
            for(var r = 0; r < rootsCount; r++){
                var nNode = headerRoots.@java.util.List::get(I)(r);
                var jsNode = nNode.@com.eas.core.HasPublished::getPublished()();
                res.push(jsNode);
            }
            return res;
        };
        Object.defineProperty(aPublished, "headerVisible", {
            get : function() {
              return aWidget.@com.eas.grid.ModelGrid::isHeaderVisible()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setHeaderVisible(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "draggableRows", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isDraggableRows()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setDraggableRows(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "frozenRows", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getFrozenRows()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setFrozenRows(I)(+aValue);
            }
        });
        Object.defineProperty(aPublished, "frozenColumns", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getFrozenColumns()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setFrozenColumns(I)(+aValue);
            }
        });
        Object.defineProperty(aPublished, "rowsHeight", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getRowsHeight()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setRowsHeight(I)(aValue * 1);
            }
        });
        Object.defineProperty(aPublished, "showHorizontalLines", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isShowHorizontalLines()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setShowHorizontalLines(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "showVerticalLines", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isShowVerticalLines()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setShowVerticalLines(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "showOddRowsInOtherColor", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isShowOddRowsInOtherColor()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setShowOddRowsInOtherColor(Z)(!!aValue);
            }
        });
        Object.defineProperty(aPublished, "gridColor", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getGridColor()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setGridColor(Lcom/eas/ui/PublishedColor;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "oddRowsColor", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOddRowsColor()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOddRowsColor(Lcom/eas/ui/PublishedColor;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "cursorProperty", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getCursorProperty()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setCursorProperty(Ljava/lang/String;)(aValue ? '' + aValue : null);
            }
        });
        
        Object.defineProperty(aPublished, "onRender", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOnRender()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "onAfterRender", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOnAfterRender()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOnAfterRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "onExpand", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOnExpand()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOnExpand(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "onCollapse", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getOnCollapse()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setOnCollapse(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
          });
        Object.defineProperty(aPublished, "selected", {
            get : function() {
                var selectionList = aWidget.@com.eas.grid.ModelGrid::getJsSelected()();
                var selectionArray = [];
                for ( var i = 0; i < selectionList.@java.util.List::size()(); i++) {
                    selectionArray[selectionArray.length] = selectionList.@java.util.List::get(I)(i);
                }
                return selectionArray;
            }
        });
        Object.defineProperty(aPublished, "editable", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isEditable()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setEditable(Z)(aValue);
            }
        });
        Object.defineProperty(aPublished, "deletable", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isDeletable()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setDeletable(Z)(aValue);
            }
        });
        Object.defineProperty(aPublished, "insertable", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::isInsertable()();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setInsertable(Z)(aValue);
            }
        });
        Object.defineProperty(aPublished, "data", {
            get : function() {
                return aWidget.@com.eas.ui.HasBinding::getData()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasBinding::setData(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(aPublished, "field", {
            get : function() {
                return aWidget.@com.eas.ui.HasBinding::getField()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasBinding::setField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(aPublished, "parentField", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getParentField();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setParentField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(aPublished, "childrenField", {
            get : function() {
                return aWidget.@com.eas.grid.ModelGrid::getChildrenField();
            },
            set : function(aValue) {
                aWidget.@com.eas.grid.ModelGrid::setChildrenField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
    }-*/;
}
