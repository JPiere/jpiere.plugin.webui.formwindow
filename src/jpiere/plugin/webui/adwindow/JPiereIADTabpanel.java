/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Plugins Form Window               *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプラグラムの著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持しており、 *
 * このプログラムを使用する場合には著作権の使用料をお支払頂く必要があります。 *
 * 著作権の使用料の支払い義務は、このプログラムから派生して作成された         *
 * プログラムにも発生します。 サポートサービスは                              *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/
package jpiere.plugin.webui.adwindow;

import org.compiere.model.GridTab;
import org.adempiere.webui.adwindow.IADTabpanel;
import org.compiere.util.Evaluatee;
import org.zkoss.zk.ui.Component;

/**
 * Interface for UI component that edit/display record using ad_tab definitions
 * @author Low Heng Sin
 *
 * @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
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
