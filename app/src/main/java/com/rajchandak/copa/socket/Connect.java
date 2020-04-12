package com.rajchandak.copa.socket;
//
// Created by rajkc on 23-02-2020.
//

import java.util.ArrayList;
import java.util.List;

public class Connect {
    private static boolean myBoolean = false;
    private static List<ConnectionBooleanChangedListener> listeners = new ArrayList<ConnectionBooleanChangedListener>();

    public static boolean getMyBoolean() { return myBoolean; }

    public static void setMyBoolean(boolean value) {
        myBoolean = value;

        for (ConnectionBooleanChangedListener l : listeners) {
            l.OnMyBooleanChanged();
        }
    }

    public static void addMyBooleanListener(ConnectionBooleanChangedListener l) {
        listeners.add(l);
    }



    private static boolean myBoolean2 = false;
    private static List<ConnectionBooleanChangedListener2> listeners2 = new ArrayList<ConnectionBooleanChangedListener2>();

    public static boolean getMyBoolean2() { return myBoolean2; }

    public static void setMyBoolean2(boolean value) {
        myBoolean2 = value;

        for (ConnectionBooleanChangedListener2 l : listeners2) {
            l.OnMyBooleanChanged2();
        }
    }

    public static void addMyBooleanListener2(ConnectionBooleanChangedListener2 l) {
        listeners2.add(l);
    }
}