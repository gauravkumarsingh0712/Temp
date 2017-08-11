package com.ncsavault.alabamavault.service;

public interface ServiceManager {
	ServiceContext getServiceContext();

	VaultApiInterface getVaultService();

}
