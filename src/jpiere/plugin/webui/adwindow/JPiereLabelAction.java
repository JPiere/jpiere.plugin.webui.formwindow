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
 * - Nicolas Micoud (TGI)                                              *
 * - Alan Lescano                                                      *
 * - Norbert Bede                                                      *
 **********************************************************************/
package jpiere.plugin.webui.adwindow;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.component.ZkCssHelper;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.zkoss.zul.Popup;
import org.zkoss.zul.impl.LabelImageElement;

import jpiere.plugin.webui.apps.JPiereLabelsSearch;
import jpiere.plugin.webui.apps.JPiereLabelsSearchController;
import jpiere.plugin.webui.panel.JPiereLabelsPanel;

public class JPiereLabelAction {
	private JPiereAbstractADWindowContent panel;	
	private Window window = null;	
	private JPiereLabelsPanel labelsPanel;
	
	/**
	 * Standard constructor
	 * @param panel
	 */
	public JPiereLabelAction(JPiereAbstractADWindowContent panel) {
		this.panel = panel;	
		int AD_Table_ID = panel.getActiveGridTab().getAD_Table_ID();
		int Record_ID = panel.getActiveGridTab().getRecord_ID();
		String Record_UU = panel.getActiveGridTab().getRecord_UU();
		labelsPanel = new JPiereLabelsPanel(panel, AD_Table_ID, Record_ID, Record_UU);
	}	

	/**
	 * Open the popup window
	 */
	public void show() {
		if(window == null) {
			window = new Window();
			ZKUpdateUtil.setWindowWidthX(window, 200);
			window.setClosable(true);
			window.setBorder("normal");
			window.setStyle("position:absolute");
			window.addCallback(Window.AFTER_PAGE_DETACHED, t -> panel.focusToLastFocusEditor());			
			JPiereLabelsSearchController controller = new JPiereLabelsSearchController(labelsPanel);
			JPiereLabelsSearch globalSearch = new JPiereLabelsSearch(controller);
			ZkCssHelper.appendStyle(globalSearch, "display: flex; flex-direction: column; margin: 8px; margin-top: 5px;");
			window.appendChild(globalSearch);			
			window.appendChild(labelsPanel);
		}

		LabelImageElement toolbarItem = panel.getToolbar().getToolbarItem("Label");
		Popup popup = LayoutUtils.findPopup(toolbarItem);
		
		if (popup != null)
			popup.appendChild(window);
		
		LayoutUtils.openPopupWindow(toolbarItem, window, "after_start");
		window.setFocus(true);
		panel.getToolbar().setPressed("Label", panel.getADTab().getSelectedGridTab().hasLabel());
	}
}