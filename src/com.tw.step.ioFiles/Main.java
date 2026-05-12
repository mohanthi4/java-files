package com.tw.step.ioFiles;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

class Main{
    static void main() throws IOException, InterruptedException {
        List<Integer> currentScore=new ArrayList<>();
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("resources.data/incoming");
        path.register(watcher, ENTRY_MODIFY,ENTRY_CREATE);
        while(true){
            WatchKey action = watcher.take();
            for (WatchEvent<?> event : action.pollEvents()) {
                Path fileName = (Path) event.context();
                String name = fileName.toString();
                if (isValidFile(name)) {
                    int totalScore = getTotalScore(readableFilePath(path, name));
                    updateCurrentScores(totalScore,currentScore);
                }
            }
            if (!action.reset()) break;
        }
    }

    private static boolean isValidFile(String name) {
        return name.startsWith("score_") && name.endsWith(".txt");
    }

    private static Scanner readableFilePath(Path path, String name) throws FileNotFoundException {
        String filePath= path.toString()+"/"+ name;
        return new Scanner(new BufferedReader(new FileReader(filePath)));
    }

    private static int getTotalScore(Scanner readFileStream)  {
        int sum = 0;
        while(readFileStream.hasNext()){
            sum+=readFileStream.nextInt();
        }
        return sum;
    }

    private static void updateCurrentScores(int totalScore,List<Integer> currentScore) throws IOException {
        try(BufferedOutputStream bufferedWriter = new BufferedOutputStream(new FileOutputStream("resources.data/scores.txt",true))){
            LocalDateTime currentTime = LocalDateTime.now();
            currentScore.add(totalScore);
            bufferedWriter.write(currentScore.getLast());
        }
    }


}
