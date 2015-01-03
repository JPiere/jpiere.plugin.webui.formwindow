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

import java.util.logging.Level;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.compiere.util.CLogger;

/**
 *  JPiere Webui Form Factory
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
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
		if(formName.startsWith("jpiere.plugin.webui.formwindow.")){

			int AD_Window_ID = new Integer(formName.substring("jpiere.plugin.webui.formwindow.".length())).intValue();

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
