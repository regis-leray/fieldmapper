package com.rayjars.fieldmapper;

import java.io.Serializable;
import java.util.*;


public class Param implements Serializable {

    protected String name;

    protected String value;

    protected final List<Param> childList;

    protected final Map<String, Param> childMap;

    public Param(String name) {
        this.name = name;
        childList = new ArrayList<>();
        childMap = new HashMap<>();
    }

    public Param(String name, String value){
        this(name);
        setValue(value);
    }

    public Param(String name, Properties properties) {
        this(name);
        load(properties);
    }

    public Param load(Properties properties) {
        for (Map.Entry entry : properties.entrySet()) {
            this.putChild((String) entry.getKey(), (String) entry.getValue());
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public Param setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Param setValue(String value) {
        this.value = value;
        return this;
    }

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------
    public Param putChild(String name, String value){
        Param child = getChild(name);

        if(child == null){
            child = new Param(name,value);
            addChild(child);
        }else{
            child.setValue(value);
        }

        return this;
    }

    public Param getChild(String name) {
        return childMap.get(name);
    }

    public Param addChild(String name, String value) {
        return addChild(new Param(name).setValue(value));
    }

    public Param addChild(Param child) {
        childList.add(child);
        childMap.put(child.getName(), child);
        return this;
    }

    public List<Param> getChilds() {
        return Collections.unmodifiableList(childList);
    }

    public Boolean containsNoChilds() {
        return getChilds().isEmpty();
    }

}
