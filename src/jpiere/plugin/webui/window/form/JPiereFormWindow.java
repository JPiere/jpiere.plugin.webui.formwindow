package jpiere.plugin.webui.window.form;

import jpiere.plugin.webui.adwindow.JPiereADWindow;

import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.compiere.util.Env;

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
