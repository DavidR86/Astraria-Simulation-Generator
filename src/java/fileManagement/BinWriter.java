package sample.fileManagement;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 1/13/18   ==
* =====================================================================
* ==      Project: Generator tool    ==
* =====================================================================

*/



import sample.Config;
import sample.algorithms.MultiThreadAlgorithm;
import sample.algorithms.ThreadOrganizer;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

public class BinWriter implements Runnable {

    private LinkedBlockingQueue<Float> queue;
    private boolean terminate;
    private DataOutputStream stream;

    private final Object lock;
    private FileOutputStream fStream;

    private short version;
    private int bodies;
    private float scale;

    private float maxAcceleration;
    private float minAcceleration;
    private float avgAcceleration;

    private ThreadOrganizer algorithm;

    private final Object lock2;

    public BinWriter(short version, int bodies, float scale, ThreadOrganizer algorithm){
        queue = new LinkedBlockingQueue<Float>();
        terminate = false;

        this.version = version;
        this.bodies = bodies;
        this.scale = scale;

        maxAcceleration = Float.MIN_VALUE;
        minAcceleration = Float.MAX_VALUE;

        lock = new Object();
        lock2 = new Object();

        this.algorithm=algorithm;
    }

    @Override
    public void run() {

        synchronized (lock) {


            try {

                //    System.out.println(version);
                //  System.out.println(bodies);
                //System.out.println(scale);
                //System.out.println(100f);
                //System.out.println(0f);

                stream.writeShort(version);
                stream.writeInt(bodies);
                stream.writeFloat(scale);


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            int i = 0;
            long u = 0;

            avgAcceleration = 0;

            while (!terminate || !queue.isEmpty()) {

                if (algorithm!=null){
                    if (algorithm.isPaused.get()){
                        try {
                            algorithm.pauseLatch.await();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                        if (!queue.isEmpty()) {
                            try {

                                if (i >= 3) {
                                    i = 0;

                                    // System.out.println(queue.size());

                                    float currAcc = queue.take();


                                    avgAcceleration = (avgAcceleration*u+currAcc)/(u+1);

                                    if (currAcc > maxAcceleration) {
                                        maxAcceleration = currAcc;
                                    }
                                    if (currAcc < minAcceleration ) {
                                        minAcceleration = currAcc;

                                    }
                                    u++;

                                    stream.writeFloat(currAcc);

                                } else {
                                    float f = queue.take();
                                    stream.writeFloat(f);

                                    i++;
                                }


                            } catch (Exception e) {
                                System.out.println("ERROR: Unable to write: " + e.getMessage());
                            }
                        }




            }

            try{
                stream.writeFloat(0);
                stream.writeFloat(0);
                stream.close();
                fStream.close();

            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            //Menu.avgAcceleration = avgAcceleration;
            //System.out.println("avg accel: "+avgAcceleration);
            //System.out.println("max accel: "+maxAcceleration);
            //System.out.println("min accel: "+minAcceleration);
/*
            try {
                System.out.println(maxAcceleration);
                stream.writeFloat(maxAcceleration);
                stream.writeFloat(minAcceleration);
                System.out.println(minAcceleration);
                System.out.println(avgAcceleration);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            try {
                stream.close();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            */
        }

    }

    public Object getLock(){
        return lock;
    }

    public void terminate(){
        terminate=true;

    }

    public boolean setFile(File output){
        try {

            fStream = new FileOutputStream(output, Config.backup);

            stream = new DataOutputStream(new BufferedOutputStream(fStream));

            return true;
        }catch (Exception e){
            System.out.println("ERROR: Output stream could not locate file: "+e.getMessage());
            return false;
        }
    }

    public LinkedBlockingQueue<Float> getQueue(){
        return queue;
    }

    public float getAvgAcceleration(){
        return avgAcceleration;
    }




}
