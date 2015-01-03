/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Plugins Form Window               *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプラグラムの著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持しており、 *
 * このプログラムを使用する場合には著作権の使用料をお支払頂く必要があります。 *
 * 著作権の使用料の支払い義務は、このプログラムから派生して作成された         *
 * プログラムにも発生します。 サポートサービスは                              *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/
package jpiere.plugin.webui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import jpiere.plugin.webui.adwindow.validator.JPiereWindowValidatorManager;

import org.adempiere.base.IDictionaryService;
import org.adempiere.util.ServerContext;
import org.compiere.Adempiere;
import org.compiere.model.Query;
import org.compiere.model.ServerStateChangeEvent;
import org.compiere.model.ServerStateChangeListener;
import org.compiere.model.X_AD_Package_Imp;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 *  JPiere Web-UI Activator
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereWebUIActivator implements BundleActivator,ServiceTrackerCustomizer<IDictionaryService, IDictionaryService>  {

	protected final static CLogger logger = CLogger.getCLogger(JPiereWebUIActivator.class.getName());
	private static BundleContext bundleContext = null;
	private ServiceTracker<IDictionaryService, IDictionaryService> serviceTracker;
	private IDictionaryService service;

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		bundleContext = context;
		JPiereWindowValidatorManager validatorMgr = new JPiereWindowValidatorManager();
		validatorMgr.start(context);

		serviceTracker = new ServiceTracker<IDictionaryService, IDictionaryService>(context, IDictionaryService.class.getName(), this);
		serviceTracker.open();
		start();
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		bundleContext = null;
		JPiereWindowValidatorManager.getInstance().stop(context);
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

	protected void packIn(String trxName) {
		URL packout = bundleContext.getBundle().getEntry("/META-INF/2Pack.zip");
		if (packout != null && bundleContext  != null) {
			FileOutputStream zipstream = null;
			try {
				// copy the resource to a temporary file to process it with 2pack
				InputStream stream = bundleContext.getBundle().getEntry("/META-INF/2Pack.zip").openStream();
				File zipfile = File.createTempFile(getName(), ".zip");
				zipstream = new FileOutputStream(zipfile);
			    byte[] buffer = new byte[1024];
			    int read;
			    while((read = stream.read(buffer)) != -1){
			    	zipstream.write(buffer, 0, read);
			    }
			    // call 2pack
				service.merge(bundleContext, zipfile);
			} catch (Throwable e) {
				logger.log(Level.SEVERE, "Pack in failed.", e);
			}
			finally{
				if (zipstream != null) {
					try {
						zipstream.close();
					} catch (Exception e2) {}
				}
			}
		}
	}

	@Override
	public IDictionaryService addingService(
			ServiceReference<IDictionaryService> reference) {
		service = bundleContext.getService(reference);
		if (Adempiere.getThreadPoolExecutor() != null) {
			Adempiere.getThreadPoolExecutor().execute(new Runnable() {
				@Override
				public void run() {
					ClassLoader cl = Thread.currentThread().getContextClassLoader();
					try {
						Thread.currentThread().setContextClassLoader(JPiereWebUIActivator.class.getClassLoader());
						setupPackInContext();
						installPackage();
					} finally {
						ServerContext.dispose();
						service = null;
						Thread.currentThread().setContextClassLoader(cl);
					}
				}
			});
		} else {
			Adempiere.addServerStateChangeListener(new ServerStateChangeListener() {
				@Override
				public void stateChange(ServerStateChangeEvent event) {
					if (event.getEventType() == ServerStateChangeEvent.SERVER_START && service != null) {
						ClassLoader cl = Thread.currentThread().getContextClassLoader();
						try {
							Thread.currentThread().setContextClassLoader(JPiereWebUIActivator.class.getClassLoader());
							setupPackInContext();
							installPackage();
						} finally {
							ServerContext.dispose();
							service = null;
							Thread.currentThread().setContextClassLoader(cl);
						}
					}
				}
			});
		}
		return null;
	}


	@Override
	public void modifiedService(ServiceReference<IDictionaryService> reference, IDictionaryService service) {
	}

	@Override
	public void removedService(ServiceReference<IDictionaryService> reference, IDictionaryService service) {

	}

	protected void setupPackInContext() {
		Properties serverContext = new Properties();
		ServerContext.setCurrentInstance(serverContext);
	};

	private void installPackage() {
		String trxName = Trx.createTrxName();
		try {

			// e.g. 1.0.0.qualifier, check only the "1.0.0" part
			String version = getVersion();
			if (version != null)
			{
				int count = 0;
				int index = -1;
				for(int i = 0; i < version.length(); i++)
				{
					if(version.charAt(i) == '.')
						count++;

					if (count == 3)
					{
						index = i;
						break;
					}
				}

				if (index == -1)
					index = version.length();
				version = version.substring(0,  index);
			}

			String where = "Name=? AND PK_Version LIKE ?";
			Query q = new Query(Env.getCtx(), X_AD_Package_Imp.Table_Name,
					where.toString(), null);
			q.setParameters(new Object[] { getName(), version + "%" });
			X_AD_Package_Imp pkg = q.first();
			if (pkg == null) {
				System.out.println("Installing " + getName() + " " + version + " ...");
				packIn(trxName);
				install();
				System.out.println(getName() + " " + version + " installed.");
			} else {
				if (logger.isLoggable(Level.INFO)) logger.info(getName() + " " + version + " was installed: "
						+ pkg.getCreated());
			}
			Trx.get(trxName, false).commit();
		} finally {
			if (Trx.get(trxName, false) != null) {
				Trx.get(trxName, false).close();
			}
		}
	}


	protected void install() {
	};

	protected void start() {
	};

	protected void stop() {
	};

	public String getName() {
		return bundleContext.getBundle().getSymbolicName();
	}

	public String getVersion() {
		return (String) bundleContext.getBundle().getHeaders().get("Bundle-Version");
	}


}
