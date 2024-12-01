package com.tl.ws;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tl.request.SMSRequest;
import com.tl.response.CommonResponse;
import com.tl.response.SMSResponse;
import com.tl.util.BawRestInvoke;
import com.tl.util.MultipleSubmit;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
public class WsServiceImpl {
	
	@Autowired
	private BawRestInvoke bawRestInvoke;
	@Autowired
	private MultipleSubmit multiSubmit;

	static Logger log = LoggerFactory.getLogger(WsServiceImpl.class);

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String testService() {
		return "Service working fine";
	}

	@RequestMapping(value = "/finish", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> finishTask(@RequestBody JSONObject requestBoby, @RequestHeader("Action-Authorization") String authHeader) {

		CommonResponse commonResponse = new CommonResponse();
		String refershCard;
		
//		for (Entry<String, List<String>> entry : requestHeader.entrySet()) {
//			log.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
//		}
		log.info("Auth : "+authHeader);
		log.info("~~~~~~~~~ Decode JWT ~~~~~~~~~");
		String jwtToken = authHeader.split("\\s+")[1];
        String[] split_string = jwtToken.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];
        String base64EncodedSignature = split_string[2];

        log.info("~~~~~~~~~ JWT Header ~~~~~~~~~");
        Base64 base64Url = new Base64(true);
        String header = new String(base64Url.decode(base64EncodedHeader));
        log.info("JWT Header : " + header);

        log.info("~~~~~~~~~ JWT Body ~~~~~~~~~");
        String body = new String(base64Url.decode(base64EncodedBody));
        log.info("JWT Body : "+body);     

		try {
			
			JSONObject jwtBody = new JSONObject(body);
			String sender = jwtBody.getString("sub").split("@")[0];
			log.info("sender : "+sender);

			String taskDetailsResponse = bawRestInvoke.taskDetails(requestBoby.getString("task_id"));
			log.info("taskDetailsResponse : "+taskDetailsResponse);
			JSONObject taskDetails = new JSONObject(taskDetailsResponse);
			String assignedTo = taskDetails.getJSONObject("data").getString("assignedTo");
			log.info("assignedTo : "+assignedTo);
			
			if(assignedTo.contains("All Users_") || assignedTo.equalsIgnoreCase(sender)) {
				String doFinishRresponse = bawRestInvoke.finishTask(requestBoby.getString("task_id"), requestBoby.getJSONObject("task_data"));
				log.info("doFinishRresponse : "+doFinishRresponse);
				commonResponse.setResponse("Success");
				
				refershCard = "{\"type\": \"AdaptiveCard\",\"version\": \"1.0\",\"hideOriginalBody\": false,\"body\": [{\"type\": \"TextBlock\",\"text\": \"Action taken successfully !\"}]}";
				return ResponseEntity.ok().header("CARD-UPDATE-IN-BODY", "true").body(refershCard);
				
			}else {
				log.info(sender+" not authorized to perform this action");
				refershCard = "{\"type\": \"AdaptiveCard\",\"version\": \"1.0\",\"hideOriginalBody\": false,\"body\": [{\"type\": \"TextBlock\",\"text\": \"You are not authorized to perform this action !\"}]}";
				return ResponseEntity.ok().header("CARD-UPDATE-IN-BODY", "true").body(refershCard);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			commonResponse.setResponse("Error");
			commonResponse.setErrorMessage(e.getMessage() + " : " + e);
			
			refershCard = "{\"type\": \"AdaptiveCard\",\"version\": \"1.0\",\"hideOriginalBody\": false,\"body\": [{\"type\": \"TextBlock\",\"text\": \"Error occured. Please visit the process portal to take action.\"}]}";
			return ResponseEntity.ok().header("CARD-UPDATE-IN-BODY", "true").body(refershCard);
		} 
	
	}
	
	@ApiOperation(value = "Post Send SMS", nickname = "Post Send SMS with SMPP", notes = "Post SMS with JSMPP", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Send SMS Request successful", response = String.class),
            @ApiResponse(code = 400, message = "Input parameters are not valid"),
            @ApiResponse(code = 500, message = "Internal server error")})
	@RequestMapping(value = "/sendSMS", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public SMSResponse sendSMS(@RequestBody SMSRequest requestBoby) {
		
		SMSResponse smsResponse = new SMSResponse();
        try {
        	
        	List<String> toNumberList = Arrays.asList(requestBoby.getTo().split("\\s*,\\s*"));
        	
        	for(int k=0; k<toNumberList.size(); k++) {
        		String toNumber = toNumberList.get(k);
        		toNumberList.set(k, toNumber.substring(toNumber.length() - 9));       		
        	}
			multiSubmit.broadcastMessage(requestBoby.getMessage(), toNumberList);
			smsResponse.setResponse("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage() + "" + e);
			smsResponse.setResponse("Failed");
			smsResponse.setErrorMessage(e.getMessage());
		}
		
		return smsResponse;
	}

}
