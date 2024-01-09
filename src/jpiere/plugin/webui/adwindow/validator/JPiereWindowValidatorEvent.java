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

import org.adempiere.webui.adwindow.ADWindow;

import jpiere.plugin.webui.adwindow.JPiereADWindow;


/**
 *  JPiere Window Validator Event
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereWindowValidatorEvent {
	/** {@link JPiereADWindow} instance **/
	private JPiereADWindow window;
	/** Event name **/
	private String name;
	/** Event data **/
	private Object data;
	
	/**
	 * @param window
	 * @param name
	 */
	public JPiereWindowValidatorEvent(JPiereADWindow window, String name) {
		this(window, name, null);
	}
	
	/**
	 * @param window
	 * @param name
	 * @param data
	 */
	public JPiereWindowValidatorEvent(JPiereADWindow window, String name, Object data) {
		this.window = window;
		this.name = name;
		this.data = data;
	}
	
	/**
	 * @return {@link JPiereADWindow}
	 */
	public JPiereADWindow getWindow() {
		return this.window;
	}

	/**
	 * @return Event name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return Event data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Set event data
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}
}
