package com.rayjars.fieldmapper;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.List;


public class ParamFieldParser extends StringFieldParser {

    public Long parseLong(Object value) {
        return super.parseLong(getStringValue(value));
    }

    public String parseString(Object value) {
        return super.parseString(getStringValue(value));
    }

    public Float parseFloat(Object value) {
        return super.parseFloat(getStringValue(value));
    }

    public Double parseDouble(Object value) {
        return super.parseDouble(getStringValue(value));
    }

    public Boolean parseBoolean(Object value) {
        return super.parseBoolean(getStringValue(value));
    }

    public Integer parseInt(Object value) {
        return super.parseInteger(getStringValue(value));
    }

    public Date parseDate(Object value) throws ParseException {
        return super.parseDate(getStringValue(value));
    }

    public File parseFile(Object value) {
        return super.parseFile(getStringValue(value));
    }


    public URL parseUrl(Object value) throws MalformedURLException {
        return super.parseUrl(getStringValue(value));
    }

    public List<String> parseList(Object value) throws InvalidFieldValueException {
        if (isNodeDom(value)) {
            Param paramValue = (Param) value;

            if(paramValue.containsNoChilds()){
                throw new InvalidFieldValueException("The param doesnt contains any childrens !");
            }

            List<String> list = new ArrayList<>();

            if(paramValue.containsNoChilds()){
                list.add(paramValue.getValue());
            }

            for (Param child : paramValue.getChilds()) {
                list.add(child.getValue());
            }
            return list;
        }
        return super.parseList((String) value);
    }


    public Map<String, String> parseMap(Object value) throws InvalidFieldValueException {
        if (isNodeDom(value)) {
            Map<String, String> map = new HashMap<>();
            Param paramValue = (Param) value;

            if(paramValue.containsNoChilds()){
                throw new InvalidFieldValueException("The param doesnt contains any childrens !");
            }

            for (Param child : paramValue.getChilds()) {
                map.put(child.getName(), child.getValue());
            }
            return map;
        }

        return super.parseMap((String)value);
    }


    public String[] parseArray(Object value) throws InvalidFieldValueException {
        if (isNodeDom(value)) {
            return parseList(value).toArray(new String[0]);
        }

        return super.parseArray((String) value);
    }


    public Color parseColor(Object value){
        return super.parseColor(getStringValue(value));
    }


    public Font parseFont(Object font) {
        return super.parseFont(getStringValue(font));
    }

//------------------------------------------------ Utils methods

    public String getStringValue(Object value) {
        Object copy = value;

        if (isNodeDom(value)) {
            copy = getValue(value);
        }

        return (String) copy;
    }

    public String getValue(Object value) {
        return ((Param) value).getValue();
    }

    public Boolean isNodeDom(Object value) {
        return value instanceof Param;
    }
}
