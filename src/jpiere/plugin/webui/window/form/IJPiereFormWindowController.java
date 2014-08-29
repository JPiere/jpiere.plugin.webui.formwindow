package jpiere.plugin.webui.window.form;


public interface IJPiereFormWindowController
{
	/**
	 * Called by org.adempiere.webui.panel.ADForm.openForm(int)
	 * @return
	 */
	public void createFormWindow(int AD_Window_ID);
	

}
