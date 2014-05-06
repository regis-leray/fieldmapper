package com.rayjars.fieldmapper;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.co.it.modular.hamcrest.date.IsSameInstant.sameInstant;


public class StringFieldParserTest {

    private StringFieldParser parser;

    @Before
    public void createParser(){
        parser = new StringFieldParser();
    }

    @Test
    public void testParseDate() throws Exception {
        assertThat(parser.parseDate("2005-10-06 2:22:55.1 PM"), sameInstant(new SimpleDateFormat(StringFieldParser.DATE_MILLSECONDS_FORMAT).parse("2005-10-06 2:22:55.1 PM")));
    }

    @Test
    public void testParseDateWithCustomFormat() throws Exception {
        assertThat(parser.parseDate(new String[]{"dd-MM-yyyy HH:mm"}, "06-10-2005 14:02"), sameInstant(new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("06-10-2005 14:02")));
    }

    @Test
    public void testParseArray() throws Exception {
       assertThat(parser.parseArray("1,2,3"), arrayContaining("1", "2", "3"));
    }

    @Test
    public void testParseFile() throws Exception {
        File file = parser.parseFile(System.getProperty("java.io.tmpdir"));
        assertThat(file, notNullValue());
        assertThat(file, hasProperty("absolutePath", notNullValue()));
    }

    @Test
    public void testParseUrl() throws Exception {
        URL url = parser.parseUrl("http://www.google.ca");
        assertThat(url, notNullValue());
        assertThat(url, hasProperty("host", is("www.google.ca")));
    }

    @Test
    public void testParseList() throws Exception {
        assertThat(parser.parseList("1,2,3"), contains("1", "2", "3"));
    }

    @Test
    public void testParseMap() throws Exception {
        Map<String,String> valueExpected = new HashMap<>();
        valueExpected.put("key1", "1");
        valueExpected.put("key2", "2");
        valueExpected.put("key3", "3");

        assertThat(parser.parseMap("key1=1,key2=2,key3=3"), equalTo(valueExpected));
    }

    @Test
    public void testParseColor(){
        assertThat(parser.parseColor("#D06000"), equalTo(Color.decode("#D06000")));
    }

    @Test
    public void testParseFont(){
        assertThat(parser.parseFont("Courier-PLAIN-24"), equalTo(Font.decode("Courier-PLAIN-24")));
    }

}
