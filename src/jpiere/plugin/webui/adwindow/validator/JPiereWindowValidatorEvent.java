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

package jpiere.plugin.webui.adwindow.validator;

import jpiere.plugin.webui.adwindow.JPiereADWindow;


/**
 *  JPiere Window Validator Event
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
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
