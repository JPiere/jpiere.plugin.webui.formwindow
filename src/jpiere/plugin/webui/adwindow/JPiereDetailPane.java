/***********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - hengsin                         								   *
 **********************************************************************/
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

package jpiere.plugin.webui.adwindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.adempiere.base.IServiceHolder;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.action.Actions;
import org.adempiere.webui.action.IAction;
import org.adempiere.webui.adwindow.ADSortTab;
import org.adempiere.webui.adwindow.ADTabpanel;				//JPIERE-0014
import org.adempiere.webui.adwindow.IADTabpanel;			//JPIERE-0014
import org.adempiere.webui.adwindow.ProcessButtonPopup;		//JPIERE-0014
import org.adempiere.webui.adwindow.ToolbarCustomButton;	//JPIERE-0014
import org.adempiere.webui.component.ADTabListModel.ADTabLabel;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.ToolBar;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
//import org.adempiere.webui.window.CustomizeGridViewDialog; //JPIERE Unused
import org.adempiere.webui.window.WRecordInfo;
import org.compiere.model.DataStatusEvent;
import org.compiere.model.GridTab;
import org.compiere.model.MToolBarButton;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.image.AImage;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.LayoutRegion;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Toolbar;

/**
 * Detail panel that display the child tabs of a parent {@link ADTabpanel} tab.<br/>
 * Implemented as a panel with {@link Tabbox}.
 * 
 * @author hengsin
 */
public class JPiereDetailPane extends Panel implements EventListener<Event>, IdSpace {

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 3764215603459946930L;

	public static final String BTN_PROCESS_ID = "BtnProcess";

	public static final String BTN_DELETE_ID = "BtnDelete";

	public static final String BTN_EDIT_ID = "BtnEdit";

	public static final String BTN_NEW_ID = "BtnNew";
	
	public static final String BTN_SAVE_ID = "BtnSave";
	
	public static final String BTN_QUICK_FORM_ID = "BtnQuickForm";

	public static final String BTN_CUSTOMIZE_ID = "BtnCustomize";
	
	private static final String BTN_TOGGLE_ID = "BtnToggle";
	
	/** Boolean execution attribute to indicate tabbox is handling ON_SELECT event **/
	private static final String TABBOX_ONSELECT_ATTRIBUTE = "detailpane.tabbox.onselect";

	/** event after handling of ON_SElECT event of a detail tab */
	private static final String ON_POST_SELECT_TAB_EVENT = "onPostSelectTab";

	/** Attribute use by {@link #messageContainers} to hold status text **/
	private static final String STATUS_TEXT_ATTRIBUTE = "status.text";

	/** Attribute use by {@link #messageContainers} to hold error text **/
	private static final String STATUS_ERROR_ATTRIBUTE = "status.error";

	private static final String CUSTOMIZE_IMAGE = "images/Customize16.png";
	private static final String DELETE_IMAGE = "images/Delete16.png";
	private static final String EDIT_IMAGE = "images/EditRecord16.png";
	private static final String NEW_IMAGE = "images/New16.png";
	private static final String PROCESS_IMAGE = "images/Process16.png";
	private static final String SAVE_IMAGE = "images/Save16.png";
	private static final String QUICK_FORM_IMAGE = "images/QuickForm16.png";
	private static final String TOGGLE_IMAGE = "images/Multi16.png";

	/** tabbox for AD_Tabs **/
	private Tabbox tabbox;

	/** Registered event listener for DetailPane events **/
	private EventListener<Event> eventListener;

	/** AD_Tab_ID:Hbox. Message (status, error) container for each tab. **/
	private Map<Integer, Hbox> messageContainers = new HashMap<Integer, Hbox>();

	/** content for message popup **/
	private Div msgPopupCnt;

	/** message popup window **/
	private Window msgPopup;
	
	/** last selected tab index **/
	private int prevSelectedIndex = 0;

	/**
	 * On activate event for detail tab.<br/>
	 * Use to activate detail tab or notify detail tab after header tab change.
	 */
	public static final String ON_ACTIVATE_DETAIL_EVENT = "onActivateDetail";
	
	/** on delete event for selected tab **/
	public static final String ON_DELETE_EVENT = "onDelete";

	/** on new event for selected tab **/
	public static final String ON_NEW_EVENT = "onNew";

	/** event to edit current row of selected tab **/
	public static final String ON_EDIT_EVENT = "onEdit";
	
	/** on save event for selected tab **/
	public static final String ON_SAVE_EVENT = "onSave";
	
	/** on quick form event for selected tab **/
	public static final String ON_QUICK_FORM_EVENT = "onQuickForm";
	
	/**
	 * Record navigation event for selected tab.<br/>
	 * Event data is the navigation action (previous, next, first and last).
	 */
	public static final String ON_RECORD_NAVIGATE_EVENT = "onRecordNavigate";
	
	/**
	 * default constructor
	 */
	public JPiereDetailPane() {
		tabbox = new Tabbox();
		tabbox.setParent(this);
		tabbox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				fireActivateDetailEvent();
				Events.postEvent(new Event(ON_POST_SELECT_TAB_EVENT, JPiereDetailPane.this));
				Executions.getCurrent().setAttribute(TABBOX_ONSELECT_ATTRIBUTE, Boolean.TRUE);
			}
		});
		tabbox.setSclass("adwindow-detailpane-tabbox");
		tabbox.setTabscroll(true);
		ZKUpdateUtil.setWidth(tabbox, "100%");
		
		createPopup();
		
		this.setSclass("adwindow-detailpane");
		
		addEventListener(LayoutUtils.ON_REDRAW_EVENT, this);		
				
		setId("detailPane");
		
	}
	
	/**
	 * Get selected tab index
	 * @return selected tab index
	 */
	public int getSelectedIndex() {
		return tabbox.getSelectedIndex();
	}
	
	/**
	 * Set selected tab index 
	 * @param curTabIndex
	 */
	public void setSelectedIndex(int curTabIndex) {
		tabbox.setSelectedIndex(curTabIndex);
		prevSelectedIndex = curTabIndex;
	}
	
	/**
	 * Get number of tabs
	 * @return number of tabs
	 */
	public int getTabcount() {
		int count = 0;
		Tabs tabs = tabbox.getTabs();
		if (tabs != null)
			count = tabs.getChildren().size();
		return count;
	}
	
	/**
	 * Undo last tab selection
	 */
	public void undoLastTabSelection() {
		tabbox.setSelectedIndex(prevSelectedIndex);
	}

	/**
	 * Redraw tabbox
	 */
	public void refresh() {
		tabbox.invalidate();
	}
	
	/**
	 * Replace or add IADTabpanel to tabbox.
	 * @param index
	 * @param tabPanel
	 * @param tabLabel
	 */
	public void setADTabpanel(int index, JPiereIADTabpanel tabPanel, ADTabLabel tabLabel) {
		if (index < getTabcount()) {
			tabbox.getTabpanel(index).appendChild(tabPanel);
		} else {
			addADTabpanel(tabPanel, tabLabel);
		}
	}
	
	/**
	 * Replace or add IADTabpanel to tabbox.
	 * @param index
	 * @param tabPanel
	 * @param tabLabel
	 * @param enabled
	 */
	public void setADTabpanel(int index, JPiereIADTabpanel tabPanel, ADTabLabel tabLabel, boolean enabled) {
		if (index < getTabcount()) {
			tabbox.getTabpanel(index).appendChild(tabPanel);
		} else {
			addADTabpanel(tabPanel, tabLabel, enabled);
		}
	}
	
	/**
	 * Add IADTabpanel to tabbox
	 * @param tabPanel
	 * @param tabLabel
	 */
	public void addADTabpanel(JPiereIADTabpanel tabPanel, ADTabLabel tabLabel) {
		addADTabpanel(tabPanel, tabLabel, true);
	}
	
	/**
	 * Add IADTabpanel to tabbox
	 * @param tabPanel
	 * @param tabLabel
	 * @param enabled
	 */
	public void addADTabpanel(JPiereIADTabpanel tabPanel, ADTabLabel tabLabel, boolean enabled) {
		Tabs tabs = tabbox.getTabs();
		if (tabs == null) {
			tabs = new Tabs();
			tabbox.appendChild(tabs);
		}
		Tab tab = new Tab();
		tabs.appendChild(tab);
		tab.setLabel(tabLabel.label);
		if (!enabled) {
			tab.setDisabled(true);
			tab.setSclass("adwindow-detailpane-sub-tab");
		}
		
		tab.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				//click on tab title trigger edit of current row
				Tab tab = (Tab) event.getTarget();
				if (!tab.isSelected()) 
					return;
				
				if (Executions.getCurrent().getAttribute(TABBOX_ONSELECT_ATTRIBUTE) != null)
					return;
							
				org.zkoss.zul.Tabpanel zkTabpanel = tab.getLinkedPanel();
				IADTabpanel adtab = null;
				for(Component c : zkTabpanel.getChildren()) {
					if (c instanceof IADTabpanel) {
						adtab = (IADTabpanel) c;
						break;
					}
				}
				if (adtab != null && adtab.isDetailPaneMode()) {
					onEdit(adtab.getGridTab().isSingleRow());
				}
			}
		});
		
		Tabpanels tabpanels = tabbox.getTabpanels();
		if (tabpanels == null) {
			tabpanels = new Tabpanels();
			ZKUpdateUtil.setWidth(tabpanels, "100%");
			tabbox.appendChild(tabpanels);
		}
		Tabpanel tp = new Tabpanel();
		tabpanels.appendChild(tp);
		
		//setup toolbar
		ToolBar toolbar = tp.getToolbar();		
		HashMap<String, ToolBarButton> buttons = new HashMap<String, ToolBarButton>();
		ToolBarButton button = new ToolBarButton();
		if (ThemeManager.isUseFontIconForImage())
			button.setIconSclass("z-icon-New");
		else
			button.setImage(ThemeManager.getThemeResource(NEW_IMAGE));
		button.setId(BTN_NEW_ID);
		button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getTarget().isVisible())
					onNew();
			}
		});
		button.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "SaveCreate")) + "    Shift+Alt+N");
		buttons.put(BTN_NEW_ID.substring(3, BTN_NEW_ID.length()), button);
		
		button = new ToolBarButton();
		if (ThemeManager.isUseFontIconForImage())
			button.setIconSclass("z-icon-Edit");
		else
			button.setImage(ThemeManager.getThemeResource(EDIT_IMAGE));
		button.setId(BTN_EDIT_ID);
		button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getTarget().isVisible())
					onEdit(true);	
			}
		});
		button.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "EditRecord")) + "    Shift+Alt+E");
        buttons.put(BTN_EDIT_ID.substring(3, BTN_EDIT_ID.length()), button);

		button = new ToolBarButton();
		if (ThemeManager.isUseFontIconForImage())
			button.setIconSclass("z-icon-Delete");
		else
			button.setImage(ThemeManager.getThemeResource(DELETE_IMAGE));
		button.setId(BTN_DELETE_ID);
		button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getTarget().isVisible()) {
					Event openEvent = new Event(ON_DELETE_EVENT, JPiereDetailPane.this);
					eventListener.onEvent(openEvent);
				}
			}
		});
		button.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Delete")) + "    Shift+Alt+D");
        buttons.put(BTN_DELETE_ID.substring(3, BTN_DELETE_ID.length()), button);

		button = new ToolBarButton();
		if (ThemeManager.isUseFontIconForImage())
			button.setIconSclass("z-icon-Save");
		else
			button.setImage(ThemeManager.getThemeResource(SAVE_IMAGE));
		button.setId(BTN_SAVE_ID);
		button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getTarget().isVisible()) {
					Event openEvent = new Event(ON_SAVE_EVENT, JPiereDetailPane.this);
					eventListener.onEvent(openEvent);
				}
			}
		});
		button.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Save")) + "    Shift+Alt+S");
        buttons.put(BTN_SAVE_ID.substring(3, BTN_SAVE_ID.length()), button);
		
		if (!tabPanel.getGridTab().isSortTab()) {
			button = new ToolBarButton();
			if (ThemeManager.isUseFontIconForImage())
				button.setIconSclass("z-icon-Process");
			else
				button.setImage(ThemeManager.getThemeResource(PROCESS_IMAGE));
			button.setId(BTN_PROCESS_ID);
			button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					if (event.getTarget().isVisible())
						onProcess(event.getTarget());
				}
			});
			button.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Process")) + "    Shift+Alt+O");
	        buttons.put(BTN_PROCESS_ID.substring(3, BTN_PROCESS_ID.length()), button);
		}
		
		// ADD Quick Form Button
		button = new ToolBarButton();
		if (ThemeManager.isUseFontIconForImage())
			button.setIconSclass("z-icon-QuickForm");
		else
			button.setImage(ThemeManager.getThemeResource(QUICK_FORM_IMAGE));
		button.setId(BTN_QUICK_FORM_ID);
		button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception
			{
				if (event.getTarget().isVisible()) {
					Event openEvent = new Event(ON_QUICK_FORM_EVENT, JPiereDetailPane.this);
					eventListener.onEvent(openEvent);
				}
			}
		});
		button.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "QuickForm")) + "    Shift+Alt+F");
		buttons.put(BTN_QUICK_FORM_ID.substring(3, BTN_QUICK_FORM_ID.length()), button);
		
		// ADD Customize grid button
		button = new ToolBarButton();
		if (ThemeManager.isUseFontIconForImage())
			button.setIconSclass("z-icon-Customize");
		else
			button.setImage(ThemeManager.getThemeResource(CUSTOMIZE_IMAGE));
		button.setId(BTN_CUSTOMIZE_ID);
		button.addEventListener(Events.ON_CLICK, e -> { if(e.getTarget().isVisible()) onCustomize(e); });
		button.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Customize")));
		buttons.put(BTN_CUSTOMIZE_ID.substring(3, BTN_CUSTOMIZE_ID.length()), button);
		
		// ADD toggle grid button
		button = new ToolBarButton();
		if (ThemeManager.isUseFontIconForImage())
			button.setIconSclass("z-icon-Multi");
		else
			button.setImage(ThemeManager.getThemeResource(TOGGLE_IMAGE));
		button.setId(BTN_TOGGLE_ID);
		button.addEventListener(Events.ON_CLICK, e -> { if(e.getTarget().isVisible()) onToggle(e); });
		button.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Toggle")) + "    Shift+Alt+T");
		buttons.put(BTN_TOGGLE_ID.substring(3, BTN_TOGGLE_ID.length()), button);

		//Detail toolbar button configure at AD_ToolBarButton
		MToolBarButton[] officialButtons = MToolBarButton.getToolbarButtons("D", null);
		for (MToolBarButton toolbarButton : officialButtons) {
			if ( !toolbarButton.isActive() ) {
				buttons.remove(toolbarButton.getComponentName());
			} else {
				if ( toolbarButton.isCustomization() ) {
					String actionId = toolbarButton.getActionClassName();
					IServiceHolder<IAction> serviceHolder = Actions.getAction(actionId);
					if ( serviceHolder != null && serviceHolder.getService() != null ) {

						String labelKey = actionId + ".label";
						String tooltipKey = actionId + ".tooltip";
						String label = Msg.getMsg(Env.getCtx(), labelKey, true);
						String tooltiptext = Msg.getMsg(Env.getCtx(), labelKey, false);
						if (Util.isEmpty(tooltiptext, true))
							tooltiptext = Msg.getMsg(Env.getCtx(), tooltipKey, true);
						if ( labelKey.equals(label) ) {
							label = toolbarButton.getName();
						}
						if ( tooltipKey.equals(tooltiptext) || labelKey.equals(tooltiptext)) {
							tooltiptext = label;
						}
						ToolBarButton btn = new ToolBarButton();
						btn.setName("Btn"+toolbarButton.getComponentName());
						btn.setId("Btn"+toolbarButton.getComponentName());
						btn.setTooltiptext(tooltiptext);
						btn.setDisabled(false);
						btn.setIconSclass(null);
						if (ThemeManager.isUseFontIconForImage()) {
        					String iconSclass = Actions.getActionIconSclass(actionId);
        					if (!Util.isEmpty(iconSclass, true)) {
        						btn.setIconSclass(iconSclass);
        						LayoutUtils.addSclass("font-icon-toolbar-button", btn);
        					}
        				}
        				//not using font icon, fallback to image or label
        				if (Util.isEmpty(btn.getIconSclass(), true)) {
							AImage aImage = Actions.getActionImage(actionId);
							if ( aImage != null ) {
								btn.setImageContent(aImage);
							} else {
								btn.setLabel(label);
							}
        				}

						ToolbarCustomButton toolbarCustomBtn = new ToolbarCustomButton(toolbarButton, btn, actionId, tabPanel.getGridTab().getWindowNo(), tabPanel.getGridTab().getTabNo());
						tp.toolbarCustomButtons.put(btn, toolbarCustomBtn);

						toolbar.appendChild(btn);
					}
				} else {
					if (buttons.get(toolbarButton.getComponentName()) != null) {
						toolbar.appendChild(buttons.get(toolbarButton.getComponentName()));
						if (toolbarButton.isAddSeparator()) {
							toolbar.appendChild(new Separator("vertical"));
						}
					}
				}
			}
		}
		
		//container for status and error text
		Hbox messageContainer = new Hbox();
		messageContainer.setPack("end");
		messageContainer.setAlign("center");
		messageContainer.setSclass("adwindow-detailpane-message");
		messageContainer.setId("messages");
		if (ClientInfo.minWidth(ClientInfo.SMALL_WIDTH))
			toolbar.appendChild(new Space());
		toolbar.appendChild(messageContainer);
		toolbar.setSclass("adwindow-detailpane-toolbar");
		ZKUpdateUtil.setVflex(toolbar, "0");
		messageContainers.put(tabLabel.AD_Tab_ID, messageContainer);
		tabPanel.setAttribute("AD_Tab_ID", tabLabel.AD_Tab_ID);
		
		if (ClientInfo.isMobile() && ClientInfo.maxWidth(ClientInfo.SMALL_WIDTH)) {
			tp.createOverflowButton();
		}
		
		RecordToolbar recordToolbar = new RecordToolbar(tabPanel.getGridTab());
		recordToolbar.addEventListener(ON_RECORD_NAVIGATE_EVENT, eventListener);
		tp.setRecordToolbar(recordToolbar);
		tp.setADTabpanel(tabPanel);

		if (tabPanel.getJPiereGridView() != null) {
			tabPanel.addEventListener(ADTabpanel.ON_DYNAMIC_DISPLAY_EVENT, this);
			tabPanel.getJPiereGridView().addEventListener(ON_EDIT_EVENT, new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					JPiereGridView gridView = (JPiereGridView) event.getTarget();
					if (gridView.isDetailPaneMode())
						onEdit(true);
				}				
			});
		}
	}
	
	/**
	 * Toggle between grid and form view
	 * @param e
	 */
	protected void onToggle(Event e) {
		var adTabPanel = getSelectedADTabpanel();
		if(!(adTabPanel instanceof ADSortTab)) {
			adTabPanel.switchRowPresentation();
			getSelectedPanel().getToolbarButton(BTN_CUSTOMIZE_ID).setDisabled(!adTabPanel.isGridView());//JPIERE-0014

			ToolBarButton btnCustomize = getSelectedPanel().getToolbarButton(BTN_CUSTOMIZE_ID);
			if (btnCustomize != null)
				btnCustomize.setDisabled(!adTabPanel.isGridView());

			Tabpanel tabPanel = (Tabpanel) tabbox.getSelectedTabpanel();			
			tabPanel.setToggleToFormView(!adTabPanel.isGridView());
			tabPanel.afterToggle();
			
			if (adTabPanel != null && adTabPanel instanceof HtmlBasedComponent) {
				((HtmlBasedComponent)adTabPanel).focus();
			}
		}
	}

	/**
	 * Open customize grid view dialog.
	 * @param e
	 */
	protected void onCustomize(Event e) {//JPIERE
//		if (getSelectedADTabpanel() instanceof ADTabpanel) {
//			ADTabpanel tabPanel = (ADTabpanel) getSelectedADTabpanel();
//			CustomizeGridViewDialog.onCustomize(tabPanel, b -> {
//				ADWindow adwindow = ADWindow.findADWindow(DetailPane.this);
//				if (adwindow != null)
//					adwindow.getADWindowContent().focusToLastFocusEditor();
//			});
//		}
	}

	/**
	 * Open process list popup
	 * @param button
	 */
	protected void onProcess(Component button) {
		ProcessButtonPopup popup = new ProcessButtonPopup();
		JPiereADTabpanel adtab = (JPiereADTabpanel) getSelectedADTabpanel();
		if (adtab.getToolbarButtons() != null && adtab.getToolbarButtons().size() > 0)
			popup.render(adtab.getToolbarButtons());
		if (popup.getChildren().size() > 0) {
			popup.setPage(button.getPage());
			popup.open(button, "after_start");
		}
	}

	/**
	 * Set event listener for DetailPane events
	 * @param listener
	 */
	public void setEventListener(EventListener<Event> listener) {
		eventListener = listener;
	}

	/**
	 * Remove all tabs and tabpanels
	 */
	public void reset() {
		if (tabbox.getTabs() != null) {
			tabbox.getTabs().getChildren().clear();
		}
		if (tabbox.getTabpanels() != null) {
			tabbox.getTabpanels().getChildren().clear();
		}
			
	}

	/**
	 * Get IADTabpanel at index
	 * @param index
	 * @return IADTabpanel at index
	 */
	public JPiereIADTabpanel getADTabpanel(int index) {
		if (index < 0 || index >= tabbox.getTabpanels().getChildren().size())
			return null;

		org.zkoss.zul.Tabpanel tabPanel = tabbox.getTabpanel(index);
		for(Component c : tabPanel.getChildren()) {
			if (c instanceof JPiereIADTabpanel)
				return (JPiereIADTabpanel)c;
		}
		return null;
	}
	
	/**
	 * Get IADTabpanel for selected tab
	 * @return selected IADTabpanel
	 */
	public JPiereIADTabpanel getSelectedADTabpanel() {
		org.zkoss.zul.Tabpanel selectedPanel = tabbox.getSelectedPanel();
		if (selectedPanel != null) {
			for(Component c : selectedPanel.getChildren()) {
				if (c instanceof JPiereIADTabpanel)
					return (JPiereIADTabpanel)c;
			}
		}
		return null;
	}

	/**
	 * Get tab panel of selected tab
	 * @return selected {@link Tabpanel}
	 */
	public Tabpanel getSelectedPanel() {
		return (Tabpanel) tabbox.getSelectedPanel();
	}
	
	/**
	 * Set status and error text for selected tab.
	 * @param status
	 * @param error
	 */
	public void setStatusMessage(String status, boolean error) {		
		JPiereIADTabpanel tabPanel = getSelectedADTabpanel();
		if (tabPanel == null) return;
		Hbox messageContainer = messageContainers.get(tabPanel.getAttribute("AD_Tab_ID"));
		
		Execution execution = Executions.getCurrent();
    	if (execution != null) {
    		String key = this.getClass().getName()+"."+messageContainer.getUuid();
    		Object o = execution.getAttribute(key);
    		if (o != null) {
    			if (status == null || status.trim().length() == 0)
    				return;
    		} else {
    			execution.setAttribute(key, Boolean.TRUE);
    		}
    	}
    	
		messageContainer.getChildren().clear();
		//store in attribute for retrieval in ON_CLICK event
		messageContainer.setAttribute(STATUS_ERROR_ATTRIBUTE, error);
    	messageContainer.setAttribute(STATUS_TEXT_ATTRIBUTE, status);
    	messageContainer.setSclass(error ? "docstatus-error" : "docstatus-normal");
    	
    	if (status == null || status.trim().length() == 0)
    		return;
    	
    	String labelText = buildLabelText(status);
    	if (error) {
    		Component ref = isCollapsed(this) ? findTabpanel(this) : findTabpanel(messageContainer);
    		Clients.showNotification(buildNotificationText(status), "error", ref, "top_left", 3500, true);
    	}
    	Label label = new Label(labelText);
    	messageContainer.appendChild(label);
    	if (labelText.length() != status.length()) {
    		label.addEventListener(Events.ON_CLICK, this);
    		label.setStyle("cursor: pointer");
    		
    		label = new Label(" ...");
    		label.setStyle("cursor: pointer");
    		messageContainer.appendChild(label);
    		label.addEventListener(Events.ON_CLICK, this);
    	} else if (ClientInfo.maxWidth(ClientInfo.SMALL_WIDTH)) {
    		label.addEventListener(Events.ON_CLICK, this);
    		label.setStyle("cursor: pointer");
    	}
    	
    	messageContainer.appendChild(new Space());
    	
    	if (!tabPanel.isGridView()) {
    		Tabpanel tp = (Tabpanel) tabbox.getSelectedTabpanel();
    		if (tp.getRecordToolbar() != null) {
    			tp.getRecordToolbar().dynamicDisplay();
    		}
    	}
	}

	/**
	 * Is parent of detailPane in collapsed state
	 * @param detailPane
	 * @return true if parent of detailPane is in collapsed state
	 */
	private boolean isCollapsed(JPiereDetailPane detailPane) {
		Component parent = detailPane.getParent();
		while (parent != null) {
			if (parent instanceof LayoutRegion lr)
				return !lr.isOpen();
			parent = parent.getParent();
		}
		return false;
	}

	/**
	 * Shorten status text to a more presentable length.
	 * @param statusText
	 * @return shorten status text
	 */
	private String buildLabelText(String statusText) {
		if (statusText == null)
			return "";
		if (statusText.length() <= 80)
			return statusText;
		
		int index = statusText.indexOf(" - java.lang.Exception");
		if (index > 0)
			return statusText.substring(0, index);
		return statusText.substring(0, 80);
	}

	/**
	 * Shorten notification text to a more presentable length.
	 * @param statusText
	 * @return shorten notification text
	 */
	private String buildNotificationText(String statusText) {
		if (statusText == null)
			return "";
		if (statusText.length() <= 140)
			return statusText;
		
		int index = statusText.indexOf(" - java.lang.Exception");
		if (index > 0)
			return statusText.substring(0, index);
		return statusText.substring(0, 136) + " ...";
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CLICK)) {
			Component messageContainer = event.getTarget().getParent();
			Boolean error = (Boolean) messageContainer.getAttribute(STATUS_ERROR_ATTRIBUTE);
			String status = (String) messageContainer.getAttribute(STATUS_TEXT_ATTRIBUTE);
			
			showPopup(error, status);
		} else if (event.getName().equals(ADTabpanel.ON_DYNAMIC_DISPLAY_EVENT)) {
			if (LayoutUtils.isReallyVisible(this))
				updateProcessToolbar();
		} else if (event.getName().equals(LayoutUtils.ON_REDRAW_EVENT)) {
			ExecutionCtrl ctrl = (ExecutionCtrl) Executions.getCurrent();
			Event evt = ctrl.getNextEvent();
			if (evt != null) {
				Events.sendEvent(evt);
				Events.postEvent(new Event(LayoutUtils.ON_REDRAW_EVENT, this));
				return;
			}
			LayoutUtils.redraw(this);
        } else if (event.getName().equals(Events.ON_CTRL_KEY)) {
        	KeyEvent keyEvent = (KeyEvent) event;
		if (LayoutUtils.isReallyVisible(this))
	        	this.onCtrlKeyEvent(keyEvent);
		}
	}
	
	/**
	 * Create popup content for message popup window
	 * @param status
	 */
	protected void createPopupContent(String status) {
		Text t = new Text(status);
		msgPopupCnt.getChildren().clear();
		msgPopupCnt.appendChild(t);
	}
	
	/**
	 * Show notification popup using Clients.showNotification
	 * @param error
	 * @param msg
	 */
	private void showPopup(boolean error, String msg) {
		Clients.showNotification(buildNotificationText(msg), "error", null, "at_pointer", 3500, true);
	}
	
	/**
	 * Create message popup window
	 */
	private void createPopup() {
		msgPopupCnt = new Div();
		ZKUpdateUtil.setVflex(msgPopupCnt, "1");
		
		msgPopup = new Window();
		msgPopup.setVisible(false);
		msgPopup.setBorder(true);
		msgPopup.setClosable(true);
		msgPopup.setSizable(true);
		msgPopup.setContentStyle("overflow: auto");
		ZKUpdateUtil.setWidth(msgPopup, "500px");
        msgPopup.appendChild(msgPopupCnt);
        msgPopup.setPage(SessionManager.getAppDesktop().getComponent().getPage());
        msgPopup.setShadow(true);
	}
	
	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);
		if (msgPopup != null)
			msgPopup.detach();
		try {
			SessionManager.getSessionApplication().getKeylistener().removeEventListener(Events.ON_CTRL_KEY, this);
		} catch (Exception e){}
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		if (newpage != null) {
			SessionManager.getSessionApplication().getKeylistener().addEventListener(Events.ON_CTRL_KEY, this);
		}
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#setVflex(java.lang.String)
	 */
	@Override
	public void setVflex(String flex) {
		if (getHeight() != null)
			setHeight(null);
		super.setVflex(flex);
		ZKUpdateUtil.setVflex(tabbox, flex);
	}

	/**
	 * Update toolbar button state 
	 * @param changed
	 * @param readOnly
	 */
	public void updateToolbar(boolean changed, boolean readOnly) {
		int index = getSelectedIndex();
		if (index < 0 || index >= getTabcount()) return;
		
		Tabpanel tabpanel = (Tabpanel) tabbox.getTabpanel(index);
		Toolbar toolbar = tabpanel.getToolbar();

		JPiereIADTabpanel adtab = getADTabpanel(index);
		if (adtab == null)
			return;
		if (adtab.getGridTab().isSortTab() || adtab.getGridTab().isReadOnly())
			readOnly = true;
		
		boolean insertRecord = !readOnly;
    	boolean deleteRecord = !readOnly;

		if (insertRecord)
        {
            insertRecord = adtab.getGridTab().isInsertRecord();
        }
        boolean enableNew = insertRecord && !adtab.getGridTab().isSortTab();
		if (deleteRecord)
        {
			deleteRecord = adtab.getGridTab().isDeleteRecord();
        }
        boolean enableDelete = !changed && deleteRecord && !adtab.getGridTab().isSortTab() && !adtab.getGridTab().isProcessed();
        boolean enableCustomize = !adtab.getGridTab().isSortTab() && adtab.isGridView();

        JPiereADWindow adwindow = JPiereADWindow.findADWindow(this);
        if (adwindow == null)
        	return;
        List<String> tabRestrictList = adwindow.getTabToolbarRestrictList(adtab.getGridTab().getAD_Tab_ID());
        List<String> windowRestrictList = adwindow.getWindowToolbarRestrictList();
        
        for(Component c : toolbar.getChildren()) {
        	if (c instanceof ToolBarButton) {
        		ToolBarButton btn = (ToolBarButton) c;
        		if (BTN_NEW_ID.equals(btn.getId())) {
        			btn.setDisabled(!enableNew);
        		} else if (BTN_DELETE_ID.equals(btn.getId())) {
        			btn.setDisabled(!enableDelete);
        		} else if (BTN_EDIT_ID.equals(btn.getId())) {
        			btn.setDisabled(false);
        		} else if (BTN_SAVE_ID.equals(btn.getId())) {
        			btn.setDisabled(!adtab.needSave(true, false));
				} else if (BTN_CUSTOMIZE_ID.equals(btn.getId())) {
        			btn.setDisabled(!enableCustomize);
				} else if (BTN_QUICK_FORM_ID.equals(btn.getId())) {
					btn.setDisabled(!(adtab.isEnableQuickFormButton() && !adtab.getGridTab().isReadOnly()));
				} else if (BTN_TOGGLE_ID.equals(btn.getId())) {
					btn.setDisabled(adtab.getGridTab().isSortTab());
				}
        		if (windowRestrictList.contains(btn.getId())) {
        			btn.setVisible(false);
        		} else if (tabRestrictList.contains(btn.getId())) {
        			btn.setVisible(false);
        		} else if (tabpanel.toolbarCustomButtons.containsKey(btn)) {
        			ToolbarCustomButton customButton = tabpanel.toolbarCustomButtons.get(btn);
        			customButton.dynamicDisplay();
        		}else {
        			btn.setVisible(true);
        		}
        	}
        }

		//Not use by ADTabpanel, for custom IADTabpanel implementation.
		adtab.updateDetailToolbar(toolbar);
	}
	
	/**
	 * Update state of Process toolbar button.
	 */
	private void updateProcessToolbar() {
		int index = getSelectedIndex();
		if (index < 0 || index >= getTabcount()) return;
		
		Tabpanel tabpanel = (Tabpanel) tabbox.getTabpanel(index);
		Toolbar toolbar = tabpanel.getToolbar();

		JPiereIADTabpanel adtab = getADTabpanel(index);
		if (adtab == null) return;
		
        for(Component c : toolbar.getChildren()) {
        	if (c instanceof ToolBarButton) {
        		ToolBarButton btn = (ToolBarButton) c;
        		if (BTN_PROCESS_ID.equals(btn.getId())) {
        			if (adtab.getGridTab().isSortTab()) {
        				btn.setDisabled(true);
        			} else {
        				boolean isToolbarDisabled =((JPiereADTabpanel)adtab).getToolbarButtons() == null || (((JPiereADTabpanel)adtab).getToolbarButtons().isEmpty());
        				btn.setDisabled(isToolbarDisabled);
        			}
        			break;
        		}
        	}
        }
	}

	/**
	 * Edit current record of selected tab.<br/>
	 * This event will make the selected tab becomes the new header tab, i.e become the selected tab of {@link CompositeADTabbox}.
	 * @param formView true to force form view.
	 * @throws Exception
	 */
	public void onEdit(boolean formView) throws Exception {
		Event openEvent = new Event(ON_EDIT_EVENT, JPiereDetailPane.this, Boolean.valueOf(formView));
		eventListener.onEvent(openEvent);
	}

	/**
	 * Fire ON_ACTIVATE_DETAIL_EVENT for selected tab.
	 */
	public void fireActivateDetailEvent() {
		int index = tabbox.getSelectedIndex();
		JPiereIADTabpanel tabPanel = (JPiereIADTabpanel) tabbox.getTabpanel(index).getChildren().get(1);
		Event activateEvent = new Event(ON_ACTIVATE_DETAIL_EVENT, tabPanel, prevSelectedIndex);
		Events.sendEvent(activateEvent);
	}

	/**
	 * Set visibility of tab at tabIndex
	 * @param tabIndex
	 * @param visible
	 */
	public void setTabVisibility(int tabIndex, boolean visible) {
		if (tabIndex < 0 || tabbox.getTabs() == null || tabIndex >= tabbox.getTabs().getChildren().size())
			return;
		
		Tab tab = (Tab) tabbox.getTabs().getChildren().get(tabIndex);
		tab.setVisible(visible);
		if (tab.isSelected()) {
			tab.setSelected(false);
		}
		if (tab.getLinkedPanel() != null) {
			tab.getLinkedPanel().setVisible(visible);
		}
	}
	
	/**
	 * Is tab at tabIndex visible
	 * @param tabIndex
	 * @return true if tab at tabIndex is visible
	 */
	public boolean isTabVisible(int tabIndex) {
		if (tabIndex < 0 || tabbox.getTabs() == null || tabIndex >= tabbox.getTabs().getChildren().size())
			return false;
		
		return tabbox.getTabs().getChildren().get(tabIndex).isVisible();
	}
	
	/**
	 * Is tab at tabIndex enable
	 * @param tabIndex
	 * @return true if tab at tabIndex is enable
	 */
	public boolean isTabEnabled(int tabIndex) {
		if (tabIndex < 0 || tabbox.getTabs() == null || tabIndex >= tabbox.getTabs().getChildren().size())
			return false;
		
		Tab tab = (Tab) tabbox.getTabs().getChildren().get(tabIndex);
		return !tab.isDisabled();
	}
	
	/**
	 * Enable/disable tab at tabIndex
	 * @param tabIndex
	 * @param enabled
	 */
	public void setTabEnabled(int tabIndex, boolean enabled) {
		if (tabIndex < 0 || tabbox.getTabs() == null || tabIndex >= tabbox.getTabs().getChildren().size())
			return;
		
		Tab tab = (Tab) tabbox.getTabs().getChildren().get(tabIndex);
		tab.setDisabled(!enabled);
	}
	
	/**
	 * Disable all toolbar buttons
	 */
	public void disableToolbar() {
		int index = getSelectedIndex();
		if (index < 0 || index >= getTabcount()) return;
		
		Tabpanel tabpanel = (Tabpanel) tabbox.getTabpanel(index);
		Toolbar toolbar = tabpanel.getToolbar();
		for(Component c : toolbar.getChildren()) {
        	if (c instanceof ToolBarButton) {
        		ToolBarButton btn = (ToolBarButton) c;
        		btn.setDisabled(true);
        	}
		}
	}
	
	/**
	 * Find first {@link Tabpanel} that own comp.
	 * @param comp
	 * @return {@link Component}
	 */
	private Component findTabpanel(Component comp) {
		Component parent = comp.getParent();
		while (parent != null) {
			if (parent instanceof org.adempiere.webui.component.Tabpanel)
				return parent;
			
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * Add new row
	 * @throws Exception
	 */
	public void onNew() throws Exception {
		Event openEvent = new Event(ON_NEW_EVENT, JPiereDetailPane.this);
		eventListener.onEvent(openEvent);
	}

    /**
     * Handle shortcut key event
     * @param keyEvent
     */
	private void onCtrlKeyEvent(KeyEvent keyEvent) {
		ToolBarButton btn = null;
		if (keyEvent.isAltKey() && !keyEvent.isCtrlKey() && keyEvent.isShiftKey()) { // Shift+Alt key
			if (keyEvent.getKeyCode() == JPiereADWindowToolbar.VK_N) { // Shift+Alt+N
				btn = getSelectedPanel().getToolbarButton(BTN_NEW_ID);
			} else if (keyEvent.getKeyCode() == JPiereADWindowToolbar.VK_T) {
				btn = getSelectedPanel().getToolbarButton(BTN_TOGGLE_ID);
			} else if (keyEvent.getKeyCode() == KeyEvent.HOME) {
				btn = getSelectedPanel().getRecordToolbar().btnFirst;
			} else if (keyEvent.getKeyCode() == KeyEvent.END) {
				btn = getSelectedPanel().getRecordToolbar().btnLast;
			} else if (keyEvent.getKeyCode() == KeyEvent.LEFT) {
				btn = getSelectedPanel().getRecordToolbar().btnPrevious;
			} else if (keyEvent.getKeyCode() == KeyEvent.RIGHT) {
				btn = getSelectedPanel().getRecordToolbar().btnNext;
			} else if (keyEvent.getKeyCode() == KeyEvent.HOME) {
				btn = getSelectedPanel().getRecordToolbar().btnFirst;
			} else if (keyEvent.getKeyCode() == JPiereADWindowToolbar.VK_E) {
				btn = getSelectedPanel().getToolbarButton(BTN_EDIT_ID);
			} else if (keyEvent.getKeyCode() == JPiereADWindowToolbar.VK_S) {
				btn = getSelectedPanel().getToolbarButton(BTN_SAVE_ID);
			} else if (keyEvent.getKeyCode() == JPiereADWindowToolbar.VK_D) {
				btn = getSelectedPanel().getToolbarButton(BTN_DELETE_ID);
			} else if (keyEvent.getKeyCode() == JPiereADWindowToolbar.VK_O) {
				btn = getSelectedPanel().getToolbarButton(BTN_PROCESS_ID);
			} else if (keyEvent.getKeyCode() == JPiereADWindowToolbar.VK_F) {
				btn = getSelectedPanel().getToolbarButton(BTN_QUICK_FORM_ID);
			}
		} 
		if (btn != null) {
			keyEvent.stopPropagation();
			if (!btn.isDisabled() && btn.isVisible()) {
				Events.sendEvent(btn, new Event(Events.ON_CLICK, btn));
				//client side script to close combobox popup
				String script = "(function(){let w=zk.Widget.$('#" + btn.getUuid()+"'); " +
						"zWatch.fire('onFloatUp', w);})()";
				Clients.response(new AuScript(script));
			}
		}
	}

	/**
	 * Custom {@link org.adempiere.webui.component.Tabpanel} implementation for DetailPane.
	 */
	public static class Tabpanel extends org.adempiere.webui.component.Tabpanel {
		/**
		 * generated serial id 
		 */
		private static final long serialVersionUID = -2502140440194514450L;

		private ToolBar toolbar;

		private RecordToolbar recordToolBar;

		private Div pagingControl;

		private boolean toggleToFormView = false;

		private IADTabpanel adTabPanel;
		
		private HashMap<ToolBarButton, ToolbarCustomButton> toolbarCustomButtons = new HashMap<ToolBarButton, ToolbarCustomButton>();

		private A overflowButton;

		private Popup overflowPopup;

		public Tabpanel() {
			setSclass("adwindow-detailpane-tabpanel");
			toolbar = new ToolBar();
			appendChild(toolbar);			
		}

		/**
		 * Update toolbar state after toggle between grid and form view
		 */
		public void afterToggle() {
			if (getPagingControl() != null)
				getPagingControl().setVisible(!toggleToFormView);
			if (getRecordToolbar() != null) {
				getRecordToolbar().setVisible(toggleToFormView);
				if (getRecordToolbar().isVisible())
					getRecordToolbar().dynamicDisplay();
			}
			boolean enableCustomize = !adTabPanel.getGridTab().isSortTab() && adTabPanel.isGridView();

			Optional<ToolBarButton> optional = getToolbarButtons().stream().filter(e -> BTN_CUSTOMIZE_ID.equals(e.getId())).findFirst();
			if (optional.isPresent())
				optional.get().setDisabled(!enableCustomize);
		}

		/**
		 * Set form view state of selected tab
		 * @param b
		 */
		public void setToggleToFormView(boolean b) {
			toggleToFormView  = b;
		}

		/**
		 * Is selected tab in form view
		 * @return true if tab have been toggle to form view
		 */
		public boolean isToggleToFormView() {
			return toggleToFormView;
		}
		
		/**
		 * 
		 * @param tabPanel
		 */
		public void setADTabpanel(IADTabpanel tabPanel) {
			appendChild(tabPanel);
			this.adTabPanel = tabPanel;
			if (tabPanel instanceof ADTabpanel) {
				tabPanel.addEventListener(ADTabpanel.ON_SWITCH_VIEW_EVENT, e -> {
					if (recordToolBar != null && tabPanel.isGridView()) {
						recordToolBar.setVisible(false);
					}
				});
			}
		}

		/**
		 * Get toolbar of the tabpanel
		 * @return {@link ToolBar}
		 */
		public ToolBar getToolbar() {
			return toolbar;
		}

		/**
		 * set record navigation toolbar
		 * @param rtb
		 */
		public void setRecordToolbar(RecordToolbar rtb) {
			recordToolBar = rtb;
			Component parent = overflowPopup != null ? overflowPopup : toolbar;
			parent.appendChild(rtb);
			rtb.setVisible(false);
			if (overflowPopup == null)
				rtb.setSclass("adwindow-detailpane-adtab-grid-south");
		}
		
		/**
		 * 
		 * @return {@link RecordToolbar}
		 */
		public RecordToolbar getRecordToolbar() {
			return recordToolBar;
		}
		
		/**
		 * Set paging control
		 * @param pagingControl
		 */
		public void setPagingControl(Div pagingControl) {
			Component parent = overflowPopup != null ? overflowPopup : toolbar;
			if ( pagingControl.getParent() != parent) { 
				parent.appendChild(pagingControl);
				ZKUpdateUtil.setHflex(pagingControl, "0");
				if (overflowPopup == null)
					pagingControl.setSclass("adwindow-detailpane-adtab-grid-south");												
			}
			this.pagingControl = pagingControl;
		}
		
		/**
		 * 
		 * @return paging control
		 */
		public Div getPagingControl() {
			return pagingControl;
		}
		
		/**
		 * Get toolbar button by id
		 * @param id
		 * @return {@link ToolBarButton}
		 */
		public ToolBarButton getToolbarButton(String id) {
			Optional<ToolBarButton> optional = getToolbarButtons().stream().filter(e -> e.getId().equals(id)).findFirst();
			return optional.isPresent() ? optional.get() : null;
		}

		/**
		 * 
		 * @return toolbar buttons from the detail toolbar
		 */
		private List<ToolBarButton> getToolbarButtons() {

			List<ToolBarButton> list = new ArrayList<>();

			for (Component c : toolbar.getChildren()) {
				if (c instanceof ToolBarButton)
					list.add((ToolBarButton) c);
			}

			return list;
		}

		/**
		 * Create overflow button (show more) for mobile client.
		 */
		private void createOverflowButton() {
			overflowButton = new A();
			overflowButton.setTooltiptext(Msg.getMsg(Env.getCtx(), "ShowMore"));
			overflowButton.setIconSclass("z-icon-ShowMore");
			overflowButton.setSclass("font-icon-toolbar-button toolbar-button mobile-overflow-link");
			toolbar.appendChild(overflowButton);
			newOverflowPopup();
			toolbar.appendChild(overflowPopup);
			overflowButton.addEventListener(Events.ON_CLICK, e -> {
				Long ts = (Long) overflowPopup.removeAttribute("popup.close");
				if (ts != null) {
					if (System.currentTimeMillis() - ts.longValue() < 500) {
						return;
					}
				}
				overflowPopup.open(overflowButton, "after_end");
			});
		}

		/**
		 * Create new overflow popup
		 */
		private void newOverflowPopup() {
			overflowPopup = new Popup();
			overflowPopup.setHflex("min");
			overflowPopup.setVflex("min");
		}
	}
	
	/**
	 * Record navigation toolbar
	 * @author hengsin
	 *
	 */
	private static class RecordToolbar extends Hlayout {
		/**
		 * generated serial id
		 */
		private static final long serialVersionUID = -3369063577339438823L;

		private ToolBarButton btnFirst;
		private ToolBarButton btnPrevious;
		private ToolBarButton btnRecordInfo;
		private ToolBarButton btnNext;
		private ToolBarButton btnLast;
		private GridTab gridTab;

		/**
		 * @param gridTab
		 */
		private RecordToolbar(GridTab gridTab) {
			this.gridTab = gridTab;
			btnFirst = createButton("First", "First", "First");
			btnFirst.setTooltiptext(btnFirst.getTooltiptext()+"    Shift+Alt+Home");
			appendChild(btnFirst);
			btnFirst.addEventListener(Events.ON_CLICK, e -> {
				Event ne = new Event(JPiereDetailPane.ON_RECORD_NAVIGATE_EVENT, this, "first");
				Events.sendEvent(this, ne);
			});
	        btnPrevious = createButton("Previous", "Previous", "Previous");
	        btnPrevious.setTooltiptext(btnPrevious.getTooltiptext()+"    Shift+Alt+Left");
	        appendChild(btnPrevious);
	        btnPrevious.addEventListener(Events.ON_CLICK, e -> {
				Event ne = new Event(JPiereDetailPane.ON_RECORD_NAVIGATE_EVENT, this, "previous");
				Events.sendEvent(this, ne);
			});
	        btnRecordInfo = new ToolBarButton();
	        btnRecordInfo.setLabel("");
	        btnRecordInfo.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Who")));
	        btnRecordInfo.addEventListener(Events.ON_CLICK,  e -> {
	        	if (gridTab.isNew() || gridTab.getRowCount() == 0)
	        		return;
	        	DataStatusEvent dse = new DataStatusEvent(gridTab, gridTab.getRowCount(), gridTab.needSave(true, true), true, false);
	        	dse.AD_Table_ID = gridTab.getAD_Table_ID();
	        	gridTab.updateDataStatusEventProperties(dse);
	        	dse.setCurrentRow(gridTab.getCurrentRow());
	        	String title = Msg.getMsg(Env.getCtx(), "Who") + btnRecordInfo.getLabel();
	        	new WRecordInfo(title, dse, gridTab);
	        });
	        btnRecordInfo.setSclass("breadcrumb-record-info link");
	        btnRecordInfo.setId("recordInfo");
	        btnRecordInfo.setStyle("float: none; display: inline-flex; width: auto;");
	        appendChild(btnRecordInfo);
	        btnNext = createButton("Next", "Next", "Next");
	        btnNext.setTooltiptext(btnNext.getTooltiptext()+"    Shift+Alt+Right");
	        btnNext.addEventListener(Events.ON_CLICK, e -> {
				Event ne = new Event(JPiereDetailPane.ON_RECORD_NAVIGATE_EVENT, this, "next");
				Events.sendEvent(this, ne);
			});
	        appendChild(btnNext);
	        btnLast = createButton("Last", "Last", "Last");
	        btnLast.setTooltiptext(btnLast.getTooltiptext()+"    Shift+Alt+End");
	        btnLast.addEventListener(Events.ON_CLICK, e -> {
				Event ne = new Event(JPiereDetailPane.ON_RECORD_NAVIGATE_EVENT, this, "last");
				Events.sendEvent(this, ne);
			});
	        appendChild(btnLast);	        
	        this.setValign("middle");
		}
		
		/**
		 * Create toolbar button
		 * @param name
		 * @param image
		 * @param tooltip
		 * @return {@link ToolBarButton}
		 */
		private ToolBarButton createButton(String name, String image, String tooltip)
	    {
	    	ToolBarButton btn = new ToolBarButton("");
	        btn.setName("Btn"+name);
	        btn.setId(name);
	    	String suffix = "16.png";
	    	if (ThemeManager.isUseFontIconForImage())
	    		btn.setIconSclass("z-icon-"+image+"Record");
	    	else
	    		btn.setImage(ThemeManager.getThemeResource("images/"+image + suffix));
	        btn.setTooltiptext(Msg.getMsg(Env.getCtx(),tooltip));
	        btn.setSclass("breadcrumb-toolbar-button");
	        
	        this.appendChild(btn);
	        //make toolbar button last to receive focus
	        btn.setTabindex(0);
	        btn.setDisabled(true);
	        btn.setStyle("float: none");

	        return btn;
	    }
		
		/**
		 * Dynamic update state of toolbar buttons
		 */
		private void dynamicDisplay() {
			int rowCount = gridTab.getRowCount();
			int currentRow = gridTab.getCurrentRow()+1;
			btnRecordInfo.setLabel(currentRow+"/"+rowCount);
			btnFirst.setDisabled(currentRow<=1);
			btnPrevious.setDisabled(currentRow<=1);
			btnNext.setDisabled(currentRow==rowCount);
			btnLast.setDisabled(currentRow==rowCount);
			this.invalidate();
		}
	}

}
