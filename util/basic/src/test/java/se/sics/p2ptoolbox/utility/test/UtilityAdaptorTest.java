package se.sics.p2ptoolbox.utility.test;

import se.sics.p2ptoolbox.util.ProbabilitiesHelper;

import java.util.*;
import java.util.logging.Logger;

/**
 * Simple Test for the Utility Adaptor Impl.
 *  
 * Created by babbarshaer on 2015-03-03.
 */

public class UtilityAdaptorTest {

    private static Map<Integer, Integer> responseMap = new HashMap<Integer, Integer>();
    
    public static void main(String[] args) {

        final int seed = 23;
        final double temperature = Integer.MAX_VALUE;
        final int viewSize = 20;
        final int iterations = 10000;
        
        
        Random random = new Random(seed);
        
        
        for(int i =0 ; i < iterations ; i++){
            int val = ProbabilitiesHelper.getSoftMaxVal(viewSize,random, temperature);
            
            if(responseMap.get(val) == null){
                responseMap.put(val,1);
            }
            else{
                responseMap.put(val, responseMap.get(val)+1);
            }
        }

        System.out.println("Finished running the simulation");
        System.out.println("Temperature: " + temperature + " ViewSize: " + viewSize + " Iterations: " + iterations);

        for(Map.Entry<Integer, Integer> entry : responseMap.entrySet()){
            System.out.println("Index : " + entry.getKey() + " Number of Hits: " + entry.getValue());
        }
    }
}
