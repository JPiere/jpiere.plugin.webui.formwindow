package jpiere.plugin.webui.window.form;

import jpiere.plugin.webui.adwindow.JPiereADWindow;

import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.compiere.util.Env;

public class HasADForm implements IFormController{

	private CustomForm form;

    public HasADForm()
    {
    	form = new CustomForm();

    	JPiereADWindow adw = new JPiereADWindow(Env.getCtx(),1000014,null);
    	adw.createPart(form);

    }


	@Override
	public ADForm getForm()
	{
		return form;
	}


}
