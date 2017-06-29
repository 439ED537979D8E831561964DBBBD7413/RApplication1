package com.rawalinfocom.rcontact.model;

import java.io.Serializable;

/**
 * Created by Aniruddh on 11/04/17.
 */

public class CallLogHistoryType implements Serializable{

    String historyNumber;
//    String historyDate;
    Long historyDate;

    public String getHistoryNumber() {
        return historyNumber;
    }

    public void setHistoryNumber(String historyNumber) {
        this.historyNumber = historyNumber;
    }

    public Long getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(Long historyDate) {
        this.historyDate = historyDate;
    }
}
