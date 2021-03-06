package com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api;

import android.content.Context;

import com.ncsavault.alabamavault.mailchimp.org.xmlrpc.android.XMLRPCException;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.data.AccountDetails;
import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.data.ChimpChatter;

import java.util.List;
import java.util.Map;


/**
 * Coverage of the Helper methods in the api
 * <br/>See the helper methods: <a href="http://www.mailchimp.com/api/rtfm/" target="_new">http://www.mailchimp.com/api/rtfm/</a>
 * @author ericmuntz
 */
public class HelperMethods extends MailChimpApi {

	public HelperMethods(CharSequence apiKey) {
		super(apiKey);
	}

	public HelperMethods(Context ctx) {
		super(ctx);
	}
	
	/**
	 * Wraps method <a href="http://www.mailchimp.com/api/rtfm/ping.func.php}" target="_new">http://www.mailchimp.com/api/rtfm/ping.func.php}</a>
	 * <br/>
	 * Pings the API
	 * 
	 * @return the string result from the API
	 */
	public String ping() throws MailChimpApiException, XMLRPCException {
		return (String) callMethod("ping");
	}

	/**
	 * Wraps method <a href="http://www.mailchimp.com/api/rtfm/chimpchatter.func.php" target="_new">http://www.mailchimp.com/api/rtfm/chimpchatter.func.php</a>
	 * <br/>
	 * Returns chimp chatter for your account
	 * @return
	 * @throws MailChimpApiException
	 */
	public List<ChimpChatter> chimpChatter() throws MailChimpApiException, XMLRPCException {
		return callListMethod(ChimpChatter.class, "chimpChatter");
	}
	
	/**
	 * Wraps method <a href="http://www.mailchimp.com/api/rtfm/getaccountdetails.func.php" target="_new">http://www.mailchimp.com/api/rtfm/getaccountdetails.func.php</a>
	 * <br/>
	 * Gets the account details for the user
	 * @return
	 * @throws MailChimpApiException
	 */
	@SuppressWarnings("unchecked")
	public AccountDetails getAccountDetails() throws MailChimpApiException, XMLRPCException {
		Object obj = callMethod("getAccountDetails");
		if (obj != null && obj instanceof Map) {
			AccountDetails details = new AccountDetails();
			details.populateFromRPCStruct(null, (Map) obj);
			return details;
		}
		return null;
	}
	
	/**
	 * Wraps method <a href="http://www.mailchimp.com/api/rtfm/inlinecss.func.php" target="_new">http://www.mailchimp.com/api/rtfm/inlinecss.func.php</a>
	 * <br/>
	 * Inlines CSS
	 * @param html
	 * @param stripCss - if true, will remove the CSS <style> elements from the original HTML, if null defaults to false
	 * @return
	 * @throws MailChimpApiException
	 */
	public String inlineCss(String html, Boolean stripCss) throws MailChimpApiException, XMLRPCException {
		return (String) callMethod("inlineCss", html, stripCss == null ? false : stripCss);
	}
	
}
