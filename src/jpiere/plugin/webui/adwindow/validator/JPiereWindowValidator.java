package jpiere.plugin.webui.adwindow.validator;

import org.adempiere.util.Callback;

public interface JPiereWindowValidator {
	public void onWindowEvent(JPiereWindowValidatorEvent event, Callback<Boolean> callback);
}
