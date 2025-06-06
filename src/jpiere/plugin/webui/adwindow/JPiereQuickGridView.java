/******************************************************************************
 * Copyright (C) 2016 Logilite Technologies LLP								  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package jpiere.plugin.webui.adwindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

import org.adempiere.base.Core;
import org.adempiere.model.MTabCustomization;
import org.adempiere.util.Callback;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
//import org.adempiere.webui.apps.form.IQuickForm; //JPIERE
import org.adempiere.webui.adwindow.ADWindowToolbar;
import org.adempiere.webui.adwindow.GridTabRowRenderer;
import org.adempiere.webui.adwindow.GridTableListModel;
import org.adempiere.webui.adwindow.IFieldEditorContainer;
import org.adempiere.webui.adwindow.QuickGridTabRowRenderer;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Searchbox;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.SortComparator;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.GridTable;
import org.compiere.model.MSysConfig;
import org.compiere.model.StateChangeEvent;
import org.compiere.model.StateChangeListener;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkforge.keylistener.Keylistener;
import org.zkoss.lang.Library;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Div;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.impl.CustomGridDataLoader;

import jpiere.plugin.webui.window.form.JPiereWQuickForm;

/**
 * Quick Grid view implemented using the Grid component (Base on {@link GridView}).
 * 
 * @author Logilite Technologies
 * @since Nov 03, 2017
 */
public class JPiereQuickGridView extends Vbox
		implements EventListener<Event>, IdSpace, IFieldEditorContainer, StateChangeListener {
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 228387400133234920L;

	static CLogger log = CLogger.getCLogger(JPiereQuickGridView.class);

	/** Style for Grid and Grid Footer **/
	private static final String HEADER_GRID_STYLE = "border: none; margin:0; padding: 0;";

	/** default paging size **/
	private static final int DEFAULT_PAGE_SIZE = 20;

	/** default paging size for mobile client **/
	private static final int DEFAULT_MOBILE_PAGE_SIZE = 20;
	
	/** minimum column width **/
	private static final int MIN_COLUMN_WIDTH = 100;

	/** maximum column width **/
	private static final int MAX_COLUMN_WIDTH = 300;

	/** minimum column width for combobox field **/
	private static final int MIN_COMBOBOX_WIDTH = 160;

	/** minimum column width for numeric field **/
	private static final int MIN_NUMERIC_COL_WIDTH = 120;

	public static final int SALES_ORDER_LINE_TAB_ID = 187;

	/** GridView boolean attribute to indicate ON_POST_SELECTED_ROW_CHANGED_EVENT have been posted in current execution cycle **/
	private static final String ATTR_ON_POST_SELECTED_ROW_CHANGED = "org.adempiere.webui.adwindow.GridView.onPostSelectedRowChanged";

	/** shortcut key for cell navigation **/
	public static final String		CNTRL_KEYS							= "#left#right#up#down#home@k@r";

	// 'Enter' Work as Down key
	private static final int		KEYBOARD_KEY_ENTER					= 13;

	// Event Listeners
	public static final String		EVENT_ON_SELECT_ROW					= "onSelectRow";
	public static final String		EVENT_ON_CUSTOMIZE_GRID				= "onCustomizeGrid";
	public static final String		EVENT_ON_PAGE_NAVIGATE				= "onPageNavigate";
	public static final String		EVENT_ON_CLICK_TO_NAVIGATE			= "onClickToNavigate";
	public static final String		EVENT_ON_SET_FOCUS_TO_FIRST_CELL	= "onSetFocusToFirstCell";
	public static final String		EVENT_ON_AFTER_SAVE					= "onAfterSave";
	public static final String		EVENT_ONFOCUS_AFTER_SAVE			= "onFocusAfterSave";
	
	// Code for navigation to setcurrentcell
	public static final int			NAVIGATE_CODE						= 1;
	public static final int			FOCUS_CODE							= 0;
	
	/** data grid instance **/
	private Grid listbox = null;

	private int pageSize = DEFAULT_PAGE_SIZE;

	/**
	 * List field display in grid mode. If there are user customize grid record,
	 * this list contain only the customized display list.
	 */
	private GridField[] gridFields;
	
	/** GridTable model for GridTab **/
	private AbstractTableModel tableModel;

	private int numColumns = 5;

	private int windowNo;

	/** GridTab that back this QuickGridView **/
	private GridTab gridTab;

	/** true if this QuickGridView instance have been init with GridTab **/
	private boolean init;

	/** Zk List model for {@link #tableModel} **/
	public GridTableListModel listModel;

	public Paging paging;

	/** Row renderer for this QuickGridView instance **/
	private JPiereQuickGridTabRowRenderer renderer;

	/** Footer for paging **/
	private Div gridFooter;

	/** true if current row is always in edit mode **/
	private boolean modeless = true;

	/** AD window content part that own this GridView instance **/
	private JPiereAbstractADWindowContent windowPanel;

	/** true when grid is refreshing its data **/
	private boolean refreshing;

	/** AD_Field_ID:Column Width **/
	private Map<Integer, String> columnWidthMap;

	/** checkbox to select all row of current page **/
	protected Checkbox selectAll;
	
	/** true if there are AD_Tab_Customization for GridTab **/
	protected boolean isHasCustomizeData = false;

	/** reference to desktop key listener **/
	protected Keylistener keyListener;

	/** true if no new row or new row have been saved **/
	public boolean isNewLineSaved = true;
	
	// Prevent focus change until render is complete
	private boolean isOnFocusAfterSave = false;
	// To prevent 'onFocus' event fire twice on same component.
	private Component preEventComponent;

	/** IQuickForm that own this QuickGridView instance **/
	public JPiereWQuickForm				quickForm;

	/**
	 * List field display in grid mode. If there are user customize grid record,
	 * this list contain only the customized display list.
	 * @return GridField[]
	 */
	public GridField[] getGridField() {
		return gridFields;
	}

	/**
	 * @param gridField
	 */
	public void setGridField(GridField[] gridField) {
		this.gridFields = gridField;
	}

	/**
	 * @return {@link QuickGridTabRowRenderer}
	 */
	public JPiereQuickGridTabRowRenderer getRenderer() {//JPIERE
		return renderer;
	}

	/**
	 * @param windowNo
	 */
	public JPiereQuickGridView(int windowNo)
	{
		this.windowNo = windowNo;
		setId("quickGridView");
		createListbox();

		ZKUpdateUtil.setHflex(this, "1");
		
		gridFooter = new Div();
		ZKUpdateUtil.setVflex(gridFooter, "0");
		
		//default paging size
		if (ClientInfo.isMobile())
		{
			//Should be <= 20 on mobile
			pageSize = MSysConfig.getIntValue(MSysConfig.ZK_MOBILE_PAGING_SIZE, DEFAULT_MOBILE_PAGE_SIZE, Env.getAD_Client_ID(Env.getCtx()));
			String limit = Library.getProperty(CustomGridDataLoader.GRID_DATA_LOADER_LIMIT);
			if (limit == null || !(limit.equals(Integer.toString(pageSize)))) {
				Library.setProperty(CustomGridDataLoader.GRID_DATA_LOADER_LIMIT, Integer.toString(pageSize));
			}
		}
		else
		{
			pageSize = MSysConfig.getIntValue(MSysConfig.QUICKFORM_PAGE_SIZE, DEFAULT_PAGE_SIZE, Env.getAD_Client_ID(Env.getCtx()));
			String limit = Library.getProperty(CustomGridDataLoader.GRID_DATA_LOADER_LIMIT);
			if (limit == null || !(limit.equals(Integer.toString(pageSize)))) {
				Library.setProperty(CustomGridDataLoader.GRID_DATA_LOADER_LIMIT, Integer.toString(pageSize));
			}
		}
		//default true for better UI experience
		if (ClientInfo.isMobile())
			modeless = MSysConfig.getBooleanValue(MSysConfig.ZK_GRID_MOBILE_EDIT_MODELESS, false) && MSysConfig.getBooleanValue(MSysConfig.ZK_GRID_MOBILE_EDITABLE, false);
		else
			modeless = MSysConfig.getBooleanValue(MSysConfig.ZK_GRID_EDIT_MODELESS, true);
		
		appendChild(listbox);
		appendChild(gridFooter);								
		ZKUpdateUtil.setVflex(this, "true");
		
		setStyle(HEADER_GRID_STYLE);
		gridFooter.setStyle(HEADER_GRID_STYLE);
		
		addEventListener(EVENT_ON_SELECT_ROW, this);
		addEventListener(EVENT_ON_CUSTOMIZE_GRID, this);
		addEventListener(EVENT_ON_PAGE_NAVIGATE, this);
		addEventListener(EVENT_ON_CLICK_TO_NAVIGATE, this);
		addEventListener(EVENT_ON_SET_FOCUS_TO_FIRST_CELL, this);
		addEventListener(EVENT_ON_AFTER_SAVE, this);
		addEventListener(EVENT_ONFOCUS_AFTER_SAVE, this);
	}

	/**
	 * @param abstractADWindowContent
	 * @param gridTab
	 * @param wQuickForm
	 */
	public JPiereQuickGridView(JPiereAbstractADWindowContent abstractADWindowContent, GridTab gridTab, JPiereWQuickForm wQuickForm)
	{
		this(abstractADWindowContent.getWindowNo());
		setADWindowPanel(abstractADWindowContent);
		this.quickForm = wQuickForm;
		init(gridTab);
	}

	/**
	 * create data grid instances
	 */
	protected void createListbox()
	{
		listbox = new Grid();
		listbox.setSizedByContent(false);
		ZKUpdateUtil.setVflex(listbox, "1");
		ZKUpdateUtil.setHflex(listbox, "1");
		listbox.setSclass("adtab-grid");
		listbox.setEmptyMessage(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Processing")));
	}
	
	/**
	 * Init data grid
	 * @param gridTab
	 */
	public void init(GridTab gridTab)
	{
		if (init) return;

		if (this.gridTab != null)
			this.gridTab.removeStateChangeListener(this);
		
		setupFields(gridTab);

		setupColumns();
		render();

		updateListIndex();

		this.init = true;
	}

	/**
	 * Setup {@link #gridFields} from gridTab.
	 * @param gridTab
	 */
	private void setupFields(GridTab gridTab) {		
		this.gridTab = gridTab;		
		gridTab.addStateChangeListener(this);
		
		tableModel = gridTab.getTableModel();
		columnWidthMap = new HashMap<Integer, String>();
		GridField[] modelFields = ((GridTable)tableModel).getFields();
		MTabCustomization tabCustomization = MTabCustomization.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()), gridTab.getAD_Tab_ID(), null, true);
		isHasCustomizeData = tabCustomization != null && tabCustomization.getAD_Tab_Customization_ID() > 0 
				&& tabCustomization.getCustom() != null && tabCustomization.getCustom().trim().length() > 0;
		if (isHasCustomizeData) {
			String custom = tabCustomization.getCustom().trim();
			String[] customComponent = custom.split(";");
			String[] fieldIds = customComponent[0].split("[,]");
			List<GridField> gridFieldsList = new ArrayList<GridField>();
			for(String fieldId : fieldIds) {
				fieldId = fieldId.trim();
				int AD_Field_ID=0;
				if (fieldId.length() == 0) continue;
				try
				{
					AD_Field_ID = Integer.parseInt(fieldId);
				}
				catch (NumberFormatException e)
				{
					log.log(Level.SEVERE, "", e);
					continue;
				}
				for(GridField gridField : modelFields) {
					if (gridField.getAD_Field_ID() == AD_Field_ID) {
						// IDEMPIERE-2204 add field in tabCustomization list to
						// display list event this field have showInGrid = false
						// if ((gridField.isDisplayedGrid() ||
						// gridField.isDisplayed())
						// && !gridField.isToolbarOnlyButton())

						// add field in tabCustomization list to display list
						// event this field have showInGrid = false
						if (gridField.isQuickForm())
							gridFieldsList.add(gridField);

						break;
					}
				}
			}
			gridFields = gridFieldsList.toArray(new GridField[0]);
			// if any new column added for quick form.
			for (GridField field : modelFields) {
				if (field.isQuickForm()) {
					boolean isFieldAvailable = false;
					for (GridField gField : gridFields) {
						if (gField.getAD_Field_ID() == field.getAD_Field_ID()) {
							isFieldAvailable = true;
							break;
						}
					}
					if (!isFieldAvailable)
						gridFieldsList.add(field);
				}
			}
			gridFields = gridFieldsList.toArray(new GridField[0]);
			if (customComponent.length == 2) {
				String[] widths = customComponent[1].split("[,]");
				for(int i = 0; i< gridFields.length && i<widths.length; i++) {
					columnWidthMap.put(gridFields[i].getAD_Field_ID(), widths[i]);
				}
			}
		} else {
			ArrayList<GridField> gridFieldsList = new ArrayList<GridField>();
			
			for(GridField field:modelFields){
				if (field.isQuickForm())
				{
					gridFieldsList.add(field);
				}
			}
			
			Collections.sort(gridFieldsList, new Comparator<GridField>() {
				@Override
				public int compare(GridField o1, GridField o2) {
					return o1.getSeqNoGrid()-o2.getSeqNoGrid();
				}
			});
			
			gridFields = new GridField[gridFieldsList.size()];
			gridFieldsList.toArray(gridFields);
		}
		numColumns = gridFields.length;
	}

	/**
	 *
	 * @return true if data grid have been init with GridTab
	 */
	public boolean isInit() {
		return init;
	}

	/**
	 * Refresh data grid (int of quick form or column setup has change)
	 * @param gridTab
	 */
	public void refresh(GridTab gridTab) {
		if (this.gridTab != gridTab || !isInit())
		{
			init = false;
			init(gridTab);
		}
		else
		{
			refreshing = true;
			listbox.setModel(listModel);
			updateListIndex();
			refreshing = false;			
			if (gridTab.getRowCount() == 0 && selectAll.isChecked())
				selectAll.setChecked(false);
		}
	}

	/**
	 * @return true if data grid is refreshing data from GridTab
	 */
	public boolean isRefreshing() {
		return refreshing;
	}

	/**
	 * Update current row index from model
	 */
	public void updateListIndex() {
		if (gridTab == null || !gridTab.isOpen()) return;

		updateEmptyMessage();		
		
		int rowIndex  = gridTab.getCurrentRow();
		if (pageSize > 0) {
			if (paging.getTotalSize() != gridTab.getRowCount())
				paging.setTotalSize(gridTab.getRowCount());
			if (paging.getPageCount() > 1 && !gridFooter.isVisible()) {
				showPagingControl();
			}
			int pgIndex = rowIndex >= 0 ? rowIndex % pageSize : 0;
			int pgNo = rowIndex >= 0 ? (rowIndex - pgIndex) / pageSize : 0;
			if (listModel.getPage() != pgNo) {
				listModel.setPage(pgNo);
			} else if (rowIndex == renderer.getCurrentRowIndex()){
				if (modeless && !renderer.isEditing())
					echoOnPostSelectedRowChanged();
				return;
			} else {
				if (renderer.isEditing()) {
					int editingRow = renderer.getCurrentRowIndex();
					if (editingRow >= 0) {
						int editingPgIndex = editingRow % pageSize;
						int editingPgNo = (editingRow - editingPgIndex) / pageSize;
						if (editingPgNo == pgNo) {
							listModel.updateComponent(renderer.getCurrentRowIndex() % pageSize);
						}
					}
				}
			}
			if (paging.getActivePage() != pgNo) {
				paging.setActivePage(pgNo);
			}
			// Fire event for first record.
			if (rowIndex >= 0 && pgIndex >= 0 && isNewLineSaved || rowIndex == 0 && pgIndex == 0 && !isNewLineSaved)
			{
				echoOnPostSelectedRowChanged();
			}
		} else {
			if (rowIndex >= 0 && isNewLineSaved) {
				echoOnPostSelectedRowChanged();
			}
		}		
	}

	/**
	 * hide paging component
	 */
	private void hidePagingControl() {
		if (gridFooter.isVisible())
			gridFooter.setVisible(false);
	}

	/**
	 * show paging component
	 */
	private void showPagingControl() {
		if (!gridFooter.isVisible())
			gridFooter.setVisible(true);		
	}

	/**
	 * echo ON_POST_SELECTED_ROW_CHANGED_EVENT after current row index has changed
	 */
	protected void echoOnPostSelectedRowChanged() {
		if (getAttribute(ATTR_ON_POST_SELECTED_ROW_CHANGED) == null) {
			setAttribute(ATTR_ON_POST_SELECTED_ROW_CHANGED, Boolean.TRUE);
			Events.echoEvent("onPostSelectedRowChanged", this, null);
		}
	}

	/**
	 * set paging size
	 * @param pageSize
	 */
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	/**
	 * remove all components
	 */
	public void clear()
	{
		this.getChildren().clear();
	}

	/**
	 * Setup {@link Columns} of data grid
	 */
	private void setupColumns()
	{
		if (init) return;

		Columns columns = new Columns();
		
		//frozen not working well on tablet devices yet
		if (!ClientInfo.isMobile())
		{
			Frozen frozen = new Frozen();
			//freeze selection and indicator column
			frozen.setColumns(1);
			listbox.appendChild(frozen);
		}
		
		org.zkoss.zul.Column selection = new Column();
		ZKUpdateUtil.setWidth(selection, "22px");
		try{
			selection.setSort("none");
		} catch (Exception e) {}
		selection.setStyle("border-right: none");
		selectAll = new Checkbox();
		selection.appendChild(selectAll);
		selectAll.setId("selectAll");
		selectAll.addEventListener(Events.ON_CHECK, this);
		columns.appendChild(selection);

		listbox.appendChild(columns);
		columns.setSizable(true);
		columns.setMenupopup("none");
		columns.setColumnsgroup(false);

		Map<Integer, String> colnames = new HashMap<Integer, String>();
		int index = 0;
		
		for (int i = 0; i < numColumns; i++)
		{
			// IDEMPIERE-2148: when has tab customize, ignore check properties isDisplayedGrid
			if (gridFields[i].isQuickForm())
			{
				colnames.put(index, gridFields[i].getHeader());
				index++;
				org.zkoss.zul.Column column = new Column();
				int colindex =tableModel.findColumn(gridFields[i].getColumnName()); 
				column.setSortAscending(new SortComparator(colindex, true, Env.getLanguage(Env.getCtx())));
				column.setSortDescending(new SortComparator(colindex, false, Env.getLanguage(Env.getCtx())));
				column.addEventListener(Events.ON_SORT, this);
				//IDEMPIERE-2898 - UX: Field only showing title at header on grid
				if( gridFields[i].isFieldOnly() )
					column.setLabel("");
				else {
					column.setLabel(gridFields[i].getHeader());
					column.setTooltiptext(gridFields[i].getDescription());
				}
				if (columnWidthMap != null && columnWidthMap.get(gridFields[i].getAD_Field_ID()) != null && !columnWidthMap.get(gridFields[i].getAD_Field_ID()).equals("")) {
					ZKUpdateUtil.setWidth(column, columnWidthMap.get(gridFields[i].getAD_Field_ID()));
				} else {
					if (gridFields[i].getDisplayType()==DisplayType.YesNo) {
						if (i > 0) {
							ZKUpdateUtil.setHflex(column, "min");
						} else {
							int estimatedWidth=60;
							int headerWidth = (gridFields[i].getHeader().length()+2) * 8;
							if (headerWidth > estimatedWidth)
								estimatedWidth = headerWidth;
							ZKUpdateUtil.setWidth(column, estimatedWidth + "px");
						}
					} else if (DisplayType.isNumeric(gridFields[i].getDisplayType()) && "Line".equals(gridFields[i].getColumnName())) {
						//special treatment for line
						if (i > 0)
							ZKUpdateUtil.setHflex(column, "min");
						else
							ZKUpdateUtil.setWidth(column, "60px");
					} else {
						int estimatedWidth = 0;
						if (DisplayType.isNumeric(gridFields[i].getDisplayType()))
							estimatedWidth = MIN_NUMERIC_COL_WIDTH;
						else if (DisplayType.isLookup(gridFields[i].getDisplayType()))
							estimatedWidth = MIN_COMBOBOX_WIDTH;
						else if (DisplayType.isText(gridFields[i].getDisplayType()))
							estimatedWidth = gridFields[i].getDisplayLength() * 8;
						else
							estimatedWidth = MIN_COLUMN_WIDTH;

						int headerWidth = (gridFields[i].getHeader().length() + 2) * 8;
						if (headerWidth > estimatedWidth)
							estimatedWidth = headerWidth;
						
						//hflex=min for first column not working well
						if (i > 0)
						{
							if (DisplayType.isLookup(gridFields[i].getDisplayType()))
							{
								if (headerWidth > MIN_COMBOBOX_WIDTH)
									ZKUpdateUtil.setHflex(column, "min");
							}
							else if (DisplayType.isNumeric(gridFields[i].getDisplayType()))
							{
								if (headerWidth > MIN_NUMERIC_COL_WIDTH)
									ZKUpdateUtil.setHflex(column, "min");
							}
							else if (!DisplayType.isText(gridFields[i].getDisplayType()))
							{
								if (headerWidth > MIN_COLUMN_WIDTH)
									ZKUpdateUtil.setHflex(column, "min");
							}
						}
						
						//set estimated width if not using hflex=min
						if (!"min".equals(column.getHflex())) {
							if (estimatedWidth > MAX_COLUMN_WIDTH)
								estimatedWidth = MAX_COLUMN_WIDTH;
							else if (estimatedWidth < MIN_COLUMN_WIDTH)
								estimatedWidth = MIN_COLUMN_WIDTH;
							ZKUpdateUtil.setWidth(column, Integer.toString(estimatedWidth) + "px");
						}
					}
				}
				columns.appendChild(column);
			}
		}
	}

	/**
	 * Render data grid
	 */
	private void render()
	{
		updateEmptyMessage();
		
		listbox.addEventListener(Events.ON_CLICK, this);

		updateModel();

		if (pageSize > 0)
		{
			paging = new Paging();
			paging.setPageSize(pageSize);
			paging.setTotalSize(tableModel.getRowCount());
			paging.setDetailed(true);
			paging.setId("paging");
			gridFooter.appendChild(paging);			
			paging.addEventListener(ZulEvents.ON_PAGING, this);
			renderer.setPaging(paging);
			showPagingControl();				
			positionPagingControl();
		}
		else
		{
			hidePagingControl();
		}		
	}

	/**
	 * Show zero records for processing message
	 */
	private void updateEmptyMessage() {
		if (gridTab.getRowCount() == 0)
		{
			listbox.setEmptyMessage(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "FindZeroRecords")));
		}
		else
		{
			listbox.setEmptyMessage(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Processing")));
		}
	}

	/**
	 * Update {@link #listModel} with {@link #tableModel} changes.
	 * Re-create {@link #renderer}. 
	 */
	private void updateModel() {
		if (listModel != null)
			((GridTable)tableModel).removeTableModelListener(listModel);
		listModel = new GridTableListModel((GridTable)tableModel, windowNo);
		listModel.setPageSize(pageSize);
		renderer = new JPiereQuickGridTabRowRenderer(gridTab, windowNo);//JPIERE
		renderer.setGridPanel(this);
		renderer.setADWindowPanel(windowPanel);
		if (pageSize > 0 && paging != null)
			renderer.setPaging(paging);

		listbox.setModel(listModel);
		if (listbox.getRows() == null) 
			listbox.appendChild(new Rows());
		listbox.setRowRenderer(renderer);
	}

	@SuppressWarnings("unchecked")
	public void onEvent(Event event) throws Exception
	{
		if (event == null)
			return;
		else if (event.getTarget() == listbox && Events.ON_CLICK.equals(event.getName()))
		{
			Object data = event.getData();
			org.zkoss.zul.Row row = null;
			if (data != null && data instanceof Component)
			{
				if (data instanceof org.zkoss.zul.Row)
					row = (org.zkoss.zul.Row) data;
				else
				{
					AbstractComponent cmp = (AbstractComponent) data;
					if (cmp.getParent() instanceof org.zkoss.zul.Row)
					{
						row = (Row) cmp.getParent();
					}
				}
			}
			if (row != null)
			{
				// click on selected row to enter edit mode
				if (row != renderer.getCurrentRow())
				{
					int index = listbox.getRows().getChildren().indexOf(row);
					if (index >= 0 && !isOnFocusAfterSave)
					{
						onSelectedRowChange(index);
					}
				}
			}
		}
		else if (event.getTarget() == paging)
		{
			isNewLineSaved = true;
			int pgNo = paging.getActivePage();
			if (pgNo != listModel.getPage())
			{
				listModel.setPage(pgNo);
				onSelectedRowChange(0);
				gridTab.clearSelection();
				// Clear Map on page change.
				renderer.clearMaps();
				Clients.resize(listbox);
				Events.postEvent(new Event(EVENT_ON_PAGE_NAVIGATE, this, null));
			}
		}
		else if (event.getTarget() == selectAll)
		{
			toggleSelectionForAll(selectAll.isChecked());
		}
		else if (event.getName().equals(EVENT_ON_SELECT_ROW))
		{
			Checkbox checkbox = (Checkbox) event.getData();
			int rowIndex = (Integer) checkbox.getAttribute(GridTabRowRenderer.GRID_ROW_INDEX_ATTR);
			if ((gridTab.getRowCount() - 1) == rowIndex && !isNewLineSaved)
			{
				checkbox.setChecked(false);
				return;
			}
			if (checkbox.isChecked())
			{
				gridTab.addToSelection(rowIndex);
				if (!selectAll.isChecked() && isAllSelected())
				{
					selectAll.setChecked(true);
				}
			}
			else
			{
				gridTab.removeFromSelection(rowIndex);
				if (selectAll.isChecked())
					selectAll.setChecked(false);
			}
			onSelectedRowChange(rowIndex % paging.getPageSize());
			isNewLineSaved = true;
		}
		else if (event.getName().equals(EVENT_ON_CUSTOMIZE_GRID))
		{
			reInit();
		}
		else if (event.getName().equals(Events.ON_CTRL_KEY) || event.getName().equals(EVENT_ON_AFTER_SAVE))
		{
			KeyEvent keyEvent;
			ArrayList<Object> dataEvent = null;

			if (event.getName().equals(EVENT_ON_AFTER_SAVE))
			{
				dataEvent = (ArrayList<Object>) event.getData();
				keyEvent = (KeyEvent) dataEvent.get(0);
			}
			else
				keyEvent = (KeyEvent) event;

			int code = keyEvent.getKeyCode();
			boolean isAlt = keyEvent.isAltKey();
			boolean isCtrl = keyEvent.isCtrlKey();
			boolean isShift = keyEvent.isShiftKey();

			int row = renderer.getCurrentRowIndex() % paging.getPageSize();
			int col = 0;
			if (renderer.getCurrentCell() != null)
				col = renderer.getCurrentRow().getChildren().indexOf(renderer.getCurrentCell());
			int totalRow = gridTab.getRowCount();

			if (event.getName().equals(EVENT_ON_AFTER_SAVE) && col == -1)
				col = (int) dataEvent.get(1);

			// Not focus on specific component through mouse
			if (col == -1 && (code == KeyEvent.LEFT || code == KeyEvent.RIGHT))
				col = (code == KeyEvent.LEFT ? 3 : 0);

			// Navigate in item selection popup then no action
			if ((code == KeyEvent.DOWN || code == KeyEvent.UP) && renderer.getCurrentCell() != null
					&& renderer.getCurrentCell().getChildren().get(0) instanceof Combobox)
			{
				return;
			}

			if (code == KEYBOARD_KEY_ENTER && !isCtrl && !isAlt && !isShift)
			{
				// If Search text is empty on ENTER key event then as default behavior to open Search Dialog otherwise move to down.
				Cell cell = renderer.getCurrentCell();
				if (cell != null && cell.getChildren().get(0) instanceof Searchbox
						&& !((Searchbox) cell.getChildren().get(0)).getText().isEmpty()
						&& (Boolean) ((Searchbox) cell.getChildren().get(0)).getAttribute(WSearchEditor.ATTRIBUTE_IS_INFO_PANEL_OPEN))
				{
					event.stopPropagation();
					return;
				}
				code = KeyEvent.DOWN;
			}


			int currentRow = row % paging.getPageSize() + (paging.getActivePage() * paging.getPageSize());
			if (code == KeyEvent.DOWN || code == KeyEvent.UP || code == KeyEvent.HOME || code == KeyEvent.END)
			{
				int rowChangedIndex = gridTab.getTableModel().getRowChanged();
				// update index of pagination if multiple records created
				// (e.g select multiple products)
				if (paging.getTotalSize() != gridTab.getRowCount())
				{
					updateListIndex();
					isNewLineSaved = true;
				}
				if (rowChangedIndex == currentRow)
				{
					if (!save(code, row, col))
					{
						event.stopPropagation();
						return;
					}
					else
					{
						// If a row is removed after filtering, update the rowIndex.
						if (totalRow != gridTab.getRowCount())
						{
							if (code == KeyEvent.DOWN)
							{
								row -= 1;
								totalRow = gridTab.getRowCount();
								gridTab.setCurrentRow((row >= 0 ? row % paging.getPageSize() : 0)
										+ paging.getActivePage() * paging.getPageSize());
								renderer.currentRowIndex = gridTab.getCurrentRow();
							}
						}
						// fire event for stopping duplicate row data
						ArrayList<Object> eData = new ArrayList<Object>();
						eData.add(keyEvent);
						eData.add(col);

						Events.echoEvent(EVENT_ON_AFTER_SAVE, this, eData);
						return;
					}
				}
			}
			if (code == KeyEvent.DOWN && !isCtrl && !isAlt && !isShift)
			{
				row += 1;
				currentRow = row % paging.getPageSize() + (paging.getActivePage() * paging.getPageSize());
				if (((currentRow == totalRow)
						|| (row % paging.getPageSize() == 0 && paging.getActivePage() == (paging.getPageCount() - 1)))
						&& isNewLineSaved)
				{
					createNewLine();
					updateListIndex();
					toggleSelectionForAll(false);
					if (!(row % paging.getPageSize() == 0))
					{
						Events.echoEvent(EVENT_ON_SET_FOCUS_TO_FIRST_CELL, this, null);
						event.stopPropagation();
					}
				}
				// stop from creating new record if new record blank or not save
				else if (((currentRow == totalRow)
						|| (row % paging.getPageSize() == 0 && paging.getActivePage() == (paging.getPageCount() - 1))))
				{
					event.stopPropagation();
					return;
				}

				if (row % paging.getPageSize() == 0)
				{
					// Clear Map on page change.
					renderer.clearMaps();
					updateListIndex();
					paging.setActivePage(
							paging.getActivePage() + (paging.getActivePage() == (paging.getPageCount() - 1) ? 0 : 1));
					listModel.setPage(paging.getActivePage());
					updateModelIndex(0);
					// on page change set focus to first editable cell of row
					Events.echoEvent(EVENT_ON_SET_FOCUS_TO_FIRST_CELL, this, null);
					row = 0;
				}
			}
			else if (code == KeyEvent.LEFT && !isCtrl && !isAlt && !isShift)
			{
				col -= 1;
			}
			else if (code == KeyEvent.RIGHT && !isCtrl && !isAlt && !isShift)
			{
				col += 1;
			}
			else if (code == KeyEvent.UP && !isCtrl && !isAlt && !isShift)
			{
				// remove new record if blank
				currentRow = gridTab.getCurrentRow();
				// Ignore all blank record except first blank record.
				if (totalRow > 1 && (totalRow - 1) == currentRow && !isNewLineSaved)
				{
					gridTab.dataIgnore();
					if (gridTab.getRowCount() <= 0)
						createNewLine();
					gridTab.setCurrentRow(currentRow);
				}

				row -= 1;
				if (paging.getActivePage() > 0 && (row + 1) % paging.getPageSize() == 0)
				{
					// Clear Map on page change.
					renderer.clearMaps();
					paging.setActivePage(paging.getActivePage() - 1);
					listModel.setPage(paging.getActivePage());
					updateModelIndex(paging.getPageSize() - 1);
					// on page change set focus to first editable cell of row
					Events.echoEvent(EVENT_ON_SET_FOCUS_TO_FIRST_CELL, this, null);
					return;
				}

				// check is new record is ignore
				// update page index after remove new record
				if (currentRow != gridTab.getCurrentRow() && !isNewLineSaved)
				{
					updateListIndex();
					isNewLineSaved = true;
				}

				if (row < 0)
				{
					row = 0;
				}
			}
			else if (code == KeyEvent.HOME)
			{
				row = 0;
			}
			else if (!isCtrl && isAlt && !isShift)
			{
				if (code == ADWindowToolbar.VK_K)
				{
					quickForm.onSave();
					quickForm.dispose();
				}
				else if (code == ADWindowToolbar.VK_S)
				{
					quickForm.onSave();
				}
				else if (code == ADWindowToolbar.VK_D)
				{
					quickForm.onDelete();
				}
				else if (code == ADWindowToolbar.VK_Z)
				{
					quickForm.onIgnore();
				}
				else if (code == ADWindowToolbar.VK_E)
				{
					quickForm.onRefresh();
				}
				else if (code == ADWindowToolbar.VK_X)
				{
					quickForm.onCancel();
				}
				else if (code == ADWindowToolbar.VK_L)
				{
					quickForm.onCustomize();
				}
				else if (code == ADWindowToolbar.VK_R)
				{
					quickForm.onUnSort();
				}

				event.stopPropagation();
				return;
			}
			else
			{
				renderer.setCurrentCell(row, col, code);
				event.stopPropagation();
				return;
			}

			if (row < 0 || row >= gridTab.getTableModel().getRowCount() || col < 0
					|| col >= gridTab.getTableModel().getColumnCount())
			{
				renderer.setFocusOnCurrentCell();
				event.stopPropagation();
				return;
			}

			Component source = listbox.getCell(row, col);
			if (source == null)
			{
				listbox.renderAll();
				source = listbox.getCell(row, col);
			}

			while (source != null && !(source.getClass() == Cell.class))
			{
				source = source.getParent();
			}
			renderer.setCurrentCell(row, col, code);
		}
		else if (event.getName().equals(Events.ON_FOCUS) || event.getName().equals(EVENT_ONFOCUS_AFTER_SAVE))
		{
			isOnFocusAfterSave = event.getName().equals(EVENT_ONFOCUS_AFTER_SAVE);
			Component eventComponent;
			if (isOnFocusAfterSave)
				eventComponent = (Component) event.getData();
			else
				eventComponent = event.getTarget();

			// update index of pagination if multiple records created if user use mouse to select record
			// (e.g select multiple products)
			if (paging.getTotalSize() != gridTab.getRowCount())
			{
				updateListIndex();
			}
			int totalRow = gridTab.getRowCount();
			int currentRow = gridTab.getCurrentRow();
			if ((totalRow - 1) != currentRow && !isNewLineSaved)
			{
				updateListIndex();
				isNewLineSaved = true;
				gridTab.dataIgnore();
				gridTab.setCurrentRow(currentRow);
			}
			if (!isOnFocusAfterSave)
			{
				// Prevent to fire event again on same component
				if (eventComponent == preEventComponent)
					return;

				preEventComponent = eventComponent;
			}

			Component source = event.getTarget();
			if (isOnFocusAfterSave)
				source = (Component) event.getData();

			while (source != null && !(source.getClass() == Cell.class))
			{
				source = source.getParent();
			}
			if (renderer.getCurrentCell() != null && !renderer.getCurrentCell().equals(source))
			{
				int row = renderer.getCurrentRowIndex();
				int col = renderer.getCurrentRow().getChildren().indexOf(renderer.getCurrentCell());
				int focusedRowIndex = getFocusedRowIndex(source);

				if (focusedRowIndex != row % pageSize)
				{
					// remove all pop-up dialog list box
					String script = "$('.z-combobox-open').remove()";
					Clients.response(new AuScript(script));
					
					int rowChangedIndex = gridTab.getTableModel().getRowChanged();
					if (rowChangedIndex == row)
					{
						if (save(KeyEvent.RIGHT, row, col))
						{
							isOnFocusAfterSave = true;
							Events.echoEvent(EVENT_ONFOCUS_AFTER_SAVE, this, event.getTarget());
						}
						else
						{
							event.stopPropagation();
						}
						return;
					}
				}

				setFocusOnDiv(source);
			}
		}
		else if (event.getName().equals(EVENT_ON_PAGE_NAVIGATE))
		{
			renderer.setCurrentCell(null);
			renderer.setCurrentCell(0, 1, NAVIGATE_CODE);
			LayoutUtils.addSclass("current-row", renderer.getCurrentRow());
		}
		else if (event.getName().equals(EVENT_ON_CLICK_TO_NAVIGATE))
		{
			int row = renderer.getCurrentRowIndex();
			int col = (int) event.getData();

			renderer.setCurrentCell(row, col, NAVIGATE_CODE);

			LayoutUtils.addSclass("current-row", renderer.getCurrentRow());
		}
		else if (event.getName().equals(EVENT_ON_SET_FOCUS_TO_FIRST_CELL))
		{
			int row = renderer.getCurrentRowIndex() % paging.getPageSize();
			row = row < 0 ? 0 : row;

			renderer.setCurrentCell(row, 1, NAVIGATE_CODE);

			// on sort current index remain same but rows get sorted so set current index row as current row.
			// remove property change listener to new current row
			renderer.addRemovePropertyChangeListener(false, 1);
			// set focus to row.
			renderer.setRowTo(row);
			// Add property change listener to new current row
			renderer.addRemovePropertyChangeListener(true, 1);
		}
		else if (Events.ON_SORT.equals(event.getName()))
		{
			int row = renderer.getCurrentRowIndex();
			int col = renderer.getCurrentRow().getChildren().indexOf(renderer.getCurrentCell());
			int rowChangedIndex = gridTab.getTableModel().getRowChanged();
			if (rowChangedIndex == row)
			{
				if (!save(KeyEvent.RIGHT, row, col))
				{
					event.stopPropagation();
					return;
				}
			}
			else if (gridTab.getRowCount() <= 1 && !isNewLineSaved)
			{
				event.stopPropagation();
				setStatusLine("NO record to Sort", false);
				return;
			}
			renderer.clearMaps();
			Events.echoEvent(EVENT_ON_PAGE_NAVIGATE, this, null);
			return;
		}
		event.stopPropagation();
	}

	/**
	 * Set focus to cell (source)
	 * @param source
	 */
	private void setFocusOnDiv(Component source) {
		int rowCount = gridTab.getTableModel().getRowCount();
		int colCount = renderer.getCurrentRow().getChildren().size();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				if (listbox.getCell(i, j) != null && listbox.getCell(i, j).equals(source)) {
					renderer.setCurrentCell(i, j, FOCUS_CODE);
					return;
				}
			}
		}
	}

	/**
	 * @return true if all row of current page is selected
	 */
	public boolean isAllSelected() {
		org.zkoss.zul.Rows rows = listbox.getRows();
		List<Component> childs = rows.getChildren();
		boolean all = false;
		for (Component comp : childs) {
			org.zkoss.zul.Row row = (org.zkoss.zul.Row) comp;
			Component firstChild = row.getFirstChild();
			if (firstChild instanceof Cell) {
				firstChild = firstChild.getFirstChild();
			}
			if (firstChild instanceof Checkbox) {
				Checkbox checkbox = (Checkbox) firstChild;
				if (!checkbox.isChecked())
					return false;
				else
					all = true;
			}
		}
		return all;
	}

	/**
	 * turn on/off select all rows for current page
	 * @param b
	 */
	public void toggleSelectionForAll(boolean b) {
		org.zkoss.zul.Rows rows = listbox.getRows();
		List<Component> childs = rows.getChildren();
		for (Component comp : childs) {
			org.zkoss.zul.Row row = (org.zkoss.zul.Row) comp;
			Component firstChild = row.getFirstChild();
			if (firstChild instanceof Cell) {
				firstChild = firstChild.getFirstChild();
			}
			if (firstChild instanceof Checkbox) {
				Checkbox checkbox = (Checkbox) firstChild;
				checkbox.setChecked(b);
				int rowIndex = (Integer) checkbox.getAttribute(GridTabRowRenderer.GRID_ROW_INDEX_ATTR);
				if ((gridTab.getRowCount() - 1) == rowIndex && !isNewLineSaved)
				{
					checkbox.setChecked(false);
					continue;
				}
				if (b)
					gridTab.addToSelection(rowIndex);
				else
					gridTab.removeFromSelection(rowIndex);
			}
		}
	}

	/**
	 * Update list model and data grid with new current row index
	 * @param index
	 */
	private void onSelectedRowChange(int index) {
		if (updateModelIndex(index)) {
			updateListIndex();
		}
	}

	/**
	 * Save changes.
	 * Call {@link #dataSave(int)}
	 * @param code cell navigation code
	 * @param row
	 * @param col 
	 * @return true if save successful
	 */
	private boolean save(int code, int row, int col)
	{
		boolean isSave = dataSave(code);
		if (isSave)
		{
			listModel.updateComponent(row % pageSize);
		}
		else
		{
			renderer.setCurrentCell(row, col, code);
		}
		return isSave;
	}

	/**
	 * Event after the current row index has changed.
	 */
	public void onPostSelectedRowChanged() {
		removeAttribute(ATTR_ON_POST_SELECTED_ROW_CHANGED);
		if (listbox.getRows() == null || listbox.getRows().getChildren().isEmpty())
			return;

		int rowIndex = gridTab.isOpen() ? gridTab.getCurrentRow() : -1;
		if (rowIndex >= 0 && pageSize > 0) {
			int pgIndex = rowIndex >= 0 ? rowIndex % pageSize : 0;
			org.zkoss.zul.Row row = (org.zkoss.zul.Row) listbox.getRows().getChildren().get(pgIndex);
			if (!isRowRendered(row, pgIndex)) {
				listbox.renderRow(row);
			} else {
				renderer.setCurrentRow(row);
				renderer.setCurrentCell(rowIndex, 1, NAVIGATE_CODE);
			}
		} else if (rowIndex >= 0) {
			org.zkoss.zul.Row row = (org.zkoss.zul.Row) listbox.getRows().getChildren().get(rowIndex);
			if (!isRowRendered(row, rowIndex)) {
				listbox.renderRow(row);
			} else {
				renderer.setCurrentRow(row);
				renderer.setCurrentCell(rowIndex, 1, NAVIGATE_CODE);
			}
		}
	}

	/**
	 * scroll grid to the current focus row
	 */
	public void scrollToCurrentRow() {
		onPostSelectedRowChanged();
	}
	
	/**
	 * @param row
	 * @param index
	 * @return true if row have been rendered by row renderer
	 */
	private boolean isRowRendered(org.zkoss.zul.Row row, int index) {
		if (row.getChildren().size() == 0) {
			return false;
		} else if (row.getChildren().size() == 1) {
			if (!(row.getChildren().get(0) instanceof Cell)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Update gridTab current row index.
	 * @param rowIndex row index of current page
	 * @return true if gridTab current row index has change
	 */
	public boolean updateModelIndex(int rowIndex) {
		if (pageSize > 0) {
			int start = listModel.getPage() * listModel.getPageSize();
			rowIndex = start + rowIndex;
		}

		if (gridTab.getCurrentRow() != rowIndex) {
			gridTab.navigate(rowIndex);
			return true;
		}
		return false;
	}

	/**
	 * @return Grid
	 */
	public Grid getListbox() {
		return listbox;
	}

	/**
	 * Validate display properties of fields of current row
	 * @param col
	 */
	public void dynamicDisplay(int col) {
		if (gridTab == null || !gridTab.isOpen())
        {
            return;
        }
		
        //  Selective
        if (col > 0)
        {
        	GridField changedField = gridTab.getField(col);
            String columnName = changedField.getColumnName();
            ArrayList<?> dependants = gridTab.getDependantFields(columnName);
			if ( ! (   dependants.size() > 0
					|| changedField.getCallout().length() > 0
					|| Core.findCallout(gridTab.getTableName(), columnName).size() > 0)) {
                return;
            }
        }

        boolean noData = gridTab.getRowCount() == 0;
        // Get editors list of Current Row
		List<WEditor> list = renderer.editorsListMap.get(renderer.getCurrentRow());
		if (list != null)
			dynamicDisplayEditors(noData, list); // all components
        
        if (gridTab.getRowCount() == 0 && selectAll.isChecked())
			selectAll.setChecked(false);        
	}

	/**
	 * Perform dynamic display for editors in list
	 * @param noData true if data grid is empty
	 * @param list
	 */
	private void dynamicDisplayEditors(boolean noData, List<WEditor> list) {
		// Get read-only editors list of Current Row
		ArrayList<WEditor> readOnlyEditorsList = renderer.readOnlyEditorsListMap.get(renderer.getCurrentRow());
		for (WEditor comp : list)
		{
			GridField mField = comp.getGridField();
			if (mField != null)
			{
				if (noData)
				{
					comp.setReadWrite(false);
				}
				else
				{
					// Enable read-only Component to get proper value of read &
					// write logic.
					if (readOnlyEditorsList != null && readOnlyEditorsList.contains(comp))
					{
						renderer.isDisableReadonlyComponent(comp.getComponent(), false);
						readOnlyEditorsList.remove(comp);
					}
					// r/w - check Context
					boolean rw = mField.isEditable(true);
					// IDEMPIERE-3421 - if it was read-only the list can contain
					// direct values
					if (rw && !comp.isReadWrite())
						mField.refreshLookup();
					comp.setReadWrite(rw);
					comp.setMandatory(mField.isMandatory(true));
					comp.dynamicDisplay();
					// Disable read-only Component for while pressing tab button
					// focus goes to read-only component.
					if (readOnlyEditorsList != null && renderer.isDisableReadonlyComponent(comp.getComponent(), true))
					{
						readOnlyEditorsList.add(comp);
					}
				}
				comp.setVisible((isHasCustomizeData || mField.isDisplayedGrid())
						&& mField.isDisplayed(mField.getVO().ctx, true));
			}
		}
		renderer.readOnlyEditorsListMap.put(renderer.getCurrentRow(), readOnlyEditorsList);
	}
	
	/**
	 *
	 * @param windowNo
	 */
	public void setWindowNo(int windowNo) {
		this.windowNo = windowNo;
	}

	/**
	 * Set AD window content part that own this QuickGridView instance
	 * @param winPanel
	 */
	public void setADWindowPanel(JPiereAbstractADWindowContent winPanel) {//JPIERE
		windowPanel = winPanel;
		if (renderer != null)
			renderer.setADWindowPanel(windowPanel);
	}

	/**
	 * Re-Init QuickGridView with cache gridTab.
	 */
	public void reInit() {
		listbox.getChildren().clear();
		listbox.detach();
		
		if (paging != null) {
			paging.detach();
			paging = null;
		}
		
		renderer = null;
		init = false;
		
		Grid tmp = listbox;
		createListbox();
		tmp.copyEventListeners(listbox);
		insertBefore(listbox, gridFooter);
		
		refresh(gridTab);
		scrollToCurrentRow();
		Clients.resize(listbox);
	}

	/**
	 * list field display in grid mode, in case user customize grid
	 * this list container only customize list.
	 * @return GridField[]
	 */
	public GridField[] getFields() {
		return gridFields;
	}
	
	@Override
	public void focusToFirstEditor() {
	}

	@Override
	public void focusToNextEditor(WEditor ref) {
	}

	@Override
	public void stateChange(StateChangeEvent event) {
		switch(event.getEventType()) {
			case StateChangeEvent.DATA_NEW:
			case StateChangeEvent.DATA_QUERY:
			case StateChangeEvent.DATA_REFRESH_ALL:
				if (selectAll.isChecked())
					selectAll.setChecked(false);
				break;
			case StateChangeEvent.DATA_DELETE:
			case StateChangeEvent.DATA_IGNORE:
				if (!selectAll.isChecked() && isAllSelected())
					selectAll.setChecked(true);
				break;
		}
	}

	/**
	 * not used. candidate for removal.
	 */
	protected void onADTabPanelParentChanged() {
		positionPagingControl();
	}

	/**
	 * not used. candidate for removal.
	 */
	private void positionPagingControl()
	{
		if (gridFooter.getParent() != this)
		{
			ZKUpdateUtil.setHflex(gridFooter, "1");
			gridFooter.setSclass("adtab-grid-south");
			appendChild(gridFooter);
		}
		if (paging != null)
			paging.setDetailed(true);
	}

	/**
	 * set status text
	 * @param text
	 * @param error
	 */
	public void setStatusLine(String text, boolean error)
	{
		windowPanel.getStatusBarQF().setStatusLine(text, error);
	}

	/**
	 * add new row to data grid
	 */
	public void createNewLine() {
		isNewLineSaved = false;
		gridTab.dataNew(false);
	}

	/**
	 * Save changes
	 * @param code cell navigation code
	 */
	public boolean dataSave(int code) {
		boolean isSave = false;
		//save only if new record modify 
		isSave = gridTab.dataSave(false);
		if (isSave) {
			isNewLineSaved = true;
		}

		int row = renderer.getCurrentRowIndex();
		int col = renderer.getCurrentRow().getChildren().indexOf(renderer.getCurrentCell());
		if (code != KeyEvent.DOWN && code != KeyEvent.UP) {
			renderer.setCurrentCell(row, col, code);
		}
		return isSave;
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage)
	{
		super.onPageAttached(newpage, oldpage);

		if (newpage != null)
		{
			keyListener = SessionManager.getSessionApplication().getKeylistener();
			keyListener.setCtrlKeys(keyListener.getCtrlKeys() + CNTRL_KEYS);
			keyListener.addEventListener(Events.ON_CTRL_KEY, this);
		}
	}

	@Override
	public void onPageDetached(Page page)
	{
		super.onPageDetached(page);
		keyListener.setCtrlKeys(keyListener.getCtrlKeys().replace(CNTRL_KEYS, ""));
		keyListener.removeEventListener(Events.ON_CTRL_KEY, this);
	}

	@Override
	public void editorTraverse(Callback<WEditor> editorTaverseCallback) {

	}

	/**
	 * @param source
	 * @return row that own source cell
	 */
	private int getFocusedRowIndex(Component source)
	{
		int rowCount = gridTab.getTableModel().getRowCount();
		int colCount = renderer.getCurrentRow().getChildren().size();
		for (int i = 0; i < rowCount; i++)
		{
			for (int j = 0; j < colCount; j++)
			{
				if (listbox.getCell(i, j) != null && listbox.getCell(i, j).equals(source))
					return i;
			}
		}
		return 0;
	} // getFocusedRowIndex

	/**
	 * @return sort column (if any)
	 */
	public Column findCurrentSortColumn()
	{
		if (listbox.getColumns() != null)
		{
			List<?> list = listbox.getColumns().getChildren();
			for (int i = 0; i < list.size(); i++)
			{
				Component comp = (Component) list.get(i);
				if (comp instanceof Column)
				{
					Column column = (Column) comp;
					if (!column.getSortDirection().equals("natural"))
						return column;
				}
			}
		}
		return null;
	} // findCurrentSortColumn

}
