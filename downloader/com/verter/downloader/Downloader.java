package com.verter.downloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Download files
 */
public class Downloader {
    /**
     * Filename from command arguments or default file name 'list of download links.txt'
     */
    private String sFileName;

    /**
     * link to file with links
     */
    private Path linksFile;

    /**
     * File for links that have error while download
     */
    private Path brokenLinksFile;
    private Path downloadDirectory;

    private List<String> urls = null;

    private static final Logger log = LoggerFactory.getLogger(Downloader.class);

    public Downloader (String sFileName) {
        log.debug("Constructor filename: " + sFileName);
        this.sFileName = sFileName;
    }

    public void start() {
        fileCheck();
        directoryCheck();
        loadFile();
        prepareBrokenFile();
        downloadFiles();
    }

    private void downloadFiles() {
        for (String sUrl:urls) {
            URL url;

            try {
                url = new URL(sUrl);
            } catch (MalformedURLException e) {
                log.error("Something wrong with url: " + sUrl, e);
                return;
            }

            downloadFileBuffered(url);
        }
    }

    private void downloadFileBuffered (URL url) {

        BufferedInputStream in = null;
        FileOutputStream fileOutputStream = null;

        int indexOfPoint = url.getFile().lastIndexOf(".");
        int indexOfLastSlash = url.getFile().lastIndexOf("/");

        String prefix = url.getFile().substring(indexOfLastSlash + 1,indexOfPoint);
        String postfix = url.getFile().substring(indexOfPoint);

        Path tempFile;
        try {
            tempFile = Files.createTempFile(prefix,postfix);
        } catch (IOException e) {
            log.error("Couldn't create temp file for download: " + prefix + postfix);
            return;
        }

        LocalDateTime start = LocalDateTime.now();
        long downloadedMegaBytes = 0;

        try {
            in = new BufferedInputStream(url.openStream());
            fileOutputStream = new FileOutputStream(tempFile.toFile());

            log.info("time = {} Starting download file: " + url, start);

            int chunk = 1024*1024*10;

            final byte data[] = new byte[chunk];
            int count;
            int i = 0;

            while ((count = in.read(data, 0, chunk)) != -1) {
                fileOutputStream.write(data, 0, count);
                downloadedMegaBytes += count;
                i++;
                if (i == 10000) {
                    log.info(downloadedMegaBytes/1024/1024 + " MB downloaded");
                    i = 0;
                }
            }
        } catch (IOException e) {
            log.error("Error while downloading file: " + url + " :",e);
            log.info("File added to 'broken links.txt' Error while downloading file: " + url + " :",e);
            addToBrokenFile(url);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("Error while closing inputstream: ", e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    log.error("Error while closing outputstream: ", e);
                }
            }
        }

        Path destFile = Paths.get(downloadDirectory.toAbsolutePath().toString() + "/" + prefix + postfix);
        log.trace("Move to destination file: " + destFile.toAbsolutePath());

        try {
            Files.move(tempFile,destFile,StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Couldn't move temp file: " + tempFile.toAbsolutePath() +
                    " to destination file: " + destFile.toAbsolutePath(), e);
            return;
        }

        log.info("File has downloaded in {} seconds. Downloaded " + downloadedMegaBytes/1024/1024 + " MB"
                , start.until(LocalDateTime.now(), ChronoUnit.SECONDS));
    }

    private void addToBrokenFile (URL url) {
        try {
            Files.write(brokenLinksFile,(url + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Minor error wile writing to 'broken list.txt'",e);
        }
    }

    private void prepareBrokenFile () {
        brokenLinksFile = Paths.get(linksFile.toAbsolutePath().getParent() + "/" + "broken links.txt");

        log.debug("Broken file is: " + brokenLinksFile.toAbsolutePath());
        //Check if file exists
        try {
            if (Files.exists(brokenLinksFile)) {
                Files.delete(brokenLinksFile);
            }
            Files.createFile(brokenLinksFile);
        }
        catch (IOException e) {
            log.error("Minor error while delete/create 'broken links.txt'", e);
        }
    }

    private void loadFile() {
        log.trace("Opening file with links: " + sFileName);
        try {
            urls = Files.readAllLines(linksFile);
        } catch (IOException e) {
            log.error("Fatal error",e);
            System.exit(-1);
        }

        log.trace("Remove zero lines");
        urls.removeAll(urls
                        .stream()
                        .filter(p -> p.equals(""))
                        .collect(Collectors.toList())
                );

        //Check if some lines in file
        if (urls.size() < 1) {
            log.error("There is no urls in file \"" + linksFile.toAbsolutePath().toString() + "\"");
            System.exit(1);
        }
    }

    private void fileCheck() {
        linksFile = Paths.get(sFileName);
        log.debug("File with urls: " + linksFile.toAbsolutePath());
        //Check if file exists
        if (!Files.exists(linksFile)) {
            log.error("File \"" + linksFile.toAbsolutePath() + "\" does not exists");
            System.exit(1);
        }

        //Check if file is empty
        try {
            if (Files.size(linksFile) == 0) {
                log.error("File \"" + linksFile.toAbsolutePath() + "\" is empty");
                System.exit(1);
            }
        } catch (IOException e) {
            log.error("Fatal error: ", e);
            System.exit(-1);
        }
    }

    private void directoryCheck () {
        log.debug("Parent directory is: " + linksFile.toAbsolutePath().getParent());

        downloadDirectory = Paths.get(linksFile.toAbsolutePath().getParent() + "/" + "download");
        log.debug("Download directory: " + downloadDirectory.toAbsolutePath());

        if (!Files.exists(downloadDirectory)) {
            log.trace("Download directory does not exists: " + downloadDirectory.toAbsolutePath());
            log.info("Create download directory: " + downloadDirectory.toAbsolutePath());
            try {
                Files.createDirectory(downloadDirectory);
            } catch (IOException e) {
                log.error("Fatal error when creating download directory", e);
                System.exit(1);
            }
        }
    }
}
