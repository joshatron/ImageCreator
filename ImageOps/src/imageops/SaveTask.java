package imageops;

import java.io.File;
import java.io.IOException;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;

/**
 *
 * @author David
 */
public class SaveTask extends Task<Void> {
    private final WritableImage saveImage;
    private final String fileName;
    
    /**
     * Constructor sets image and file name variables, then creates and runs a
     * thread to save the image to the specified file.
     * 
     * @param saveImage     WritableImage to save as a PNG
     * @param fileName    String name of the file to write to
     */
    public SaveTask(WritableImage saveImage, String fileName) {
        this.saveImage = saveImage;
        this.fileName = fileName;
        
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    /**
     * Implementation of the abstract call method in the Task class. Creates a
     * File object from the String passed through the constructor and writes the
     * WritableImage object to that file.
     */
    protected Void call() {
                        
        // Create a file object
        File outFile = new File(fileName);
        
        // Write snapshot to file system as a .png image
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(saveImage, null),
                "png", outFile);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(System.currentTimeMillis());
        return null;
    }
}
