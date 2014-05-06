package com.rayjars.fieldmapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class ParamFieldParserTest {

    private ParamFieldParser parser;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void createParser(){
        parser = new ParamFieldParser();
    }

    @Test
    public void shouldParseList() throws Exception {
        Param parameters = new Param("parameters")
                .addChild("param", "value1")
                .addChild("param", "value2");

        List<String> list = parser.parseList(parameters);

        assertThat(list, hasSize(2));
        assertThat(list, contains("value1", "value2"));
    }

    @Test
    public void shouldNotParseList() throws Exception {
        exception.expect(InvalidFieldValueException.class);
        parser.parseList(new Param("parameters"));
    }

    @Test
    public void shouldParseMap() throws Exception {
        Param parameters = new Param("parameters")
                .addChild("key1", "value1")
                .addChild("key2", "value2");

        Map<String, String> map = parser.parseMap(parameters);

        assertThat(map.size(), is(2));
        assertThat(map.keySet(), containsInAnyOrder("key1", "key2"));
        assertThat(map.values(), containsInAnyOrder("value1", "value2"));
    }

    @Test
    public void shouldNotParseMap() throws Exception {
        exception.expect(InvalidFieldValueException.class);
        parser.parseMap(new Param("parameters"));
    }
}
