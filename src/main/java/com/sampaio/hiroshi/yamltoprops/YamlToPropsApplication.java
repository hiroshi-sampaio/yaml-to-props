package com.sampaio.hiroshi.yamltoprops;

import org.apache.commons.cli.*;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.beans.factory.config.YamlProcessor.MatchStatus;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.*;

@SpringBootApplication
public class YamlToPropsApplication implements CommandLineRunner {

    // Command line options
    public static final String OPT_INPUT = "input";
    public static final String OPT_STDOUT = "stdout";
    public static final String OPT_SAME_FOLDER = "same-folder";
    public static final String OPT_FOLDER = "folder";

    public static void main(String[] args) {
        SpringApplication.run(YamlToPropsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Options options = getOptions();

        final CommandLine commandLine;
        try {
            commandLine = new GnuParser().parse(options, args);
        } catch (ParseException e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp(this.getClass().getSimpleName(), options);
            return;
        }

        final String argInputFile = commandLine.getOptionValue("input");
/*
        final String argFolder = commandLine.getOptionValue("folder");
        final boolean argIsSameFolder = commandLine.hasOption("same-folder");
        final boolean argIsStdOut = Objects.isNull(argFolder) && !argIsSameFolder;

        final File inputFile = new File(argInputFile);

        if (!argIsStdOut) {
            final File sameFolder;
            if (argIsSameFolder) {
                sameFolder = inputFile.getParentFile();
                Paths.get(sameFolder.toString(),"");
            }

            final String fileName = inputFile.getName();

        }
*/

        final String folder = new File(argInputFile).getParent();


        final Resource resource = new FileSystemResource(argInputFile);
//        final File output = Paths.get(folder, "application" + (StringUtils.hasText(springProfiles) ? "-" + springProfiles : "") + ".properties").toFile();
//        System.out.println("output = " + output.toString());
//        try {
//            final PrintWriter writer = new PrintWriter(new FileOutputStream(output));
//            properties.entrySet()
//                    .stream()
//                    .sorted(Comparator.comparing(o -> (String) o.getKey()))
//                    .forEach(e -> writer.println(e.getKey() + "=" + e.getValue()));
//            writer.close();
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        get(resource);
    }

    private Map<String, Map<String, String>> get(Resource resource) {
        final YamlMapFactoryBean yml = new YamlMapFactoryBean();
        yml.setResources(resource);

        final Map<String, Map<String, String>> propsMap = new HashMap<>();

        yml.setDocumentMatchers(properties -> {
            final String springProfiles = properties.getProperty("spring.profiles");
            final Map<String, String> map = properties.stringPropertyNames().stream()
                    .map(name -> Collections.singletonMap(name, properties.getProperty(name)))
                    .findFirst()
                    .orElse(Collections.emptyMap());
            
            propsMap.put(springProfiles, map);
            return MatchStatus.NOT_FOUND;
        });

        yml.getObject();

        return propsMap;
    }

    private Options getOptions() {
        Options options = new Options();

        Option input = new Option("i", OPT_INPUT, true, "input file path (required)");
        input.setRequired(true);
        options.addOption(input);

        options.addOption(new Option("s", OPT_STDOUT, false, "write resulting property files to stdout (default)"));
        options.addOption(new Option("sf", OPT_SAME_FOLDER, false, "write resulting property files to the same folder of input file"));
        options.addOption(new Option("f", OPT_FOLDER, true, "write resulting property files to specified folder"));
        return options;
    }
}

class YamlProc extends YamlProcessor{
    
}