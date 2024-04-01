package com.traveller.components;

public class HistoryHeaderItem extends HistoryListItem {
    String header;

    public HistoryHeaderItem(String title) {
        header = title;
    }
    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}
