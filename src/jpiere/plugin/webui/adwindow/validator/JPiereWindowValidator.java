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

import org.adempiere.util.Callback;

/**
 *  JPiere Window Validator
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public interface JPiereWindowValidator {
	public void onWindowEvent(JPiereWindowValidatorEvent event, Callback<Boolean> callback);
}
