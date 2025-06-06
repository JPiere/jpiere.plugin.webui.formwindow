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
 *                                                                            *
 * Contributors:                                                              *
 * - Heng Sin Low                                                             *
 *                                                                            *
 * Sponsors:                                                                  *
 * - Idalica Corporation                                                      *
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

package jpiere.plugin.webui.adwindow;

import java.util.Properties;

import org.adempiere.util.Callback;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.panel.IHelpContext;
import org.adempiere.webui.panel.ITabOnCloseHandler;
import org.adempiere.webui.part.WindowContainer;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.DataStatusEvent;
import org.compiere.model.X_AD_CtxHelp;
import org.compiere.util.CLogger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Vlayout;

/**
 * Controller for {@link ADWindow} content.
 * 
 * @author <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @author <a href="mailto:hengsin@gmail.com">Low Heng Sin</a>
 * @date Feb 25, 2007
 */
public class JPiereADWindowContent extends JPiereAbstractADWindowContent
{
    @SuppressWarnings("unused")
	private static final CLogger logger = CLogger.getCLogger(JPiereADWindowContent.class);

    /** Main layout component **/
    private Vlayout layout;
    /** Center Div of {@link #layout}, host {@link CompositeADTabbox} **/
    private Div contentArea;

    /**
     * @param ctx
     * @param windowNo
     * @param adWindowId
     */
	public JPiereADWindowContent(Properties ctx, int windowNo, int adWindowId)
    {
        super(ctx, windowNo, adWindowId);
    }

	/**
	 * Layout UI.<br/>
	 * Vertical layout of toolbar, breadCrumb, statusBar and {@link #contentArea}.
	 */
	@Override
   	protected Component doCreatePart(Component parent)
    {
   		layout = new JPiereADWindowVlayout(this);
        if (parent != null) {
	        layout.setParent(parent);
	        layout.setSclass("adwindow-layout");
        } else {
        	layout.setPage(page);
        }
        layout.setSpacing("0px");

        //toolbar
        Div north = new Div();
        north.setParent(layout);        
        north.setSclass("adwindow-north");
        Div div = new Div();
        div.setStyle("height: 100%; width: 100%");
        north.appendChild(div);
        ZKUpdateUtil.setVflex(north, "0");
        toolbar.setParent(div);
        toolbar.setWindowNo(getWindowNo());
        breadCrumb = new JPiereBreadCrumb(this, getWindowNo());
        breadCrumb.setToolbarListener(this);
        breadCrumb.setId("breadCrumb");
        div.appendChild(breadCrumb);
        
        //status bar
        div.appendChild(statusBar);

        LayoutUtils.addSclass("adwindow-status", statusBar);

        //IADTabbox
        contentArea = new Div();
        contentArea.setParent(layout);
        ZKUpdateUtil.setVflex(contentArea, "1");
        ZKUpdateUtil.setHflex(contentArea, "1");
        contentArea.setStyle("overflow: auto;");
        adTabbox.createPart(contentArea);
        
        if (parent instanceof Tabpanel) {
        	TabOnCloseHanlder handler = new TabOnCloseHanlder();
        	((Tabpanel)parent).setOnCloseHandler(handler);
        }

        //JPIERE-0014:Form window can not set TabOnCloseHanlder this timing.
        //Because, Tabpanel do not create yet. see TabbledDesktop#openForm().
        //So, set TabOnCloseHanlder at dataStatusChanged.
//        if (parent instanceof Tabpanel) {
//        	TabOnCloseHanlder handler = new TabOnCloseHanlder();
//        	((Tabpanel)parent).setOnCloseHandler(handler);
//        }

        SessionManager.getSessionApplication().getKeylistener().addEventListener(Events.ON_CTRL_KEY, this);
        
        layout.addEventListener(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT, this);
        
        return layout;
    }

	/**
	 * Create {@link CompositeADTabbox}
	 */
	@Override
    protected JPiereIADTabbox createADTab()
    {
    	JPiereCompositeADTabbox composite = new JPiereCompositeADTabbox();
    	return composite;
    }

	/**
	 * Get main layout component
	 * @return {@link Vlayout}
	 */
	@Override
	public Vlayout getComponent() {
		return layout;
	}

	@Override
    public void onEvent(Event event) {
    	if (Events.ON_CTRL_KEY.equals(event.getName())) {
    		KeyEvent keyEvent = (KeyEvent) event;
    		//enter == 13
    		if (keyEvent.getKeyCode() == 13 && this.getComponent().getParent().isVisible()) {
    			JPiereIADTabpanel panel = adTabbox.getSelectedTabpanel();
    			if (panel != null) {
    				if (panel.onEnterKey()) {
    					keyEvent.stopPropagation();
    				}
    			}
    		}
    	}
    	else if (event.getName().equals(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT)) {
    		SessionManager.getAppDesktop().updateHelpContext(X_AD_CtxHelp.CTXTYPE_Tab, adTabbox.getSelectedGridTab().getAD_Tab_ID());
    	}
    	else {
    		super.onEvent(event);
    	}
    }

	/**
	 * ITabOnCloseHandler to call {@link ADWindowContent#onExit(Callback)} when user wants to close an AD Window
	 */
	class TabOnCloseHanlder implements ITabOnCloseHandler, Callback<Boolean> {
		private Tabpanel tabPanel;
		public void onClose(Tabpanel tabPanel) {
			this.tabPanel = tabPanel;
			JPiereADWindowContent.this.onExit(this);
		}
		@Override
		public void onCallback(Boolean result) {
			if (result){
				closeTab (tabPanel);			
			} 
			this.tabPanel = null;
		}
	}
	
	/**
	 * Close tab related to tabPanel
	 * @param tabPanel Tabpanel that represent AD_Window
	 */
	protected void closeTab (Tabpanel tabPanel) {
		Tab tab = tabPanel.getLinkedTab();
		tab.close();
		if (getWindowNo() > 0)
			SessionManager.getAppDesktop().unregisterWindow(getWindowNo());
	}
	
	/**
	 * Vlayout subclass to override onPageDetached. 
	 */
	public static class JPiereADWindowVlayout extends Vlayout implements IHelpContext {
		/**
		 * generated serial id
		 */
		private static final long serialVersionUID = 6104341168705201721L;
		private JPiereADWindowContent content;

		protected JPiereADWindowVlayout(JPiereADWindowContent content) {
			super();
			this.content = content;
		}

		/**
		 * clean up listeners
		 */
		@Override
		public void onPageDetached(Page page) {
			super.onPageDetached(page);
			try {
				SessionManager.getSessionApplication().getKeylistener().removeEventListener(Events.ON_CTRL_KEY, content);
			} catch (Exception e){}
			content.layout.removeEventListener(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT, content);
		}
	}

	@Override
	protected void switchEditStatus(boolean editStatus) {
		layout.setWidgetOverride("isEditting", "'" + String.valueOf(editStatus) + "'");
	}	

	//JPIERE-0014 - set Tab Close handler - Start
	private boolean isSetOnCloseHandler = false;

    private void setOnCloseHandler()
    {
    	if(isSetOnCloseHandler)
    		return ;

    	Component customForm = this.getADTab().getSelectedTabpanel().getParent().getParent().getParent().getParent();
    	if(customForm.getParent() == null)
    		return ;

    	Component tabboxComponent = customForm.getParent().getParent().getParent();
    	if(tabboxComponent instanceof Tabbox)
    	{
    		Tabbox tabbox = (Tabbox)tabboxComponent;
    		org.zkoss.zul.Tabpanel selectedPanel = tabbox.getSelectedPanel();
	        if (selectedPanel instanceof org.zkoss.zul.Tabpanel)
	        {
	        	TabOnCloseHanlder handler = new TabOnCloseHanlder();
	        	((org.adempiere.webui.component.Tabpanel)selectedPanel).setOnCloseHandler(handler);
	        	isSetOnCloseHandler = true;
	        }
    	}
    }

    @Override
    public void dataStatusChanged(DataStatusEvent e)
    {
    	super.dataStatusChanged(e);
    	setOnCloseHandler();
    }
  //JPiere-0014 - set Tab Close handler - finish
}
