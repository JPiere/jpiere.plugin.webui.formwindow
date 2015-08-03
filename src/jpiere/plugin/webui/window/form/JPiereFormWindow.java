/******************************************************************************
 * Product: JPiere(Japan + iDempiere)                                         *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere supported by OSS ERP Solutions Co., Ltd.                            *
 * (http://www.oss-erp.co.jp)                                                 *
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
    	form.setHeight("100%");
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
