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

import org.adempiere.webui.adwindow.IADTabpanel;
import org.compiere.util.Evaluatee;
import org.zkoss.zk.ui.Component;

/**
 * Interface for UI component that edit/display record using ad_tab definitions
 * @author Low Heng Sin
 *
 * @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
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
