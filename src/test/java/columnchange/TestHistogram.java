package columnchange;

import datamodel.Histogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestHistogram {


  public static void main(String args[]){

    // exp 1
//    List<Double> data = new ArrayList<>();
//    for(int i = 0; i < 10; i++) data.add(Math.random()*10+1);
//    System.out.println(Arrays.toString(data.toArray()));
//    Histogram his = new Histogram(data, 5);
//    System.out.println(his.toString());


    //exp 2
    double[] dataArr = {2.3323261393704486, 3.0518623180915863, 8.36318932003092, 9.95565016267215, 8.740958943341353, 4.565067185408271, 3.1587375897425343, 10.364437902617253, 6.626632811656046, 3.540533277525462};
    List<Double> data = new ArrayList<>();
    for(int i = 0; i < dataArr.length; i++) data.add(dataArr[i]);
    System.out.println(Arrays.toString(data.toArray()));
    Histogram his = new Histogram(data, 5);
    System.out.println(his.toString());
    System.out.println(his.getProbability(3.7));
    System.out.println(his.getProbability(-10, 4));
    System.out.println();
    System.out.println(his.getProbability(-1 ,1000));
    System.out.println();
    System.out.println(his.getProbability(4.5, 9.8));
    System.out.println();
    System.out.println(his.getProbability(4.5, 100));

  }
}
