/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageops;

import java.net.MalformedURLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.PriorityBlockingQueue;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class WriteImage extends Application {
    
    private static DBReader database;
    private static int dimensionality = 256;
    private static BlockingQueue<ImageTask> tasks = new PriorityBlockingQueue<>();
    
    @Override
    public void start(Stage primaryStage) throws MalformedURLException, InterruptedException, ExecutionException {
        
        // Create a TaskService instance to manage ImageTask thread creation
        TaskService imageManager = new TaskService(tasks);
        
        int saveCount = 0;
        while (saveCount < 240) {
            if (!tasks.isEmpty() && tasks.peek().isDone()) {
                
                String fileName = String.format("C:\\Users\\David\\Documents\\NetBeansProjects\\ImageOpTest\\heatmaps\\image%d.png", ++saveCount);
                    
                Scene scene = new Scene(tasks.poll().getPane(), 256, 256);
                WritableImage write = scene.snapshot(null);
        
                SaveTask saveImage = new SaveTask(write, fileName);
            }
        }
        
    }

    public static void main(String[] args) throws MalformedURLException {
        // TIMESTAMP ON IMAGES
        //              startdate    starttime    enddate     endtime   events  heatmap
        //args[] order: "yyyy/mm/dd" "hh:mm:ss" "yyyy/mm/dd" "hh:mm:ss" "none" "entropy"
        //heatmap flags = entropy, mean, std_deviation, fractal_dim, skewness, t_directionality, t_contrast, rel_smoothness, uniformity, kurtosis, raw
        //event flags = ar, ch, fi, fl, sg, ss
//        String[] input = new String[6];
//        int index = 0;
//        for (String in: args) {
//            input[index++] = in;
//            System.out.println(input[index]);
//        }
//        
//        if (index != 6) {
//            System.err.println("Requires six inputs");
//        }
//        
//        WriteImage.database = new DBReader(input[0], input[1], input[2], input[3], input[4], input[5]);
        System.out.println(System.currentTimeMillis());
        launch(args);
    }
}
