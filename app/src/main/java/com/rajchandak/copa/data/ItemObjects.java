package com.rajchandak.copa.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemObjects {
    private String _name;
    private long _date;
    private String _id;
    private String fromType;
    private String from;

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

    public String getID()
    {
        return _id;
    }

    public void setID(String _id)
    {
        this._id= _id;
    }

    public String getFromType()
    {
        return fromType;
    }

    public void setFromType(String fromType)
    {
        this.fromType = fromType;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }
}
