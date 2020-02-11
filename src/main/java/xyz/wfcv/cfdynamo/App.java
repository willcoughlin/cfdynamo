package xyz.wfcv.cfdynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.cli.*;
import xyz.wfcv.cfdynamo.input.ClassToTableMapper;
import xyz.wfcv.cfdynamo.input.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        new App(args).Run();
    }

    private List<String> targetFilePaths;
    private DynamoDB db;

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
        var ddbClient = AmazonDynamoDBClientBuilder.standard().build();
        db = new DynamoDB(ddbClient);
    }

    private void Run() {
        if (targetFilePaths == null || targetFilePaths.isEmpty()) {
            System.err.println("No class files have been discovered yet");
            System.exit(1);
        }

        for (String path : targetFilePaths) {
            JavaClass ddbClass = null;
            try {
                ddbClass = new ClassParser(path).parse();
            } catch (IOException e) {
                System.err.println("Failed to read class file: " + path);
            }
            if (ddbClass == null) {
                System.exit(1);
            }

            var mapper = new ClassToTableMapper(db, ddbClass);
        }
    }
}
