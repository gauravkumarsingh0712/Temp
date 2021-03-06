package com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.data;


import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.MailChimpApiException;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.RPCStructConverter;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.Utils;

import java.util.Map;


/**
 * Adapter class implementing RPCStructConverter by default reflection, allowing classes to subclass this and 
 * ride for free.
 * @author ericmuntz
 *
 */
public abstract class GenericStructConverter implements RPCStructConverter {

	@SuppressWarnings("unchecked")
	public void populateFromRPCStruct(String key, Map struct) throws MailChimpApiException {
		Utils.populateObjectFromRPCStruct(this, struct, true);
	}

}
