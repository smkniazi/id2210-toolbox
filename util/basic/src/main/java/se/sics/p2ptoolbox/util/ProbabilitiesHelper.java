package se.sics.p2ptoolbox.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Adaptor for the utilities to be used internally by the services.
 *  
 * Created by babbarshaer on 2015-03-03.
 */
public class ProbabilitiesHelper {

    /**
     *  Soft Max approach based on the provided temperature for the run, provides you the index of the sorted list.
     *  The temperature is a double value with bounds [0,infinity). 
     *  
     *  CASE 1: Temperature = 0, the outcome will always be the nearby entry in the list i.e size() - 1 index is returned.
     *  CASE 2: Temperature > 0, the outcome in this case involves the entries mostly present at top, higher utility nodes.
     *  CASE 3: Temperature = high, for a high enough value of temperature like Integer.MAX_VALUE, the randomness in the returned sample set increases.
     *  
     *  <i> Having said that, the value of the temperature for different scenarios like best, random, top half of the set defined depends on trial and error. :)</i>
     *
     * @param size 
     *              Size of view 
     * @param random
     *              Random Variable. 
     * @param temperature
     *              Temperature to control the bias in outcome.
     *
     * @return value chosen
     */
    public static int getSoftMaxVal(int size, Random random, double temperature){
        
        double rnd = random.nextDouble();
        double total = 0.0d;
        double[] values = new double[size];
        int j = size + 1;
        for (int i = 0; i < size; i++) {
            // get inverse of values - lowest have highest value.
            double val = j;
            j--;
            values[i] = Math.exp(val / temperature);
            total += values[i];
        }

        for (int i = 0; i < values.length; i++) {
            if (i != 0) {
                values[i] += values[i - 1];
            }
            // normalise the probability for this entry
            double normalisedUtility = values[i] / total;
            if (normalisedUtility >= rnd) {
                return i;
            }
        }
        
        return size - 1;
    }
}
