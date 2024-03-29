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
package jpiere.plugin.webui.window.form;

import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.compiere.model.MQuery;
import org.compiere.util.Env;

import jpiere.plugin.webui.adwindow.JPiereADWindow;


/**
 *  JPiere Form Window
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereFormWindow extends AbstractJPiereFormWindow {

	private CustomForm form;

    public JPiereFormWindow()
    {
    	form = new CustomForm();
    	form.setHeight("100%");
    	form.setWidth("100%");
    }

    @Override
    public void createFormWindow(int AD_Window_ID){

    	JPiereADWindow adw = new JPiereADWindow(Env.getCtx(), AD_Window_ID, null);
    	adw.createPart(form);
    }


	@Override
	public void createFormWindow(int AD_Window_ID, MQuery query) {

    	JPiereADWindow adw = new JPiereADWindow(Env.getCtx(), AD_Window_ID, query);
    	adw.createPart(form);
	}

	@Override
	public ADForm getForm()
	{
		return form;
	}




}
