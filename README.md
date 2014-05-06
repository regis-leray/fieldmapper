fieldmapper [![Build Status](https://travis-ci.org/regis-leray/fieldmapper.png?branch=master)](https://travis-ci.org/regis-leray/fieldmapper)
===========

Inject values into an annotated object

### Exemple with a Simple Object and Properties

Create your object with the Field annotation
```
private class User {
        @Field
        private String firstName;

        @Field(name = "name")
        private String lastName;

        @Field(name = "birthDate", required = true)
        private Date date;

        @Field
        private List<String> categories;
}
```

Instanciate a mapper and inject values

```
    Properties properties = new Properties();
    properties.put("firstName", "Steeve");
    properties.put("lastName", "Jobs"); // and not lastName
    properties.put("birthDate", "2005-10-06"); //need to respect the pattern yyyy-MM-dd or yyyy-MM-dd HH:mm:ssa or yyyy-MM-dd HH:mm:ss.S a

    User user = new User();
    new FieldMapper().mapParamToObject(properties, user)

```

### Exemple with a Simple Object and Param Object (holder of tree value) handle complex object (Array, List, Map)

```
    Param properties = new Param("root")
    .addChild("firstName", "Steeve");
    .addChild("lastName", "Jobs"); // and not lastName
    .addChild("birthDate", "2005-10-06"); //need to respect the pattern yyyy-MM-dd or yyyy-MM-dd HH:mm:ssa or yyyy-MM-dd HH:mm:ss.S a
    .addchild(new Param("categories").addChild("cat", "apple").addChild("cat", "technology"));

    User user = new User();
    new FieldMapper().mapParamToObject(properties, user)

```