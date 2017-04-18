package com.rawalinfocom.rcontact.model;

import java.io.Serializable;

/**
 * Created by Aniruddh on 11/04/17.
 */

public class CallLogHistoryType implements Serializable{

    String historyNumber;
//    String historyDate;
    long historyDate;

    public String getHistoryNumber() {
        return historyNumber;
    }

    public void setHistoryNumber(String historyNumber) {
        this.historyNumber = historyNumber;
    }

    public long getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(long historyDate) {
        this.historyDate = historyDate;
    }
}
