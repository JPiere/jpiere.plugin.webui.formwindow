/******************************************************************************
 * Copyright (C) 2008 Low Heng Sin                                            *
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

import org.adempiere.webui.adwindow.IADTabbox;
import org.adempiere.webui.part.UIPart;
import org.compiere.model.GridTab;

/**
 *
 * @author <a href="mailto:hengsin@gmail.com">Low Heng Sin</a>
 *
 * @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public interface JPiereIADTabbox extends IADTabbox,UIPart {

	/**
	 * @return selected tab panel reference
	 */
	public JPiereIADTabpanel getSelectedTabpanel();

	/**
	 *
	 * @param tab
	 * @param tabPanel
	 */
	public void addTab(GridTab tab, JPiereIADTabpanel tabPanel);

	/**
	 * @param index
	 * @return IADTabpanel
	 */
	public JPiereIADTabpanel getADTabpanel(int index);

	/**
	 * @param gTab
	 * @return IADTabpanel or null if not found
	 */
	public JPiereIADTabpanel findADTabpanel(GridTab gTab);

	/**
	 *
	 * @param abstractADWindowPanel
	 */
	public void setADWindowPanel(JPiereAbstractADWindowContent abstractADWindowPanel);

	/**
	 * @return the currently selected detail adtabpanel
	 */
	public JPiereIADTabpanel getSelectedDetailADTabpanel();

	/**
	 * @return dirty adtabpanel that need save ( if any )
	 */
	public JPiereIADTabpanel getDirtyADTabpanel();

}
