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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jpiere.plugin.webui.adwindow.JPiereADWindow;

import org.adempiere.util.Callback;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


/**
 *  JPiere Window Validator Manager
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereWindowValidatorManager implements BundleActivator, ServiceTrackerCustomizer<JPiereWindowValidator, JPiereWindowValidator> {

	private static JPiereWindowValidatorManager instance = null;

	private BundleContext context;
	private Map<String, List<JPiereWindowValidator>> validatorMap = new HashMap<String, List<JPiereWindowValidator>>();
	private List<JPiereWindowValidator> globalValidators = new ArrayList<JPiereWindowValidator>();

	private ServiceTracker<JPiereWindowValidator, JPiereWindowValidator> serviceTracker;

	@Override
	public JPiereWindowValidator addingService(
			ServiceReference<JPiereWindowValidator> reference) {
		JPiereWindowValidator service = context.getService(reference);
		String uuid = (String) reference.getProperty("AD_Window_UU");
		if (uuid == null || "*".equals(uuid)) {
			globalValidators.add(service);
			return service;
		}

		List<JPiereWindowValidator> list = validatorMap.get(uuid);
		if (list == null) {
			list = new ArrayList<JPiereWindowValidator>();
			validatorMap.put(uuid, list);
		}
		list.add(service);

		return service;
	}

	@Override
	public void modifiedService(ServiceReference<JPiereWindowValidator> reference,
			JPiereWindowValidator service) {
	}

	@Override
	public void removedService(ServiceReference<JPiereWindowValidator> reference,
			JPiereWindowValidator service) {
		String uuid = (String) reference.getProperty("AD_Window_UU");
		if (uuid == null || "*".equals(uuid)) {
			globalValidators.remove(service);
		} else {
			List<JPiereWindowValidator> list = validatorMap.get(uuid);
			if (list != null) {
				list.remove(service);
			}
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		serviceTracker = new ServiceTracker<JPiereWindowValidator, JPiereWindowValidator>(context, JPiereWindowValidator.class.getName(), this);
		serviceTracker.open();

		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		serviceTracker.close();
		this.context = null;
		instance = null;
	}

	public static JPiereWindowValidatorManager getInstance() {
		return instance;
	}

	public void fireWindowValidatorEvent(JPiereWindowValidatorEvent event, Callback<Boolean> callback) {
		JPiereADWindow window = event.getWindow();
		String uuid = window.getAD_Window_UU();
		List<JPiereWindowValidator> list = validatorMap.get(uuid);
		int listSize = list != null ? list.size() : 0;
		JPiereWindowValidator[] validators = new JPiereWindowValidator[listSize+globalValidators.size()];
		int index = -1;
		if (listSize > 0) {
			for(JPiereWindowValidator validator : list) {
				index++;
				validators[index] = validator;
			}
		}
		for(JPiereWindowValidator validator : globalValidators) {
			index++;
			validators[index] = validator;
		}
		ChainCallback chain = new ChainCallback(event, validators, callback);
		chain.start();
	}

	private static class ChainCallback implements Callback<Boolean> {

		private Callback<Boolean> callback;
		private JPiereWindowValidator[] validators;
		private JPiereWindowValidatorEvent event;
		private int index = -1;

		public ChainCallback(JPiereWindowValidatorEvent event, JPiereWindowValidator[] validators, Callback<Boolean> callback) {
			this.event = event;
			this.validators = validators;
			this.callback = callback;
		}

		public void start() {
			index = 0;
			if (index < validators.length)
				validators[index].onWindowEvent(event, this);
			else if (callback != null)
				callback.onCallback(true);
		}

		@Override
		public void onCallback(Boolean result) {
			if (result) {
				if (index < validators.length-1) {
					index++;
					validators[index].onWindowEvent(event, this);
				} else if (callback != null){
					callback.onCallback(result);
				}
			} else if (callback != null){
				callback.onCallback(result);
			}
		}

	}
}
