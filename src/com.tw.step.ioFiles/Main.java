package com.tw.step.ioFiles;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

class Main{
    static void main() throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("resources.data/incoming");
        readInitialScores(path);
        path.register(watcher, ENTRY_MODIFY,ENTRY_CREATE);
        while(true){
            WatchKey action = watcher.take();
            for (WatchEvent<?> event : action.pollEvents()) {
                Path fileName = (Path) event.context();
                String name = fileName.toString();
                if (isValidFile(name)) {
                    performFileOperation(validFileFormat(path, name));
                }
            }
            if (!action.reset()) break;
        }
    }

    private static void readInitialScores(Path path) throws IOException {
        try (Stream<Path> stream = Files.list(path)) {
            stream.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            performFileOperation(file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private static void performFileOperation(Path file) throws IOException {
        int totalScore = getTotalScore(file.toString());
        updateCurrentScores(file.toString(), totalScore);
    }

    private static boolean isValidFile(String name) {
        return name.startsWith("score_") && name.endsWith(".txt");
    }

    private static Path validFileFormat(Path path, String name) {
        return  path.resolve(name);
    }

    private static int getTotalScore(String readFileStream) throws FileNotFoundException {
        int sum = 0;
        try (Scanner scoreFile = new Scanner(new BufferedReader(new FileReader(readFileStream)))) {
            while (scoreFile.hasNext()) {
                sum += scoreFile.nextInt();
            }
        }
        return sum;
    }

    private static void updateCurrentScores(String file, int totalScore) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources.data/scores.txt", true))) {
            LocalDateTime now = LocalDateTime.now();
            writeALine(writer, String.valueOf(now),"updated time : ");
            writeALine(writer, String.valueOf(totalScore), file +": ");
        }

    }

    private static void writeALine(BufferedWriter writer, String now, String tag) throws IOException {
        writer.write(tag);
        writer.write(now);
        writer.newLine();
    }


}
