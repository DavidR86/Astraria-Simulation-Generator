package sample.algorithms;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 1/11/18   ==
* =====================================================================
* ==      Project: Generator tool    ==
* =====================================================================

*/


public abstract class ThreadOrganizer {

        //private double lastCalc;
        //private double lastAvg;

        private boolean terminate = false;
        private double lastTime;
        protected final Object lock;

        protected boolean fixedDelta;


        private double tmp;
        protected double cycles;
        protected double duration;
        private double deltaConst;


    protected long l;
    protected long l2;

        //public static boolean PRINT_CALC_SEC = true;


        public ThreadOrganizer(Object lock, boolean fixedDelta, double cycles, double duration) {
            this.lock = lock;
            this.fixedDelta = fixedDelta;
            this.cycles = cycles;
            this.duration = duration;
            deltaConst = 1/(cycles*60);


        }

        public void run() {
/*
            int calcSec = 0;
            double timeSinceCalcSec = 0;

            double average = 0;
            double times = 0;

            */


            while (!terminate){
                runAlgorithm();
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

            }

            endThreads();
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
            double temp = currTime - lastTime;
            lastTime = currTime;

            this.tmp = temp;
            return temp;
        }


    public synchronized double getL(){
            return l;
    }


}


