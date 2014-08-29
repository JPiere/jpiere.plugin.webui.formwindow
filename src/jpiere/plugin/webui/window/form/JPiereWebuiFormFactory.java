/******************************************************************************
 * Copyright (C) 2013 Heng Sin Low                                            *
 * Copyright (C) 2013 Trek Global                 							  *
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
package jpiere.plugin.webui.window.form;

import java.util.logging.Level;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.compiere.util.CLogger;

/**
 * @author hengsin
 *
 */
public class JPiereWebuiFormFactory implements IFormFactory {

	private static final CLogger log = CLogger.getCLogger(JPiereWebuiFormFactory.class);

	/**
	 * default constructor
	 */
	public JPiereWebuiFormFactory() {
	}

	/* (non-Javadoc)
	 * @see org.adempiere.webui.factory.IFormFactory#newFormInstance(java.lang.String)
	 */
	@Override
	public ADForm newFormInstance(String formName) {
		
		Object form = null;
		if(formName.startsWith("jpiere.plugin.webui.window.")){
					
			int AD_Window_ID = new Integer(formName.substring("jpiere.plugin.webui.window.".length())).intValue();
			
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
					controller.createFormWindow(AD_Window_ID);
					ADForm adForm = controller.getForm();
					adForm.setICustomForm(controller);
					return adForm;
				}
			}
		}

		return null;
	}


}
