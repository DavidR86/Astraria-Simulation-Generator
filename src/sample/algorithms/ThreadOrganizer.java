package sample.algorithms;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 1/11/18   ==
* =====================================================================
* ==      Project: Generator tool    ==
* =====================================================================

*/


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ThreadOrganizer {

        //private double lastCalc;
        //private double lastAvg;

        public AtomicBoolean isPaused;
        public CountDownLatch pauseLatch;

        private boolean terminate = false;
        private double lastTime;

        protected boolean fixedDelta;


        private double tmp;
        protected double cycles;
        protected double duration;
        private double deltaConst;

        protected double pausedTime;

        private final Object lock;


    protected long l;
    protected long l2;

        //public static boolean PRINT_CALC_SEC = true;


        public ThreadOrganizer(boolean fixedDelta, double cycles, double duration) {
            this.fixedDelta = fixedDelta;
            this.cycles = cycles;
            this.duration = duration;
            deltaConst = 1/(cycles*60);

            isPaused=new AtomicBoolean(false);
            pausedTime=0;

            lock=new Object();

        }

        public void run() {
/*
            int calcSec = 0;
            double timeSinceCalcSec = 0;

            double average = 0;
            double times = 0;

            */


            while (!terminate){
                synchronized (lock){
                    runAlgorithm();
                }
                // System.out.println("Algorithm done");

                //System.out.println(tmp);

/*
                if(PRINT_CALC_SEC) {
                    calcSec++;
                    timeSinceCalcSec += tmp;
                    if (timeSinceCalcSec >= 1) {
                        timeSinceCalcSec = 0;
                        lastCalc = calcSec;

                        average = (average*times+calcSec)/(times+1);

                        lastAvg = average;



                        calcSec = 0;
                        times++;
                    }
                }
                */
                if (isPaused.get()){
                    try {
                        pauseLatch.await();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            endThreads();
        }

    public void pauseAndResumeAlgorithm(){
        if (!isPaused.get()){
            pauseLatch = new CountDownLatch(1);
            isPaused.set(true);
            pausedTime=System.nanoTime();
        }else {
            isPaused.set(false);
            pausedTime=System.nanoTime()-pausedTime;
            pauseLatch.countDown();
        }
    }


        protected abstract void runAlgorithm();

        protected void terminate(){
            terminate = true;
        }

        public abstract void endThreads();

        protected double getDelta(){


            if (fixedDelta){
                return deltaConst;
            }else {
               return getRealDelta();
            }



        }

        public double getRealDelta(){
            double currTime = (double) System.nanoTime()/1000000000.0;
            if(lastTime==0){
                lastTime = currTime;
            }
            double temp = (currTime - lastTime);
            if (pausedTime!=0){
                temp-=(pausedTime/1000000000.0);
                pausedTime=0;
            }
            lastTime = currTime;

            this.tmp = temp;
            return temp;
        }


    public long getL(){
            return l;
    }

    public Object getLock(){
        return lock;
    }

}


