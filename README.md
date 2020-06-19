# code-first-dynamodb
Java code-first DynamoDB table creation

## Prerequisites
 - AWS credentials and region must be configured on your local file system. The recommended way is to [install AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html). Otherwise you can [configure manually](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/setup-credentials.html).
 - [Apache Maven 3+](https://maven.apache.org/install.html)

## Building the JAR
From the root of the project (where *pom.xml* is located), run:
```
mvn install
```

## Input
Input must be a compiled Java class file, or a directory containing one or more class files. The source code for the input file(s) must be annotated with DynamoDB annotations (see [here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.Annotations.html) for available annotations).

### Sample Class
```java
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import java.util.Set;

@DynamoDBTable(tableName = "TestTable")
public class TestClass {

    private Long key;
    private double rangeKey;
    private Long version;

    private Set<Integer> integerSetAttribute;

    @DynamoDBHashKey
    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    @DynamoDBRangeKey
    public double getRangeKey() {
        return rangeKey;
    }

    public void setRangeKey(double rangeKey) {
        this.rangeKey = rangeKey;
    }

    @DynamoDBAttribute(attributeName = "integerSetAttribute")
    public Set<Integer> getIntegerAttribute() {
        return integerSetAttribute;
    }

    public void setIntegerAttribute(Set<Integer> integerAttribute) {
        this.integerSetAttribute = integerAttribute;
    }

    @DynamoDBVersionAttribute
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
```
[Source](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/datamodeling/DynamoDBMapper.html)

## Example Usage
### Input a Single File
This example demonstrates running the tool with a compiled class called *Entity* as its input.
```
java -jar cfdynamo.jar Entity.class
```

### Input a Directory Containing Class Files
This example demonstrates running the tool with a directory called *entities* as its input. Note the `-d` flag.
```
java -jar cfdynamo.jar -d entities
```
## Limitations
- There is a limitation with the AWS Java SDK method `generateCreateTableRequest` as described below:
  >Note that [...] all secondary indexes are initialized with the default projection type - KEY_ONLY
  
  [Source](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/datamodeling/AbstractDynamoDBMapper.html#generateCreateTableRequest-java.lang.Class-)
- Additionally, it is impossible to supply Java source files as input. It would be nice to have this capability in the future.
