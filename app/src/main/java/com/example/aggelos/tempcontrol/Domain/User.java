package com.example.aggelos.tempcontrol.Domain;

/**
 * Created by aggel on 12/14/2017.
 */

public class User {

    private int _priority;
    private String _name;
    private int _optim_temp;

    public void setPriority(int value)
    {
        _priority = value;
    }

    public int getPriority()
    {
        return _priority;
    }

    public void setName(String value)
    {
        _name = value;
    }

    public String getName()
    {
        return _name;
    }

    public void setOptimTemp(int value)
    {
        _optim_temp = value;
    }

    public int getOptimTemp()
    {
        return _optim_temp;
    }
}
