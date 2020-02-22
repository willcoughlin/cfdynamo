package xyz.wfcv.cfdynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.apache.commons.cli.*;
import xyz.wfcv.cfdynamo.input.CreateTableRequestFactory;
import xyz.wfcv.cfdynamo.input.FileUtils;

import java.util.List;

public class App {
    public static void main(String[] args) {
        new App(args).Run();
    }

    private List<String> targetFilePaths;
    private AmazonDynamoDB dbClient;

    private App(String[] args) {
        var options = new Options();
        options.addOption("d", false,
                "Treat input as a directory from which to input all class files");

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        if (cmd.getArgs().length == 0) {
            new HelpFormatter().printHelp("cfdynamo [option] input", options);
            System.exit(0);
        }

        // validate input
        var arg = cmd.getArgs()[0];
        try {
            targetFilePaths = FileUtils.validateAndGetAbsolutePaths(arg, cmd.hasOption("d"));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Target files discovered: ");
        targetFilePaths.forEach(it -> System.out.println("\t" + it));

        // create DynamoDB connection
        dbClient = AmazonDynamoDBClientBuilder.standard().build();
        System.out.println("Instantiated with connection to DynamoDB.");
    }

    private void Run() {
        System.out.println("Processing target files...");

        if (targetFilePaths == null || targetFilePaths.isEmpty()) {
            System.err.println("No class files have been discovered yet");
            System.exit(1);
        }

        var classes = FileUtils.loadClassesFromFiles(targetFilePaths);

        if (classes == null) {
            System.err.println("Failed to load Classes from input file(s)");
            System.exit(1);
        }

        for (var tableClass : classes) {
            var request = CreateTableRequestFactory.createWithTableClass(dbClient, tableClass);
            dbClient.createTable(request);
            System.out.println("Created table for " + tableClass.getName());
        }
    }
}
