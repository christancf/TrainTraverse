package com.traveller.components;

/**
 * Reference - https://stackoverflow.com/questions/34848401/divide-elements-on-groups-in-recyclerview
 */
public abstract class HistoryListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_END_FOOTER = 1;
    public static final int TYPE_ITEM = 2;

    abstract public int getType();
}
