package io.github.keymaster65.timecharts.application;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommandLineFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CommandLineFactory.class);

    static CommandLine createCommandLine(final String[] args) throws ParseException {
        // create the parser
        CommandLineParser parser = new DefaultParser();
        final Options options = createOptions();

        try {
            CommandLine line = parser.parse(options, args, true);

            final List<String> argList = line.getArgList();
            LOG.debug("Arg list is {}", argList);

            if (!argList.isEmpty()) {
                throw new ParseException("Args are not supported: " + argList);
            }
            if (line.hasOption("--help")) {
                new HelpFormatter().printHelp("time-chart", options);
            }

            return line;
        } catch (ParseException parseException) {
            new HelpFormatter().printHelp("time-chart", options);
            throw parseException;
        }
    }

    private static Options createOptions() {
        Option property = Option.builder("bam")
                .desc("Bucket aggregation method")
                .argName("seriesName=aggregationMethod")
                .longOpt("bucketAggregationMethod")
                .hasArgs()
                .valueSeparator('=')
                .build();

        final Options options = new Options();
        options
                .addOption(
                        "bd",
                        "bucketDuration",
                        true,
                        "Duration or size of a time bucket in seconds"
                )

                .addOption(
                        "t",
                        "title",
                        true,
                        "Title of window and chart"
                )

                .addOption(
                        "cmy",
                        "chartMaxY",
                        true,
                        "Maximum Y axis value in chart"
                )

                .addOption(
                        "h",
                        "help",
                        false,
                        "Display help only"
                )
                .addOption(property)
        ;

        return options;
    }
}
