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
import org.adempiere.webui.window.Dialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
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
public class JPiereContractInOutWindowValidator implements JPiereWindowValidator {
	
	@Override
	public void onWindowEvent(JPiereWindowValidatorEvent event, Callback<Boolean> callback)
	{
		if(event.getName().equals(WindowValidatorEventType.BEFORE_SAVE.getName()))
		{
			GridTab gridTab =event.getWindow().getJPiereADWindowContent().getActiveGridTab();
			GridField gf_ContractProcPeriod_ID = gridTab.getField("JP_ContractProcPeriod_ID");
			if(gf_ContractProcPeriod_ID != null)
			{
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
				
				int Record_ID =((Integer)gridTab.getRecord_ID()).intValue();
				if(Record_ID > 0 && old_ContractProcPeriod_ID == new_ContractProcPeriod_ID)
				{
					;//Notihg to do
					
				}else{
					
					if(gridTab.getTabNo() == 0 && new_ContractProcPeriod_ID > 0)
					{
//						Object obj_ContracContent_ID = gridTab.getValue("JP_ContractContent_ID");
//						if(obj_ContracContent_ID == null)
//						{
//							;//Nothing to do
//						}else{
//							
//							int JP_ContractContent_ID = ((Integer)obj_ContracContent_ID).intValue();
//							MInOut[] inouts = getInOutByContractPeriod(Env.getCtx(),JP_ContractContent_ID, new_ContractProcPeriod_ID);
//							for(int i = 0; i < inouts.length; i++)
//							{
//								if(inouts[i].getM_InOut_ID() == Record_ID)
//								{
//									continue;
//								}else{
//										
//									String docInfo = Msg.getElement(Env.getCtx(), "DocumentNo") + " : " + inouts[i].getDocumentNo();
//									String msg = docInfo + " " + Msg.getMsg(Env.getCtx(),"JP_DoYouConfirmIt");//Do you confirm it?
//									final MInOut io = inouts[i];
//									Callback<Boolean> isZoom = new Callback<Boolean>()
//									{
//											@Override
//											public void onCallback(Boolean result)
//											{
//												if(result)
//												{
//													AEnv.zoom(MInOut.Table_ID, io.getM_InOut_ID());
//												}
//											}
//										
//									};
//									FDialog.ask( event.getWindow().getJPiereADWindowContent().getWindowNo(), event.getWindow().getComponent(),Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID"), "JP_OverlapPeriod", msg, isZoom);
//									break;
//								}
//							}//for
//						}
					}//gridTab.getTabNo() == 0 
					
					else if(gridTab.getTabNo() == 1 && new_ContractProcPeriod_ID > 0)
					{
						Object obj_ContracLine_ID = gridTab.getValue("JP_ContractLine_ID");
						if(obj_ContracLine_ID == null)
						{
							;//Nothing to do
						}else{
							
							int JP_ContractLine_ID = ((Integer)obj_ContracLine_ID).intValue();
							MInOutLine[] ioLines = getInOutLineByContractPeriod(Env.getCtx(), JP_ContractLine_ID ,new_ContractProcPeriod_ID);
							for(int i = 0; i < ioLines.length; i++)
							{
								if(ioLines[i].getM_InOutLine_ID() == Record_ID)
								{
									continue;
								}else{
										
									String docInfo = Msg.getElement(Env.getCtx(), "DocumentNo") + " : " + ioLines[i].getParent().getDocumentNo()
														+" - " + Msg.getElement(Env.getCtx(), "C_InvoiceLine_ID") + " : " + ioLines[i].getLine();
									String msg = docInfo + " " + Msg.getMsg(Env.getCtx(),"JP_DoYouConfirmIt");//Do you confirm it?
									final MInOutLine ioLine = ioLines[i];
									Callback<Boolean> isZoom = new Callback<Boolean>()
									{
											@Override
											public void onCallback(Boolean result)
											{
												if(result)
												{
													AEnv.zoom(MInOutLine.Table_ID, ioLine.getM_InOutLine_ID());
												}
											}
										
									};
									Dialog.ask( event.getWindow().getJPiereADWindowContent().getWindowNo(), Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID"), "JP_OverlapPeriod", msg, isZoom);
									break;
								}
							}//for
						}
					}//gridTab.getTabNo() == 1
					
				}//if(Record_ID > 0 
			
			}//if(gf_ContractProcPeriod_ID != null)
			
		}//BEFORE_SAVE
		
		callback.onCallback(true);
	}
	
	public MInOut[] getInOutByContractPeriod(Properties ctx, int JP_ContractContent_ID, int JP_ContractProcPeriod_ID)
	{
		ArrayList<MInOut> list = new ArrayList<MInOut>();
		final String sql = "SELECT * FROM M_InOut WHERE JP_ContractContent_ID=? AND JP_ContractProcPeriod_ID=? AND DocStatus NOT IN ('VO','RE')";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, JP_ContractContent_ID);
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MInOut(ctx, rs, null));
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
		
		
		MInOut[] inouts = new MInOut[list.size()];
		list.toArray(inouts);
		return inouts;
	}
	
	public MInOutLine[] getInOutLineByContractPeriod(Properties ctx, int JP_ContractLine_ID,int JP_ContractProcPeriod_ID)
	{
		ArrayList<MInOutLine> list = new ArrayList<MInOutLine>();
		final String sql = "SELECT il.* FROM M_InOutLine il  INNER JOIN  M_InOut i ON(i.M_InOut_ID = il.M_InOut_ID) "
					+ " WHERE il.JP_ContractLine_ID=? AND il.JP_ContractProcPeriod_ID=? AND i.DocStatus NOT IN ('VO','RE')";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, JP_ContractLine_ID);
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MInOutLine(ctx, rs, null));
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
		
		MInOutLine[] iLines = new MInOutLine[list.size()];
		list.toArray(iLines);
		return iLines;
	}
}
