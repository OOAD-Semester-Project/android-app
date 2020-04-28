package com.rajchandak.copa.view;
//
// Created by rajkc on 22-02-2020.
//

import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemObjects {
    private String _name;
    private long _date;

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        this._name = name;
    }

    public String getDate() {
        return new SimpleDateFormat("MM/dd/yyyy").format(new Date(_date));
    }

    public long getLongDate() {
        return _date;
    }
    public void setDate(long _date) {
        this._date = _date;
    }
}
