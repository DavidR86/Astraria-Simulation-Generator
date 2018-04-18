package sample.fileManagement;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 4/13/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import sample.Config;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class BackupFileReader implements IniReader{
    private FileInputStream ifStreamD;
    private DataInputStream streamD;

    private FileInputStream ifStreamB;
    private DataInputStream streamB;

    private FileOutputStream fOutputStream;
    private DataOutputStream outputStream;

    private File fileD;
    private File fileB;
    private File output;

    private float x[];
    private float y[];
    private float z[];

    int bodyCount;
    long elapsedFrames;

    public BackupFileReader (File fileD, File fileB, int bodyCount, long elapsedFrames) throws Exception{

        this.fileB=fileB;
        this.fileD=fileD;


        ifStreamB=new FileInputStream(fileB);
        streamB=new DataInputStream(ifStreamB);

        this.bodyCount=bodyCount;
        this.elapsedFrames=elapsedFrames;


    }

    public File copyFileContent()throws Exception{

      //  if (new File(fileD.getParent()+"/oldOutput.nbd").exists()){
      //      new File(fileD.getParent()+"/oldOutput.nbd").delete();
      //      System.out.println("a :"+fileD.getParent()+"/oldOutput.nbd");
      //  }
        //if (fileD.renameTo(new File(fileD.getParent()+"/oldOutput.nbd"))){
        //    output=new File(fileD.getCanonicalPath());
        //    fileD=new File(fileD.getParent()+"/oldOutput.nbd");
        //    System.out.println("B");
       // }else {
            output=new File(fileD.getParent()+"/"+fileD.getName().substring(0,fileD.getName().length()-4)+"(new).nbd");
        System.out.println(output.getAbsolutePath());
            System.out.println("c");
       // }

        ifStreamD=new FileInputStream(fileD);
        streamD=new DataInputStream(new BufferedInputStream(ifStreamD));

        fOutputStream=new FileOutputStream(output);
        outputStream=new DataOutputStream(new BufferedOutputStream(fOutputStream));

        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        CountDownLatch latch = new CountDownLatch(2);

         x = new float[bodyCount];
         y = new float[bodyCount];
         z = new float[bodyCount];

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("E. "+elapsedFrames);
                System.out.println("B.C. "+bodyCount);
                
                int k= (int) (16*bodyCount*(elapsedFrames))+10-(16*bodyCount);
                try {
                   for (int i = 0; i<(k); i++){
                       queue.add(streamD.read());
                   }

                    latch.countDown();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Thread b = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int i;
                    while (latch.getCount()>1||(!queue.isEmpty())){
                        outputStream.write(queue.take());
                    }
                    latch.countDown();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        a.start();
        b.start();
        latch.await();
        a.interrupt();
        b.interrupt();
        int j = bodyCount;
        for (int i =0; i<j; i++){
            x[i]=streamD.readFloat();
            y[i]=streamD.readFloat();
            z[i]=streamD.readFloat();
            streamD.skipBytes(4);
        }
        outputStream.close();
        streamD.close();
        ifStreamD.close();
        fOutputStream.close();

        return output;
    }

    @Override
    public String read() throws Exception {
        return "m x y z vx vy vz";
    }

    @Override
    public void sort(float[] x, float[] y, float[] z, float[] vx, float[] vy, float[] vz, float[] m) {
        x=this.x;
        y=this.y;
        z=this.z;

        try {
            streamB.readBoolean();
            streamB.readInt();
            streamB.readInt();
            streamB.readFloat();
            streamB.readFloat();
            streamB.readFloat();
            streamB.readInt();
            streamB.readLong();

            m[0]=streamB.readFloat();

            for (int i =0; i<(bodyCount); i++){
                vx[i]=streamB.readFloat();
                vy[i]=streamB.readFloat();
                vz[i]=streamB.readFloat();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getBodyCount(String order) {
        return bodyCount;
    }
}
