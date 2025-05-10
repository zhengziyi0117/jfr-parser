package org.example;

import com.google.gson.Gson;
import entity.FrameTree;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        FrameTree frameTree = PprofParser.parsePprofFile("/home/zhengziyi/GolandProjects/awesomeProject/cpu.pprof");
        Gson gson = new Gson();
        String json = gson.toJson(frameTree);
        System.out.println(json);
    }
}