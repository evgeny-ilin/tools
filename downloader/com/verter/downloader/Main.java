package com.verter.downloader;




import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Evgeny Ilin evgeny.ilin@gmail.com
 *
 * This is software created for Simon Hsu offer:
 * I bought some download files. But there are too many of them, each with a separate download link. I want to be able to download them with automated script. See attached file for details.
 * This web page has some 2 hundred separate download links. And I need a simple script that will accept many download links at then download them one after another or in an orderly manner (one every 3 minutes)
 *
 * Implementation:
 * Console Java software
 *
 * Usage:
 * downloader.jar your_file.txt. Format of file - plain text from Notepad, for example
 * Downloaded files you'll find in download directory which will create automatically
 *
 *  Expexted args:
 *  'filename.txt' - file with list links for download
 *  '-?' or '-help' - print help
 *  If no params added program try to find list of download links.txt
 *
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Usage: downloader.jar your_file.txt
     * File should be in plain text format
     *
     * @param args filename.txt expected or '-?', '-help' for print help
     */
    public static void main(String[] args) {

        log.info("-------------------------------");
        log.info("Downloader has started, time = {}", LocalDateTime.now());
        String arg = "";

        if (args.length > 0 && args[0] != null)
            arg = args[0];

        switch (arg) {
            case "-help":
            case "-?":
                System.out.println("Please use: downloader.jar your_file.txt. Format of file - plain text from Notepad, for example\\n" +
                        "Downloaded files you'll find in download directory which will create automatically");
                System.exit(0);
            case "":
                arg = "list of download links.txt";
                log.trace("Load from default file: \"" + arg + "\"");
            default:
                if (!arg.endsWith(".txt")) {
                    log.error("Ooops! There is not recognized parameter: " + arg);
                    System.exit(1);
                }
        }

        try {
            Downloader downloader = new Downloader(arg);
            downloader.start();
        } catch (Exception e) {
            log.error("Fatal unhandled error: ", e);
            System.exit(-1);
        }

        log.info("Downloader has end of work, time= {}", LocalDateTime.now());
        log.info("-------------------------------");
    }
}
