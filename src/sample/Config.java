package sample;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 3/15/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import sample.fileManagement.IniReader;

import java.io.File;

public class Config {

    public static File inputFile;
    public static File outputFile;
    public static int cpsPerFrame;
    public static double simDuration;

    public static boolean constantCPS=false;
    public static boolean durationSet=false;
    public static boolean cpsSet=false;

    public static int autoSaveInterval=Integer.MAX_VALUE;

    public static float grav;
    public static float smoothingConstant;
    public static float simSpeed;

    public static float bodyScale=1;

    public static boolean backup = false;

    public static int bodyCount;

    public static IniReader txtReader;

    public static int amountOfThreads;

    public static boolean allSet() {

        return  (durationSet&&inputFile.exists()&&outputFile.getParentFile().exists()&&cpsSet);



    }
}
