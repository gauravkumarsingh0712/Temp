package com.ncsavault.alabamavault.serviceimpl;


import com.ncsavault.alabamavault.service.ServiceContext;
import com.ncsavault.alabamavault.service.ServiceManager;
import com.ncsavault.alabamavault.service.VaultApiInterface;
import com.ncsavault.alabamavault.service.VaultService;

import java.util.HashMap;


public class AbstractServiceManagerImpl implements ServiceManager {
	private ServiceContext serviceContext;
	private HashMap<Class<?>, Object> services;

	private static final Class<?>[] DefaultServices = {
			VaultApiCallImpl.class };

	public AbstractServiceManagerImpl(ServiceContext serviceContext)
			throws Exception {
		this(serviceContext, DefaultServices);
	}

	public AbstractServiceManagerImpl(ServiceContext serviceContext,
			Class<?>[] defaultService) throws Exception {
		this.serviceContext = serviceContext;
		services = new HashMap<Class<?>, Object>();
		for (Class<?> cls : defaultService) {
			VaultService service = (VaultService) cls.getConstructor(
					ServiceManager.class).newInstance(
					AbstractServiceManagerImpl.this);
			service.init();
			services.put(cls, service);
		}
	}

	@Override
	public ServiceContext getServiceContext() {
		return serviceContext;
	}

	@Override
	public VaultApiInterface getVaultService() {
		return (VaultApiInterface) services.get(VaultApiCallImpl.class);
	}

}
