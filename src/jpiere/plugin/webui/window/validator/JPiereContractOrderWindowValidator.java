package jpiere.plugin.webui.window.validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import org.adempiere.util.Callback;
import org.adempiere.webui.adwindow.validator.WindowValidatorEventType;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridTab;
import org.compiere.model.MOrder;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.plugin.webui.adwindow.validator.JPiereWindowValidator;
import jpiere.plugin.webui.adwindow.validator.JPiereWindowValidatorEvent;

public class JPiereContractOrderWindowValidator implements JPiereWindowValidator {
	
	@Override
	public void onWindowEvent(JPiereWindowValidatorEvent event, Callback<Boolean> callback)
	{
		if(event.getName().equals(WindowValidatorEventType.BEFORE_SAVE.getName()))
		{
			GridTab gridTab =event.getWindow().getJPiereADWindowContent().getActiveGridTab();
			int JP_ContractProcPeriod_ID = ((Integer)gridTab.getValue("JP_ContractProcPeriod_ID")).intValue();
			if(JP_ContractProcPeriod_ID > 0)
			{
				int Record_ID =((Integer)gridTab.getRecord_ID()).intValue();
				int JP_ContractContent_ID = ((Integer)gridTab.getValue("JP_ContractContent_ID")).intValue();
				MOrder[] orders = getOrderByContractPeriod(Env.getCtx(),JP_ContractContent_ID, JP_ContractProcPeriod_ID);
				for(int i = 0; i < orders.length; i++)
				{
					if(orders[i].getC_Order_ID() == Record_ID)
					{
						continue;
					}else{
							
						String docInfo = Msg.getElement(Env.getCtx(), "DocumentNo") + " : " + orders[i].getDocumentNo();
						String msg = docInfo + " " + Msg.getMsg(Env.getCtx(),"JP_DoYouConfirmIt");//Do you confirm it?
						final MOrder order = orders[i];
						Callback<Boolean> isZoom = new Callback<Boolean>()
						{
								@Override
								public void onCallback(Boolean result)
								{
									if(result)
									{
										AEnv.zoom(MOrder.Table_ID, order.getC_Order_ID());
									}
								}
							
						};
						FDialog.ask( event.getWindow().getJPiereADWindowContent().getWindowNo(), event.getWindow().getComponent(),Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID"), "JP_OverlapPeriod", msg, isZoom);
						break;
					}
				}//for

			}else{
				callback.onCallback(true);
			}
		}
		
		callback.onCallback(true);
	}
	
	public MOrder[] getOrderByContractPeriod(Properties ctx, int JP_ContractContent_ID, int JP_ContractProcPeriod_ID)
	{
		ArrayList<MOrder> list = new ArrayList<MOrder>();
		final String sql = "SELECT * FROM C_Order WHERE JP_ContractContent_ID=? AND JP_ContractProcPeriod_ID=? AND DocStatus NOT IN ('VO','RE')";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, JP_ContractContent_ID);
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MOrder(ctx, rs, null));
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
		
		
		MOrder[] orderes = new MOrder[list.size()];
		list.toArray(orderes);
		return orderes;
	}
}
