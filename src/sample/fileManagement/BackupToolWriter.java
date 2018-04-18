package sample.fileManagement;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 4/9/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import sample.Config;
import sample.algorithms.MultiThreadAlgorithm;

import java.io.*;

public class BackupToolWriter {

    private FileOutputStream ifStream;
    private DataOutputStream stream;
    private MultiThreadAlgorithm multiThreadAlgorithm;

    public BackupToolWriter(File file, MultiThreadAlgorithm multiThreadAlgorithm) throws Exception{
        ifStream=new FileOutputStream(file);
        stream=new DataOutputStream(ifStream);
        this.multiThreadAlgorithm=multiThreadAlgorithm;
    }

    public void writeData() throws Exception{
        stream.writeBoolean(Config.constantCPS);
        stream.writeInt(Config.cpsPerFrame);
        stream.writeInt(Config.autoSaveInterval);
        stream.writeFloat(Config.grav);
        stream.writeFloat(Config.smoothingConstant);
        stream.writeFloat(Config.simSpeed);
        stream.writeInt(Config.bodyCount);
        stream.writeLong(multiThreadAlgorithm.getL());

        stream.writeFloat(multiThreadAlgorithm.getM());

        for (int i = 0; i<Config.bodyCount; i++ ){
            stream.writeFloat(multiThreadAlgorithm.getVx()[i]);
            stream.writeFloat(multiThreadAlgorithm.getVy()[i]);
            stream.writeFloat(multiThreadAlgorithm.getVz()[i]);
        }
    }


}
