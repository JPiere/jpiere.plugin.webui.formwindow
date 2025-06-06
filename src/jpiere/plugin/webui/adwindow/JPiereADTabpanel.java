/******************************************************************************
 * Product: Posterita Ajax UI 												  *
 * Copyright (C) 2007 Posterita Ltd.  All Rights Reserved.                    *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/
/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/

package jpiere.plugin.webui.adwindow;		//JPIERE

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.Callback;
import org.adempiere.webui.AdempiereIdGenerator;
import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADTreePanel;				//JPIERE
import org.adempiere.webui.adwindow.ADWindowToolbar;			//JPIERE
import org.adempiere.webui.adwindow.AbstractADWindowContent;	//JPIERE
import org.adempiere.webui.adwindow.DetailPane;				//JPIERE
import org.adempiere.webui.adwindow.GridView;					//JPIERE
import org.adempiere.webui.adwindow.IADTabpanel;				//JPIERE
import org.adempiere.webui.adwindow.IFieldEditorContainer;		//JPIERE
import org.adempiere.webui.adwindow.ToolbarProcessButton;		//JPIERE
import org.adempiere.webui.apps.CalloutDialog;
import org.adempiere.webui.component.Borderlayout;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.EditorBox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Group;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.SimpleTreeModel;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Urlbox;
import org.adempiere.webui.editor.IZoomableEditor;
import org.adempiere.webui.editor.WButtonEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WImageEditor;
import org.adempiere.webui.editor.WPaymentEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.panel.HelpController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.GridTabDataBinder;
import org.adempiere.webui.util.TreeUtils;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.DataStatusEvent;
import org.compiere.model.DataStatusListener;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.GridTable;
import org.compiere.model.GridWindow;
import org.compiere.model.I_AD_Preference;
import org.compiere.model.MColumn;
import org.compiere.model.MPreference;
import org.compiere.model.MRole;
import org.compiere.model.MStyle;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MToolBarButton;
import org.compiere.model.MToolBarButtonRestrict;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.model.MUserPreference;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.SystemProperties;
import org.compiere.model.X_AD_FieldGroup;
import org.compiere.model.X_AD_ToolBarButton;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DefaultEvaluatee;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Evaluatee;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Div;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.West;
import org.zkoss.zul.impl.XulElement;

/**
 * Panel for an AD_Tab content (AD_Tab + AD_Fields).
 *
 * @author <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date Feb 25, 2007
 *
 * @author Low Heng Sin
 */
public class JPiereADTabpanel extends Div implements Evaluatee, EventListener<Event>,
DataStatusListener, JPiereIADTabpanel, IdSpace, IFieldEditorContainer
{
	//css for slide animation
	private static final String SLIDE_LEFT_IN_CSS = "slide-left-in";

	private static final String SLIDE_LEFT_OUT_CSS = "slide-left-out";

	private static final String SLIDE_RIGHT_IN_CSS = "slide-right-in";

	private static final String SLIDE_RIGHT_OUT_CSS = "slide-right-out";

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -5335610241895151024L;

	/** event to save open/close state of detail pane as user preference for ad window **/
	private static final String ON_SAVE_OPEN_PREFERENCE_EVENT = "onSaveOpenPreference";

	/** post init event for tab panel **/
	public static final String ON_POST_INIT_EVENT = "onPostInit";

	/** event after tab panel had switch presentation between form and list view **/
	public static final String ON_SWITCH_VIEW_EVENT = "onSwitchView";

	/** Event after execution of {@link #dynamicDisplay(int)} **/
	public static final String ON_DYNAMIC_DISPLAY_EVENT = "onDynamicDisplay";
	
	/** defer event to set selected tree node **/
	private static final String ON_DEFER_SET_SELECTED_NODE = "onDeferSetSelectedNode";
	
	/** ADTabpanel attribute to prevent ON_DEFER_SET_SELECTED_NODE event posted twice within 1 execution **/
	private static final String ON_DEFER_SET_SELECTED_NODE_ATTR = "onDeferSetSelectedNode.Event.Posted";
	
	private static final CLogger logger;
	public static final String ON_SWIPE_RIGHT = "onSwipeRight";
	public static final String ON_SWIPE_LEFT = "onSwipeLeft";

    static
    {
        logger = CLogger.getCLogger(JPiereADTabpanel.class);	//JPIERE
    }

    private GridTab           gridTab;

    private GridWindow        gridWindow;

	/** AD Window content part that own this ADTabpanel instance **/
    private JPiereAbstractADWindowContent      windowPanel;		//JPIERE

    private int               windowNo;

    /** form view for center of {@link #formContainer} **/
    private Grid              form;

    /** field editors **/
    private ArrayList<WEditor> editors = new ArrayList<WEditor>();
    
    /** components for field editors **/
    private ArrayList<Component> editorComps = new ArrayList<Component>();
    
    /** editor toolbar buttons**/
    private ArrayList<WButtonEditor> toolbarButtonEditors = new ArrayList<WButtonEditor>();
    
    /** toolbar buttons for AD_ToolBarButton **/
    private ArrayList<ToolbarProcessButton> toolbarProcessButtons = new ArrayList<ToolbarProcessButton>();

    /** true if UI have been created for form and list **/
    private boolean			  uiCreated = false;

    /** list view for center of {@link #formContainer} **/
    private JPiereGridView		  listPanel;	//JPIERE

    /** content rows for group (Group Name:List of Rows) **/
    private Map<String, List<Row>> fieldGroupContents;

    /** header row for group **/
    private Map<String, List<org.zkoss.zul.Row>> fieldGroupHeaders;
    
    /** tabs for group (for tab type field group) **/
    private Map<String, List<Tab>> fieldGroupTabHeaders;

    /** all rows for current group (regardless of field group type) **/
	private ArrayList<Row> rowList;

	/** all collapsible groups **/
	protected List<Group> allCollapsibleGroups;

	/** main layout for header (center), tree (west) and detail pane (south) **/
	private Borderlayout formContainer = null;

	/** Tree panel for west of {@link #formContainer} **/
	private ADTreePanel treePanel = null;

	/** Sync field editor changes to GridField **/
	private GridTabDataBinder dataBinder;

	/** true if tab have been activated **/
	protected boolean activated = false;

	/**
	 * current group for collapsible type field group 
	 */
	private Group currentGroup;

	/** Panel for child tabs, south of {@link #formContainer} **/
	private JPiereDetailPane jpiereDetailPane;	//JPIERE

	/** true if this ADTabpanel instance is own by detail pane **/
	private boolean detailPaneMode;

	/** tab no within an AD Window (sequence start from 0) **/
	private int tabNo;
	
	/** Default focus field		*/
	private WEditor	defaultFocusField = null;

	/** number of columns for {@link #form} **/
	private int numberOfFormColumns;

	/** event to toggle between form and list view **/
	public static final String ON_TOGGLE_EVENT = "onToggle";

	/** default width for west tree panel **/
	private static final String DEFAULT_PANEL_WIDTH = "300px";

	private static CCache<Integer, Boolean> quickFormCache = new CCache<Integer, Boolean>(null, "QuickForm", 20, false);
	
	/** Tab Box for Tab Type Field Groups */
	private Tabbox tabbox = new Tabbox();
	/** List of Grid/Form for tab type field group */
	private List<Grid> tabGroupForms;
	/** Current Rows for tab type field group */
	private Rows currentTabGroupRows;

	/** Event for south of {@link #formContainer} **/
	private static enum SouthEvent {
    	SLIDE(),
    	OPEN(),
    	CLOSE();
    	
		private SouthEvent() {}
    }
	
	/**
	 * default constructor
	 */
	public JPiereADTabpanel()		//JPIERE
	{
        init();
    }

	/**
	 * Initialize components and event listeners
	 */
    private void init()
    {
        initComponents();
        addEventListener(ON_DEFER_SET_SELECTED_NODE, this);
        addEventListener(WPaymentEditor.ON_SAVE_PAYMENT, this);
        
        addEventListener(ON_ACTIVATE_EVENT, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				removeAttribute(ATTR_ON_ACTIVATE_POSTED);
			}
		});
        addEventListener(ON_POST_INIT_EVENT, this);
        addEventListener(ON_SAVE_OPEN_PREFERENCE_EVENT, this);
        if (ClientInfo.isMobile())
        	ClientInfo.onClientInfo(this, this::onClientInfo);
    }

    /**
     * Create new {@link #form} and {@link #listPanel} instance.
     */
    private void initComponents()
    {
    	LayoutUtils.addSclass("adtab-content", this);

    	ZKUpdateUtil.setWidth(this, "100%");
    	
        form = new Grid();
        ZKUpdateUtil.setHflex(form, "1");
        ZKUpdateUtil.setHeight(form, null);
        form.setVflex(false);
        form.setSclass("grid-layout adwindow-form");
        form.setClientAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "form");
        //swipe listener for mobile
        if (ClientInfo.isMobile())
        {
	        form.addEventListener(ON_SWIPE_RIGHT, e -> {
	        	if (windowPanel != null && windowPanel.getBreadCrumb() != null && windowPanel.getBreadCrumb().isPreviousEnabled())
	        	{
	        		windowPanel.saveAndNavigate(b -> {
	        			if (b) {
	        				LayoutUtils.addSclass(SLIDE_RIGHT_OUT_CSS, form);
	    					windowPanel.onPrevious();
	        			}
	        		});	        		
	        	}
	        });
	        form.addEventListener(ON_SWIPE_LEFT, e -> {
	        	if (windowPanel != null && windowPanel.getBreadCrumb() != null && windowPanel.getBreadCrumb().isNextEnabled())
	        	{
	        		windowPanel.saveAndNavigate(b -> {
	        			if (b) {
	        				LayoutUtils.addSclass(SLIDE_LEFT_OUT_CSS, form);	        	
	    					windowPanel.onNext();
	        			}
	        		});	        		
	        	}
	        });
        }

        listPanel = new JPiereGridView();			//JPIERE
        if( "Y".equals(Env.getContext(Env.getCtx(), "P|ToggleOnDoubleClick")) )
        	listPanel.getListbox().addEventListener(Events.ON_DOUBLE_CLICK, this);
    }

    /**
     * Setup client side form swipe listener for mobile.<br/>
     * Send onSwipeRight and onSwipeLeft event to target component.
	 * @param form Target component for onSwipeRight and onSwipeLeft event
     */
	public void setupFormSwipeListener(HtmlBasedComponent form) {
		String uuid = form.getUuid();
		StringBuilder script = new StringBuilder("(function(){let w=zk.Widget.$('")
				.append(uuid)
				.append("');");
		script.append("jq(w).on('touchstart', function(e) {let w=zk.Widget.$(this);w._touchstart=e;});");
		script.append("jq(w).on('touchmove', function(e) {let w=zk.Widget.$(this);w._touchmove=e;});");
		script.append("jq(w).on('touchend', function(e) {let w=zk.Widget.$(this);let ts = w._touchstart; let tl = w._touchmove;"
				+ "w._touchstart=null;w._touchmove=null;"
				+ "if (ts && tl) {"
				+ "if (ts.originalEvent) ts = ts.originalEvent;"
				+ "if (tl.originalEvent) tl = tl.originalEvent;"
				+ "if (ts.changedTouches && ts.changedTouches.length==1 && tl.changedTouches && tl.changedTouches.length==1) {"
				+ "let diff=(tl.timeStamp-ts.timeStamp)/1000;if (diff > 1) return;"
				+ "let diffx=tl.changedTouches[0].pageX-ts.changedTouches[0].pageX;"
				+ "let diffy=tl.changedTouches[0].pageY-ts.changedTouches[0].pageY;"
				+ "if (Math.abs(diffx) >= 100 && Math.abs(diffy) < 80) {"
				+ "if (diffx > 0) {let event = new zk.Event(w, 'onSwipeRight', null, {toServer: true});zAu.send(event);} "
				+ "else {let event = new zk.Event(w, 'onSwipeLeft', null, {toServer: true});zAu.send(event);}"
				+ "}"
				+ "}"
				+ "}"
				+ "});");
		script.append("})()");
		Clients.response(new AuScript(script.toString()));
	}

	@Override
    public void setJPiereDetailPane(JPiereDetailPane component) {
    	jpiereDetailPane = component;

		Borderlayout borderLayout = formContainer;
		South south = borderLayout.getSouth();
		if (south == null) {			
			south = new South();
			LayoutUtils.addSlideSclass(south);
			borderLayout.appendChild(south);
			south.addEventListener(Events.ON_OPEN, this);
			south.addEventListener(Events.ON_SLIDE, this);			
		} 
		south.appendChild(component);
		
		south.setVisible(true);
		south.setCollapsible(true);
		south.setSplittable(true);
		south.setOpen(isOpenDetailPane());		
		south.setSclass("adwindow-gridview-detail");
		if (!south.isOpen())
			LayoutUtils.addSclass("slide", south);
		String height = heigthDetailPane();
		if (! Util.isEmpty(height)) {
			try {
				ClientInfo browserInfo = SessionManager.getAppDesktop().getClientInfo();
				int browserHeight = browserInfo.desktopHeight;
				int prefHeight = Integer.valueOf(height.replace("px", ""));
				int topmarginpx = MSysConfig.getIntValue(MSysConfig.TOP_MARGIN_PIXELS_FOR_HEADER, 222);
				int maxHeight = browserHeight - topmarginpx;
				if (prefHeight <= maxHeight) {
					height = Integer.toString(prefHeight) + "px";
					ZKUpdateUtil.setHeight(formContainer.getSouth(), height);	
				}
			} catch (Exception e) {
				// just ignore, exception is harmless here, consequence is just not setting height so it will assume the default of theme
			}
		}
    }

	@Override
    public JPiereDetailPane getJPiereDetailPane() {
    	return jpiereDetailPane;
    }
    
    /**
     * Initialize tab panel layout ({@link #formContainer} and listeners
     * @param winPanel
     * @param gridTab
     */
    public void init(JPiereAbstractADWindowContent winPanel, GridTab gridTab)
    {
        this.gridWindow = gridTab.getGridWindow();
        this.windowNo = gridWindow.getWindowNo();
        this.gridTab = gridTab;
        // callout dialog ask for input - devCoffee #3390
        gridTab.setCalloutUI(new CalloutDialog(Executions.getCurrent().getDesktop(), windowNo));
        this.windowPanel = winPanel;
        gridTab.addDataStatusListener(this);
        this.dataBinder = new GridTabDataBinder(gridTab);

        this.getChildren().clear();

		if (SystemProperties.isZkUnitTest())
			setId(AdempiereIdGenerator.escapeId(gridTab.getName()));

        int AD_Tree_ID = 0;
		if (gridTab.isTreeTab())
			AD_Tree_ID = MTree.getDefaultAD_Tree_ID (
				Env.getAD_Client_ID(Env.getCtx()), gridTab.getKeyColumnName());

		if (gridTab.isTreeTab() && AD_Tree_ID != 0)
		{
			Borderlayout layout = new Borderlayout();
			layout.setParent(this);
			layout.setSclass("adtab-form-borderlayout");
			if (ClientInfo.isMobile())
				LayoutUtils.addSclass("mobile", layout);
			
			treePanel = new ADTreePanel(windowNo, gridTab.getTabNo());
			West west = new West();
			west.appendChild(treePanel);
			ZKUpdateUtil.setWidth(west, widthTreePanel());
			west.setCollapsible(true);
			west.setSplittable(true);
			west.setAutoscroll(true);
			layout.appendChild(west);
			LayoutUtils.addSlideSclass(west);
			if (isMobile()) {
				west.setOpen(false);
				LayoutUtils.addSclass("slide", west);
			}

			Center center = new Center();
			Vlayout div = new Vlayout();
			div.appendChild(form);
			center.appendChild(div);
			ZKUpdateUtil.setVflex(div, "1");
			ZKUpdateUtil.setHflex(div, "1");
			div.setSclass("adtab-form");
			div.setStyle("overflow-y: visible;");
			div.setSpacing("0px");
			layout.appendChild(center);

			formContainer = layout;
			treePanel.getTree().addEventListener(Events.ON_SELECT, this);
			
		}
		else
		{
			Vlayout div = new Vlayout();
			div.setSclass("adtab-form");
			div.appendChild(form);
			div.setStyle("overflow-y: visible;");
			ZKUpdateUtil.setVflex(div, "1");
			ZKUpdateUtil.setWidth(div, "100%");
			div.setSpacing("0px");
						
			Borderlayout layout = new Borderlayout();
			layout.setParent(this);
			layout.setSclass("adtab-form-borderlayout");
			if (ClientInfo.isMobile())
				LayoutUtils.addSclass("mobile", layout);
						
			Center center = new Center();
			layout.appendChild(center);
			center.appendChild(div);
			formContainer = layout;			
		}
		
		form.getParent().appendChild(tabbox);
		setGroupTabboxVisibility();
		ZKUpdateUtil.setWidth(tabbox, "100%");
		tabbox.setStyle("margin: 20px 0px 20px 0px; padding: 0px 20px 0px 20px; ");
		if (ClientInfo.isMobile()) {
			tabbox.setStyle("");
			tabbox.setMold("accordion");
		}
		

		form.getParent().appendChild(listPanel);
        listPanel.setVisible(false);
        listPanel.setWindowNo(windowNo);
        listPanel.setADWindowPanel(winPanel);

    }

    @Override
    public void createUI()
    {
    	createUI(false);
    }

	private boolean isLabelAboveInput() {
		if (isMobile())
			if (ClientInfo.maxWidth(ClientInfo.EXTRA_SMALL_WIDTH - 1))
				return MSysConfig.getBooleanValue(MSysConfig.ZK_FIELD_MOBILE_SMALL_WIDTH_LABEL_ABOVE_INPUT, true, Env.getAD_Client_ID(Env.getCtx()));
			else
				return MSysConfig.getBooleanValue(MSysConfig.ZK_FIELD_MOBILE_LABEL_ABOVE_INPUT, false, Env.getAD_Client_ID(Env.getCtx()));
		else
			return MSysConfig.getBooleanValue(MSysConfig.ZK_FIELD_LABEL_ABOVE_INPUT, false, Env.getAD_Client_ID(Env.getCtx()));
	}

    /**
     * Create UI for AD_Fields
     * @param update true if it is update instead of create new
     */
    protected void createUI(boolean update)
    {
    	if (update) 
    	{
    		if (!uiCreated) return;
    	}
    	else
    	{
    		if (uiCreated) return;
    		uiCreated = true;
    	}
    	
    	fieldGroupContents = new HashMap<String, List<Row>>();
    	fieldGroupHeaders = new HashMap<String, List<org.zkoss.zul.Row>>();
    	allCollapsibleGroups = new ArrayList<Group>();
    	
    	tabGroupForms = new ArrayList<Grid>();
    	fieldGroupTabHeaders = new HashMap<String, List<Tab>>();

		// number of form columns
    	int numCols=gridTab.getNumColumns();
    	if (numCols <= 0) { 
    		numCols=6;
    	}

		//adapt layout for phone and tablet
		int diffWithConfigureColumns = 0;
		if (isMobile())
		{
			if (ClientInfo.maxWidth(ClientInfo.EXTRA_SMALL_WIDTH-1)) {
				int limit = isLabelAboveInput() ? 1 : 3;
	    		if (numCols > limit) {
	    			diffWithConfigureColumns = numCols - limit;
	    			numCols=limit;
	    		}
	    	} else if (ClientInfo.maxWidth(ClientInfo.MEDIUM_WIDTH-1)) {
	    		if (numCols > 6) {
	    			diffWithConfigureColumns = numCols - 6;
	    			numCols=6;
	    		}
	    	}			
		}
    	
    	this.numberOfFormColumns = numCols;
    	
    	if (update)
    		form.getColumns().detach();
    	// set size in percentage per column leaving a MARGIN on right
    	Columns columns = new Columns();    	
    	form.appendChild(columns);
		// margin on right not needed for label above input layout
    	double equalWidth = (isLabelAboveInput() ? 100.00d : 95.00d) / numCols;
    	DecimalFormat decimalFormat = new DecimalFormat("0.00");
    	decimalFormat.setRoundingMode(RoundingMode.DOWN);
    	String columnWidth = decimalFormat.format(equalWidth);

    	for (int h=0;h<(isLabelAboveInput() ? numCols : numCols+1);h++){
    		Column col = new Column();
    		if (h == numCols && !isLabelAboveInput()) {
    			ZKUpdateUtil.setWidth(col, "5%");
    		} else {
    			ZKUpdateUtil.setWidth(col, columnWidth + "%");
    		}
    		columns.appendChild(col);
    	}

    	if (update) {
    		form.getRows().detach();
    		rowList = null;
    		currentGroup = null;
    	}

		if (isLabelAboveInput())
			LayoutUtils.addSclass("form-label-above-input", form);

    	Rows rows = form.newRows();
        GridField fields[] = gridTab.getFields();
        Row row = new Row();
		// current x pointer
        int actualxpos = 0;

        String currentFieldGroup = null;
        for (int i = 0; i < fields.length; i++)
        {
        	GridField field = fields[i];
        	if (!field.isDisplayed())
        		continue;

        	if (field.isToolbarButton()) {
        		WButtonEditor editor = null;
        		if (update)
        			editor = (WButtonEditor) findEditor(field);
        		else
        			editor = (WButtonEditor) WebEditorFactory.getEditor(gridTab, field, false);

        		if (editor != null) {
        			if (!update) {
	        			if (windowPanel != null)
	    					editor.addActionListener(windowPanel);
	        			editor.setGridTab(this.getGridTab());
	        			editor.setADTabpanel(this);
	        			field.addPropertyChangeListener(editor);
	        			editors.add(editor);
	        			editor.getComponent().setId(field.getColumnName());
	        			toolbarButtonEditors.add(editor);
        			}
                	if (field.isToolbarOnlyButton())
                		continue;
        		}
        	}

        	// field group
        	String fieldGroup = field.getFieldGroup();
        	if (!Util.isEmpty(fieldGroup) && !fieldGroup.equals(currentFieldGroup)
        		&& !X_AD_FieldGroup.FIELDGROUPTYPE_DoNothing.equals(field.getFieldGroupType())) // group changed
        	{
        		currentFieldGroup = fieldGroup;

				// fill remaining columns before moving to next row
        		if (!isLabelAboveInput() && numCols - actualxpos + 1 > 0)
        			row.appendCellChild(createSpacer(), numCols - actualxpos + 1);
				else if (isLabelAboveInput() && (numCols - (actualxpos + 1) > 0))
					row.appendCellChild(createSpacer(), numCols - (actualxpos + 1));
				// tab or non tab field group
        		if (currentTabGroupRows != null) {
        			currentTabGroupRows.appendChild(row);
        		} else {
            		row.setGroup(currentGroup);
            		rows.appendChild(row);
        		}

                if (rowList != null)
        			rowList.add(row);

        		List<org.zkoss.zul.Row> headerRows = new ArrayList<org.zkoss.zul.Row>();
        		fieldGroupHeaders.put(fieldGroup, headerRows);

        		rowList = new ArrayList<Row>();
        		fieldGroupContents.put(fieldGroup, rowList);

        		if (X_AD_FieldGroup.FIELDGROUPTYPE_Label.equals(field.getFieldGroupType()))
        		{
					// non collapsible group
        			row = new Row();
        			Label groupLabel = new Label(fieldGroup);
        			row.appendCellChild(groupLabel, numCols);
        			rows.appendChild(row);
        			headerRows.add(row);

        			row = new Row();
        			Separator separator = new Separator();
        			separator.setBar(true);
        			row.appendCellChild(separator, numCols);
        			rows.appendChild(row);
        			headerRows.add(row);
        			currentGroup = null;
        			currentTabGroupRows = null;
        		} else if(X_AD_FieldGroup.FIELDGROUPTYPE_Tab.equals(field.getFieldGroupType())) {
        			// Create New Tab for FieldGroup
            		List<Tab> headerTabs = new ArrayList<Tab>();
            		fieldGroupTabHeaders.put(fieldGroup, headerTabs);

        			Tabs tabs = tabbox.getTabs();
    				if (tabs == null) {
    					tabs = new Tabs();
    					tabbox.appendChild(tabs);
    					setGroupTabboxVisibility();
    				}
    				Tab tab = new Tab(fieldGroup);
    				tabs.appendChild(tab);
    				headerTabs.add(tab);

    				Grid tabForm = new Grid();
    				tabGroupForms.add(tabForm);
    				ZKUpdateUtil.setHflex(tabForm, "1");
    			    ZKUpdateUtil.setHeight(tabForm, null);
    			    tabForm.setVflex(false);
    			    tabForm.setSclass("grid-layout adwindow-form");

    		    	Columns tabColumns = new Columns();
    		    	tabForm.appendChild(tabColumns);
    		    	double tabEqualWidth = 95.5d / numCols;
    		    	DecimalFormat tabDecimalFormat = new DecimalFormat("0.00");
    		    	decimalFormat.setRoundingMode(RoundingMode.DOWN);
    		    	String tabColumnWidth = tabDecimalFormat.format(tabEqualWidth);
    		    	for (int h=0;h<numCols+1;h++){
    		    		Column col = new Column();
    		    		if (h == numCols) {
    		    			ZKUpdateUtil.setWidth(col, "4.5%");
    		    		} else {
    		    			ZKUpdateUtil.setWidth(col, tabColumnWidth + "%");
    		    		}
    		    		tabColumns.appendChild(col);
    		    	}

    		    	tabForm.appendChild(tabColumns);

    			    Rows tabRows = tabForm.newRows();

    				Tabpanels tabpanels = tabbox.getTabpanels();
    				if (tabpanels == null) {
    					tabpanels = new Tabpanels();
    					ZKUpdateUtil.setWidth(tabpanels, "100%");
    					tabbox.appendChild(tabpanels);
    				}
    				Tabpanel tp = new Tabpanel();
    				tabpanels.appendChild(tp);
    			    tp.setStyle(" padding: 20px 0px 20px 0px; ");
    				tp.appendChild(tabForm);

    				currentGroup = null;
    				currentTabGroupRows = tabRows;
        		}
        		else
        		{
					// collapsible group
        			Group rowg = new Group(fieldGroup);
        			Cell cell = (Cell) rowg.getFirstChild();
        			cell.setSclass("z-group-inner");
        			cell.setColspan(numCols+1);

    				allCollapsibleGroups.add(rowg);
        			if (X_AD_FieldGroup.FIELDGROUPTYPE_Tab.equals(field.getFieldGroupType()) || field.getIsCollapsedByDefault())
        			{
        				rowg.setOpen(false);
        			}
        			currentTabGroupRows = null;
        			currentGroup = rowg;
        			rows.appendChild(rowg);
        			headerRows.add(rowg);
        		}

        		row = new Row();
        		actualxpos = 0;
        	}

			boolean paintLabel = ! (field.getDisplayType() == DisplayType.Button || field.getDisplayType() == DisplayType.YesNo || field.isFieldOnly());
        	// get the column span for field
			int columnSpan = field.getColumnSpan();
			// get x position define at AD_Field
        	int xpos = field.getXPosition();
        	if (xpos + columnSpan > numCols && diffWithConfigureColumns > 0)
        	{
        		xpos = xpos - diffWithConfigureColumns;
        		if (xpos <= 0)
        			xpos = 1;
				if (xpos > 1 && numCols == 3 && isLabelAboveInput())
					xpos = 1;
        		else if (xpos == 1 && !paintLabel && !isLabelAboveInput())
        			xpos = 2;
        	}

			// condition to start new row
        	if (xpos <= actualxpos) {
        		// Fill remaining columns with spacers before moving to next row
        		if (!isLabelAboveInput() && (numCols - actualxpos + 1 > 0))
        			row.appendCellChild(createSpacer(), numCols - actualxpos + 1);
				else if (isLabelAboveInput() && (numCols - (actualxpos + 1) > 0))
					row.appendCellChild(createSpacer(), numCols - (actualxpos + 1));
        		// Tab Group vs Grid Group
        		if (currentTabGroupRows != null) {
        			currentTabGroupRows.appendChild(row);
        		} else {
        			row.setGroup(currentGroup);
            		rows.appendChild(row);
        		}
                if (rowList != null)
        			rowList.add(row);
				// start new row
        		row=new Row();
        		actualxpos = 0;
        	}
    		// Fill left side of field with space (if needed)
        	if (!isLabelAboveInput() && (xpos-1 - actualxpos > 0))
        		row.appendCellChild(createSpacer(), xpos-1 - actualxpos);

        	// Ensure column span doesn't exceed remaining number of columns
			int remainCols = numCols - actualxpos;
    		if (columnSpan > remainCols)
    			columnSpan = remainCols-1 > 0 ? remainCols-1 : 1;

			// move current x pointer
        	if (field.isHeading())
        		actualxpos = xpos;
        	else
        		actualxpos = xpos + columnSpan-1 + (paintLabel && !isLabelAboveInput() ? 1 : 0);

        	if (! field.isHeading()) {
        		WEditor editor = update ? findEditor(field) : WebEditorFactory.getEditor(gridTab, field, false);

        		if (editor != null) // Not heading only
        		{
        			if (!update)
        			{
        				String entityTypeInf = Env.IsShowTechnicalInfOnHelp(Env.getCtx())?"this.fieldEntityType());":"'');";
        				editor.getComponent().setWidgetOverride("fieldHeader", HelpController.escapeJavascriptContent(field.getHeader()));
        				editor.getComponent().setWidgetOverride("fieldDescription", HelpController.escapeJavascriptContent(field.getDescription()));
        				editor.getComponent().setWidgetOverride("fieldHelp", HelpController.escapeJavascriptContent(field.getHelp()));
        				editor.getComponent().setWidgetOverride("fieldEntityType", HelpController.escapeJavascriptContent(field.getEntityType()));
        				editor.getComponent().setWidgetListener("onFocus", "zWatch.fire('onFieldTooltip', this, null, this.fieldHeader(), this.fieldDescription(), this.fieldHelp(),"+entityTypeInf);

        				editor.setGridTab(this.getGridTab());
        				field.addPropertyChangeListener(editor);
        				editors.add(editor);
        				editorComps.add(editor.getComponent());
        			}
        			if (paintLabel) {
        				Div div = new Div();
        				div.setSclass("form-label");
        				Label label = editor.getLabel();
        				div.appendChild(label);
        				if (label.getDecorator() != null)
        					div.appendChild(label.getDecorator());
						if (isLabelAboveInput()) {
							Div editorDiv = new Div();
							editorDiv.appendChild(div);
							editorDiv.appendChild(editor.getComponent());
							row.appendCellChild(editorDiv, columnSpan);
							row.getLastCell().setSclass("form-label-above-input");
							// with label on top, space between field move from right align label to right side of input
							if (numCols > 1)
								row.appendCellChild(createSpacer(), 1);
						} else {
							row.appendCellChild(div, 1);
						}
        			}

					if (!paintLabel) {
						// no label, input only
						row.appendCellChild(editor.getComponent(), columnSpan);
						if (isLabelAboveInput()) {
							row.appendCellChild(createSpacer(), 1);
							row.setValign("bottom");
						}
					} else if (!isLabelAboveInput()) {
						row.appendCellChild(editor.getComponent(), columnSpan);
					}
        			//to support float/absolute editor
        			row.getLastCell().setStyle("position: relative; overflow: visible;");

        			if (!update)
        			{
	        			if (editor instanceof WButtonEditor)
	        			{
	        				if (windowPanel != null)
	        					((WButtonEditor)editor).addActionListener(windowPanel);
	        			}
	        			else
	        			{
	        				editor.addValueChangeListener(dataBinder);
	        			}
        			}

        			//	Default Focus
        			if (defaultFocusField == null && field.isDefaultFocus())
        				defaultFocusField = editor;

        			if (!update)
        			{
	        			//stretch component to fill grid cell
	        			editor.fillHorizontal();

	        			Component fellow = editor.getComponent().getFellowIfAny(field.getColumnName());
	        			if (fellow == null) {
	        				editor.getComponent().setId(field.getColumnName());
	        			}

	        			//setup editor context menu
	        			WEditorPopupMenu popupMenu = editor.getPopupMenu();
	        			if (popupMenu == null)
	        			{
	        				popupMenu = new WEditorPopupMenu(false, false, false, false, false, false, null);
	        			}
	        			if (popupMenu != null)
	        			{
	        				if (editor instanceof ContextMenuListener)
	        					popupMenu.addMenuListener((ContextMenuListener)editor);
	        				popupMenu.setId(field.getColumnName()+"-popup");
	        				this.appendChild(popupMenu);
	        				if (!field.isFieldOnly())
	        				{
	        					Label label = editor.getLabel();
	        					if (ClientInfo.isMobile())
	        					{
	        						WEditorPopupMenu finalPopupMenu = popupMenu;
	        						label.addEventListener(Events.ON_CLICK, evt-> finalPopupMenu.open(label, "after_start"));
	        					}
	        					else
	        					{
		        					if (popupMenu.isZoomEnabled() && editor instanceof IZoomableEditor)
		        					{
		        						label.addEventListener(Events.ON_CLICK, new ZoomListener((IZoomableEditor) editor));
		        					}

		        					popupMenu.addContextElement(label);
	        					}
	        				}
	        				popupMenu.addSuggestion(field);
	        				if(!ClientInfo.isMobile())
	        				{
	        					if (editor.getComponent() instanceof XulElement)
	        					{
	        						popupMenu.addContextElement((XulElement) editor.getComponent());
	        					}
	        				}
	        			}
        			}
        		}
        	}
        	else // just heading
        	{
        		//display just a label if we are "heading only"
        		Label label = new Label(field.getHeader());
        		Div div = new Div();
        		div.setSclass("form-label-heading");
        		if (field.getAD_LabelStyle_ID() > 0) {
            		MStyle style = MStyle.get(Env.getCtx(), field.getAD_LabelStyle_ID());
            		String cssStyle = style.buildStyle(ThemeManager.getTheme(), field);
            		if (cssStyle != null && cssStyle.startsWith(MStyle.SCLASS_PREFIX)) {
    					String sclass = cssStyle.substring(MStyle.SCLASS_PREFIX.length());
    					div.setSclass(sclass);
    				} else if (style != null && cssStyle.startsWith(MStyle.ZCLASS_PREFIX)) {
    					String zclass = cssStyle.substring(MStyle.ZCLASS_PREFIX.length());
    					div.setZclass(zclass);
    				} else {
    					div.setStyle(cssStyle);
    				}
        		}

        		row.appendCellChild(createSpacer());
        		div.appendChild(label);
        		row.appendCellChild(div);
        	}
        }

		// fill remaining columns
		if (!isLabelAboveInput() && numCols - actualxpos + 1 > 0)
			row.appendCellChild(createSpacer(), numCols - actualxpos + 1);
		else if (isLabelAboveInput() && (numCols - (actualxpos + 1) > 0))
			row.appendCellChild(createSpacer(), numCols - (actualxpos + 1));
		// Tab Group vs Grid Group
		if (currentTabGroupRows != null) {
			currentTabGroupRows.appendChild(row);
		} else {
			row.setGroup(currentGroup);
			rows.appendChild(row);
		}
        if (rowList != null)
			rowList.add(row);

        if (!update)
        	loadToolbarButtons();
        		
        //create tree
        if (!update && gridTab.isTreeTab() && treePanel != null) {
        	int AD_Tree_ID = Env.getContextAsInt (Env.getCtx(), getWindowNo(), "AD_Tree_ID", true);
        	int AD_Tree_ID_Default = MTree.getDefaultAD_Tree_ID (Env.getAD_Client_ID(Env.getCtx()), gridTab.getKeyColumnName());
        	
    		if (AD_Tree_ID != 0) {
    			treePanel.initTree(AD_Tree_ID, windowNo);
    			Events.echoEvent(ON_DEFER_SET_SELECTED_NODE, this, null);
    		} else if (AD_Tree_ID_Default != 0) {
    			int linkColId = MTree.get(Env.getCtx(), AD_Tree_ID_Default, null).getParent_Column_ID();
    			String linkColName = null;
    			int linkID = 0;
    			if (linkColId > 0) {
    				linkColName = MColumn.getColumnName(Env.getCtx(), linkColId);
    				linkID = Env.getContextAsInt(Env.getCtx(), windowNo, linkColName, true);
    			}
    			treePanel.initTree(AD_Tree_ID_Default, windowNo, linkColName, linkID);
    			Events.echoEvent(ON_DEFER_SET_SELECTED_NODE, this, null);
    		}        	
        }
        
        if (!update && !gridTab.isSingleRow() && !isGridView())
        	switchRowPresentation(); 
        
    }

    /**
     * Find editor for field
     * @param field
     * @return {@link WEditor} or null if not found
     */
	private WEditor findEditor(GridField field) {
		for(WEditor editor : editors) {
			if (editor.getGridField() == field)
				return editor;
		}
		return null;
	}

	/**
	 * load toolbar buttons from AD_ToolBarButton
	 */
	private void loadToolbarButtons() {
		//get extra toolbar process buttons
        MToolBarButton[] mToolbarButtons = MToolBarButton.getProcessButtonOfTab(gridTab.getAD_Tab_ID(), null);
        for(MToolBarButton mToolbarButton : mToolbarButtons) {
        	Boolean access = MRole.getDefault().getProcessAccess(mToolbarButton.getAD_Process_ID());
        	if (access != null && access.booleanValue()) {
        		ToolbarProcessButton toolbarProcessButton = new ToolbarProcessButton(mToolbarButton, this, windowPanel, windowNo);
        		toolbarProcessButtons.add(toolbarProcessButton);
        	}
        }
        
        if (toolbarProcessButtons.size() > 0) {
        	int ids[] = MToolBarButtonRestrict.getProcessButtonOfTab(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx()), gridTab.getAD_Tab_ID(), null);
        	if (ids != null && ids.length > 0) {
        		for(int id : ids) {
        			X_AD_ToolBarButton tbt = new X_AD_ToolBarButton(Env.getCtx(), id, null);
        			for(ToolbarProcessButton btn : toolbarProcessButtons) {
        				if (tbt.getComponentName().equals(btn.getColumnName())) {
        					toolbarProcessButtons.remove(btn);
        					break;
        				}
        			}
        		}
        	}
        }
	}

	private Component createSpacer() {
		return new Space();
	}

	@Override
	public void dynamicDisplay (int col)
	{
        if (!gridTab.isOpen())
        {
            return;
        }

        //css animation for slide
        if (form.getSclass() != null && form.getSclass().contains(SLIDE_RIGHT_OUT_CSS)) {
        	Executions.schedule(getDesktop(), e -> {
        		LayoutUtils.removeSclass(SLIDE_RIGHT_OUT_CSS, form);
        		LayoutUtils.addSclass(SLIDE_RIGHT_IN_CSS, form);
        		Executions.schedule(getDesktop(), e1 -> onAfterSlide(e1), new Event("onAfterSlide", form));
        	}, new Event("onAfterSlideRightOut", form));
        } else if (form.getSclass() != null && form.getSclass().contains(SLIDE_LEFT_OUT_CSS)) {
        	Executions.schedule(getDesktop(), e -> {
        		LayoutUtils.removeSclass(SLIDE_LEFT_OUT_CSS, form);
        		LayoutUtils.addSclass(SLIDE_LEFT_IN_CSS, form);
        		Executions.schedule(getDesktop(), e1 -> onAfterSlide(e1), new Event("onAfterSlide", form));
        	}, new Event("onAfterSlideLeftOut", form));
        }
        
    	List<Group> collapsedGroups = new ArrayList<Group>();
    	for (Group group : allCollapsibleGroups) {
    		if (! group.isOpen())
    			collapsedGroups.add(group);
    	}

        //  Selective
        if (col > 0)
        {
            GridField changedField = gridTab.getField(col);
            String columnName = changedField.getColumnName();
            ArrayList<GridField> dependants = gridTab.getDependantFields(columnName);
            if (logger.isLoggable(Level.CONFIG)) logger.config("(" + gridTab.toString() + ") "
            			+ columnName + " - Dependents=" + dependants.size());
			if ( ! (   dependants.size() > 0
					|| changedField.getCallout().length() > 0
					|| Core.findCallout(gridTab.getTableName(), columnName).size() > 0)) 
			{
				for (WEditor comp : editors)
				{
					comp.updateStyle();
				}
                return;
            }
        }

        boolean noData = gridTab.getRowCount() == 0;
        if (logger.isLoggable(Level.CONFIG)) logger.config(gridTab.toString() + " - Rows=" + gridTab.getRowCount());

        for (WEditor comp : editors)
        {
            GridField mField = comp.getGridField();
            if (mField != null)
            {
                if (mField.isDisplayed(true))       //  check context
                {
                    if (!comp.isVisible())
                    {
                        comp.setVisible(true);      //  visibility
                    }
                    if (noData)
                    {
                        comp.setReadWrite(false);
                    }
                    else
                    {
                        boolean rw = mField.isEditable(true);   //  r/w - check Context
                        if (rw && !comp.isReadWrite()) // IDEMPIERE-3421 - if it was read-only the list can contain direct values
                        	mField.refreshLookup();
                        comp.setReadWrite(rw);
                        comp.setMandatory(mField.isMandatory(true));    //  check context
                    	comp.dynamicDisplay();
                    }
                }
                else if (comp.isVisible())
                {
                    comp.setVisible(false);
                }
            }
            comp.updateStyle();
        }   //  all components

        //hide row if all editor within the row is invisible
        List<Component> rows = form.getRows().getChildren();
        for (Component comp : rows)
        {
        	if (comp instanceof Row) {
            	Row row = (Row) comp;
            	boolean visible = false;
            	boolean editorRow = false;
            	for (Component cellComponent : row.getChildren())
            	{
            		Component component = cellComponent.getFirstChild();
					if (isLabelAboveInput() && component instanceof Div div && component.getFirstChild() != null)
						component = div.getFirstChild().getNextSibling();
            		if (editorComps.contains(component))
            		{
            			editorRow = true;
            			// open the group if there is a mandatory unfilled field
            			WEditor editor = editors.get(editorComps.indexOf(component));
            			if (editor != null
            					&& row.getGroup() != null 
            					&& ! row.getGroup().isOpen()
            					&& editor.isMandatoryStyle()) {
            				row.getGroup().setOpen(true);
            				if (collapsedGroups.contains(row.getGroup())) {
            					collapsedGroups.remove(row.getGroup());
            				}
            			}
            			if (component.isVisible())
            			{
            				visible = true;
            				break;
            			}
            		}
            	}
            	if (editorRow && (row.isVisible() != visible))
            	{
            		row.setAttribute(Group.GROUP_ROW_VISIBLE_KEY, visible ? "true" : "false");
            		row.setVisible(visible);
            	}
        	}
        }

        //hide row if all editor within the row is invisible in Tabbox grid
        for(Grid tabForm: tabGroupForms) {
            List<Component> tabrows = tabForm.getRows().getChildren();
            for (Component comp : tabrows)
            {
            	if (comp instanceof Row) {
                	Row row = (Row) comp;
                	boolean visible = false;
                	boolean editorRow = false;
                	for (Component cellComponent : row.getChildren())
                	{
                		Component component = cellComponent.getFirstChild();
						if (isLabelAboveInput() && component instanceof Div div)
							component = div.getFirstChild();
                		if (editorComps.contains(component))
                		{
                			editorRow = true;
                			if (component.isVisible())
                			{
                				visible = true;
                				break;
                			}
                		}
                	}
                	if (editorRow && (row.isVisible() != visible))
                	{
                		row.setVisible(visible);
                	}
            	}
            }
        }
        
        //hide fieldgroup if all editor row within the fieldgroup is invisible
        for(Iterator<Entry<String, List<org.zkoss.zul.Row>>> i = fieldGroupHeaders.entrySet().iterator(); i.hasNext();)
        {
        	Map.Entry<String, List<org.zkoss.zul.Row>> entry = i.next();
        	List<Row> contents = fieldGroupContents.get(entry.getKey());
        	boolean visible = false;
        	for (Row row : contents)
        	{
        		if (row.isVisible())
        		{
        			visible = true;
        			break;
        		}
        	}
        	List<org.zkoss.zul.Row> headers = entry.getValue();
        	for(org.zkoss.zul.Row row : headers)
        	{
        		if (row.isVisible() != visible)
        			row.setVisible(visible);
        	}
        }
        
        // Check Field Group Tabs and Hide if all rows are invisible        
        Tab visibleTab = null;	// Change Selected Tab which will become invisible to another Tab
        boolean isSelectedTabInvisible = false;
        for(Iterator<Entry<String, List<Tab>>> i = fieldGroupTabHeaders.entrySet().iterator(); i.hasNext();)
        {
        	Map.Entry<String, List<Tab>> entry = i.next();
        	List<Row> contents = fieldGroupContents.get(entry.getKey());
        	boolean visible = false;
        	for (Row row : contents)
        	{
        		if (row.isVisible())
        		{
        			visible = true;
        			break;
        		}
        	}
        	List<Tab> tabs = entry.getValue();

        	for(Tab tab : tabs)
        	{
        		if (tab.isVisible() != visible) {
        			if(tab.isSelected() && !visible)
        				isSelectedTabInvisible = true;
        			tab.setVisible(visible);
        		}
        		if(tab.isVisible())
        			visibleTab = tab;
        	}
        }

        if(isSelectedTabInvisible && visibleTab != null) {
    		tabbox.setSelectedTab(visibleTab);
        }
        // collapse the groups closed
        for (Group group : collapsedGroups) {
        	group.setOpen(false);
        }
        
        if (listPanel.isVisible()) {
        	listPanel.dynamicDisplay(col);
        }
        
		for (ToolbarProcessButton btn : toolbarProcessButtons) {
			btn.dynamicDisplay();
			btn.readOnlyLogic();
			btn.pressedLogic();
		}

        Events.sendEvent(this, new Event(ON_DYNAMIC_DISPLAY_EVENT, this));
        echoDeferSetSelectedNodeEvent();
        if (logger.isLoggable(Level.CONFIG)) logger.config(gridTab.toString() + " - fini - " + (col<=0 ? "complete" : "seletive"));
    }   //  dynamicDisplay

	/**
	 * Handle after slide event
	 * @param e
	 */
	private void onAfterSlide(Event e) {
		//delay to let animation complete
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {}
		LayoutUtils.removeSclass(SLIDE_LEFT_IN_CSS, form);
		LayoutUtils.removeSclass(SLIDE_RIGHT_IN_CSS, form);
	}

	/**
	 * Echo set selected node event for tree
	 */
	private void echoDeferSetSelectedNodeEvent() {
		if (getAttribute(ON_DEFER_SET_SELECTED_NODE_ATTR) == null) {
        	setAttribute(ON_DEFER_SET_SELECTED_NODE_ATTR, Boolean.TRUE);
        	Events.echoEvent(ON_DEFER_SET_SELECTED_NODE, this, null);
        }
	}
	
    @Override
    public String getDisplayLogic()
    {
        return gridTab.getDisplayLogic();
    }

    @Override
    public String getTitle()
    {
        return gridTab.getName();
    } // getTitle

    @Override
    public String get_ValueAsString(String variableName)
    {
    	return new DefaultEvaluatee(getGridTab(), windowNo, tabNo).get_ValueAsString(Env.getCtx(), variableName);
    } // get_ValueAsString

    @Override
    public int getTabLevel()
    {
        return gridTab.getTabLevel();
    }

    @Override
    public String getTableName()
    {
        return gridTab.getTableName();
    }

    @Override
    public int getRecord_ID()
    {
        return gridTab.getRecord_ID();
    }

    @Override
    public boolean isCurrent()
    {
        return gridTab != null ? gridTab.isCurrent() : false;
    }

    /**
     * Get window number
     * @return windowNo
     */
    public int getWindowNo()
    {
        return windowNo;
    }

    /**
     * Retrieve from DB.<br/>
     * Delegate to {@link GridTab#query(boolean)}
     */
    @Override
    public void query()
    {
    	boolean open = gridTab.isOpen();
        gridTab.query(false);
        if (listPanel.isVisible() && !open)
        	gridTab.getTableModel().fireTableDataChanged();
    }

    @Override
    public void query (boolean onlyCurrentRows, int onlyCurrentDays, int maxRows)
    {
    	boolean open = gridTab.isOpen();
        gridTab.query(onlyCurrentRows, onlyCurrentDays, maxRows);
        if (listPanel.isVisible() && !open)
        	gridTab.getTableModel().fireTableDataChanged();
    }

    @Override
	public void resetDetailForNewParentRecord ()
    {
    	boolean open = gridTab.isOpen();
        if (open) 
        {
        	gridTab.resetDetailForNewParentRecord();
        }
        else
        {
        	gridTab.setCurrentRow(-1, true);
        }
    }
    
    @Override
    public GridTab getGridTab()
    {
        return gridTab;
    }

    @Override
    public ADTreePanel getTreePanel()
    {
    	return treePanel;
    }

    /**
     * When tree should be visible
     * @return master, detail or both
     */
    public String getTreeDisplayedOn()
    {
    	return gridTab.getTreeDisplayedOn();
    }

    @Override
    public void refresh()
    {
        gridTab.dataRefresh();
    }

    @Override
    public void activate(boolean activate)
    {
    	if (activate) {
	    	if (getAttribute(ATTR_ON_ACTIVATE_POSTED) != null) {
	    		return;
	    	}
	    	
	    	setAttribute(ATTR_ON_ACTIVATE_POSTED, Boolean.TRUE);
    	}
    	
    	activated = activate;
        if (listPanel.isVisible()) {
        	if (activate)
        		listPanel.activate(gridTab);
        	else
        		listPanel.deactivate();
        } else {
        	if (activate) {
        		formContainer.setVisible(activate);
        		if (!isMobile() && !isDetailPaneMode())
        			focusToFirstEditor();
        	}
        }

        if (gridTab.getRecord_ID() > 0 && gridTab.isTreeTab() && treePanel != null) {
        	echoDeferSetSelectedNodeEvent();
        }
      
        Event event = new Event(ON_ACTIVATE_EVENT, this, activate);
        Events.postEvent(event);
    }

    @Override
    public void focusToFirstEditor() {
    	focusToFirstEditor(false);
    }
    
    /**
     * Delegate to {@link #focusToEditor(WEditor, boolean)}
     * @param checkCurrent
     */
    public void focusToFirstEditor(boolean checkCurrent) {
		WEditor toFocus = null;
		
		if (defaultFocusField != null 
				&& defaultFocusField.isVisible() && defaultFocusField.isReadWrite() && defaultFocusField.getComponent().getParent() != null
				&& !(defaultFocusField instanceof WImageEditor)) {
			toFocus = defaultFocusField;
		}
		else
		{		
			for (WEditor editor : editors) {
				if (editor.isVisible() && editor.isReadWrite() && editor.getComponent().getParent() != null
					&& !(editor instanceof WImageEditor)) {
					toFocus = editor;
					break;
				}
			}
		}
		if (toFocus != null) {
			focusToEditor(toFocus, checkCurrent);
		}
	}

    @SuppressWarnings("unchecked")
    @Override
	public void onEvent(Event event)
    {
    	if (event.getTarget() == listPanel.getListbox())
    	{    		
    		Events.sendEvent(this, new Event(ON_TOGGLE_EVENT, this));
    	}
    	else if (treePanel != null && event.getTarget() == treePanel.getTree()) {
    		Treeitem item =  treePanel.getTree().getSelectedItem();
    		if (item != null && item.getValue() != null)
    			navigateTo((DefaultTreeNode<MTreeNode>)item.getValue());
    	}
    	else if (ON_DEFER_SET_SELECTED_NODE.equals(event.getName())) {
    		removeAttribute(ON_DEFER_SET_SELECTED_NODE_ATTR);
    		setSelectedNode();
    	}
    	else if (WPaymentEditor.ON_SAVE_PAYMENT.equals(event.getName())) {
    		windowPanel.onSavePayment();
    	}
    	else if (ON_POST_INIT_EVENT.equals(event.getName())) {
    		if (isDetailVisible() && jpiereDetailPane.getSelectedADTabpanel() != null) {
    			jpiereDetailPane.getSelectedADTabpanel().activate(true);
    		}
    	}
    	else if (event.getTarget() instanceof South) {
    		if (jpiereDetailPane != null) {
    			boolean openEvent = event instanceof OpenEvent;
    			if (openEvent) {
    				OpenEvent oe = (OpenEvent)event;
    				onSouthEvent(oe.isOpen() ? SouthEvent.OPEN : SouthEvent.CLOSE);
    			} else {
    				onSouthEvent(SouthEvent.SLIDE);
    			}
    		}
    	}
    	else if (event.getName().equals(ON_SAVE_OPEN_PREFERENCE_EVENT)) {
    		Boolean value = (Boolean) event.getData();
    		int windowId = getGridTab().getAD_Window_ID();
    		int adTabId = getGridTab().getAD_Tab_ID();
    		if (windowId > 0 && adTabId > 0) {
    			Query query = new Query(Env.getCtx(), MTable.get(Env.getCtx(), I_AD_Preference.Table_ID), "AD_Window_ID=? AND Attribute=? AND AD_User_ID=? AND AD_Process_ID IS NULL AND PreferenceFor = 'W'", null);
    			int userId = Env.getAD_User_ID(Env.getCtx());
    			MPreference preference = query.setOnlyActiveRecords(true)
    										  .setApplyAccessFilter(true)
    										  .setClient_ID()
    										  .setParameters(windowId, adTabId+"|DetailPane.IsOpen", userId)
    										  .first();
    			if (preference == null || preference.getAD_Preference_ID() <= 0) {
    				preference = new MPreference(Env.getCtx(), 0, null);
    				preference.setAD_Window_ID(windowId);
    				preference.setAD_User_ID(userId);
    				preference.setAttribute(adTabId+"|DetailPane.IsOpen");
    			}
				preference.setValue(value ? "Y" : "N");
    			preference.saveEx();
    			//update current context
    			Env.getCtx().setProperty("P"+windowId+"|"+adTabId+"|DetailPane.IsOpen", value ? "Y" : "N");
    		}
    	}
    }

    /**
     * Set selected tree node for current row (if there's tree)
     */
	public void setSelectedNode() {
		if (gridTab.getRecord_ID() >= 0 && gridTab.isTreeTab() && treePanel != null) {
			setSelectedNode(gridTab.getRecord_ID());
		}
	}

	/**
	 * Handle open/close event of south panel (detail pane)
	 * @param event
	 */
    private void onSouthEvent(SouthEvent event) {
    	if (event == SouthEvent.OPEN || event == SouthEvent.CLOSE) {
    		boolean open = event == SouthEvent.OPEN ? true : false; 
    		Events.echoEvent(ON_SAVE_OPEN_PREFERENCE_EVENT, this, open);
    		if (!open)
    			return;
    	}

		if (jpiereDetailPane.getParent() == null) {
			formContainer.appendSouth(jpiereDetailPane);
		}
		JPiereIADTabpanel tabPanel = jpiereDetailPane.getSelectedADTabpanel();
    	if (tabPanel != null) {
    		if (!tabPanel.isActivated() || !jpiereDetailPane.isVisible()) {
    			if (!jpiereDetailPane.isVisible())
    				jpiereDetailPane.setVisible(true);
    			tabPanel.activate(true);
    		} else if (tabPanel.getJPiereGridView() != null){
    			tabPanel.getJPiereGridView().invalidateGridView();
    		}

	    	if (!tabPanel.isGridView()) {
	    		if (jpiereDetailPane.getSelectedPanel().isToggleToFormView()) {
	    			jpiereDetailPane.getSelectedPanel().afterToggle();
	    		} else {
	    			tabPanel.switchRowPresentation();
	    		}
	    	}	    		    	
    	}
    }
    
    /**
     * Is detail pane open/visible
     * @return true if detail pane is open/visible
     */
    private boolean isOpenDetailPane() {
    	if (isMobile())
    		return false;
    	boolean open = true;
    	int windowId = getGridTab().getAD_Window_ID();
		int adTabId = getGridTab().getAD_Tab_ID();
		if (windowId > 0 && adTabId > 0) {
			String preference = Env.getPreference(Env.getCtx(), windowId, adTabId+"|DetailPane.IsOpen", false);
			if (preference != null && preference.trim().length() > 0) {
				open = "Y".equals(preference);
			}
		}
    	return open;
    }

    /**
     * Get height of detail pane from user preference
     * @return height of detail pane from user preference
     */
    private String heigthDetailPane() {
    	String height = null;
    	int windowId = getGridTab().getAD_Window_ID();
		int adTabId = getGridTab().getAD_Tab_ID();
		if (windowId > 0 && adTabId > 0) {
			height = Env.getPreference(Env.getCtx(), windowId, adTabId+"|DetailPane.Height", false);
		}
    	return height;
    }

    /**
     * Get width of tree panel from user preference
     * @return width of tree panel from user preference (or default if no user preference)
     */
    private String widthTreePanel() {
    	String width = null;
    	int windowId = getGridTab().getAD_Window_ID();
    	int adTabId = getGridTab().getAD_Tab_ID();
    	if (windowId > 0 && adTabId > 0)
    		width = Env.getPreference(Env.getCtx(), windowId, adTabId+"|TreePanel.Width", false);
    	return Util.isEmpty(width) ? DEFAULT_PANEL_WIDTH : width;
    }

    /**
     * Navigate to a tree node
     * @param value
     */
    private void navigateTo(DefaultTreeNode<MTreeNode> value) {
    	MTreeNode treeNode = value.getData();
    	//  We Have a TreeNode
		int nodeID = treeNode.getNode_ID();

		//  Search all rows for mode id
		int size = gridTab.getRowCount();
		int row = -1;
		for (int i = 0; i < size; i++)
		{
			if (gridTab.getKeyID(i) == nodeID)
			{
				row = i;
				break;
			}
		}
		if (row == -1)
		{
			if (nodeID > 0 && logger.isLoggable(Level.WARNING))
				logger.log(Level.WARNING, "Tab does not have ID with Node_ID=" + nodeID);
			if (gridTab.getCurrentRow() >= 0) 
			{
				gridTab.setCurrentRow(gridTab.getCurrentRow(), true);
			}
			throw new AdempiereException(Msg.getMsg(Env.getCtx(),"RecordIsNotInCurrentSearch"));
		}

//		windowPanel.onTreeNavigate(row);
		gridTab.navigate(row);//JPIERE-0014
	}

    @Override
	public void dataStatusChanged(DataStatusEvent e)
    {
    	//ignore background event
    	if (Executions.getCurrent() == null || e.isInitEdit()) return;

        int col = e.getChangedColumn();
        if (logger.isLoggable(Level.CONFIG)) logger.config("(" + gridTab + ") Col=" + col + ": " + e.toString());

        //  Process Callout
        GridField mField = gridTab.getField(col);
        if (mField != null
            && (mField.getCallout().length() > 0
            		|| (Core.findCallout(gridTab.getTableName(), mField.getColumnName())).size()>0
            		|| gridTab.hasDependants(mField.getColumnName())))
        {
	        // IDEMPIERE-4106 Refresh the list (lookup) on dependant fields was moved inside processFieldChange->processDependencies
            String msg = gridTab.processFieldChange(mField);     //  Dependencies & Callout
            if (msg.length() > 0)
            {
                Dialog.error(windowNo, msg);
            }
        }
        
        //update UI state
        if (!uiCreated)
        	createUI();
        dynamicDisplay(col);

        //sync tree 
        if (treePanel != null) 
        {
        	if (getTreeDisplayedOn().equals(MTab.TREEDISPLAYEDON_MasterTab))
        		treePanel.getParent().setVisible(!isDetailPaneMode());
        	else if (getTreeDisplayedOn().equals(MTab.TREEDISPLAYEDON_DetailTab))
        		treePanel.getParent().setVisible(isDetailPaneMode());

        	if ("Deleted".equalsIgnoreCase(e.getAD_Message()))
        	{
        		if (e.Record_ID != null && e.Record_ID instanceof Integer && ((Integer)e.Record_ID != gridTab.getRecord_ID()))
        			deleteNode((Integer)e.Record_ID);
        		else
        			setSelectedNode(gridTab.getRecord_ID());
        	}        		
        	else if (!e.isInserting())
        	{
        		boolean refresh=true;      		
        		Treeitem item = treePanel.getTree().getSelectedItem();
        		SimpleTreeModel model = (SimpleTreeModel)(TreeModel<?>) treePanel.getTree().getModel();
        		if (item != null && item.getValue() != null)
        		{
        			@SuppressWarnings("unchecked")
					MTreeNode treeNode = ((DefaultTreeNode<MTreeNode>) item.getValue()).getData();        		
            		if (treeNode.getNode_ID() == gridTab.getRecord_ID()){
            			setSelectedNode(gridTab.getRecord_ID());
            			refresh = false;
            		}
				}
        		// Remove the node if driven by value; will be re-added right after
           		if ("Saved".equals(e.getAD_Message()) && model.find(null, gridTab.getRecord_ID())!=null && isTreeDrivenByValue())
        			model.removeNode(model.find(null, gridTab.getRecord_ID()));
        		if ("Saved".equals(e.getAD_Message()) && model.find(null, gridTab.getRecord_ID())==null) 
        		{
					addNewNode();
        			if (isTreeDrivenByValue())
        				treePanel.prepareForRefresh();
				}
        		
        		if ("Saved".equals(e.getAD_Message()) && model.find(null, gridTab.getRecord_ID()) != null && !isTreeDrivenByValue())
        		{
        			DefaultTreeNode<Object> treeNode = model.find(null, gridTab.getRecord_ID());
        			if (treeNode != null) { // 
        				MTreeNode data = (MTreeNode) treeNode.getData();

        				String label = (isValueDisplayed() ? (gridTab.getValue("Value").toString() + " - ") : "") + gridTab.get_ValueAsString("Name");
        				if (!data.getName().equals(label)) {
        					data.setName(label);
        					treeNode.setData(data);
        				}
        			}
				}

        		if (refresh)
        		{
        			int AD_Tree_ID = Env.getContextAsInt (Env.getCtx(), getWindowNo(), "AD_Tree_ID", true);
        		
    				if (AD_Tree_ID != 0)
    				{
            			if (treePanel.initTree(AD_Tree_ID, windowNo))
            				echoDeferSetSelectedNodeEvent();
            			else
            				setSelectedNode(gridTab.getRecord_ID());
            			
            		}   
    				else
    				{
    					AD_Tree_ID = MTree.getDefaultAD_Tree_ID (Env.getAD_Client_ID(Env.getCtx()), gridTab.getKeyColumnName());
        				treePanel.prepareForRefresh();
            			int linkColId = MTree.get(Env.getCtx(), AD_Tree_ID, null).getParent_Column_ID();
            			String linkColName = null;
            			int linkID = 0;
            			if (linkColId > 0) {
            				linkColName = MColumn.getColumnName(Env.getCtx(), linkColId);
            				linkID = Env.getContextAsInt(Env.getCtx(), windowNo, linkColName, true);
            			}
            			if (treePanel.initTree(AD_Tree_ID, windowNo, linkColName, linkID))
            				echoDeferSetSelectedNodeEvent();
            			else
            				setSelectedNode(gridTab.getRecord_ID());
    				}
					
				}    

        	} else if (e.isInserting() && gridTab.getRecord_ID() < 0 && gridTab.getTabLevel() > 0
        			&& gridTab.getParentTab() != null && gridTab.getParentTab().getValue("AD_Tree_ID") != null)
        	{
    			int AD_Tree_ID = Integer.parseInt(gridTab.getParentTab().getValue("AD_Tree_ID").toString());
    			treePanel.initTree(AD_Tree_ID, windowNo);
    		}
        }
        
        //update list view
        if (listPanel.isVisible()) {
        	listPanel.updateListIndex();
        	listPanel.dynamicDisplay(col);
        	if (GridTable.DATA_REFRESH_MESSAGE.equals(e.getAD_Message()) || 
        		"Sorted".equals(e.getAD_Message())) {
        		listPanel.invalidateGridView();
        	}
        }
    }

    /**
     * Delete tree node by recordId
     * @param recordId
     */
    private void deleteNode(int recordId) {
		if (recordId <= 0) return;

		SimpleTreeModel model = (SimpleTreeModel)(TreeModel<?>) treePanel.getTree().getModel();

		if (treePanel.getTree().getSelectedItem() != null) {
			DefaultTreeNode<Object> treeNode =  treePanel.getTree().getSelectedItem().getValue();
			MTreeNode data = (MTreeNode) treeNode.getData();
			if (data.getNode_ID() == recordId) {
				model.removeNode(treeNode);
				return;
			}
		}

		DefaultTreeNode<Object> treeNode = model.find(null, recordId);
		if (treeNode != null) {
			model.removeNode(treeNode);
		}
	}

    /**
     * Add new tree node for current row
     */
	private void addNewNode() {
    	if (gridTab.getRecord_ID() > 0) {
	    	String name = (String)gridTab.getValue("Name");
			String description = (String)gridTab.getValue("Description");
			boolean summary = gridTab.getValueAsBoolean("IsSummary");
			String imageIndicator = (String)gridTab.getValue("Action");  //  Menu - Action
			//
			SimpleTreeModel model = (SimpleTreeModel)(TreeModel<?>) treePanel.getTree().getModel();
			DefaultTreeNode<Object> treeNode = model.getRoot();
			MTreeNode root = (MTreeNode) treeNode.getData();

			int parentID = root.getNode_ID();
			DefaultTreeNode<Object> parentNode = null;
			if (isTreeDrivenByValue()) {
				String value = gridTab.getValue("Value").toString();
				parentID = PO.retrieveIdOfParentValue(value, getTableName(), Env.getAD_Client_ID(Env.getCtx()), null);
				parentNode = model.find(treeNode, parentID);
				if (isValueDisplayed()) {
					name = value + " - " + name;
				}
			}
			MTreeNode node = new MTreeNode (gridTab.getRecord_ID(), 0, name, description,
					parentID, summary, imageIndicator, false, null);
			DefaultTreeNode<Object> newNode = new DefaultTreeNode<Object>(node);

			if (isTreeDrivenByValue() && parentNode != null) {
				model.addNode(parentNode, newNode, 0);
			} else {
				model.addNode(newNode);
			}

			int[] path = model.getPath(newNode);
			Treeitem ti = treePanel.getTree().renderItemByPath(path);
			treePanel.getTree().setSelectedItem(ti);			
    	}
	}

	/**
	 * Set selected tree node by recordId
	 * @param recordId
	 */
	private void setSelectedNode(int recordId) {
		if (recordId <= 0) return;
		
		//force on init render
		if (TreeUtils.isOnInitRenderPosted(treePanel.getTree()) || treePanel.getTree().getTreechildren() == null
			|| treePanel.getTree().getTreechildren().getItemCount() == 0) {
			treePanel.getTree().onInitRender();
		}

		SimpleTreeModel model = (SimpleTreeModel)(TreeModel<?>) treePanel.getTree().getModel();
		if (treePanel.getTree().getSelectedItem() != null) {
			Treeitem treeItem = treePanel.getTree().getSelectedItem();
			if (!treeItem.isLoaded()){
				return;
			}
			
			DefaultTreeNode<Object> treeNode = treeItem.getValue();
			 
			MTreeNode data = (MTreeNode) treeNode.getData();
			if (data.getNode_ID() == recordId) {
				int[] path = model.getPath(treeNode);
				Treeitem ti = treePanel.getTree().renderItemByPath(path);
				if (ti.getPage() == null) {
					echoDeferSetSelectedNodeEvent();
				}

				boolean changed = false;
				if (isValueDisplayed()) {
					String value = (String) gridTab.getValue("Value");
					String name = (String) gridTab.getValue("Name");
					String full = value + " - " + name;

					if (full != null && !full.equals(data.getName())) {
						data.setName(full);
						changed = true;
					}
				} else if (Env.isBaseLanguage(Env.getCtx(), "AD_Menu")) {
					String name = (String) gridTab.getValue("Name");
					if (name != null && !name.equals(data.getName())) {
						data.setName(name);
						changed = true;
					}				
				}

				Object summaryobj = gridTab.getValue("IsSummary");
				boolean summary = false;
				if (summaryobj != null) {
					if (summaryobj instanceof Boolean) {
						summary = ((Boolean)summaryobj).booleanValue();
					} else {
						summary = "Y".equals(summaryobj.toString());
					}
				}
				if (summary != data.isSummary()) {
					data.setSummary(summary);
					changed = true;
				}

				if (changed) {
					treeNode.setData(data);
				}
				
				return;
			}
		}
		
		DefaultTreeNode<Object> treeNode = model.find(null, recordId);
		if (treeNode != null) {
			int[] path = model.getPath(treeNode);
			Treeitem ti = treePanel.getTree().renderItemByPath(path);
			treePanel.getTree().selectItem(ti);
		} else {
			addNewNode();
		}
	}

	@Override
	public void switchRowPresentation() {
		if (form.isVisible()) {
			form.setVisible(false);
			((HtmlBasedComponent)form.getParent()).setStyle("");
		} else {
			form.setVisible(true);
			((HtmlBasedComponent)form.getParent()).setStyle("overflow-y: visible;");
		}
		
		setGroupTabboxVisibility();
		
		listPanel.setVisible(!form.isVisible());
		if (listPanel.isVisible()) {
			listPanel.refresh(gridTab);
			listPanel.scrollToCurrentRow();
			listPanel.invalidate();
		} else {
			listPanel.deactivate();
		}
		
		Events.sendEvent(this, new Event(ON_SWITCH_VIEW_EVENT, this));
	}
	

    
    /**
     * After Find window closes switch to grid view if configured
     * @return void
     */
    public void onAfterFind() {

 		if(!isGridView()) {
 			
 			String userPreference = Env.getContext(Env.getCtx(), MUserPreference.COLUMNNAME_ViewFindResult);
 			
 			// User preference "View find result" has priority, if "Always In Grid" is set
 			if( userPreference.equals(MUserPreference.VIEWFINDRESULT_AlwaysInGridView) ) {
 				switchRowPresentation();
 			}
 			
 			// If User preference "View find result" is set to "According To Threshold"
 			else if( 
 				userPreference.equals(MUserPreference.VIEWFINDRESULT_AccordingToThreshold) &&
 				this.getGridTab().getRowCount() >= Env.getContextAsInt(Env.getCtx(), MUserPreference.COLUMNNAME_GridAfterFindThreshold)
 			) {
 				switchRowPresentation();
 			}
 			
 			// If user preference for View is set to "Default" we will check ZK_GRID_AFTER_FIND system configuration
 			else if( MSysConfig.getBooleanValue(MSysConfig.ZK_GRID_AFTER_FIND, false, Env.getAD_Client_ID(Env.getCtx())) ) {
 				switchRowPresentation();
 			}
 			
 			// Last, fallback to default behavior - "Is single row" option for specific tab
 			else if( !gridTab.isSingleRow() ) {
 				switchRowPresentation();
 			}
 			
		}
    	
    }

	/**
	 * Listener for zoom event
	 */
	static class ZoomListener implements EventListener<Event> {

		private IZoomableEditor zoomableEditor;

		ZoomListener(IZoomableEditor editor) {
			zoomableEditor = editor;
		}

		public void onEvent(Event event) throws Exception {
			if (Events.ON_CLICK.equals(event.getName())) {
				zoomableEditor.actionZoom();
			}

		}

	}

	/**
	 * @see IADTabpanel#afterSave(boolean)
	 */
	@Override
	public void afterSave(boolean onSaveEvent) {
	}

	@Override
	public void focus() {
		if (form.isVisible() && !isMobile())
			this.focusToFirstEditor(true);
		else
			listPanel.focus();
	}

	/**
	 * Set field focus by column name 
	 * @param columnName
	 */
	public void setFocusToField(String columnName) {
		if (formContainer.isVisible()) {
			for (WEditor editor : editors) {
				if (columnName.equals(editor.getColumnName())) {
					Clients.response(new AuFocus(editor.getComponent()));
					break;
				}
			}
		} else {
			listPanel.setFocusToField(columnName);
		}
	}

	/**
	 * @see IADTabpanel#onEnterKey()
	 */
	@Override
	public boolean onEnterKey() {
		if (listPanel.isVisible()) {
			return listPanel.onEnterKey();
		}
		return false;
	}

	@Override
	public boolean isGridView() {
		return listPanel.isVisible();
	}

	@Override
	public JPiereGridView getJPiereGridView() {
		return listPanel;
	}
	
	@Override
	public boolean isActivated() {
		return activated;
	}
	
	@Override
	public void setDetailPaneMode(boolean detailPaneMode) {
		if (this.detailPaneMode != detailPaneMode) {
			this.detailPaneMode = detailPaneMode;
			if (detailPaneMode) {
				detachDetailPane();
			} else {
				attachDetailPane();
			}
			ZKUpdateUtil.setVflex(this, "true");
			listPanel.setDetailPaneMode(detailPaneMode, gridTab);
		}		
	}

	/**
	 * Show detail pane
	 */
	private void attachDetailPane() {
		if (formContainer.getSouth() != null) {
			formContainer.getSouth().setVisible(true);
			if (formContainer.getSouth().isOpen()) {
				if (jpiereDetailPane != null) {
					if (jpiereDetailPane.getParent() != formContainer.getSouth())
						formContainer.appendSouth(jpiereDetailPane);
					else
						jpiereDetailPane.setVisible(true);
				}
			}
		}
	}

	/**
	 * Hide detail pane
	 */
	private void detachDetailPane() {
		if (formContainer.getSouth() != null) {
			formContainer.getSouth().setVisible(false);
			if (jpiereDetailPane != null && jpiereDetailPane.getParent() != null) {
				jpiereDetailPane.setVisible(false);
			}
		}
	}
	
	@Override
	public List<Button> getToolbarButtons() {
		List<Button> buttonList = new ArrayList<Button>();
		for(WButtonEditor editor : toolbarButtonEditors) {
			if (editor.getComponent() != null 
					&& editor.getComponent().isVisible()) {
				buttonList.add(editor.getComponent());
			}
		}
		
		for(ToolbarProcessButton processButton : toolbarProcessButtons) {
			if (processButton.getButton().isVisible()) {
				buttonList.add(processButton.getButton());
			}
		}
		return buttonList;
	}

	@Override
	public boolean needSave(boolean rowChange, boolean onlyRealChange) {
		return getGridTab().needSave(rowChange, onlyRealChange);
	}

	@Override
	public boolean dataSave(boolean onSaveEvent) {
		return getGridTab().dataSave(onSaveEvent);
	}

	@Override
	public boolean isDetailPaneMode() {
		return this.detailPaneMode;
	}

	@Override
	public void setTabNo(int tabNo) {
		this.tabNo = tabNo;
	}

	@Override
	public int getTabNo() {
		return tabNo;
	}

	/**
	 * Activate selected detail tab if it is visible
	 */
	public void activateJPiereDetailIfVisible() {
		if (isDetailVisible()) {
			JPiereIADTabpanel tabPanel = jpiereDetailPane.getSelectedADTabpanel();
	    	if (tabPanel != null && !tabPanel.isActivated()) {
		    	tabPanel.activate(true);
		    	if (!tabPanel.isGridView()) {
		    		tabPanel.switchRowPresentation();	
		    	}	    		    	
	    	} else if (tabPanel != null && !tabPanel.getGridTab().isCurrent()) {
	    		tabPanel.activate(true);
	    	} else if (tabPanel != null && tabPanel.isGridView()) {
	    		//ensure row indicator is not lost
    			RowRenderer<Object[]> renderer = tabPanel.getJPiereGridView().getListbox().getRowRenderer();
    			JPiereGridTabRowRenderer gtr = (JPiereGridTabRowRenderer)renderer; //JPIERE
    			org.zkoss.zul.Row row = gtr.getCurrentRow();
    			if (row != null)	
    				gtr.setCurrentRow(row);
	    	}
		}		
	}
	
	@Override
	public boolean isDetailVisible() {
		if (formContainer.getSouth() == null || !formContainer.getSouth().isVisible()
			|| !formContainer.getSouth().isOpen()) {
			return false;
		}

		return jpiereDetailPane != null;
	}
	
	/**
	 * Is selected header tab has one or more detail tab
	 * @return true if selected tab has one or more detail/child tab
	 */
	public boolean hasDetailTabs() {
		if (formContainer.getSouth() == null || !formContainer.getSouth().isVisible()) {
			return false;
		}

		return jpiereDetailPane != null && jpiereDetailPane.getTabcount() > 0;
	}
	
	/**
	 * Set focus to next readwrite editor from ref
	 * @param ref
	 */
	@Override
	public void focusToNextEditor(WEditor ref) {
		boolean found = false;
		for (WEditor editor : editors) {
			if (editor == ref) {
				found = true;
				continue;
			}
			if (found) {
				if (editor.isVisible() && editor.isReadWrite()
					// note, no auto focus on next button - if interested in
					// focusing next button must implement to check if the button
					// is just showin in toolbar, just focus on window fields must be auto focused
					&& ! (editor instanceof WButtonEditor)) {
					focusToEditor(editor, false);
					break;
				}
			}
		}
	}
	
	/**
	 * Set focus to editor
	 * @param toFocus
	 * @param checkCurrent true to check if form currently has focus (using zk.currentFocus)
	 */
	protected void focusToEditor(WEditor toFocus, boolean checkCurrent) {
		Component c = toFocus.getComponent();
		if (c instanceof EditorBox) {
			c = ((EditorBox)c).getTextbox();
		} else if (c instanceof NumberBox) {
			c = ((NumberBox)c).getDecimalbox();
		} else if (c instanceof Urlbox) {
			c = ((Urlbox)c).getTextbox();
		}
		if (!checkCurrent) {
			((HtmlBasedComponent)c).focus();
		} else {
			StringBuilder script = new StringBuilder("(function(){let b=true;try{if (zk.currentFocus) {");
			script.append("let p=zk.Widget.$('#").append(formContainer.getCenter().getUuid()).append("');");
			script.append("if (zUtl.isAncestor(p, zk.currentFocus)) {");
			script.append("b=false;}}}catch(error){}");
			script.append("if(b){let w=zk.Widget.$('#").append(c.getUuid()).append("');w.focus(0);}");
			script.append("})()");
			Clients.response(new AuScript(script.toString()));
		}
	}

	@Override
	public void setParent(Component parent) {
		super.setParent(parent);
		if (parent != null) {
			listPanel.onADTabPanelParentChanged();
			if (ClientInfo.isMobile())
				setupFormSwipeListener(form);
		}
	}

	/**
	 * Is tree order by value
	 * @return true if tree is order by value
	 */
	private boolean isTreeDrivenByValue() {
		SimpleTreeModel model = (SimpleTreeModel)(TreeModel<?>) treePanel.getTree().getModel();
		boolean retValue = false;
		retValue = model.isTreeDrivenByValue();
		return retValue;
	}

	/**
	 * Is value shown in tree
	 * @return true if value is shown in tree
	 */
	private boolean isValueDisplayed() {
		SimpleTreeModel model = (SimpleTreeModel)(TreeModel<?>) treePanel.getTree().getModel();
		boolean retValue = false;
		retValue = model.isValueDisplayed();
		return retValue;
	}

	@Override
	public void onPageDetached(Page page) {
		if (formContainer.getSouth() != null) {
			if (formContainer.getSouth().isVisible() && formContainer.getSouth().isOpen()) {
				String height = formContainer.getSouth().getHeight();
				if (! Util.isEmpty(height))
					savePreference("DetailPane.Height", height);
			}
		}
		if (treePanel != null && formContainer.getWest() != null) {
			if (formContainer.getWest().isVisible() && formContainer.getWest().isOpen()) {
				String width = formContainer.getWest().getWidth();
				if (! Util.isEmpty(width))
					savePreference("TreePanel.Width", width);
			}
		}
		for(WEditor editor : editors) {
			editor.getComponent().setWidgetListener("onFocus", null);
		}
		super.onPageDetached(page);
	}

	/**
	 * Save user preference for this AD Window
	 * @param attribute
	 * @param value
	 */
	protected void savePreference(String attribute, String value)
	{
		int windowId = getGridTab().getAD_Window_ID();
		int adTabId = getGridTab().getAD_Tab_ID();
		if (windowId > 0 && adTabId > 0) {
			Query query = new Query(Env.getCtx(), MTable.get(Env.getCtx(), I_AD_Preference.Table_ID), "AD_Window_ID=? AND Attribute=? AND AD_User_ID=? AND AD_Process_ID IS NULL AND PreferenceFor = 'W'", null);
			int userId = Env.getAD_User_ID(Env.getCtx());
			MPreference preference = query.setOnlyActiveRecords(true)
					.setApplyAccessFilter(true)
					.setClient_ID()
					.setParameters(windowId, adTabId+"|"+attribute, userId)
					.first();
			if (preference == null || preference.getAD_Preference_ID() <= 0) {
				preference = new MPreference(Env.getCtx(), 0, null);
				preference.setAD_Window_ID(windowId);
				preference.setAD_User_ID(userId);
				preference.setAttribute(adTabId+"|"+attribute);
			}
			preference.setValue(value);
			preference.saveEx();
			//update current context
			Env.getCtx().setProperty("P"+windowId+"|"+adTabId+"|"+attribute, value);
		}
	}

	/**
	 * handle client info event
	 */
	protected void onClientInfo() {
		if (!uiCreated || gridTab == null) return;
		int numCols=gridTab.getNumColumns();
    	if (numCols <= 0) {
    		numCols=6;
    	}

    	if (ClientInfo.maxWidth(ClientInfo.EXTRA_SMALL_WIDTH-1)) {
    		if (numCols > 3) {
    			numCols=3;
    		}
    	} else if (ClientInfo.maxWidth(ClientInfo.MEDIUM_WIDTH-1)) {
    		if (numCols > 6) {
    			numCols=6;
    		}
    	}
		if (numCols > 0 && numCols != numberOfFormColumns) {
			createUI(true);
			dynamicDisplay(0);
		}
	};
	
	/**
	 * Is client using mobile browser
	 * @return true if client is mobile
	 */
	protected boolean isMobile() {
		return ClientInfo.isMobile();
	}
	
	@Override
	public void editorTraverse(Callback<WEditor> editorTaverseCallback) {
		editorTraverse(editorTaverseCallback, editors);		
	}

	@Override
	public boolean isEnableQuickFormButton()
	{
		boolean hasQuickForm = false;
		int tabID = getGridTab().getAD_Tab_ID();
		
		if (quickFormCache.containsKey(tabID))
		{
			hasQuickForm = quickFormCache.get(tabID);
		}
		else if (getGridTab() != null)
		{
			for (GridField field : getGridTab().getFields())
			{
				if (field.isQuickForm())
				{
					hasQuickForm = true;
					break;
				}
			}
			quickFormCache.put(tabID, hasQuickForm);
		}
		
		return hasQuickForm;
	}
	
	/**
	 * Set Visibility for {@link #tabbox} based on {@link #form} Visibility and whether {@link #tabbox} is empty
	 */
	private void setGroupTabboxVisibility() {
		boolean isGroupTabVisible = false;
		if(tabbox.getChildren() != null && tabbox.getChildren().size() > 0) {
			isGroupTabVisible = form.isVisible();
		}
		tabbox.setVisible(isGroupTabVisible);
	}

	@Override
	public boolean isEnableCustomizeButton()
	{
		return isGridView();
	}

	@Override
	public void updateToolbar(ADWindowToolbar toolbar)
	{

	}

	@Override
	public void updateDetailToolbar(Toolbar toolbar)
	{

	}
	
	/**
	 * Get parent AD window
	 * @return {@link AbstractADWindowContent}
	 */
	public JPiereAbstractADWindowContent getADWindowContent()
	{
		return windowPanel;
	}

	@Override
	public GridView getGridView() {
		GridView noUse = new GridView();
		return noUse;
	}

	@Override
	public void setDetailPane(DetailPane detailPane) {
	}

	@Override
	public DetailPane getDetailPane() {
		DetailPane noUse = new DetailPane();
		return noUse;
	}

	@Override
	public void init(AbstractADWindowContent winPanel, GridTab gridTab) {
		;
	}

}