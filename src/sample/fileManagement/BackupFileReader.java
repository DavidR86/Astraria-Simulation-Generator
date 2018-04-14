package sample.fileManagement;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 4/13/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class BackupFileReader {
    private FileInputStream ifStream;
    private DataInputStream stream;

    public BackupFileReader (File file) throws Exception{
        ifStream=new FileInputStream(file);
        stream=new DataInputStream(ifStream);
    }

   // public int read(){
//
  //  }
}
