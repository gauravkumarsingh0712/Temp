package com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.lists;

import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.MailChimpApiException;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.MailChimpErrorMessage;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.RPCStructConverter;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.Utils;

import java.util.List;
import java.util.Map;


/**
 * Represents the results of a batch call, such as batchSubscribe
 * @author ericmuntz
 *
 */
public class BatchResults implements RPCStructConverter {

	public Integer successCount;
	public Integer errorCount;
	public List<MailChimpErrorMessage> errors;
	
	@SuppressWarnings("unchecked")
	public void populateFromRPCStruct(String key, Map struct) throws MailChimpApiException {
		Utils.populateObjectFromRPCStruct(this, struct, true, "errors");
		
		Object errorsObj = struct.get("errors");
		if (errorsObj != null && errorsObj instanceof Object[]) {
			errors = Utils.extractObjectsFromList(MailChimpErrorMessage.class, (Object[]) errorsObj);
		}
	}

}
