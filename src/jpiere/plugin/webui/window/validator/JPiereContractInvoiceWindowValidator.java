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

package jpiere.plugin.webui.window.validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import org.adempiere.util.Callback;
import org.adempiere.webui.adwindow.validator.WindowValidatorEventType;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MInvoice;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.plugin.webui.adwindow.validator.JPiereWindowValidator;
import jpiere.plugin.webui.adwindow.validator.JPiereWindowValidatorEvent;


/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class JPiereContractInvoiceWindowValidator implements JPiereWindowValidator {
	
	@Override
	public void onWindowEvent(JPiereWindowValidatorEvent event, Callback<Boolean> callback)
	{
		if(event.getName().equals(WindowValidatorEventType.BEFORE_SAVE.getName()))
		{
			GridTab gridTab =event.getWindow().getJPiereADWindowContent().getActiveGridTab();
			GridField gf_ContractProcPeriod_ID = gridTab.getField("JP_ContractProcPeriod_ID");
			int old_ContractProcPeriod_ID = 0;
			int new_ContractProcPeriod_ID = 0;
			Object old_value = gf_ContractProcPeriod_ID.getOldValue();
			Object new_value = gf_ContractProcPeriod_ID.getValue();
			if(old_value == null)
				old_ContractProcPeriod_ID = 0;
			else
				old_ContractProcPeriod_ID = ((Integer)old_value).intValue();
				
			if(new_value == null)
				new_ContractProcPeriod_ID = 0;
			else
				new_ContractProcPeriod_ID = ((Integer)new_value).intValue();
			
			if(old_ContractProcPeriod_ID == new_ContractProcPeriod_ID)
			{
				;//Notihg to do
				
			}else{
				
				if(new_ContractProcPeriod_ID > 0)
				{
					int Record_ID =((Integer)gridTab.getRecord_ID()).intValue();
					Object obj_ContracContent_ID = gridTab.getValue("JP_ContractContent_ID");
					if(obj_ContracContent_ID == null)
					{
						;//Nothing to do
					}else{
						
						int JP_ContractContent_ID = ((Integer)obj_ContracContent_ID).intValue();
						MInvoice[] invoices = getInvoiceByContractPeriod(Env.getCtx(),JP_ContractContent_ID, new_ContractProcPeriod_ID);
						for(int i = 0; i < invoices.length; i++)
						{
							if(invoices[i].getC_Order_ID() == Record_ID)
							{
								continue;
							}else{
									
								String docInfo = Msg.getElement(Env.getCtx(), "DocumentNo") + " : " + invoices[i].getDocumentNo();
								String msg = docInfo + " " + Msg.getMsg(Env.getCtx(),"JP_DoYouConfirmIt");//Do you confirm it?
								final MInvoice invoice = invoices[i];
								Callback<Boolean> isZoom = new Callback<Boolean>()
								{
										@Override
										public void onCallback(Boolean result)
										{
											if(result)
											{
												AEnv.zoom(MInvoice.Table_ID, invoice.getC_Invoice_ID());
											}
										}
									
								};
								FDialog.ask( event.getWindow().getJPiereADWindowContent().getWindowNo(), event.getWindow().getComponent(),Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID"), "JP_OverlapPeriod", msg, isZoom);
								break;
							}
						}//for
					}
				}
			}//if(obj == null)
			
			callback.onCallback(true);
			
		}//BEFORE_SAVE
		
		callback.onCallback(true);
	}
	
	public MInvoice[] getInvoiceByContractPeriod(Properties ctx, int JP_ContractContent_ID, int JP_ContractProcPeriod_ID)
	{
		ArrayList<MInvoice> list = new ArrayList<MInvoice>();
		final String sql = "SELECT * FROM C_Invoice WHERE JP_ContractContent_ID=? AND JP_ContractProcPeriod_ID=? AND DocStatus NOT IN ('VO','RE')";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, JP_ContractContent_ID);
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MInvoice(ctx, rs, null));
		}
		catch (Exception e)
		{
//			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		
		MInvoice[] invoices = new MInvoice[list.size()];
		list.toArray(invoices);
		return invoices;
	}
}
