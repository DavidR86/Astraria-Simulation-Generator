package sample.fileManagement.helpers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 2/7/18   ==
* =====================================================================
* ==      Project: Generator tool    ==
* =====================================================================

*/

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AppendableDataOutputStream extends DataOutputStream {

    public AppendableDataOutputStream(OutputStream out) throws IOException {
        super(out);
    }
}


