/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
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

package jpiere.plugin.webui.adwindow;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.adempiere.webui.AdempiereIdGenerator;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADTreePanel; 				//JPIERE
import org.adempiere.webui.adwindow.ADWindowToolbar;			//JPIERE
import org.adempiere.webui.adwindow.AbstractADWindowContent;	//JPIERE
import org.adempiere.webui.adwindow.DetailPane;				//JPIERE
import org.adempiere.webui.adwindow.GridView;					//JPIERE
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.component.ListHeader;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.SimpleListModel;
import org.adempiere.webui.factory.ButtonFactory;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.GridTab;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.SystemProperties;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DefaultEvaluatee;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.NamePair;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.event.ListDataEvent;

/**
 *	Tab to maintain Order/Sequence
 *
 * 	@author 	Jorg Janke
 *
 *  @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * 				FR [ 1779410 ] VSortTab: display ID for not visible columns
 *
 *  @author victor.perez@e-evolution.com, e-Evolution
 * 				FR [ 2826406 ] The Tab Sort without parent column
 *				<li> https://sourceforge.net/p/adempiere/feature-requests/776/
 *  Zk Port
 *  @author Low Heng Sin
 *  @author Juan David Arboleda : Refactoring Yes and No List to work with multiple choice.
 */
public class JPiereADSortTab extends Panel implements JPiereIADTabpanel
{
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -4161399343247477912L;
	
	/**
	 * default constructor
	 */
	public JPiereADSortTab()
	{
	}

	/**
	 * Initiate
	 * 
	 * @param winPanel
	 * @param gridTab
	 */
	@Override
	public void init(AbstractADWindowContent winPanel, GridTab gridTab)
	{
	}
	
	public void init(JPiereAbstractADWindowContent winPanel, GridTab gridTab) 
	{
		this.adWindowPanel = winPanel;
		if (log.isLoggable(Level.CONFIG)) log.config("SortOrder=" + gridTab.getAD_ColumnSortOrder_ID() + ", SortYesNo=" + gridTab.getAD_ColumnSortYesNo_ID());
		m_WindowNo = winPanel.getWindowNo();
		this.gridTab = gridTab;

		m_AD_Table_ID = gridTab.getAD_Table_ID();
		ZKUpdateUtil.setVflex(this, "true");

		addEventListener(ON_ACTIVATE_EVENT, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				removeAttribute(ATTR_ON_ACTIVATE_POSTED);
			}
		});
	} // init

	/**	Logger			*/
	protected static final  CLogger log = CLogger.getCLogger(JPiereADSortTab.class);
	private int			m_WindowNo;
	private int			m_AD_Table_ID;
	private String		m_TableName = null;
	private String		m_ColumnSortName= null;
	private String		m_ColumnYesNoName = null;
	private String		m_KeyColumnName = null;
	private String		m_IdentifierSql = null;
	private boolean		m_IdentifierTranslated = false;

	private String		m_ParentColumnName = null;
	private JPiereAbstractADWindowContent adWindowPanel = null;

	//	UI variables
	private Label noLabel = new Label();
	private Label yesLabel = new Label();
	private Button bAdd = ButtonFactory.createButton(null, ThemeManager.getThemeResource("images/MoveRight16.png"), null);
	private Button bRemove = ButtonFactory.createButton(null, ThemeManager.getThemeResource("images/MoveLeft16.png"), null);
	private Button bUp = ButtonFactory.createButton(null, ThemeManager.getThemeResource("images/MoveUp16.png"), null);
	private Button bDown = ButtonFactory.createButton(null, ThemeManager.getThemeResource("images/MoveDown16.png"), null);
	//
	protected SimpleListModel noModel = new SimpleListModel() {
		/**
		 * generated serial id
		 */
		private static final long serialVersionUID = 3488081120336708285L;

		@Override
		public void addElement(Object obj) {
			Object[] elements = list.toArray();
			Arrays.sort(elements);
			int index = Arrays.binarySearch(elements, obj);
			if (index < 0)
				index = -1 * index - 1;
			if (index >= elements.length)
				list.add(obj);
			else
				list.add(index, obj);
			fireEvent(ListDataEvent.INTERVAL_ADDED, index, index);
		}
	};
	protected SimpleListModel yesModel = new SimpleListModel();
	protected Listbox noList = new Listbox();
	protected Listbox yesList = new Listbox();

	private GridTab gridTab;
	private boolean uiCreated;
	/** true if tab have been activated **/
	private boolean active = false;
	private boolean isChanged;
	private boolean detailPaneMode;
	private int tabNo;

	/**
	 * 	Dynamic Init
	 *  @param AD_Table_ID Table No
	 *  @param AD_ColumnSortOrder_ID Sort Column
	 *  @param AD_ColumnSortYesNo_ID YesNo Column
	 */
	private void dynInit (int AD_Table_ID, int AD_ColumnSortOrder_ID, int AD_ColumnSortYesNo_ID)
	{
		m_AD_Table_ID = AD_Table_ID;
		int identifiersCount = 0;
		StringBuilder identifierSql = new StringBuilder();
		String sql = "SELECT t.TableName, c.AD_Column_ID, c.ColumnName, e.Name,"	//	1..4
			+ "c.IsParent, c.IsKey, c.IsIdentifier, c.IsTranslated, c.ColumnSQL, c.AD_Reference_ID "	//	5..10
			+ "FROM AD_Table t, AD_Column c, AD_Element e "
			+ "WHERE t.AD_Table_ID=?"						//	#1
			+ " AND t.AD_Table_ID=c.AD_Table_ID"
			+ " AND (c.AD_Column_ID=? OR AD_Column_ID=?"	//	#2..3
			+ " OR c.IsParent='Y' OR c.IsKey='Y' OR c.IsIdentifier='Y')"
			+ " AND c.AD_Element_ID=e.AD_Element_ID";
		boolean trl = !Env.isBaseLanguage(Env.getCtx(), "AD_Element");
		if (trl)
			sql = "SELECT t.TableName, c.AD_Column_ID, c.ColumnName, et.Name,"	//	1..4
				+ "c.IsParent, c.IsKey, c.IsIdentifier, c.IsTranslated, c.ColumnSQL, c.AD_Reference_ID "	//	5..10
				+ "FROM AD_Table t, AD_Column c, AD_Element_Trl et "
				+ "WHERE t.AD_Table_ID=?"						//	#1
				+ " AND t.AD_Table_ID=c.AD_Table_ID"
				+ " AND (c.AD_Column_ID=? OR AD_Column_ID=?"	//	#2..3
				+ "	OR c.IsParent='Y' OR c.IsKey='Y' OR c.IsIdentifier='Y')"
				+ " AND c.AD_Element_ID=et.AD_Element_ID"
				+ " AND et.AD_Language=?";						//	#4
		sql += " ORDER BY c.SeqNo";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, AD_Table_ID);
			pstmt.setInt(2, AD_ColumnSortOrder_ID);
			pstmt.setInt(3, AD_ColumnSortYesNo_ID);
			if (trl)
				pstmt.setString(4, Env.getAD_Language(Env.getCtx()));
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				m_TableName = rs.getString(1);
				//	Sort Column
				if (AD_ColumnSortOrder_ID == rs.getInt(2))
				{
					if (log.isLoggable(Level.FINE)) log.fine("Sort=" + rs.getString(1) + "." + rs.getString(3));
					m_ColumnSortName = rs.getString(3);
					yesLabel.setValue(rs.getString(4));
				}
				//	Optional YesNo
				else if (AD_ColumnSortYesNo_ID == rs.getInt(2))
				{
					if (log.isLoggable(Level.FINE)) log.fine("YesNo=" + rs.getString(1) + "." + rs.getString(3));
					m_ColumnYesNoName = rs.getString(3);
				}
				//	Parent2
				else if (rs.getString(5).equals("Y"))
				{
					if (log.isLoggable(Level.FINE)) log.fine("Parent=" + rs.getString(1) + "." + rs.getString(3));
					m_ParentColumnName = rs.getString(3);
				}
				//	KeyColumn
				else if (rs.getString(6).equals("Y"))
				{
					if (log.isLoggable(Level.FINE)) log.fine("Key=" + rs.getString(1) + "." + rs.getString(3));
					m_KeyColumnName = rs.getString(3);
				}
				//	Identifier
				if (rs.getString(7).equals("Y"))
				{
					if (log.isLoggable(Level.FINE)) log.fine("Identifier=" + rs.getString(1) + "." + rs.getString(3));
					boolean isTranslated = trl && "Y".equals(rs.getString(8));
					int AD_Reference_ID = rs.getInt(10);
					if (identifierSql.length() > 0)
					{
						identifierSql.append(" || '")
							.append(MSysConfig.getValue(MSysConfig.IDENTIFIER_SEPARATOR, "_", Env.getAD_Client_ID(Env.getCtx())))
							.append("' || ");
					}
					identifierSql.append("NVL(");
					if (!Util.isEmpty(rs.getString(9)))
					{
						String value = rs.getString(9).replace(m_TableName + ".", isTranslated ? "tt." : "t.");
						identifierSql.append(DB.TO_CHAR("("+value+")", AD_Reference_ID, Env.getAD_Language(Env.getCtx())));			
					}
					else
						identifierSql.append(DB.TO_CHAR((isTranslated ? "tt." : "t.")+rs.getString(3), AD_Reference_ID, Env.getAD_Language(Env.getCtx())));
					identifierSql.append(",'')");
					identifiersCount++;
					if (isTranslated)
						m_IdentifierTranslated = true;
				}
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		//
		if (identifiersCount == 0)
			m_IdentifierSql = "NULL";
		else
			m_IdentifierSql = identifierSql.toString();
		//
		noLabel.setValue(Msg.getMsg(Env.getCtx(), "Available"));
		if (log.isLoggable(Level.FINE))
			log.fine(m_ColumnSortName);
	}	//	dynInit

	/**
	 * 	Layout panel
	 * 	@throws Exception
	 */
	private void init() throws Exception
	{
		//
		noLabel.setValue("No");
		yesLabel.setValue("Yes");

		ZKUpdateUtil.setVflex(yesList, true);
		ZKUpdateUtil.setVflex(noList, true);

		if (SystemProperties.isZkUnitTest())
			setId(AdempiereIdGenerator.escapeId(gridTab.getName()));

        EventListener<Event> mouseListener = new EventListener<Event>()
		{

			public void onEvent(Event event) throws Exception
			{
				if (Events.ON_DOUBLE_CLICK.equals(event.getName()))
				{
					migrateValueAcrossLists(event);
				}
			}
		};
		yesList.addDoubleClickListener(mouseListener);
		noList.addDoubleClickListener(mouseListener);
		//
		EventListener<Event> actionListener = new EventListener<Event>()
		{
			public void onEvent(Event event) throws Exception {
				migrateValueAcrossLists(event);
			}
		};
		yesModel.setMultiple(true);
		noModel.setMultiple(true);

		LayoutUtils.addSclass("btn-small", bAdd);
		LayoutUtils.addSclass("btn-sorttab small-img-btn", bAdd);
		bAdd.addEventListener(Events.ON_CLICK, actionListener);

		LayoutUtils.addSclass("btn-small", bRemove);
		LayoutUtils.addSclass("btn-sorttab small-img-btn", bRemove);
		bRemove.addEventListener(Events.ON_CLICK, actionListener);

		EventListener<Event> crossListMouseListener = new DragListener();
		yesList.addOnDropListener(crossListMouseListener);
		noList.addOnDropListener(crossListMouseListener);
		yesList.setItemDraggable(true);
		noList.setItemDraggable(true);

		EventListener<Event> actionListener2 = new EventListener<Event>()
		{
			public void onEvent(Event event) throws Exception {
				migrateValueWithinYesList(event);
			}
		};

		LayoutUtils.addSclass("btn-small", bUp);
		LayoutUtils.addSclass("btn-sorttab small-img-btn", bUp);
		bUp.addEventListener(Events.ON_CLICK, actionListener2);

		LayoutUtils.addSclass("btn-small", bDown);
		LayoutUtils.addSclass("btn-sorttab small-img-btn", bDown);
		bDown.addEventListener(Events.ON_CLICK, actionListener2);

		ListHead listHead = new ListHead();
		listHead.setParent(yesList);
		ListHeader listHeader = new ListHeader();
		listHeader.appendChild(yesLabel);
		Hlayout yesButtonLayout = new Hlayout();
		yesButtonLayout.appendChild(bUp);
		yesButtonLayout.appendChild(bDown);
		listHeader.appendChild(yesButtonLayout);
		yesButtonLayout.setStyle("display: inline-block; float: right;");
		listHeader.setParent(listHead);

		listHead = new ListHead();
		listHead.setParent(noList);
		listHeader = new ListHeader();
		listHeader.appendChild(noLabel);
		Hlayout noButtonLayout = new Hlayout();
		noButtonLayout.appendChild(bRemove);
		noButtonLayout.appendChild(bAdd);
		listHeader.appendChild(noButtonLayout);
		noButtonLayout.setStyle("display: inline-block; float: right;");
		listHeader.setParent(listHead);

		Hlayout hlayout = new Hlayout();
		ZKUpdateUtil.setVflex(hlayout, "true");
		ZKUpdateUtil.setHflex(hlayout, "true");
		hlayout.setStyle("margin: auto;");
		appendChild(hlayout);
		ZKUpdateUtil.setHflex(noList, "1");
		ZKUpdateUtil.setVflex(noList, true);
		hlayout.appendChild(noList);

		ZKUpdateUtil.setVflex(yesList, true);
		ZKUpdateUtil.setHflex(yesList, "1");
		hlayout.appendChild(yesList);
	}	//	Init

	/**
	 * Load data
	 */
	public void loadData()
	{
		yesModel.removeAllElements();
		noModel.removeAllElements();

		boolean isReadWrite = true;
		//	SELECT t.AD_Field_ID,t.Name,t.SeqNo,t.IsDisplayed FROM AD_Field t WHERE t.AD_Tab_ID=? ORDER BY 4 DESC,3,2
		//	SELECT t.AD_PrintFormatItem_ID,t.Name,t.SeqNo,t.IsPrinted FROM AD_PrintFormatItem t WHERE t.AD_PrintFormat_ID=? ORDER BY 4 DESC,3,2
		//	SELECT t.AD_PrintFormatItem_ID,t.Name,t.SortNo,t.IsOrderBy FROM AD_PrintFormatItem t WHERE t.AD_PrintFormat_ID=? ORDER BY 4 DESC,3,2
		StringBuilder sql = new StringBuilder();
		//	Columns
		sql.append("SELECT t.").append(m_KeyColumnName)				//	1
		.append(",").append(m_IdentifierSql)						//	2
		.append(",t.").append(m_ColumnSortName)				//	3
		.append(", t.AD_Client_ID, t.AD_Org_ID");		// 4, 5
		if (m_ColumnYesNoName != null)
			sql.append(",t.").append(m_ColumnYesNoName);			//	6
		//	Tables
		sql.append(" FROM ").append(m_TableName).append( " t");
		if (m_IdentifierTranslated)
			sql.append(", ").append(m_TableName).append("_Trl tt");
		//	Where
		//FR [ 2826406 ]
		if(m_ParentColumnName != null)
		{
			sql.append(" WHERE t.").append(m_ParentColumnName).append("=?");
		}
		else
		{
			sql.append(" WHERE 1=?");
		}

		int reportView_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, "AD_ReportView_ID");
		if ("AD_PrintFormatItem".equals(m_TableName) && reportView_ID > 0) {
			sql.append(" AND (t.AD_Column_ID IN (SELECT AD_Column_ID FROM AD_ReportView_Column WHERE AD_ReportView_ID=")
			.append(reportView_ID).append(" AND IsActive='Y')")
			.append(" OR ((SELECT COUNT(*) FROM AD_ReportView_Column WHERE AD_ReportView_ID=").append(reportView_ID).append(" AND IsActive='Y') = 0))");
		}

		if (m_IdentifierTranslated)
			sql.append(" AND t.").append(m_KeyColumnName).append("=tt.").append(m_KeyColumnName)
			.append(" AND tt.AD_Language=?");
		sql.append(" AND t.AD_Client_ID IN (0,?)");
		//	Order
		sql.append(" ORDER BY ");
		if (m_ColumnYesNoName != null)
			sql.append("6 DESC,");		//	t.IsDisplayed DESC
		sql.append("3,2");				//	t.SeqNo, tt.Name
		//FR [ 2826406 ]
		int ID = 0;
		if(m_ParentColumnName != null)
		{	
			ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, m_ParentColumnName);
			if (log.isLoggable(Level.FINE)) log.fine(sql.toString() + " - ID=" + ID);
		}	
		else
		{
			ID = 1;
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			int idx = 1;
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(idx++, ID);

			if (m_IdentifierTranslated)
				pstmt.setString(idx++, Env.getAD_Language(Env.getCtx()));

			pstmt.setInt(idx++, Env.getAD_Client_ID(Env.getCtx()));
			
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				int key = rs.getInt(1);
				String name = rs.getString(2);
				int seq = rs.getInt(3);
				boolean isYes = seq != 0;
				int AD_Client_ID = rs.getInt(4);
				int AD_Org_ID = rs.getInt(5);
				if (m_ColumnYesNoName != null)
					isYes = rs.getString(6).equals("Y");

				//
				ListElement pp = new ListElement(key, name, seq, isYes, AD_Client_ID, AD_Org_ID);
				if (isYes)
					yesModel.addElement(pp);
				else
					noModel.addElement(pp);
				// If one item from "Yes" list is readonly make entire tab readonly
				if (isYes && !pp.isUpdateable()) {
					isReadWrite = false;
				}
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		if (!gridTab.getParentTab().needSave(true, true))
			setIsChanged(false);

		bAdd.setEnabled(isReadWrite);
		bRemove.setEnabled(isReadWrite);
		bUp.setEnabled(isReadWrite);
		bDown.setEnabled(isReadWrite);
		yesList.setEnabled(isReadWrite);
		noList.setEnabled(isReadWrite);

		yesList.setItemRenderer(yesModel);
		yesList.setModel(yesModel);
		noList.setItemRenderer(noModel);
		noList.setModel(noModel);
	}	//	loadData

	/**
	 * Set tab change status.
	 * @param value true for dirty/changed state, false otherwise
	 */
	public void setIsChanged(boolean value) {
		isChanged = value;
		if (adWindowPanel != null) {
			adWindowPanel.getToolbar().enableSave(value);
			adWindowPanel.getToolbar().enableIgnore(value);
		}
	}

	/**
	 * Is tab has changes
	 * @return true if tab has changes
	 */
	public boolean isChanged() {
		return isChanged;
	}
	
	/**
	 * Move an item between yes and no list.<br/>
	 * Delegate to {@link #migrateLists(Listbox, Listbox, int)}
	 * @param event
	 */
	protected void migrateValueAcrossLists (Event event)
	{
		Object source = event.getTarget();
		if (source instanceof ListItem) {
			source = ((ListItem)source).getListbox();
		}
		Listbox listFrom = (source == bAdd || source == noList) ? noList : yesList;
		Listbox listTo =  (source == bAdd || source == noList) ? yesList : noList;

		int endIndex = yesList.getIndexOfItem(listTo.getSelectedItem());	
		//Listto is empty. 
		if (endIndex<0 )
			endIndex=0;

		migrateLists (listFrom,listTo,endIndex);
	}	//	migrateValueAcrossLists

	/**
	 * Move an item from listFrom to listTo.
	 * @param listFrom
	 * @param listTo
	 * @param endIndex destination index
	 */
	protected void migrateLists (Listbox listFrom , Listbox listTo , int endIndex)
	{
		int index = 0; 
		SimpleListModel lmFrom = (listFrom == yesList) ? yesModel:noModel;
		SimpleListModel lmTo = (lmFrom == yesModel) ? noModel:yesModel;
		Set<?> selectedItems = listFrom.getSelectedItems();
		List<ListElement> selObjects = new ArrayList<ListElement>();
		for (Object obj : selectedItems) {
			ListItem listItem = (ListItem) obj;
			index = listFrom.getIndexOfItem(listItem);
			ListElement selObject = (ListElement)lmFrom.getElementAt(index);
			selObjects.add(selObject);
		}
		index = 0;
		Arrays.sort(selObjects.toArray());	
		for (ListElement selObject : selObjects)
		{
			if (selObject == null || !selObject.isUpdateable())
				continue;

			lmFrom.removeElement(selObject);
			lmTo.add(endIndex, selObject);
		}
		//  Enable explicit Save
		setIsChanged(true);
		if ( listTo.getSelectedItem() != null)
		{
			AuFocus focus = new AuFocus(listTo.getSelectedItem());
			Clients.response(focus);
		}
	}

	/**
	 * 	Move an item within Yes List
	 *	@param event event
	 */
	protected void migrateValueWithinYesList (Event event)
	{
		Object[] selObjects = yesList.getSelectedItems().toArray();
		if (selObjects == null)
			return;
		int length = selObjects.length;
		if (length == 0)
			return;
		//
		int[] indices = yesList.getSelectedIndices();
		//
		boolean change = false;
		//
		Object source = event.getTarget();
		if (source == bUp)
		{
			for (int i = 0; i < length; i++) {
				int index = indices[i];
				if (index == 0)
					break;
				ListElement selObject = (ListElement) yesModel.getElementAt(index);
				ListElement newObject = (ListElement)yesModel.getElementAt(index - 1);
				if (!selObject.isUpdateable() || !newObject.isUpdateable())
					break;
				yesModel.setElementAt(newObject, index);
				yesModel.setElementAt(selObject, index - 1);
				indices[i] = index - 1;
				change = true;
			}
		}	//	up

		else if (source == bDown)
		{
			for (int i = length - 1; i >= 0; i--) {
				int index = indices[i];
				if (index  >= yesModel.getSize() - 1)
					break;
				ListElement selObject = (ListElement) yesModel.getElementAt(index);
				ListElement newObject = (ListElement)yesModel.getElementAt(index + 1);
				if (!selObject.isUpdateable() || !newObject.isUpdateable())
					break;
				yesModel.setElementAt(newObject, index);
				yesModel.setElementAt(selObject, index + 1);
				yesList.setSelectedIndex(index + 1);
				indices[i] = index + 1;
				change = true;
			}
		}	//	down

		//
		if (change) {
			yesList.setSelectedIndices(indices);
			setIsChanged(true);
			if ( yesList.getSelectedItem() != null)
			{
				AuFocus focus = new AuFocus(yesList.getSelectedItem());
				Clients.response(focus);
			}
		}
	}	//	migrateValueWithinYesList


	/**
	 * 	Move items within Yes List with Drag Event and Multiple Choice
	 *  @param endIndex move items after endIndex
	 *  @param selObjects selected items to move
	 */
	protected void migrateValueWithinYesList (int endIndex, List<ListElement> selObjects)
	{
		int iniIndex =0;
		Arrays.sort(selObjects.toArray());	
		ListElement selObject= null;
		ListElement endObject = (ListElement)yesModel.getElementAt(endIndex);
		for (ListElement selected : selObjects) {
   		    iniIndex = yesModel.indexOf(selected);
			selObject = (ListElement)yesModel.getElementAt(iniIndex);
			yesModel.removeElement(selObject);
			endIndex = yesModel.indexOf(endObject);
			yesModel.add(endIndex, selObject);			
		}	
		yesList.removeAllItems();
	    for(int i=0 ; i<yesModel.getSize(); i++) { 	
			ListElement pp = (ListElement)yesModel.getElementAt(i);
			yesList.addItem(new KeyNamePair(pp.m_key, pp.getName()));
		}
		setIsChanged(true);
	}

	/**
	 * Set AD Window content part that own this ADSortTab instance.
	 * @param panel
	 */
	public void registerAPanel (JPiereAbstractADWindowContent panel)
	{
		adWindowPanel = panel;
	}	//	registerAPanel

	/**
	 * Save changes to db.
	 */
	public void saveData()
	{
		if (!adWindowPanel.getToolbar().isSaveEnable())
			return;
		boolean ok = true;
		StringBuilder info = new StringBuilder();
		MTable table = MTable.get(Env.getCtx(), m_TableName);
		Map<Integer, ListElement> noModelBackup = new HashMap<>();
		Map<Integer, ListElement> yesModelBackup = new HashMap<>();
		
		Trx trx = Trx.get(Trx.createTrxName("ADSortTab_save"), true);
		try {
			trx.start();
			//	noList - Set SortColumn to null and optional YesNo Column to 'N'
			for (int i = 0; i < noModel.getSize(); i++)
			{
				ListElement pp = (ListElement)noModel.getElementAt(i);
				if (!pp.isUpdateable())
					continue;
				if(pp.getSortNo() == 0 && (m_ColumnYesNoName == null || !pp.isYes()))
					continue; // no changes
				//			
				PO po = table.getPO(pp.getKey(), trx.getTrxName());
				po.set_ValueOfColumn(m_ColumnSortName, 0);
				if (m_ColumnYesNoName != null)
					po.set_ValueOfColumn(m_ColumnYesNoName, "N");
				try {
					po.saveEx();
					ListElement backup = new ListElement(pp.getKey(), pp.getName(), pp.getSortNo(), pp.isYes(), pp.getAD_Client_ID(), pp.getAD_Org_ID());
					noModelBackup.put(i, backup);
					pp.setSortNo(0);
					pp.setIsYes(false);
				} catch (Exception e) {
					ok = false;
					trx.rollback();
					if (info.length() > 0)
						info.append(", ");
					info.append(pp.getName());
					log.log(Level.SEVERE, "NoModel - Not updated: " + m_KeyColumnName + "=" + pp.getKey(), e);
					break;
				}
			}
			
			if (ok) {
				//	yesList - Set SortColumn to value and optional YesNo Column to 'Y'
				int index = 0;
				for (int i = 0; i < yesModel.getSize(); i++)
				{
					ListElement pp = (ListElement)yesModel.getElementAt(i);
					if (!pp.isUpdateable())
						continue;
					index += 10;
					if(pp.getSortNo() == index && (m_ColumnYesNoName == null || pp.isYes()))
						continue; // no changes
					//
					PO po = table.getPO(pp.getKey(), trx.getTrxName());
					po.set_ValueOfColumn(m_ColumnSortName, index);
					if (m_ColumnYesNoName != null)
						po.set_ValueOfColumn(m_ColumnYesNoName, "Y");
					try {
						po.saveEx();
						ListElement backup = new ListElement(pp.getKey(), pp.getName(), pp.getSortNo(), pp.isYes(), pp.getAD_Client_ID(), pp.getAD_Org_ID());
						yesModelBackup.put(i, backup);
						pp.setSortNo(index);
						pp.setIsYes(true);
					} catch (Exception e) {
						ok = false;
						trx.rollback();
						if (info.length() > 0)
							info.append(", ");
						info.append(pp.getName());
						log.log(Level.SEVERE, "YesModel - Not updated: " + m_KeyColumnName + "=" + pp.getKey(), e);
						break;
					}
				}
			}
			
			if (ok) {
				try {
					trx.commit(true);
				} catch (Exception e) {
					ok = false;
					trx.rollback();
					info.append("Failed to commit database transaction");
					log.log(Level.SEVERE, "Failed to commit database transaction", e);
				}
			}
			
			if (!ok) {
				//rollback changes to yes and no model
				for(Integer index : noModelBackup.keySet()) {
					ListElement e = noModelBackup.get(index);
					noModel.setElementAt(e, index);
				}
				for(Integer index : yesModelBackup.keySet()) {
					ListElement e = yesModelBackup.get(index);
					yesModel.setElementAt(e, index);
				}
			}
		} finally {
			trx.close();
		}
		//
		if (ok) {
			setIsChanged(false);
		}
		else {
			Dialog.error(m_WindowNo, "SaveError", info.toString());
		}
	}	//	saveData

	/**
	 * List Item
	 * @author Teo Sarca
	 */
	private class ListElement extends NamePair {
		/**
		 * generated serial id
		 */
		private static final long serialVersionUID = -6319536467438753815L;
		private int		m_key;
		private int		m_AD_Client_ID;
		private int		m_AD_Org_ID;
		/** Initial seq number */
		private int		m_sortNo;
		/** Initial selection flag */
		private boolean m_isYes;
		private boolean	m_updateable;

		public ListElement(int key, String name, int sortNo, boolean isYes, int AD_Client_ID, int AD_Org_ID) {
			super(name);
			this.m_key = key;
			this.m_AD_Client_ID = AD_Client_ID;
			this.m_AD_Org_ID = AD_Org_ID;
			this.m_sortNo = sortNo;
			this.m_isYes = isYes;
			this.m_updateable = MRole.getDefault().canUpdate(m_AD_Client_ID, m_AD_Org_ID, m_AD_Table_ID, m_key, false);
		}
		public int getKey() {
			return m_key;
		}
		public void setSortNo(int sortNo) {
			m_sortNo = sortNo;
		}
		public int getSortNo() {
			return m_sortNo;
		}
		public void setIsYes(boolean value) {
			m_isYes = value;
		}
		public boolean isYes() {
			return m_isYes;
		}
		public int getAD_Client_ID() {
			return m_AD_Client_ID;
		}
		public int getAD_Org_ID() {
			return m_AD_Org_ID;
		}
		public boolean isUpdateable() {
			return m_updateable;
		}
		@Override
		public String getID() {
			return m_key != -1 ? String.valueOf(m_key) : null;
		}
		@Override
		public int hashCode() {
			return m_key;
		}
		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ListElement)
			{
				ListElement li = (ListElement)obj;
				return
					li.getKey() == m_key
					&& li.getName() != null
					&& li.getName().equals(getName())
					&& li.getAD_Client_ID() == m_AD_Client_ID
					&& li.getAD_Org_ID() == m_AD_Org_ID;
			}
			return false;
		}	//	equals

		@Override
		public String toString() {
			String s = super.toString();
			if (s == null || s.trim().length() == 0)
				s = "<" + getKey() + ">";
			return s;
		}
	}

	/**
	 * Listener for drop event
	 * @author eslatis
	 */
	private class DragListener implements EventListener<Event>
	{

		/**
		 * Creates a ADSortTab.DragListener.
		 */
		public DragListener()
		{
		}

		@Override
		public void onEvent(Event event) throws Exception {
			if (event instanceof DropEvent)
			{
				int endIndex = 0;
				DropEvent me = (DropEvent) event;
				ListItem endItem = (ListItem) me.getTarget();
				ListItem startItem = (ListItem) me.getDragged();

				if (!startItem.isSelected())
					startItem.setSelected(true);
				
				if (!(startItem.getListbox() == endItem.getListbox()))
				{
					Listbox listFrom = (Listbox)startItem.getListbox();
					Listbox listTo =  (Listbox)endItem.getListbox();
					endIndex = yesList.getIndexOfItem(endItem);
					migrateLists (listFrom,listTo,endIndex);
				} else if (startItem.getListbox() == endItem.getListbox() && startItem.getListbox() == yesList)
				{
					List<ListElement> selObjects = new ArrayList<ListElement>();
					endIndex = yesList.getIndexOfItem(endItem);	
					for (Object obj : yesList.getSelectedItems()) {
						ListItem listItem = (ListItem) obj;
						int index = yesList.getIndexOfItem(listItem);
						ListElement selObject = (ListElement)yesModel.getElementAt(index);				
						selObjects.add(selObject);						
					}
					migrateValueWithinYesList (endIndex, selObjects);
			   }
		   }
		}
	}

	@Override
	public void activate(boolean b) {
		if (b) {
	    	if (getAttribute(ATTR_ON_ACTIVATE_POSTED) != null) {
	    		return;
	    	}
	    	
	    	setAttribute(ATTR_ON_ACTIVATE_POSTED, Boolean.TRUE);
    	}

    	active = b;

    	Event event = new Event(ON_ACTIVATE_EVENT, this, b);
        Events.postEvent(event);
	}

	@Override
	public void createUI() {
		if (uiCreated) return;
		try
		{
			init();
			dynInit (gridTab.getAD_Table_ID(), gridTab.getAD_ColumnSortOrder_ID(), gridTab.getAD_ColumnSortYesNo_ID());
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
		uiCreated = true;
	}

	@Override
	public void dynamicDisplay(int i) {
	}

	@Deprecated(forRemoval = true, since = "11")
	public void editRecord(boolean b) {
	}

	@Override
	public String getDisplayLogic() {
		return gridTab.getDisplayLogic();
	}

	@Override
	public GridTab getGridTab() {
		return gridTab;
	}

	@Override
	public int getTabLevel() {
		return gridTab.getTabLevel();
	}

	@Override
    public String getTableName()
    {
        return gridTab.getTableName();
    }

	@Override
	public int getRecord_ID() {
		return gridTab.getRecord_ID();
	}

	@Override
	public String getTitle() {
		return gridTab.getName();
	}

	@Override
	public boolean isCurrent() {
		return gridTab != null ? gridTab.isCurrent() : false;
	}

	@Override
	public void query() {
		loadData();
	}

	@Override
	public void query(boolean currentRows, int currentDays, int i) {
		loadData();
	}

	@Override
	public void refresh() {
		createUI();
		loadData();
	}

	@Override
	public void switchRowPresentation() {
	}
	
	@Override
	public void onAfterFind() {
	}

	@Override
	public String get_ValueAsString(String variableName) {
		return new DefaultEvaluatee(getGridTab(), m_WindowNo, tabNo).get_ValueAsString(Env.getCtx(), variableName);
	}

	@Override
	public void afterSave(boolean onSaveEvent) {
	}

	@Override
	public boolean onEnterKey() {
		return false;
	}

	@Override
	public boolean isGridView() {
		return false;
	}

	@Override
	public boolean isActivated() {
		return active;
	}

	@Override
	public void setDetailPaneMode(boolean detailMode) {
		this.detailPaneMode = detailMode;
		ZKUpdateUtil.setVflex(this, "true");
	}
	
	@Override
	public boolean isDetailPaneMode() {
		return this.detailPaneMode;
	}

	@Override
	public JPiereGridView getJPiereGridView() {
		return null;
	}

	@Override
	public boolean needSave(boolean rowChange, boolean onlyRealChange) {
		return isChanged();
	}

	@Override
	public boolean dataSave(boolean onSaveEvent) {
		if (isChanged()) {
			saveData();
			return isChanged() == false;
		} else {
			return true;
		}
	}

	@Override
	public void setTabNo(int tabNo) {
		this.tabNo = tabNo;
	}

	@Override
	public int getTabNo() {
		return tabNo;
	}

	@Override
	public void setJPiereDetailPane(JPiereDetailPane detailPane) {
	}

	@Override
	public JPiereDetailPane getJPiereDetailPane() {
		return null;
	}

	@Override
	public void resetDetailForNewParentRecord() {
		yesModel.removeAllElements();
		noModel.removeAllElements();

		//setIsChanged(false);
		bAdd.setEnabled(false);
		bRemove.setEnabled(false);
		bUp.setEnabled(false);
		bDown.setEnabled(false);
		yesList.setEnabled(false);
		noList.setEnabled(false);

		yesList.setItemRenderer(yesModel);
		yesList.setModel(yesModel);
		noList.setItemRenderer(noModel);
		noList.setModel(noModel);
	}

	@Override
	public ADTreePanel getTreePanel() {
		return null;
	}

	@Override
	public boolean isEnableQuickFormButton() 
	{
		return false;
	}

	@Override
	public List<org.zkoss.zul.Button> getToolbarButtons()
	{
		return new ArrayList<org.zkoss.zul.Button>();
	}

	@Override
	public boolean isEnableCustomizeButton()
	{
		return false;
	}

	@Override
	public boolean isEnableProcessButton()
	{
		return false;
	}

	@Override
	public void updateToolbar(ADWindowToolbar toolbar)
	{

	}

	@Override
	public void updateDetailToolbar(Toolbar toolbar)
	{

	}

	@Override
	public GridView getGridView() {//JPIERE-0014
		return null;
	}

	@Override
	public void setDetailPane(DetailPane detailPane) {//JPIERE-0014

	}

	@Override
	public DetailPane getDetailPane() {//JPIERE-0014
		return null;
	}

}	//ADSortTab

