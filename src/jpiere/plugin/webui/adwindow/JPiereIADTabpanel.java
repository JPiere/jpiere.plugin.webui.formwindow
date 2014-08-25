/******************************************************************************
 * Copyright (C) 2008 Low Heng Sin  All Rights Reserved.                      *
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

import org.adempiere.webui.adwindow.IADTabpanel;
import org.compiere.util.Evaluatee;
import org.zkoss.zk.ui.Component;

/**
 * Interface for UI component that edit/display record using ad_tab definitions
 * @author Low Heng Sin
 *
 */
public interface JPiereIADTabpanel extends IADTabpanel, Component, Evaluatee {

	public static final String ON_ACTIVATE_EVENT = "onActivate";
	public static final String ATTR_ON_ACTIVATE_POSTED = "org.adempiere.webui.adwindow.IADTabpanel.onActivatePosted";


	/**
	 *
	 * @return gridview instance
	 */
	public abstract JPiereGridView getJPiereGridView();

	/**
	 *
	 * @param detailPane
	 */
	public void setJPiereDetailPane(JPiereDetailPane detailPane);

	/**
	 *
	 * @return detailpane
	 */
	public JPiereDetailPane getJPiereDetailPane();



}
