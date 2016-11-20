/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author admin
 * this will initiate the network
 * 
 */
public class NnSetup {
    
    /**
     * the input level is an array list because we have the entore text corups as an input
     * 
     */
    public void initializeNetwork(ArrayList input, int hiddenLevels, ArrayList output){
        
        /**
         * we calculate the number of nodes in the hidden levels like this:
         * difference between output and input divided bz hidden levels +1
         */
        
        int inputLevelNodes = input.size();
        int outputLevelNodes = output.size();
        
        HashMap nodeSizes = new HashMap();
        
        int step = (inputLevelNodes - outputLevelNodes) / (hiddenLevels + 1);
        int tmp = 0; 
        
        for(int i = 1; i <= hiddenLevels; i++ ){
            tmp += step;
            nodeSizes.put(i, tmp);            
        }
        
        
    }
    
    
}
