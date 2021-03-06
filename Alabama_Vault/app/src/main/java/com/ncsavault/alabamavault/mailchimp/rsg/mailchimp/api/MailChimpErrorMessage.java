package com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api;

import com.ncsavault.alabamavault.mailchimp.rsg.mailchimp.api.data.GenericStructConverter;

/**
 * This class is a generic handling of error messages from the API in functions like batch subscribe.
 * 
 * @author ericmuntz
 *
 */
public class MailChimpErrorMessage extends GenericStructConverter {

	public Integer code;
	public String message;
	
	
}
