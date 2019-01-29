package sample.fileManagement;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 4/13/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import java.util.ArrayList;

public interface IniReader {

    String read()throws Exception;

    void sort(float x[], float y[], float[] z, float vx[], float vy[], float vz[], float [] m);

    int getBodyCount(String order);
}
