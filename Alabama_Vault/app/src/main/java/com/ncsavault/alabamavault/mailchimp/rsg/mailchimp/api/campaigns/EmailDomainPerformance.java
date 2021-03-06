package com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.campaigns;


import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.MailChimpApiException;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.RPCStructConverter;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.Utils;

import java.util.Map;


/**
 * Object representation of the EmailDomainPerformance
 * @author ericmuntz
 *
 */
public class EmailDomainPerformance implements RPCStructConverter {

	public String domain;
	public Integer totalSent;
	public Integer emails;
	public Integer bounces;
	public Integer opens;
	public Integer clicks;
	public Integer unsubs;
	public Integer delivered;
	public Integer emailsPct;
	public Integer bouncesPct;
	public Integer opensPct;
	public Integer clicksPct;
	public Integer unsubsPct;
	
	@SuppressWarnings("unchecked")
	public void populateFromRPCStruct(String key, Map struct) throws MailChimpApiException {
		Utils.populateObjectFromRPCStruct(this, struct, true);
	}

}
