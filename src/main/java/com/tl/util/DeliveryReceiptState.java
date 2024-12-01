/*
 *  ***************************************
 *  * @author Gihan Liyange
 *  * @date Jul 10, 2020 - 11:25:31 AM
 *  ***************************************
 */

package com.tl.util;

public enum DeliveryReceiptState {

    ESME_ROK(0, "Ok - Message Acceptable"),

    ESME_RINVMSGLEN(1, "Invalid Message Length"),

    ESME_RINVCMDLEN(2, "Invalid Command Length"),

    ESME_RINVCMDID(3, "Invalid Command ID"),

    ESME_RINVBNDSTS(4, "Invalid bind status"),

    ESME_RALYBND(5,	"Bind attempted when already bound"),

    ESME_RINVPRTFLG(6, "Invalid priority flag"),

    ESME_RINVREGDLVFLG(7, "Invalid registered-delivery flag"),

    ESME_RSYSERR(8,	"SMSC system error"),

    ESME_RINVSRCADR(9, "Invalid source address"),

    ESME_RINVDSTADR(11, "Invalid destination address"),

    ESME_RINVMSGID(12, "Invalid message-id"),

    NOT_FOUND(000, "Couldn't resolve.Ask admin to add.");

    private int value;
    private String description;

    DeliveryReceiptState(int value, String description) {
      this.value = value;
      this.description = description;
    }

    public static DeliveryReceiptState getDescription(int value) {
      for (DeliveryReceiptState item : values()) {
        if (item.value() == value) {
          return item;
        }
      }
      return NOT_FOUND;
    }

    public int value() {
        return value;
    }

    public String description() {
        return description;
    }

}
