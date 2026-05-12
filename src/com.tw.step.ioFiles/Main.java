package com.tw.step.ioFiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.Scanner;

class Main{
    static void main() throws FileNotFoundException {
        String path = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("incoming/score_1.txt")).getPath();
        Scanner newScore = new Scanner(new BufferedReader(new FileReader(path)));
        int sum = 0;
        while(newScore.hasNext()){
           sum+=newScore.nextInt();
        }
        System.out.println(sum);

    }
}