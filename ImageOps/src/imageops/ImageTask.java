/**
 * LICENSE
 */

package imageops;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * -ImageTask creates a thread to make one PNG frame for FFmpeg video conversion
 * -The PNG is saved to a fixed location and is named incrementally
 * -Must be invoked by WriteImage
 * 
 * @author David
 */
public class ImageTask extends Task<Void> implements Comparable {

    private float[][] grid;       // Grid of values for the heatmap
    private final String imagePath;     // Absolute file name of raw image
    private final float scalar;         // Scalar to convert grid values to colors
    private final String id;
    private final String[] data;
    private StackPane root;
    /**
     * Initialize all variables
     * @param imagePath
     * @param grid
     * @param scalar
     * @param id
     */
    public ImageTask(String imagePath, float[][] grid, float scalar, String id) {
        
        this.imagePath = imagePath;
        //this.grid = grid;
        this.scalar = scalar;
        this.id = id;
        this.data = null;
        
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    /**
     * -Implementation of abstract method Task.call()
     * -Contains the logic for the ImageTask class
     * -Increments/decrements threadCount at the start/end of Task execution to 
     *  limit the number of active threads
     */
    protected Void call() throws Exception {
        readFile(id);
        Image saveImage = null;     // Image for raw PNG
        Image heatmap = null;       // Image for heat map
        
        // Load raw PNG
        try {
            saveImage = new Image(new File(imagePath).toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(WriteImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Create heat map
        if (scalar != 0) 
            heatmap = createHeatMap();
        
        float opacity = 1f;     // How opaque the heat map will be
        int dim = 256;          // Dimensionality of images
        
        // ImageView: "frame" objects for individual images
        ImageView raw = new ImageView();
        ImageView heat = new ImageView();
        
        // StackPane: "canvas" where the final image is built
        root = new StackPane();
        
        // Add raw image to root pane
        if (saveImage != null) {
            raw.setImage(saveImage);
            opacity = 0.35f;
            dim = (int) saveImage.getWidth();
        }
        
        // Scale image, set opacity, and add heat map to root pane
        if (heatmap != null) {
            heat.setImage(heatmap);
            heat.setFitWidth(dim);
            heat.setPreserveRatio(true);
            heat.setOpacity(opacity);
        }
        
        root.getChildren().add(raw);
        root.getChildren().add(heat);
        
        return null;
    }
    
    // Calculate and return color for a heat map pixel
    public Color getHeatColor(float dataPoint) {
        dataPoint *= scalar;
        dataPoint = 255f - dataPoint;
        Color color = Color.hsb(dataPoint, 1f, 1f);
        return color;
    }
    
    // Create heat map from grid[][] values
    public WritableImage createHeatMap() {
        // Create WritableImage
        WritableImage makeImage = new WritableImage(64, 64);
            
        PixelWriter pixelWriter = makeImage.getPixelWriter();

        // Write the color of each pixel
        for (int yLoc = 0; yLoc < makeImage.getHeight(); yLoc++) {
            for (int xLoc = 0; xLoc < makeImage.getWidth(); xLoc++) {
                Color color = getHeatColor(grid[yLoc][xLoc]);

                // Set the color of 4x4 pixel grid where top left = (readX, readY
                pixelWriter.setColor(xLoc, yLoc, color);
            }
        }
        return makeImage;
    }

    @Override
    // compareTo() for Comparable
    public int compareTo(Object o) {
        return this.toString().compareTo(o.toString());
    }
    
    @Override
    // toString() for Comparable
    public String toString() {
        return id;
    }
    
    // Retrieve StackPane object from this thread
    public synchronized StackPane getPane() {
        return root;
    }
    
    // Populate heat map grid from String[] data
    public void populate() {
        for (String nextLine : data) {
            String[] allTokens = nextLine.split("\\s");
            
            int x = Integer.parseInt(allTokens[0]);
            int y = Integer.parseInt(allTokens[1]);
            grid[y][x] = Integer.parseInt(allTokens[2]);
        }
    }
    
    /**
     * Reads raw data files into float arrays for testing purposes.
     * @param fileName
     * @return
     * @throws IOException 
     */
    public float[][] readFile(String fileName) throws IOException {
        grid = new float[64][64];
        // read in a text file
        try {
            BufferedReader read = new BufferedReader(new FileReader(fileName));
            String next;
            for (int yloc = 0; yloc < 64; yloc++) {
                for (int xloc = 0; xloc < 64; xloc++) {
                    next = read.readLine();
                    int index = next.indexOf('.') - 1;
                    int end = index + 6;
                    while (next.charAt(index - 1) > 44) {
                        index--;
                    }
                    next = next.substring(index, end);
                    grid[yloc][xloc] = Float.valueOf(next);
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(WriteImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return grid;
    }
}
