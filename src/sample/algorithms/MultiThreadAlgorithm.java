package sample.algorithms;

/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 1/12/18   ==
* =====================================================================
* ==      Project: Generator tool    ==
* =====================================================================
jkkkkk
*/



import sample.Config;
import sample.controllers.InitController;
import sample.fileManagement.BinWriter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadAlgorithm extends ThreadOrganizer{

    //private static AtomicInteger progress = new AtomicInteger(0);


    private int amountOfThreads;
        private ExecutorService executorService;
        private float [] x;
        private float [] y;
        private float [] z;
        private float [] vx;
        private float [] vy;
        private float [] vz;
        private float [] ax;
        private float [] ay;
        private float [] az;
        private float m;
        private float simSpeed;

        private float [] x2;
        private float [] y2;
        private float [] z2;
        private float [] vx2;
        private float [] vy2;
        private float [] vz2;
        private float [] ax2;
        private float [] ay2;
        private float [] az2;

        private double timer;
        private BinWriter writer;

        private double ops;

        private float smoothingFactor;

        private double avgCPF;
        private long cpf;
        private volatile double timePerC;
        private double lastTimeMeasure;






    public MultiThreadAlgorithm( float[] x, float[] y, float[] z, float[] vx, float[] vy, float[] vz, float m, float simSpeed, BinWriter writer, double duration, boolean fixedDelta, double cycles, float smoothingFactor){
            super(fixedDelta, cycles, duration);

            if (Runtime.getRuntime().availableProcessors()==1){
                this.amountOfThreads = Runtime.getRuntime().availableProcessors();
            }else {
                this.amountOfThreads = Runtime.getRuntime().availableProcessors()-1;
            }

            executorService = Executors.newFixedThreadPool(this.amountOfThreads);
            int k = x.length;

            this.fixedDelta = fixedDelta;
            ops = 0;

            this.smoothingFactor = square(smoothingFactor);

            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.m = m;
            this.simSpeed = simSpeed;
            this.ax = new float[x.length];
            this.ay = new float[x.length];
            this.az = new float[x.length];

            this.x2 = new float[x.length];
            this.y2 = new float[x.length];
            this.z2 = new float[x.length];
            this.vx2 = new float[x.length];
            this.vy2 = new float[x.length];
            this.vz2 = new float[x.length];
            this.ax2 = new float[x.length];
            this.ay2 = new float[x.length];
            this.az2 = new float[x.length];

            avgCPF=0;




            accelInit();
            timer = (double) System.nanoTime();
            this.writer = writer;

            l = 0;
            l2 = 0;
        }

        @Override
        protected void runAlgorithm() {


                CountDownLatch countDownLatch = new CountDownLatch(x.length);

                //


                float delta = (float) ( getDelta() * simSpeed);
                lastTimeMeasure=System.nanoTime();

                for (int i = 0; i < x.length; i++) {

                    executorService.submit(new VelocityVerlet(countDownLatch, this, i, delta, smoothingFactor));

                }



                try {

                    countDownLatch.await();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                updateBodies();

                cpf++;

                timePerC = (System.nanoTime()-lastTimeMeasure)/1000000000.0;

                if (!fixedDelta){
                    if (((double) System.nanoTime())-timer>=16666666.6667){
                        timer = (double) System.nanoTime();
                        l++;
                        l2++;

                        avgCPF= (avgCPF*(l2-1)+cpf)/ (double) l2;
                        cpf=0;

                        for (int a = 0; a<x.length; a++){
                            writer.getQueue().add(x[a]);
                            writer.getQueue().add(y[a]);
                            writer.getQueue().add(z[a]);

                            writer.getQueue().add(ax[a]);
                            writer.getQueue().add(ay[a]);
                            writer.getQueue().add(az[a]);
                        }


                        /*
                        if (l2 >= 60*3){
                            //printStatus();
                            l2 = 0;
                        }
                        */

                        if (l >= (duration*60) ){
                            this.terminate();
                        }
                    }
                }else {
                    if (ops >= cycles){
                        l++;

                        ops = 0;


                        for (int a = 0; a<x.length; a++){
                            writer.getQueue().add(x[a]);
                            writer.getQueue().add(y[a]);
                            writer.getQueue().add(z[a]);

                            writer.getQueue().add(ax[a]);
                            writer.getQueue().add(ay[a]);
                            writer.getQueue().add(az[a]);

                        }





                        if (l >= (duration*60) ){
                            this.terminate();
                        }
                    }else {
                        ops++;
                    }

                    /*
                    if (((double) System.nanoTime()) - timer >= 3000000000D){
                        timer = (double) System.nanoTime();
                       // printStatus();
                    }
                    */
                }






        }

        @Override
        public void endThreads() {
            executorService.shutdown();
        }

        public float[] getX() {
            return x;
        }

        public float[] getY() {
            return y;
        }

        public float[] getZ() {
            return z;
        }

        public float[] getVx() {
            return vx;
        }

        public float[] getVy() {
            return vy;
        }

        public float[] getVz() {
            return vz;
        }

        public float[] getAx(){
            return ax;
        }

        public float[] getAy(){
            return ay;
        }

        public float[] getAz(){
            return az;
        }

        public float[] getX2() {
        return x2;
    }

        public float[] getY2() {
        return y2;
    }

        public float[] getZ2() {
        return z2;
    }

        public float[] getVx2() {
        return vx2;
    }

        public float[] getVy2() {
        return vy2;
    }

        public float[] getVz2() {
        return vz2;
    }

        public float[] getAx2(){
        return ax2;
    }

        public float[] getAy2(){
        return ay2;
    }

        public float[] getAz2(){
        return az2;
    }

        public float getM(){
            return m;
        }

        public double avgCPF(){
            if (fixedDelta){
                return cycles;
            }else {
                return avgCPF;
            }
        }

        public double timePerCycle(){
            return timePerC;
        }



        private void updateBodies(){

         //   int i = 0;
         //   for (Body current : bodies){
         //       current.setX(x[i]);
         //       current.setY(y[i]);
         //       current.setZ(z[i]);
         //       current.setvX(vx[i]);
         //       current.setvY(vy[i]);
         //       current.setvZ(vz[i]);
         //       current.setCurrAccelX(ax[i]);
         //       current.setCurrAccelY(ay[i]);
         //       current.setCurrAccelZ(az[i]);
         //
         //       i++;

         //   }

            float[] kx = x2;
            float[] ky = y2;
            float[] kz = z2;
            float[] kvx = vx2;
            float[] kvy = vy2;
            float[] kvz = vz2;
            float[] kax = ax2;
            float[] kay = ay2;
            float[] kaz = az2;

            x2 = x;
            y2 = y;
            z2 = z;
            vx2 = vx;
            vy2 = vy;
            vz2 = vz;
            ax2 = ax;
            ay2 = ay;
            az2 = az;

            x = kx;
            y = ky;
            z = kz;
            vx = kvx;
            vy = kvy;
            vz = kvz;
            ax = kax;
            ay = kay;
            az = kaz;
        }

        private void accelInit(){

            for (int i = 0; i < x.length; i++){


                for (int l = 0; l < i; l++) {
                    parseAcceleration(i, l, x[i], y[i], z[i]);


                }
                for (int l = i + 1; l < x.length; l++) {
                    parseAcceleration(i, l, x[i], y[i], z[i]);
                }


                ax [i] *= Config.grav;
                ay [i] *= Config.grav;
                az [i] *= Config.grav;

                //progress.incrementAndGet();

            }



    }

    private void parseAcceleration(int current, int other, float pX, float pY, float pZ) {


        float temporary = m / (cubed((float) Math.sqrt(square(x[other] - pX) + square(y[other] - pY) + square(z[other] - pZ)+smoothingFactor)));



        ax [current] += (x[other] - pX) * temporary;
        ay [current] += (y[other] - pY) * temporary;
        az [current] += (z[other] - pZ) * temporary;

    }

    public static float cubed(float x){
            return x*x*x;
    }

    public static float square(float x){
        return x*x;
    }

    public static int getProgress(){
        return 1;
    }
}
