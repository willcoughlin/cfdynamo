package xyz.wfcv.cfdynamo.input;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

public class CreateTableRequestFactory {
    private CreateTableRequestFactory() {}

    public static CreateTableRequest createWithTableClass(AmazonDynamoDB ddb, Class tableClass) {
        var mapper = new DynamoDBMapper(ddb);
        var request = mapper.generateCreateTableRequest(tableClass);
        request.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
        return request;
    }
}
