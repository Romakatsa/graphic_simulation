package com.fuzzygroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

class DataWriter {
    private static final Logger logger = LoggerFactory.getLogger(DataWriter.class);
    private PrintWriter fileWriter;

    public DataWriter(String fileName) {
//            this.fileName = fileName;
        try {
            fileWriter = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void appendLine(String line) {
        if (line.matches("[\r\n]]")) {
            logger.warn("File writer: Line contain '\\n' or '\\r'");
        }
        String processedLine = line.replaceAll("[\n\r]", "").trim();
        fileWriter.println(processedLine);
        fileWriter.flush();
    }

    public void appendText(String text) {
        Arrays.stream(text.split("\n")).forEach(fileWriter::println);
    }

    public void close() {
        fileWriter.close();
    }
}
