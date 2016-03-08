package ru.max64.myappstime.Event;


import java.util.ArrayList;

import ru.max64.myappstime.Data.ProcessData;

/**
 * Created by aditya on 01/07/15.
 */
public class ProcessInfoEvent {

    private ArrayList<ProcessData> processDataList;

    public ArrayList<ProcessData> getProcessDataList() {
        return processDataList;
    }

    public void setProcessDataList(ArrayList<ProcessData> processDataList) {
        this.processDataList = processDataList;
    }
}
