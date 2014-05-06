package com.rayjars.fieldmapper;

import com.rayjars.fieldmapper.utils.SimpleFileUtils;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class StringFieldParser {
    public static final String ONLY_DATE = "yyyy-MM-dd";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ssa";
    public static final String DATE_MILLSECONDS_FORMAT = "yyyy-MM-dd HH:mm:ss.S a";

    public static final String REGEX_MAP = ",|=";

    public Long parseLong(String value){
        return Long.parseLong(value);
    }

    public String parseString(String value) {
        return value;
    }

    public Double parseDouble(String value){
        return Double.parseDouble(value);
    }

    public Boolean parseBoolean(String value){
        return Boolean.parseBoolean(value);
    }

    public Integer parseInteger(String value){
        return Integer.parseInt(value);
    }

    public Float parseFloat(String value) {
        return Float.parseFloat(value);
    }

    public Date parseDate(String str) throws ParseException {
        return parseDate(new String[]{DATE_FORMAT, DATE_MILLSECONDS_FORMAT, ONLY_DATE}, str);
    }

    public Date parseDate(String[] formats, String str) throws ParseException {
        ParseException current = null;
        SimpleDateFormat sdf = new SimpleDateFormat();

        for (String format : formats) {
            sdf.applyPattern(format);

            try {
                return sdf.parse(str);
            } catch (ParseException e) {
                current = e;
            }
        }
        throw current;
    }


    public File parseFile(String value){
        String path = SimpleFileUtils.separatorsToSystem(value);
        return new File(path);
    }

    public URL parseUrl(String value) throws MalformedURLException {
        return new URL(value);
    }



    public String[] parseArray(String str) {
        return str.split(",");
    }



    public List<String> parseList(String str) {
        return Arrays.asList(parseArray(str));
    }

    /**
     * format key=value,key=value
     * A=4,H=X,PO=87
     *
     *
     * @param str
     * @return
     */
    public Map<String, String> parseMap(String str) {
        String[] tokens = str.split(REGEX_MAP);
        Map<String, String> map = new HashMap<>();
        for (int i=0; i< tokens.length-1; ) {
            String key = tokens[i++];
            String value = tokens[i++];
            map.put(key, value);
        }
        return map;
    }

    public Color parseColor(String color){
        return Color.decode(color);
    }

    public Font parseFont(String font){
        return Font.decode(font);
    }

}