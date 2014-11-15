/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package imageops;

import java.util.concurrent.BlockingQueue;
import javafx.concurrent.Task;

/**
 *
 * @author David
 */
public class TaskService extends Task<Void> {
 
    private final BlockingQueue<ImageTask> tasks;
    
    public TaskService(BlockingQueue tasks) {
        this.tasks = tasks;
        
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    /**
     * Implementation of the abstract call method in the Task class. 
     * Creates ImageTask objects.
     */
    protected Void call() {
        
        //keep track of the timestamp.
            int[] time = new int[4];
            time[0] = 0;
            time[1] = 0;
            time[2] = 0;
            time[3] = 0;
                
            float scalar = setScalar(2);

            for (int index = 0; index < 240; index++) {

                if (time[2] == 5) {                 // Roll over hours
                    time[1]++;                          // Add one hour
                    time[0] += time[1] / 10;            // Add 10 hours if hour ones-digit = 10
                    time[1] -= time[1] - time[1] % 10;  // Subtract 10 hours if ones-digit = 10
                    time[2] = 0;                        // Roll over minute tens-digit
                    time[3] = 0;                        // Roll over minute ones-digit
                } else {
                    time[2] += time[3] / 10;            // Correct minute tens-digit
                    time[3] -= time[3] - time[3] % 10;  // Correct minute ones-digit
                }
                
                String fileName = String.format("AIA20120101_%d%d%d%d11_0131.txt", time[0], time[1], time[2], time[3]);
                final String imageName = String.format("AIA20120101_%d%d%d%d11_0131_th.png", time[0], time[1], time[2], time[3]);
                
                time[3] += 6;
                tasks.add(new ImageTask(imageName, null, scalar, fileName));
            }
        
        return null;
    }
    
    /**
     * Set the scalar to convert measurement values to heat map pixel colors
     * @param measurementID
     * @return 
     */
    private float setScalar(int measurementID) {
        float scale;
        switch(measurementID) {
            case 1:
                scale = 1.27188f;
                break;
            case 2:
                scale = 35.456f;
                break;
//            case 3:
//                scalar = ;
//                break;
//            case 4:
//                scalar = ;
//                break;
//            case 5:
//                scalar = ;
//                break;
//            case 6:
//                scalar = ;
//                break;
//            case 7:
//                scalar = ;
//                break;
//            case 8:
//                scalar = ;
//                break;
            case 9:
                scale = 65384f;
                break;
            case 0:
                scale = 7.075f;
                break;
            default:
                scale = 0f;
                break;
        }
        
        return scale;
    }
}
