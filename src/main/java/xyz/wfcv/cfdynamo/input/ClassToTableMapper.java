package xyz.wfcv.cfdynamo.input;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassToTableMapper {
    private DynamoDB db;
    private JavaClass javaClass;
    private String tableName;
    private List<KeySchemaElement> keys;
    private List<AttributeDefinition> attributes;

    public ClassToTableMapper(DynamoDB db, JavaClass javaClass) {
        this.db = db;
        this.javaClass = javaClass;
        keys = new ArrayList<>();
        attributes = new ArrayList<>();

        // set table attributes
        setTableName();
        setKeySchema();
    }

    private void setTableName() {
        tableName = Arrays.stream(javaClass.getAnnotationEntries())
                .filter(it -> it.getAnnotationType().endsWith("DynamoDBTable;"))
                .map(it -> it.getElementValuePairs()[0].getValue().stringifyValue())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No DynamoDbTable annotation found"));
    }

    private void setKeySchema() {
        var annotatedMethods = getAnnotatedMethods();
        // TODO: finish this here
    }

    private List<Method> getAnnotatedMethods() {
        return Arrays.stream(javaClass.getMethods())
                .filter(m -> Arrays.stream(m.getAttributes())
                    .anyMatch(a -> a.toString().equals("RuntimeVisibleAnnotations"))
                )
                .collect(Collectors.toList());
    }
}
