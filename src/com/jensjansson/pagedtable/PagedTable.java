package com.jensjansson.pagedtable;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Reindeer;

public class PagedTable extends Table {

    interface PageChangeListener {
        public void pageChanged(PagedTableChangeEvent event);
    }

    public class PagedTableChangeEvent {

        final PagedTable table;

        public PagedTableChangeEvent(PagedTable table) {
            this.table = table;
        }

        public PagedTable getTable() {
            return table;
        }

        public int getCurrentPage() {
            return table.getCurrentPage();
        }

        public int getTotalAmountOfPages() {
            return table.getTotalAmountOfPages();
        }
    }

    private static final long serialVersionUID = 6881455780158545828L;
    // first item shown in the view for the momemnt
    int index = 0;
    List<PageChangeListener> listeners = null;

    // Real container
    Container.Indexed realContainer;
    IndexedContainer shownContainer = new IndexedContainer();

    public PagedTable() {
        this(null);
    }

    public PagedTable(String caption) {
        super(caption);
        setPageLength(25);
        addStyleName("pagedtable");
        alwaysRecalculateColumnWidths = true;
    }

    public HorizontalLayout createControls() {
        Label itemsPerPageLabel = new Label("Items per page:");
        final ComboBox itemsPerPageSelect = new ComboBox();

        itemsPerPageSelect.addItem("5");
        itemsPerPageSelect.addItem("10");
        itemsPerPageSelect.addItem("25");
        itemsPerPageSelect.addItem("50");
        itemsPerPageSelect.addItem("100");
        itemsPerPageSelect.setImmediate(true);
        itemsPerPageSelect.setNullSelectionAllowed(false);
        itemsPerPageSelect.setWidth("50px");
        itemsPerPageSelect.addListener(new ValueChangeListener() {
            private static final long serialVersionUID = -2255853716069800092L;

            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                setPageLength(Integer.valueOf(String.valueOf(event
                        .getProperty().getValue())));
            }
        });
        itemsPerPageSelect.select("25");
        Label pageLabel = new Label("Page:&nbsp;", Label.CONTENT_XHTML);
        final TextField currentPageTextField = new TextField();
        currentPageTextField.setValue(String.valueOf(getCurrentPage()));
        currentPageTextField.addValidator(new IntegerValidator(null));
        Label separatorLabel = new Label("&nbsp;/&nbsp;", Label.CONTENT_XHTML);
        final Label totalPagesLabel = new Label(
                String.valueOf(getTotalAmountOfPages()), Label.CONTENT_XHTML);
        currentPageTextField.setStyleName(Reindeer.TEXTFIELD_SMALL);
        currentPageTextField.setImmediate(true);
        currentPageTextField.addListener(new ValueChangeListener() {
            private static final long serialVersionUID = -2255853716069800092L;

            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                if (currentPageTextField.isValid()
                        && currentPageTextField.getValue() != null) {
                    int page = Integer.valueOf(String
                            .valueOf(currentPageTextField.getValue()));
                    setCurrentPage(page);
                }
            }
        });
        pageLabel.setWidth(null);
        currentPageTextField.setWidth("20px");
        separatorLabel.setWidth(null);
        totalPagesLabel.setWidth(null);

        HorizontalLayout controlBar = new HorizontalLayout();
        HorizontalLayout pageSize = new HorizontalLayout();
        HorizontalLayout pageManagement = new HorizontalLayout();
        final Button first = new Button("<<", new ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

            public void buttonClick(ClickEvent event) {
                setCurrentPage(0);
            }
        });
        final Button previous = new Button("<", new ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

            public void buttonClick(ClickEvent event) {
                previousPage();
            }
        });
        final Button next = new Button(">", new ClickListener() {
            private static final long serialVersionUID = -1927138212640638452L;

            public void buttonClick(ClickEvent event) {
                nextPage();
            }
        });
        final Button last = new Button(">>", new ClickListener() {
            private static final long serialVersionUID = -355520120491283992L;

            public void buttonClick(ClickEvent event) {
                setCurrentPage(getTotalAmountOfPages());
            }
        });
        first.setStyleName(Reindeer.BUTTON_LINK);
        previous.setStyleName(Reindeer.BUTTON_LINK);
        next.setStyleName(Reindeer.BUTTON_LINK);
        last.setStyleName(Reindeer.BUTTON_LINK);

        itemsPerPageLabel.addStyleName("pagedtable-itemsperpagecaption");
        itemsPerPageSelect.addStyleName("pagedtable-itemsperpagecombobox");
        pageLabel.addStyleName("pagedtable-pagecaption");
        currentPageTextField.addStyleName("pagedtable-pagefield");
        separatorLabel.addStyleName("pagedtable-separator");
        totalPagesLabel.addStyleName("pagedtable-total");
        first.addStyleName("pagedtable-first");
        previous.addStyleName("pagedtable-previous");
        next.addStyleName("pagedtable-next");
        last.addStyleName("pagedtable-last");

        itemsPerPageLabel.addStyleName("pagedtable-label");
        itemsPerPageSelect.addStyleName("pagedtable-combobox");
        pageLabel.addStyleName("pagedtable-label");
        currentPageTextField.addStyleName("pagedtable-label");
        separatorLabel.addStyleName("pagedtable-label");
        totalPagesLabel.addStyleName("pagedtable-label");
        first.addStyleName("pagedtable-button");
        previous.addStyleName("pagedtable-button");
        next.addStyleName("pagedtable-button");
        last.addStyleName("pagedtable-button");

        pageSize.addComponent(itemsPerPageLabel);
        pageSize.addComponent(itemsPerPageSelect);
        pageSize.setComponentAlignment(itemsPerPageLabel, Alignment.MIDDLE_LEFT);
        pageSize.setComponentAlignment(itemsPerPageSelect,
                Alignment.MIDDLE_LEFT);
        pageSize.setSpacing(true);
        pageManagement.addComponent(first);
        pageManagement.addComponent(previous);
        pageManagement.addComponent(pageLabel);
        pageManagement.addComponent(currentPageTextField);
        pageManagement.addComponent(separatorLabel);
        pageManagement.addComponent(totalPagesLabel);
        pageManagement.addComponent(next);
        pageManagement.addComponent(last);
        pageManagement.setComponentAlignment(first, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(pageLabel, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(currentPageTextField,
                Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(separatorLabel,
                Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(totalPagesLabel,
                Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(next, Alignment.MIDDLE_LEFT);
        pageManagement.setComponentAlignment(last, Alignment.MIDDLE_LEFT);
        pageManagement.setWidth(null);
        pageManagement.setSpacing(true);
        controlBar.addComponent(pageSize);
        controlBar.addComponent(pageManagement);
        controlBar.setComponentAlignment(pageManagement,
                Alignment.MIDDLE_CENTER);
        controlBar.setWidth("100%");
        controlBar.setExpandRatio(pageSize, 1);
        addListener(new PageChangeListener() {
            public void pageChanged(PagedTableChangeEvent event) {
                previous.setEnabled(true);
                next.setEnabled(true);
                currentPageTextField.setValue(String.valueOf(getCurrentPage()));
                totalPagesLabel.setValue(getTotalAmountOfPages());
                itemsPerPageSelect.setValue(String.valueOf(getPageLength()));
            }
        });
        return controlBar;
    }

    @Override
    public Container.Indexed getContainerDataSource() {
        return realContainer;
    }

    @Override
    public void setContainerDataSource(Container newDataSource) {
        if (!(newDataSource instanceof Container.Indexed)) {
            throw new IllegalArgumentException(
                    "PagedTable can only use containers that implement Container.Indexed");
        }
        Container.Indexed realContainer = (Container.Indexed) newDataSource;
        this.realContainer = realContainer;
        shownContainer = new IndexedContainer();
        for (Object object : realContainer.getContainerPropertyIds()) {
            shownContainer.addContainerProperty(object,
                    realContainer.getType(object), null);
        }
        fillVisibleContainer(0);
        super.setContainerDataSource(shownContainer);
    }

    private void setPageFirstIndex(int firstIndex) {
        if (realContainer != null) {
            if (firstIndex <= 0) {
                firstIndex = 0;
            }
            if (firstIndex > realContainer.size() - 1) {
                int size = realContainer.size() - 1;
                int pages = 0;
                if (getPageLength() != 0) {
                    pages = (int) Math.floor(0.0 + size / getPageLength());
                }
                firstIndex = pages * getPageLength();
            }
            shownContainer
                    .removeListener((Container.ItemSetChangeListener) this);
            fillVisibleContainer(firstIndex);
            shownContainer.addListener((Container.ItemSetChangeListener) this);
            containerItemSetChange(new Container.ItemSetChangeEvent() {
                private static final long serialVersionUID = -5083660879306951876L;

                public Container getContainer() {
                    return shownContainer;
                }
            });
        }
    }

    private void fillVisibleContainer(int firstIndex) {
        shownContainer.removeAllItems();
        if (realContainer.size() != 0) {
            Object itemId = realContainer.getIdByIndex(firstIndex);
            addShownItem(itemId);
            for (int i = 1; i < getPageLength(); i++) {
                itemId = realContainer.nextItemId(itemId);
                if (itemId == null) {
                    break;
                }
                addShownItem(itemId);
            }
        }
        index = firstIndex;
        if (listeners != null) {
            PagedTableChangeEvent event = new PagedTableChangeEvent(this);
            for (PageChangeListener listener : listeners) {
                listener.pageChanged(event);
            }
        }
    }

    private void addShownItem(Object itemId) {
        Item realItem = realContainer.getItem(itemId);
        Item shownItem = shownContainer.addItem(itemId);
        for (Object property : realContainer.getContainerPropertyIds()) {
            shownItem.getItemProperty(property).setValue(
                    realItem.getItemProperty(property).getValue());
        }
    }

    @Override
    public void setPageLength(int pageLength) {
        if (pageLength >= 0 && getPageLength() != pageLength) {
            super.setPageLength(pageLength);
            setPageFirstIndex(index);
        }
    }

    public void nextPage() {
        setPageFirstIndex(index + getPageLength());
    }

    public void previousPage() {
        setPageFirstIndex(index - getPageLength());
    }

    public int getCurrentPage() {
        double pageLength = getPageLength();
        int page = (int) Math.floor(index / pageLength) + 1;
        if (page < 1) {
            page = 1;
        }
        return page;
    }

    public void setCurrentPage(int page) {
        int newIndex = (page - 1) * getPageLength();
        if (newIndex < 0) {
            newIndex = 0;
        }
        if (newIndex >= 0 && newIndex != index) {
            setPageFirstIndex(newIndex);
        }
    }

    public int getTotalAmountOfPages() {
        int size = realContainer.size();
        double pageLength = getPageLength();
        int pageCount = (int) Math.ceil(size / pageLength);
        if (pageCount < 1) {
            pageCount = 1;
        }
        return pageCount;
    }

    public void addListener(PageChangeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<PageChangeListener>();
        }
        listeners.add(listener);
    }

    public void removeListener(PageChangeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<PageChangeListener>();
        }
        listeners.remove(listener);
    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending)
            throws UnsupportedOperationException {
        if (realContainer instanceof Container.Sortable) {
            ((Container.Sortable) realContainer).sort(propertyId, ascending);
        } else if (realContainer != null) {
            throw new UnsupportedOperationException(
                    "Underlying Data does not allow sorting");
        }
        setPageFirstIndex(index);
    }
}