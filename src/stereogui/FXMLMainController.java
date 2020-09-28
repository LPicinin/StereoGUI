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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoMatcher;
import org.opencv.calib3d.StereoSGBM;
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
    private StereoBM stereo;
    private Imgcodecs imageCodecs;
    @FXML
    private ImageView imgParidade;
    @FXML
    private ImageView imgEsq;
    @FXML
    private ImageView imgDir;
    @FXML
    private Slider sltexture;
    @FXML
    private Slider slSpeckWindowsSize;
    @FXML
    private Slider slNumberDisparities;
    @FXML
    private Slider slminDisparity;
    @FXML
    private Slider sluniqueness_ratio;
    @FXML
    private Slider slPrefilter_Cap;
    @FXML
    private Slider slPrefilter_Size;
    @FXML
    private Label lbtexture;
    @FXML
    private Label lbSpeckleWindows;
    @FXML
    private Label lbNumberDisparities;
    @FXML
    private Label lbMinDisparity;
    @FXML
    private Label lbUniqueRatio;
    @FXML
    private Label lbPreFilter_Cap;
    @FXML
    private Label lbPreFilter_Size;
    @FXML
    private RadioButton rbStereoBM;
    @FXML
    private RadioButton rbStereoSGBM;
    @FXML
    private Slider slBlockSize;
    @FXML
    private Label lbBlockSize;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        String path = (System.getProperty("user.dir") + "\\pacotes\\opencv\\build\\java\\x64\\" + Core.NATIVE_LIBRARY_NAME + ".dll");
        System.load(path);
        this.imageCodecs = new Imgcodecs();
        this.stereo = StereoBM.create(16, 5);
        atualiza_parametros();
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
        
        Mat disparity = new Mat(m_left.rows(), m_left.cols(), CvType.CV_8UC1);
        preparaStereoBMParametros();
        try
        {
            stereo.compute(m_left, m_right, disparity);
            Image dis = Mat2Image(disparity);
            imgParidade.setImage(dis);
        } catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
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

    @FXML
    private void evtDragTexture(Event event)
    {
        lbtexture.setText(Integer.toString((int)sltexture.getValue()));
        evtBuild(null);
    }

    @FXML
    private void evtDragSpeckleWindowsSize(MouseEvent event)
    {
        lbSpeckleWindows.setText(Integer.toString((int)slSpeckWindowsSize.getValue()));
        evtBuild(null);
    }

    @FXML
    private void evtDragNumberDisp(MouseEvent event)
    {
        
        lbNumberDisparities.setText(getND(slNumberDisparities.getValue()));
        evtBuild(null);
    }

    @FXML
    private void evtDragMinDisp(MouseEvent event)
    {
        lbMinDisparity.setText(Integer.toString((int)slminDisparity.getValue()));
        evtBuild(null);
    }

    @FXML
    private void evtDragUniquenessRatio(MouseEvent event)
    {
        lbUniqueRatio.setText(Integer.toString((int)sluniqueness_ratio.getValue()));
        evtBuild(null);
    }

    @FXML
    private void evtDragPrefilter_Cap(MouseEvent event)
    {
        lbPreFilter_Cap.setText(Integer.toString((int)slPrefilter_Cap.getValue()));
        evtBuild(null);
    }

    @FXML
    private void evtDragPrefilterSize(MouseEvent event)
    {
        lbPreFilter_Size.setText(Integer.toString((int)slPrefilter_Size.getValue()));
        evtBuild(null);
    }
    
    @FXML
    private void evtDragBlockSize(MouseEvent event)
    {
        lbBlockSize.setText(Integer.toString((int)slBlockSize.getValue()));
        evtBuild(null);
    }

    private void preparaStereoBMParametros()
    {
        stereo.setNumDisparities(Integer.parseInt(lbNumberDisparities.getText()));
        int odd = odd(Integer.parseInt(lbBlockSize.getText()));        
        stereo.setBlockSize(odd);
        stereo.setSpeckleWindowSize(Integer.parseInt(lbSpeckleWindows.getText()));
        stereo.setTextureThreshold(Integer.parseInt(lbtexture.getText()));
        stereo.setMinDisparity(Integer.parseInt(lbMinDisparity.getText()));
        stereo.setUniquenessRatio(Integer.parseInt(lbUniqueRatio.getText()));
        stereo.setPreFilterCap(Integer.parseInt(lbPreFilter_Cap.getText()));
        odd = odd(Integer.parseInt(lbPreFilter_Size.getText()));
        stereo.setPreFilterSize(odd);
        /**/
    }

    private void atualiza_parametros()
    {
        lbtexture.setText(Integer.toString((int)sltexture.getValue()));
        lbSpeckleWindows.setText(Integer.toString((int)slSpeckWindowsSize.getValue()));
        lbNumberDisparities.setText(getND(slNumberDisparities.getValue()));
        lbMinDisparity.setText(Integer.toString((int)slminDisparity.getValue()));
        lbUniqueRatio.setText(Integer.toString((int)sluniqueness_ratio.getValue()));
        lbPreFilter_Cap.setText(Integer.toString((int)slPrefilter_Cap.getValue()));
        lbPreFilter_Size.setText(Integer.toString((int)slPrefilter_Size.getValue()));
        lbBlockSize.setText(Integer.toString((int)slBlockSize.getValue()));
    }

    private String getND(double value)
    {
        return Integer.toString((int) value*16);
    }
    
    private int odd(int value)
    {
        if(value % 2 == 0)
            value++;
        return value;
    }

    

}
