/*
 *  ***************************************
 *  * @author Gihan Liyange
 *  * @date Jul 14, 2020 - 1:09:26 AM
 *  ***************************************
 */

package com.tl.response;

public class SMSResponse {
	
	private String response;
	private String errorMessage;
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
