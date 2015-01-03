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

import org.adempiere.webui.adwindow.IADTabbox;
import org.adempiere.webui.part.UIPart;
import org.compiere.model.GridTab;

/**
 *
 * @author <a href="mailto:hengsin@gmail.com">Low Heng Sin</a>
 *
 * @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
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
