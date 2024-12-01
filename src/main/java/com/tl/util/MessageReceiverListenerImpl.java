/*
 *  ***************************************
 *  * @author Gihan Liyange
 *  * @date Jul 10, 2020 - 11:25:09 AM
 *  ***************************************
 */

package com.tl.util;

import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.MessageType;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReceiverListenerImpl implements MessageReceiverListener {

    private static final Logger log = LoggerFactory.getLogger(MessageReceiverListenerImpl.class);
    private static final String DATASM_NOT_IMPLEMENTED = "data_sm not implemented";

    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        
        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {

            try {
                DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();

                long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                String messageId = Long.toString(id, 16).toUpperCase();

                log.info("Receiving delivery receipt for message '{}' from {} to {}: {}",
                    messageId, deliverSm.getSourceAddr(), deliverSm.getDestAddress(), delReceipt);
            } catch (InvalidDeliveryReceiptException e) {
            	log.error("Failed getting delivery receipt", e);
            }
        }
    }
    
    public void onAcceptAlertNotification(AlertNotification alertNotification) {
    	log.info("AlertNotification not implemented");
    }
    
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
            throws ProcessRequestException {
    	log.info("DataSm not implemented");
        throw new ProcessRequestException(DATASM_NOT_IMPLEMENTED, SMPPConstant.STAT_ESME_RINVCMDID);
    }
}
