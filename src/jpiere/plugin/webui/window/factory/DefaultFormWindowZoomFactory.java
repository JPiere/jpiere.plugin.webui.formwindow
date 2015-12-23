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
package jpiere.plugin.webui.window.factory;

import java.util.logging.Level;

import jpiere.plugin.webui.window.form.AbstractJPiereFormWindow;

import org.adempiere.webui.factory.IFormWindowZoomFactory;
import org.adempiere.webui.panel.ADForm;
import org.compiere.model.MQuery;
import org.compiere.util.CLogger;

/**
 *  JPiere Webui Form Factory
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public class DefaultFormWindowZoomFactory implements IFormWindowZoomFactory {

	private static final CLogger log = CLogger.getCLogger(DefaultFormWindowZoomFactory.class);

	/**
	 * default constructor
	 */
	public DefaultFormWindowZoomFactory() {
	}

	/* (non-Javadoc)
	 * @see org.adempiere.webui.factory.IFormFactory#newFormInstance(java.lang.String)
	 */
	@Override
	public ADForm newFormInstance(int AD_Window_ID, MQuery query) {

		Object form = null;

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class<?> clazz = null;
		if (loader != null) {
    		try
    		{
        		clazz = loader.loadClass("jpiere.plugin.webui.window.form.JPiereFormWindow");
    		}
    		catch (Exception e)
    		{
    			if (log.isLoggable(Level.INFO))
    				log.log(Level.INFO, e.getLocalizedMessage(), e);
    		}
		}
		if (clazz == null) {
			loader = this.getClass().getClassLoader();
			try
    		{
    			//	Create instance w/o parameters
        		clazz = loader.loadClass("jpiere.plugin.webui.window.form.JPiereFormWindow");
    		}
    		catch (Exception e)
    		{
    			if (log.isLoggable(Level.INFO))
    				log.log(Level.INFO, e.getLocalizedMessage(), e);
    		}
		}

		if (clazz != null) {
			try
    		{
    			form = clazz.newInstance();
    		}
    		catch (Exception e)
    		{
    			if (log.isLoggable(Level.WARNING))
    				log.log(Level.WARNING, e.getLocalizedMessage(), e);
    		}
		}

		if (form != null) {
			if (form instanceof AbstractJPiereFormWindow ) {
				AbstractJPiereFormWindow  controller = (AbstractJPiereFormWindow) form;
				controller.createFormWindow(AD_Window_ID,query);
				ADForm adForm = controller.getForm();
				adForm.setICustomForm(controller);
				return adForm;
			}
		}


		return null;
	}


}
