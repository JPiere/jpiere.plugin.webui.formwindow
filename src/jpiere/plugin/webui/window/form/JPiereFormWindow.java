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
package jpiere.plugin.webui.window.form;

import jpiere.plugin.webui.adwindow.JPiereADWindow;

import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.compiere.util.Env;


/**
 *  JPiere Form Window
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereFormWindow extends AbstractJPiereFormWindow {

	private CustomForm form;

    public JPiereFormWindow()
    {
    	form = new CustomForm();
    }

    @Override
    public void createFormWindow(int AD_Window_ID){

    	JPiereADWindow adw = new JPiereADWindow(Env.getCtx(), AD_Window_ID, null);
    	adw.createPart(form);
    }

	@Override
	public ADForm getForm()
	{
		return form;
	}


}
