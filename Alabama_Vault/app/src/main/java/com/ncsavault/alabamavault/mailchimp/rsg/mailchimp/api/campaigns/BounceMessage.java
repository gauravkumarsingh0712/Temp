package com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.campaigns;

import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.MailChimpApiException;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.RPCStructConverter;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.Utils;

import java.util.Date;
import java.util.Map;


/**
 * See <a href="http://www.mailchimp.com/api/rtfm/campaignbouncemessages.func.php" target="_new">http://www.mailchimp.com/api/rtfm/campaignbouncemessages.func.php</a>
 * <br/>
 * Representation of the BounceMessage data
 * @author ericmuntz
 */
public class BounceMessage implements RPCStructConverter {

	public Date date;
	public String email;
	public String message;
	
	@SuppressWarnings("unchecked")
	public void populateFromRPCStruct(String key, Map struct) throws MailChimpApiException {
		Utils.populateObjectFromRPCStruct(this, struct, true);
	}

}
