
package jtkdemo.mosaic;

import java.awt.*;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.*;
import edu.mines.jtk.awt.ColorMap;
import edu.mines.jtk.dsp.Sampling;
import edu.mines.jtk.mosaic.*;
import static edu.mines.jtk.util.ArrayMath.*;

/**
 * Demo {@link edu.mines.jtk.mosaic.Mosaic} and associates.
 * @author Werner M. Heigl, NanoSeis
 * @version 2004.12.27
 * @see MosaicDemo
 */
public class MosaicDemo2 {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        go();
      }
    });
  }
  private static void go() {
    int nrow = 1;
    int ncol = 2;
    Set<Mosaic.AxesPlacement> axesPlacement = EnumSet.of(
      Mosaic.AxesPlacement.TOP,
      Mosaic.AxesPlacement.LEFT,
      Mosaic.AxesPlacement.BOTTOM,
      Mosaic.AxesPlacement.RIGHT
    );
    Mosaic mosaic = new Mosaic(nrow,ncol,axesPlacement);
    mosaic.setBackground(Color.WHITE);
    mosaic.setFont(new Font("SansSerif",Font.PLAIN,12));

    int n1 = 101;
    double d1 = 0.03;
    double f1 = -1.3;
    Sampling s1 = new Sampling(n1,d1,f1);
    int n2 = 101;
    double d2 = 0.029;
    double f2 = 0.033;
    Sampling s2 = new Sampling(n2,d2,f2);
    float[][] f = sin(rampfloat(0.0f,0.1f,0.1f,n1,n2));
    
    PixelsView pxv0 = new PixelsView(s1,s2,f);
    pxv0.setColorModel(ColorMap.GRAY);
    mosaic.getTile(0,0).addTiledView(pxv0);
    PixelsView pxv1 = new PixelsView(s1,s2,f);
    pxv1.setColorModel(ColorMap.JET);
    mosaic.getTile(0,1).addTiledView(pxv1);

    mosaic.getTileAxisTop(0).setLabel("axis label");
    mosaic.getTileAxisTop(1).setLabel("axis label");
    mosaic.getTileAxisLeft(0).setLabel("axis label");
    mosaic.getTileAxisBottom(0).setLabel("axis label");
    mosaic.getTileAxisBottom(1).setLabel("axis label");
    mosaic.getTileAxisRight(0).setLabel("axis label");

    TileZoomMode zoomMode = new TileZoomMode(mosaic.getModeManager());
    zoomMode.setActive(true);
    MouseTrackMode trackMode = new MouseTrackMode(mosaic.getModeManager());
    trackMode.setActive(true);

    JLabel dataReadOutLabel = new JLabel(" << Move mouse into a tile >>  ");
    DataReadOutMode dataReadOutMode = new DataReadOutMode(mosaic.getModeManager());
    dataReadOutMode.setDestination(dataReadOutLabel);
    dataReadOutMode.setDataSource(f);
    dataReadOutMode.setZFormat("%,.3f");
    dataReadOutMode.setHSampling(s1);
    dataReadOutMode.setVSampling(s2);
    dataReadOutMode.setActive(true);
    
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(dataReadOutLabel,BorderLayout.NORTH);
    frame.add(mosaic,BorderLayout.CENTER);
    frame.setSize(800,500);
    frame.setLocation(600,0);
    frame.setVisible(true);
  }
    
}
