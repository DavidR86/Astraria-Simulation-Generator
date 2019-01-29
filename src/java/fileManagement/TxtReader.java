package sample.fileManagement;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 1/11/18   ==
* =====================================================================
* ==      Project: Generator tool    ==
* =====================================================================

*/

import com.sun.deploy.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class TxtReader implements IniReader{

    private File file;
    private ArrayList<Float> data;
    private int v;
    private String order;
    private int bodyCount;

    public TxtReader(File file){
        this.file = file;
        data = new ArrayList<Float>();
    }

    @Override
    public String read() throws Exception {

        Scanner reader = new Scanner(file);
        String currentLine;
        String order=null;


        while (reader.hasNextLine()){
            currentLine=reader.nextLine();
            if (currentLine.contains(",")){
                currentLine = currentLine.replace(",", "");
            }
            if (!currentLine.isEmpty()){
                if (currentLine.charAt(0)=='['){
                    order = currentLine.substring(1, currentLine.length()-1);
                }else if (currentLine.charAt(0)!='#'){


                    for (String current : currentLine.split("\\s")){
                        if (!current.equals(" ")&&!current.isEmpty()&&!current.equals(",")){
                            data.add(Float.parseFloat(current));
                            // System.out.println(current);
                        }
                    }
                }
            }
        }

        return order;
    }

    public int read2() throws Exception{



            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()){

                String k = reader.nextLine();


                if (k.length()>1){
                    if (!(k.charAt(0)=='#'&&k.charAt(1)=='#')){

                        if ((k.charAt(0)=='['&&(k.charAt(1)=='1'))){
                            //version 1
                            v = 1;
                        }else if (v == 1||v==2||v==3||v==4){
                            readLineV1(k);

                        }else if (k.charAt(0)=='['&&k.charAt(1)=='2'){
                            v = 2;
                        }else if (k.charAt(0)=='['&&k.charAt(1)=='3'){
                            v = 3;
                        }else if (k.charAt(0)=='['&&k.charAt(1)=='4'){
                            v = 4;
                        }

                    }
                }

            }

            if (v==1){
                System.out.println("    CORRECT: Initial condition file successfully loaded");

                System.out.println("        File contains "+data.size()/8+" bodies");
                //Menu.iniLoaded = true;

                return data.size()/8;

            }else if (v==2){
                System.out.println("    CORRECT: Initial condition file successfully loaded");

                System.out.println("        File contains "+data.size()/7+" bodies");
                //Menu.iniLoaded = true;

                return data.size()/7;

            }else if (v==3){
                System.out.println("    CORRECT: Initial condition file successfully loaded");

                System.out.println("        File contains "+data.size()/11+" bodies");
                //Menu.iniLoaded = true;

                return data.size()/11;

            }else if (v==4){
                System.out.println("    CORRECT: Initial condition file successfully loaded");

                System.out.println("        File contains "+data.size()/7+" bodies");
                //Menu.iniLoaded = true;

                return data.size()/7;

            }else {
                System.out.println("ERROR: FILE FORMAT NOT SUPPORTED");
                throw new Exception();
            }


        // for (float b : data){
        //    System.out.println(b);
        //}

    }

    private void readLineV1(String k){
        for (int i = 0; i < k.length(); i++){
            if (k.charAt(i)=='-'||k.charAt(i)=='0'||k.charAt(i)=='1'||k.charAt(i)=='2'||k.charAt(i)=='3'||k.charAt(i)=='4'||k.charAt(i)=='5'||k.charAt(i)=='6'||k.charAt(i)=='7'||k.charAt(i)=='8'||k.charAt(i)=='9'){
                int a = getNumberV1(i, k);

                if (a == k.length()-1){
                    data.add(Float.parseFloat(k.substring(i)));
                    i = k.length()-1;
                }else {
                    data.add(Float.parseFloat(k.substring(i, a)));
                    i = a-1;
                }



            }
        }
    }

    private int getNumberV1(int a, String k){
        for (int i = a; i < k.length(); i++){
            if (k.charAt(i)==' '||k.charAt(i)==','){
                return i;
            }
        }
        return k.length()-1;
    }

    public ArrayList<Float> getData(){
        return  data;
    }

    public int getV(){
        return v;
    }

    @Override
    public void sort(float[] x, float[] y, float[] z, float[] vx, float[] vy, float[] vz, float [] m) {

        int C = 0;



        String k [] = order.split("\\s");
        int counter=0;
        for (String current  : k){
            System.out.println(current);
        }
        for (String orderLetter : k){
            C=0;
            for (int i = counter; i<data.size();i+=k.length){

                if(orderLetter.equals("x")){
                    x[C]=data.get(i);
                    C++;
                }else if (orderLetter.equals("y")){
                    y[C]=data.get(i);
                    C++;
                }else if (orderLetter.equals("z")){
                    z[C]=data.get(i);
                    C++;
                }else if (orderLetter.equals("vx")){
                    vx[C]=data.get(i);
                    C++;
                }else if (orderLetter.equals("vy")){
                    vy[C]=data.get(i);
                    C++;
                }else if (orderLetter.equals("vz")){
                    vz[C]=data.get(i);
                    C++;
                }
                else if (orderLetter.equals("m")){
                    m[0]=data.get(i);
                    break;
                }else if (orderLetter.equals("i")){
                    break;
                }
            }
            counter++;
        }

    }

    @Override
    public int getBodyCount(String order) {
        this.order = order;
        String k [] = order.split("\\s");
        bodyCount=data.size()/k.length;
        return bodyCount;
    }
}
