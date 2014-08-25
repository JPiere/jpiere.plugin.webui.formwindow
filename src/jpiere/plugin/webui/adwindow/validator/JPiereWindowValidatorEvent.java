package jpiere.plugin.webui.adwindow.validator;

import jpiere.plugin.webui.adwindow.JPiereADWindow;

public class JPiereWindowValidatorEvent {
	private JPiereADWindow window;
	private String name;

	public JPiereWindowValidatorEvent(JPiereADWindow window, String name) {
		this.window = window;
		this.name = name;
	}

	public JPiereADWindow getWindow() {
		return this.window;
	}

	public String getName() {
		return this.name;
	}
}
