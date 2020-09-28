/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stereogui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.opencv.calib3d.StereoBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author Luis
 */
public class FXMLMainController implements Initializable
{

    private Image left;
    private Mat m_left;
    private Image right;
    private Mat m_right;
    private Imgcodecs imageCodecs;
    @FXML
    private ImageView imgParidade;
    @FXML
    private ImageView imgEsq;
    @FXML
    private ImageView imgDir;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        String path = (System.getProperty("user.dir") + "\\pacotes\\opencv\\build\\java\\x64\\" + Core.NATIVE_LIBRARY_NAME + ".dll");
        System.load(path);
        this.imageCodecs = new Imgcodecs();

    }

    @FXML
    private void loadLeft(MouseEvent event)
    {
        FileChooser fc = new FileChooser();
        File arq = fc.showOpenDialog(null);
        left = loadImage(arq);
        m_left = loadImage_mat(arq);
        imgEsq.setImage(left);
    }

    @FXML
    private void loadRight(MouseEvent event)
    {
        FileChooser fc = new FileChooser();
        File arq = fc.showOpenDialog(null);
        right = loadImage(arq);
        m_right = loadImage_mat(arq);
        imgDir.setImage(right);
    }

    @FXML
    private void evtClear(MouseEvent event)
    {
        left = right = null;
        imgParidade.setImage(null);
    }

    @FXML
    private void evtBuild(MouseEvent event)
    {
        StereoBM stereo = StereoBM.create(16, 5);
        Mat disparity = new Mat(m_left.rows(), m_left.cols(), CvType.CV_8UC1);
        stereo.compute(m_left, m_right, disparity);

        try
        {
            Image dis = Mat2Image(disparity);
            imgParidade.setImage(dis);
        } catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        System.out.println("ok ok");
    }

    private Image loadImage(File arq)
    {

        if (arq != null)
            return new Image(arq.toURI().toString());
        else
            return null;
    }

    private Mat loadImage_mat(File arq)
    {
        if (arq != null)
            return imageCodecs.imread(arq.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
        else
            return null;
    }

    private Image Mat2Image(Mat matrix) throws Exception
    {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".png", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return SwingFXUtils.toFXImage(bi, null);
    }

}
