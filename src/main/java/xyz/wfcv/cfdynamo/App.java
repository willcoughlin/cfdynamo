package xyz.wfcv.cfdynamo;

import org.apache.commons.cli.*;

import java.io.IOException;

public class App {
    
    public static void main(String[] args) {
        new App(args).Run();
    }

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

        InputHandler inputHandler = null;
        try {
            inputHandler = new InputHandler(arg, cmd.hasOption("d"));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // get target files
        var targetFilePaths = inputHandler.getTargetFilePaths();

        System.out.println("Target files discovered: ");
        targetFilePaths.forEach(it -> System.out.println("\t" + it));

        // TODO continue implementation
    }

    private void Run() {

    }
}
