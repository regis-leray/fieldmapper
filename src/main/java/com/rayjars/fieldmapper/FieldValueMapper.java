package com.rayjars.fieldmapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Regis Leray
 */
public class FieldValueMapper {

    private ParamFieldParser parser;

    private Logger logger = LoggerFactory.getLogger(FieldValueMapper.class);

    private boolean throwExceptionInvalidValueParameter = true;

    public FieldValueMapper() {
        setParser(new ParamFieldParser());
    }

    public void setParser(ParamFieldParser parser) {
        this.parser = parser;
    }


    private void displayDebug(String msg, Object... args) {
        logger.debug("{}: {}", Field.class.getSimpleName(), String.format("%s -> %s", (Object[]) args));
    }

    public void mapParamToObject(Properties properties, Object instance) throws InvalidFieldException {
        mapParamToObject(new Param("root", properties), instance);
    }


    /**
     * Maps the values of the Properties instance to instance.
     * The object's fields are mapped if they are annotated via <code>@eu.rbecker.propertymapper.annotation.MappedProperty("propertyName")</code> to the corresponding property.
     * The value of the annotation must match the name of the property in the Properties object parameters.
     * <br/>
     * Supported field types: String, Long, Integer, Double, Float, Boolean and their primitive equivalents.
     *
     * @param parameters
     * @param instance
     * @throws IllegalAccessException
     */
    public void mapParamToObject(Param parameters, Object instance) throws InvalidFieldException {
        displayDebug("Mapping %s to object %s", parameters, instance);

        //We need to retreive the target object instance and not the proxy
        Object unwrapObject = instance; //SimpleReflectionUtils.unwrapProxy(instance);

        for (java.lang.reflect.Field field : unwrapObject.getClass().getDeclaredFields()) {
            FieldObject fieldObject = new FieldObject(unwrapObject, field, parameters);

            if (fieldObject.isAnnotationExist()) {
                // get value from properties
                displayDebug("%s -> %s", field.getName(), fieldObject.getValue());

                if(fieldObject.validate()){
                    assignValueWrapException(fieldObject);
                }
            }
        }
    }

    private void assignValueWrapException(FieldObject fieldObject) throws InvalidFieldException {
        try {
            assignValueToField(fieldObject);
        } catch (IllegalAccessException | ParseException | MalformedURLException e) {
            throw new InvalidFieldException("Cannot assign value to the parameter "+ fieldObject.getName()+". Cause by "+e.getMessage(), e);
        }  catch(InvalidFieldValueException e){
            if(throwExceptionInvalidValueParameter) {
                throw new InvalidFieldException("The parameter " + fieldObject.getName() + " is required and cannot assign value. Cause by " + e.getMessage(), e);
            }
        }
    }

    private void assignValueToField(FieldObject fieldObject) throws InvalidFieldException, IllegalAccessException, MalformedURLException, ParseException {

        // enable property access
        boolean oldAccess = fieldObject.field.isAccessible();
        fieldObject.field.setAccessible(true);
        // set value in object
        Class<?> fieldType = fieldObject.field.getType();

        if (fieldType.equals(String.class) || fieldObject.getValue() == null) {
            fieldObject.set(parser.parseString(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(Long.class) || fieldType.isAssignableFrom(long.class)) {
            fieldObject.set(parser.parseLong(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)) {
            fieldObject.set(parser.parseInt(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(Float.class) || fieldType.isAssignableFrom(float.class)) {
            fieldObject.set(parser.parseFloat(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(Double.class) || fieldType.isAssignableFrom(double.class)) {
            fieldObject.set(parser.parseDouble(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)) {
            fieldObject.set(parser.parseBoolean(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(Date.class)) {
            // "yyyy-MM-dd HH:mm:ss.S a" (a sample date is "2005-10-06 2:22:55.1 PM")
            //  or "yyyy-MM-dd HH:mm:ssa" (a sample date is "2005-10-06 2:22:55PM")
            fieldObject.set(parser.parseDate(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(File.class)) {
            fieldObject.set(parser.parseFile(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(URL.class)) {
            fieldObject.set(parser.parseUrl(fieldObject.getValue()));
        } else if (fieldType.isArray()) {
            fieldObject.set(parser.parseArray(fieldObject.getValue()));
        }else if (fieldType.isAssignableFrom(List.class)) {
            fieldObject.set(parser.parseList(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(Map.class)) {
            fieldObject.set(parser.parseMap(fieldObject.getValue()));
        } else if (fieldType.isAssignableFrom(Color.class)) {
            fieldObject.set(parser.parseColor(fieldObject.getValue()));
        }else if (fieldType.isAssignableFrom(Font.class)) {
            fieldObject.set(parser.parseFont(fieldObject.getValue()));
        } else {
            throw new InvalidFieldException("Type " + fieldType + " not available for property mapping.");
        }

        // revert write access
        fieldObject.field.setAccessible(oldAccess);
    }

    public void setThrowExceptionInvalidValueParameter(boolean b) {
        this.throwExceptionInvalidValueParameter = b;
    }


    private class FieldObject {
        private final Param params;
        private final java.lang.reflect.Field field;
        private final Field annotation;
        private final Object annotatedObject;

        private FieldObject(Object annotatedObject, java.lang.reflect.Field field, Param params) {
            this.annotatedObject = annotatedObject;
            this.field = field;
            this.params = params;
            //Need to use annotation provided by spring
            annotation = field.getAnnotation(Field.class);
        }

        public String getDefaultValue(){
            return annotation.defaultValue().isEmpty() ? null : annotation.defaultValue();
        }

        public Object getValue() {
           Param paramValue = params.getChild(getName());
           return paramValue != null ? paramValue : getDefaultValue() ;
        }

        public String getName() {
            return annotation.name().isEmpty() ? field.getName() : annotation.name();
        }

        public Boolean isAnnotationExist(){
            return annotation != null;
        }

        /**
         * @throws InvalidFieldException if the parameter is required and there is no value provided
         */
        public boolean validate() throws InvalidFieldException {
            if(isRequired() && getValue() == null){
                throw new InvalidFieldException("The parameter "+getName()+" is required. Cannot be null");
            }

            return getValue() != null;
        }

        private boolean isRequired() {
            return annotation.required();
        }


        public void set(Object value) throws IllegalAccessException {
            field.set(annotatedObject, value);
        }
    }
}