package com.melodygram.model;

/**
 * Created by FuGenX-01 on 24-08-2016.
 */
public class EditedMsgBean {
    String messageId, actualMsg,editId;

    public String getEditId() {
        return editId;
    }

    public void setEditId(String editId) {
        this.editId = editId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getActualMsg() {
        return actualMsg;
    }

    public void setActualMsg(String actualMsg) {
        this.actualMsg = actualMsg;
    }
}
