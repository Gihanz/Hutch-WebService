/*
 *  ***************************************
 *  * @author Gihan Liyange
 *  * @date Jul 10, 2020 - 11:24:31 AM
 *  ***************************************
 */

package com.tl.util;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Address;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.ReplaceIfPresentFlag;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.SubmitMultiResult;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config.properties")
public class MultipleSubmit {

    private static final Logger log = LoggerFactory.getLogger(MultipleSubmit.class);

    private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();

    @Value("${SMSC_IP}")
    private String smppIp;

    @Value("${PORT}")
    private int port;

    @Value("${SMPP_SYSTEM_ID}")
    private String username;

    @Value("${PASSWORD}")
    private String password;

    public void broadcastMessage(String message, List numbers) {
    	log.info("Broadcasting sms");
        SubmitMultiResult result = null;
        Address[] addresses = prepareAddress(numbers);
        SMPPSession session = initSession();
        if(session != null) {
            try {
                result = session.submitMultiple(null, TypeOfNumber.NATIONAL, NumberingPlanIndicator.UNKNOWN, null,
                        addresses, new ESMClass(), (byte) 0, (byte) 1, TIME_FORMATTER.format(new Date()), null,
                        new RegisteredDelivery(SMSCDeliveryReceipt.FAILURE), ReplaceIfPresentFlag.REPLACE,
                        new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), (byte) 0,
                        message.getBytes());
						
                log.info("Messages submitted, result is {}", result);
                Thread.sleep(1000);
            } catch (PDUException e) {
            	log.error("Invalid PDU parameter", e);
            } catch (ResponseTimeoutException e) {
            	log.error("Response timeout", e);
            } catch (InvalidResponseException e) {
            	log.error("Receive invalid response", e);
            } catch (NegativeResponseException e) {
            	log.error("Receive negative response", e);
            } catch (IOException e) {
            	log.error("I/O error occured", e);
            } catch (Exception e) {
            	log.error("Exception occured submitting SMPP request", e);
            }
        }else {
        	log.error("Session creation failed with SMPP broker.");
        }
        if(result != null && result.getUnsuccessDeliveries() != null && result.getUnsuccessDeliveries().length > 0) {
        	log.error(DeliveryReceiptState.getDescription(result.getUnsuccessDeliveries()[0].getErrorStatusCode()).description() + " - " +result.getMessageId());
        }else {
        	log.info("Pushed message to broker successfully");
        }
        if(session != null) {
            session.unbindAndClose();
        }
    }

    private Address[] prepareAddress(List numbers) {
        Address[] addresses = new Address[numbers.size()];
        for(int i = 0; i< numbers.size(); i++){
            addresses[i] = new Address(TypeOfNumber.NATIONAL, NumberingPlanIndicator.UNKNOWN, (String) numbers.get(i));
        }
        return addresses;
    }

    private SMPPSession initSession() {
        SMPPSession session = new SMPPSession();
        try {
            session.setMessageReceiverListener(new MessageReceiverListenerImpl());
            String systemId = session.connectAndBind(smppIp, Integer.valueOf(port), new BindParameter(BindType.BIND_TX, username, password, "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
            log.info("Connected with SMPP with system id {}", systemId);
            System.out.println("Connected with SMPP with system id {}"+ systemId);
        } catch (IOException e) {
        	System.out.println(e);
        	log.error("I/O error occured", e);
            session = null;
        }
        return session;
    }

}