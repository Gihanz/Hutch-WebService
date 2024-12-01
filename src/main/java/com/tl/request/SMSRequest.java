/*
 *  ***************************************
 *  * @author Gihan Liyange
 *  * @date Jul 14, 2020 - 12:19:39 AM
 *  ***************************************
 */

package com.tl.request;

public class SMSRequest {
	
	private String message;
	private String to;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}	
}
