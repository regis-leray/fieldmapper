package com.rayjars.fieldmapper;

import com.rayjars.fieldmapper.utils.SimpleFileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.co.it.modular.hamcrest.date.IsSameInstant.sameInstant;


public class FieldMapperTest {

    private Param parameters;
    private FieldValueMapper mapper;
    private AnnotatedObject annotatedObject;
    private AnnotatedObjectWithRequired annotatedObjectWithRequired;


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void createProperties() {
        parameters = new Param("parameters");
        annotatedObject = new AnnotatedObject();
        annotatedObjectWithRequired = new AnnotatedObjectWithRequired();

        mapper = new FieldValueMapper();

    }

    @Test
    public void shouldInjectInteger() throws InvalidFieldException {
        parameters.addChild("test.int", String.valueOf(Integer.MAX_VALUE));
        parameters.addChild("test.Integer", String.valueOf(Integer.MAX_VALUE));

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestPrimitiveInt(), is(Integer.MAX_VALUE));
        assertThat(annotatedObject.getTestInteger(), is(Integer.MAX_VALUE));
        assertThat(annotatedObject.getTestDefaultInteger(), is(1));

    }

    @Test
    public void shouldInjectFloat() throws InvalidFieldException {
        parameters.addChild("test.float", String.valueOf(Float.MAX_VALUE));
        parameters.addChild("test.Float", String.valueOf(Float.MAX_VALUE));

        mapper.mapParamToObject(parameters, annotatedObject);


        assertThat(annotatedObject.getTestFloat(), is(Float.MAX_VALUE));
        assertThat(annotatedObject.getTestPrimitiveFloat(), is(Float.MAX_VALUE));
        assertThat(annotatedObject.getTestDefaultFloat(), is(0.1f));
    }

    @Test
    public void shouldInjectLong() throws InvalidFieldException {

        parameters.addChild("test.long", String.valueOf(Long.MAX_VALUE));
        parameters.addChild("test.Long", String.valueOf(Long.MAX_VALUE));

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestLong(), is(Long.MAX_VALUE));
        assertThat(annotatedObject.getTestPrimitiveLong(), is(Long.MAX_VALUE));
        assertThat(annotatedObject.getTestDefaultLong(), is(1L));

    }

    @Test
    public void shouldInjectDouble() throws InvalidFieldException {
        parameters.addChild("test.Double", String.valueOf(Double.MAX_VALUE));
        parameters.addChild("test.double", String.valueOf(Double.MAX_VALUE));

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestDouble(), is(Double.MAX_VALUE));
        assertThat(annotatedObject.getTestPrimitiveDouble(), is(Double.MAX_VALUE));
        assertThat(annotatedObject.getTestDefaultDouble(), is(0.1d));
    }

    @Test
    public void shouldInjectBoolean() throws InvalidFieldException {
        parameters.addChild("test.Boolean", String.valueOf(Boolean.TRUE));
        parameters.addChild("test.boolean", String.valueOf(Boolean.TRUE));

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestBoolean(), is(Boolean.TRUE));
        assertThat(annotatedObject.getTestPrimitiveBoolean(), is(Boolean.TRUE));
        assertThat(annotatedObject.getTestDefaultBoolean(), is(Boolean.TRUE));
    }

    @Test
    public void shouldInjectString() throws InvalidFieldException {
        parameters.addChild("test.String", "hello");

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestString(), is("hello"));
        assertThat(annotatedObject.getTestDefaultString(), is("Hello World"));
    }

    @Test
    public void shouldInjectDate() throws InvalidFieldException, ParseException {
        parameters.addChild("testDate", "2005-10-06 2:22:55.1 PM");

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestDate(), sameInstant(new SimpleDateFormat(StringFieldParser.DATE_MILLSECONDS_FORMAT).parse("2005-10-06 2:22:55.1 PM")));
        assertThat(annotatedObject.getTestDefaultDate(), sameInstant(new SimpleDateFormat(StringFieldParser.DATE_FORMAT).parse("2005-10-06 2:22:55PM")));
    }

    @Test
    public void shouldInjectOnlyDate() throws InvalidFieldException, ParseException {
        parameters.addChild("testDate", "2005-10-06");

        mapper.mapParamToObject(parameters, annotatedObject);
        assertThat(annotatedObject.getTestDate(), sameInstant(new SimpleDateFormat(StringFieldParser.ONLY_DATE).parse("2005-10-06")));
    }


    @Test
    public void shouldThrowExceptionIfInvalidDate() throws InvalidFieldException {
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectCause(isA(ParseException.class));

        parameters.addChild("testDate", "2005-10-06 2:22:55.1 RWE");
        mapper.mapParamToObject(parameters, annotatedObject);

    }

    @Test
    public void shouldInjectColor() throws InvalidFieldException {
        parameters.addChild("testColor", "#000000");

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestColor(), is(Color.decode("#000000")));
        assertThat(annotatedObject.getTestDefaultColor(), is(Color.decode("#FFFFFF")));
    }

    @Test
    public void shouldInjectFont() throws InvalidFieldException {
        parameters.addChild("testFont", "Arial-BOLDITALIC-14");

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestFont(), is(Font.decode("Arial-BOLDITALIC-14")));
        assertThat(annotatedObject.getTestDefaultFont(), is(Font.decode("Courier-PLAIN-24")));
    }


    @Test
    public void shouldInjectFile() throws InvalidFieldException {
        parameters.addChild("testFile", "c:/temp");

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestFile().getPath(), is(SimpleFileUtils.separatorsToSystem("c:/temp")));
        assertThat(annotatedObject.getTestDefaultFile().getPath(), is(SimpleFileUtils.separatorsToSystem("/home/joe/documents/test.txt")));
    }

    @Test
    public void shouldInjectUrl() throws InvalidFieldException {
        parameters.addChild("testUrl", "ftp://gwashing1234@samples.oakton.edu");

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestUrl().getHost(), is("samples.oakton.edu"));
        assertThat(annotatedObject.getTestDefaultUrl().getHost(), is("www.askida.com"));
    }

    @Test
    public void shouldThrowExceptionIfInvalidUrl() throws InvalidFieldException {
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectCause(isA(MalformedURLException.class));

        parameters.addChild("testUrl", "http58://www.samples.oakton.edu");

        mapper.mapParamToObject(parameters, annotatedObject);
    }

    @Test
    public void shouldInjectStringWithRequired() throws InvalidFieldException {
        parameters.addChild("myString", "Java Rocks!");
        parameters.addChild("test.String", "hello");
        parameters.addChild(new Param("testList").addChild("param", "value1"));
        parameters.addChild(new Param("testMap").addChild("key1", "value1"));

        mapper.mapParamToObject(parameters, annotatedObjectWithRequired);

        assertThat(annotatedObjectWithRequired.getMyString(), is("Java Rocks!"));
        assertThat(annotatedObjectWithRequired.getTestString(), is("hello"));
        assertThat(annotatedObjectWithRequired.getTestDefaultString(), is("Hello World"));
        assertThat(annotatedObjectWithRequired.getTestList(), hasSize(1));
        assertThat(annotatedObjectWithRequired.getTestMap().size(), is(1));
    }

    @Test
    public void shouldThrowExceptionInjectStringWithRequired() throws InvalidFieldException {
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectMessage(containsString("The parameter test.String is required. Cannot be null"));

        mapper.mapParamToObject(parameters, annotatedObjectWithRequired);
    }


    @Test
    public void shouldThrowExceptionInjectListWithRequired() throws InvalidFieldException {
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectMessage(containsString("The parameter testList is required. Cannot be null"));

        parameters.addChild("test.String", "Java Rocks!");
        mapper.mapParamToObject(parameters, annotatedObjectWithRequired);
    }

    @Test
    public void shouldThrowExceptionInjectListWithRequiredAndWrongType() throws InvalidFieldException{
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectMessage(containsString("The parameter testList is required and cannot assign value."));

        parameters.addChild("test.String", "Java Rocks!");
        parameters.addChild("testList", "MyValue");
        mapper.mapParamToObject(parameters, annotatedObjectWithRequired);
    }


    @Test
    public void shouldThrowExceptionInjectMapWithRequired() throws InvalidFieldException {
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectMessage(containsString("The parameter testMap is required."));

        parameters.addChild("test.String", "Java Rocks!");
        parameters.addChild(new Param("testList").addChild("param", "value1"));

        mapper.mapParamToObject(parameters, annotatedObjectWithRequired);
    }


    @Test
    public void shouldThrowExceptionInjectMapWithRequiredWithWrongType() throws InvalidFieldException {
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectMessage(containsString("The parameter testMap is required and cannot assign value."));

        parameters.addChild("test.String", "Java Rocks!");
        parameters.addChild(new Param("testList").addChild("param", "value1"));
        parameters.addChild("testMap", "MyValue");
        mapper.mapParamToObject(parameters, annotatedObjectWithRequired);
    }


    @Test
    public void shouldInjectArrayOfString() throws InvalidFieldException {
        Param list = new Param("testArray");
        list.addChild("param", "Java");
        list.addChild("param", "Rocks!");
        list.addChild("param", "Like");
        list.addChild("param", "hell");

        parameters.addChild(list);

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestArray(), arrayContaining("Java","Rocks!","Like","hell"));
        assertThat(annotatedObject.getTestDefaultArray(), arrayContaining("joe","dan","mike"));
    }

    @Test
    public void shouldInjectArrayListOfString() throws InvalidFieldException {
        Param list = new Param("testList");
        list.addChild("param", "Java");
        list.addChild("param", "Rocks!");
        list.addChild("param", "Like");
        list.addChild("param", "hell");

        parameters.addChild(list);

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestList(), contains("Java","Rocks!","Like","hell"));
        assertThat(annotatedObject.getTestDefaultList(), contains("joe","dan","mike"));

    }

    @Test
    public void shouldThrowExceptionWhenInjectArrayListOfStringWithOneElement() throws InvalidFieldException {
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectCause(is(InvalidFieldValueException.class));

        parameters.addChild(new Param("testList","Java" ));

        mapper.mapParamToObject(parameters, annotatedObject);
    }

    @Test
    public void shouldNotThrowExceptionWhenInjectArrayListOfStringWithOneElement() throws InvalidFieldException {
        parameters.addChild(new Param("testList","Java" ));

        mapper.setThrowExceptionInvalidValueParameter(false);
        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestList(), nullValue());
    }

    @Test
    public void shouldInjectMapOfString() throws InvalidFieldException {
        Param map = new Param("testMap");
        map.addChild("key1", "joe");
        map.addChild("key2", "dan");
        map.addChild("key3", "mike");
        parameters.addChild(map);


        Map<String,String> valueExpected = new HashMap<>();
        valueExpected.put("key1", "joe");
        valueExpected.put("key2", "dan");
        valueExpected.put("key3", "mike");

        mapper.mapParamToObject(parameters, annotatedObject);

        assertThat(annotatedObject.getTestMap(), equalTo(valueExpected));
        assertThat(annotatedObject.getTestDefaultMap(), equalTo(valueExpected));
    }

    @Test
    public void shouldThrowExceptionIfTypeNotSupported() throws InvalidFieldException {
        expectedException.expect(InvalidFieldException.class);
        expectedException.expectMessage(containsString("Type class java.lang.Math not available for property mapping."));

        parameters.addChild("math", String.valueOf(Math.PI));

        mapper.mapParamToObject(parameters, new AnnotatedObjectNotSupported());

    }

    @Test
    public void shouldDoNothing() throws InvalidFieldException {
        parameters.addChild("test.String", "hello");
        mapper.mapParamToObject(parameters, new EmptyObject());
    }

    @Test
    public void shouldInjectProperties() throws InvalidFieldException {
        Properties properties = new Properties();
        properties.put("test.String", "Java Dude");
        properties.put("test.Integer", "10");

        mapper.mapParamToObject(properties, annotatedObject);

        assertThat(annotatedObject.getTestString(), is("Java Dude"));
        assertThat(annotatedObject.getTestInteger(), is(10));
    }


    private class EmptyObject {

    }

    private class AnnotatedObjectNotSupported {
        @Field
        private Math math;
    }

    private class AnnotatedObjectWithRequired {

        @Field
        private String myString;

        @Field(name = "test.String", required = true)
        private String testString;
        @Field(name = "test.defaultString", required = true, defaultValue = "Hello World")
        private String testDefaultString;

        @Field(required = true)
        private List<String> testList;

        @Field(required = true)
        private Map<String, String> testMap;

        public List<String> getTestList() {
            return testList;
        }

        public Map<String, String> getTestMap() {
            return testMap;
        }

        public String getTestString() {
            return testString;
        }

        public String getMyString() {
            return myString;
        }

        public String getTestDefaultString() {
            return testDefaultString;
        }
    }


    private class AnnotatedObject {

        @Field(name = "test.String")
        private String testString;
        @Field(name = "test.defaultString", defaultValue = "Hello World")
        private String testDefaultString;

        @Field(name = "test.int")
        private int testPrimitiveInt;
        @Field(name ="test.Integer")
        private Integer testInteger;
        @Field(name ="test.defaultInteger", defaultValue = "1")
        private Integer testDefaultInteger;

        @Field(name ="test.long")
        private long testPrimitiveLong;
        @Field(name ="test.Long")
        private Long testLong;
        @Field(name ="test.defaultLong", defaultValue = "1")
        private Long testDefaultLong;

        @Field(name ="test.float")
        private float testPrimitiveFloat;
        @Field(name ="test.Float")
        private Float testFloat;
        @Field(name ="test.defaultFloat", defaultValue = "0.1")
        private Float testDefaultFloat;

        @Field(name ="test.double")
        private double testPrimitiveDouble;
        @Field(name ="test.Double")
        private Double testDouble;
        @Field(name ="test.defaultDouble", defaultValue = "0.1")
        private Double testDefaultDouble;

        @Field(name ="test.boolean")
        private boolean testPrimitiveBoolean;
        @Field(name ="test.Boolean")
        private Boolean testBoolean;
        @Field(name ="test.defaultBoolean", defaultValue = "true")
        private Boolean testDefaultBoolean;

        @Field
        private Date testDate;
        @Field(defaultValue = "2005-10-06 2:22:55PM")
        private Date testDefaultDate;

        @Field
        private File testFile;
        @Field(defaultValue = "/home/joe/documents/test.txt")
        private File testDefaultFile;

        @Field
        private URL testUrl;
        @Field(defaultValue = "http://www.askida.com")
        private URL testDefaultUrl;

        @Field
        private Font testFont;
        @Field(defaultValue = "Courier-PLAIN-24")
        private Font testDefaultFont;

        @Field
        private Color testColor;
        @Field(defaultValue = "#FFFFFF")
        private Color testDefaultColor;

        @Field
        private String[] testArray;
        @Field(defaultValue = "joe,dan,mike")
        private String[] testDefaultArray;

        @Field
        private List<String> testList;
        @Field(defaultValue = "joe,dan,mike")
        private List<String> testDefaultList;

        @Field
        private Map<String, String> testMap;
        @Field(defaultValue = "key1=joe,key2=dan,key3=mike")
        private Map<String, String> testDefaultMap;

        public Font getTestFont() {
            return testFont;
        }

        public Font getTestDefaultFont() {
            return testDefaultFont;
        }

        public Color getTestColor() {
            return testColor;
        }

        public Color getTestDefaultColor() {
            return testDefaultColor;
        }

        public String[] getTestArray() {
            return testArray;
        }

        public String[] getTestDefaultArray() {
            return testDefaultArray;
        }

        public List<String> getTestList() {
            return testList;
        }

        public List<String> getTestDefaultList() {
            return testDefaultList;
        }

        public Map<String,String> getTestMap() {
            return testMap;
        }

        public Map<String,String> getTestDefaultMap() {
            return testDefaultMap;
        }

        public String getTestString() {
            return testString;
        }

        public int getTestPrimitiveInt() {
            return testPrimitiveInt;
        }

        public Integer getTestInteger() {
            return testInteger;
        }

        public long getTestPrimitiveLong() {
            return testPrimitiveLong;
        }

        public Long getTestLong() {
            return testLong;
        }

        public float getTestPrimitiveFloat() {
            return testPrimitiveFloat;
        }

        public Float getTestFloat() {
            return testFloat;
        }

        public double getTestPrimitiveDouble() {
            return testPrimitiveDouble;
        }

        public Double getTestDouble() {
            return testDouble;
        }

        public boolean getTestPrimitiveBoolean() {
            return testPrimitiveBoolean;
        }

        public Boolean getTestBoolean() {
            return testBoolean;
        }

        public Integer getTestDefaultInteger() {
            return testDefaultInteger;
        }

        public Long getTestDefaultLong() {
            return testDefaultLong;
        }

        public Float getTestDefaultFloat() {
            return testDefaultFloat;
        }

        public Double getTestDefaultDouble() {
            return testDefaultDouble;
        }

        public Boolean getTestDefaultBoolean() {
            return testDefaultBoolean;
        }

        public String getTestDefaultString() {
            return testDefaultString;
        }

        public Date getTestDate() {
            return testDate;
        }

        public Date getTestDefaultDate() {
            return testDefaultDate;
        }

        public File getTestFile() {
            return testFile;
        }

        public File getTestDefaultFile() {
            return testDefaultFile;
        }

        public URL getTestUrl() {
            return testUrl;
        }

        public URL getTestDefaultUrl() {
            return testDefaultUrl;
        }
    }

}
