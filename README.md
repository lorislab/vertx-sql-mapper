# vertx-sql-mapper

[![License](https://img.shields.io/github/license/lorislab/vertx-sql-mapper?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/lorislab/vertx-sql-mapper/build/master?logo=github&style=for-the-badge)](https://github.com/lorislab/vertx-sql-mapper/actions?query=workflow%3Abuild)
[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/lorislab/vertx-sql-mapper?sort=semver&logo=github&style=for-the-badge)](https://github.com/lorislab/vertx-sql-mapper/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/org.lorislab.vertx/vertx-sql-mapper?logo=java&style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.lorislab.vertx/vertx-sql-mapper)

Maven dependency:
```xml
<dependency>
    <groupId>org.lorislab.vertx</groupId>
    <artifactId>vertx-sql-mapper</artifactId>
    <version>{latest-release-version}</version>
</dependency>
``` 

Vertx SQL mapper for `io.vertx.mutiny.sqlclient.Row` and `io.vertx.sqlclient.Row`
Default configuration generate CDI bean. To generate only the POJO class use this configuration:

```java
@SqlMapper(cdi = false, instanceField = true, staticMethod = true)
public interface MyMapper {

}
```

> No runtime dependencies are created in the generated classes.

Define SQL mapper `interface` or `abstract class`
 ```java
@SqlMapper
public interface SimpleMapper {

    Model map(Row row);
}
```
Create a model class which will represent the `row` result.
```java
public class Model {

    public String id;

    public Integer version;
}
```
Compile your project, and you get meta model class `Model_.java`
```java
public interface Model_ {

    String ID = "id";

    String VERSION = "version";
}
```
The SQL mapper implementation `SimpleMapperImpl`
```java
public class SimpleMapperImpl implements SimpleMapper {

    public static final SimpleMapper INSTANCE = new SimpleMapperImpl();

    @Override
    public Model map(Row row) {
        if (row == null) {
            return null;
        }
        Model result = new Model();
        result.id = row.getString(Model_.ID);
        result.version = row.getInteger(Model_.VERSION);
        return result;
    }

    public static Model mapS(Row row) {
        return INSTANCE.map(row);
    }

}
```
