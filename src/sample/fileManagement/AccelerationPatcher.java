package sample.fileManagement;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 3/21/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import sample.algorithms.MultiThreadAlgorithm;
import sample.fileManagement.helpers.AppendableObjectOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

public class AccelerationPatcher {

        private FileInputStream ifStream;
        private ObjectInputStream stream;
        private float average;
        private File path;
        private int totalBytes;
        private int remainingBytes;


        public AccelerationPatcher(float average, File path){
            this.average = average;
            System.out.println(average);


            try {
                FileInputStream ifStream = new FileInputStream(path);
                ObjectInputStream stream = new ObjectInputStream(ifStream);

                stream.readShort();
                stream.readInt();
                stream.readFloat();

                this.ifStream = ifStream;
                this.stream = stream;
                this.path=path;

            }catch (Exception e){
                System.out.println("ERROR: "+e.getMessage());
            }

        }

        public void start() throws Exception{
            appendMaxAndMin(getVariance(), path);
        }

        public int getTotalBytes(){
            return totalBytes;
        }

        public synchronized int getRemainingBytes(){
            return remainingBytes;
        }

        private float getVariance(){
            long k = 0;
            float variance = 0;

            try {
                totalBytes=ifStream.available();
                while (ifStream.available()>16){
                    remainingBytes=ifStream.available();
                    stream.readFloat();
                    stream.readFloat();
                    stream.readFloat();

                    float currAccel = stream.readFloat();

                    variance = ((variance*k)+ MultiThreadAlgorithm.square((currAccel - average)))/(k+1);
                    k++;
                }

                variance = (float) Math.sqrt(variance);


                ifStream.close();
                stream.close();
                System.out.println(variance);
            }catch (Exception e){
                System.out.println("ERROR: "+e.getMessage());
            }

            return variance;

        }

        private void appendMaxAndMin(float variance, File path) throws Exception{

            FileOutputStream ofStream = new FileOutputStream(path, true);
            AppendableObjectOutputStream oStream = new AppendableObjectOutputStream(ofStream);

            oStream.writeFloat(average+variance);
            oStream.writeFloat(average-variance);


            System.out.println(variance);
            System.out.println(average+variance);
            System.out.println(average-variance);


            oStream.close();
            ofStream.close();

        }
    }
