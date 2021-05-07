/****************************************************************************
Copyright 2004, Colorado School of Mines and others.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
****************************************************************************/
package jtkdemo.mosaic;

import java.awt.*;
import java.awt.event.*;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import javax.swing.*;

import edu.mines.jtk.awt.ColorMap;
import edu.mines.jtk.dsp.Sampling;
import edu.mines.jtk.mosaic.*;
import edu.mines.jtk.util.*;
import static edu.mines.jtk.util.ArrayMath.*;


/**
 * Demo of {@link edu.mines.jtk.mosaic.TileAxis} illustrating how to add
 * customary tic labels.
 * @author Werner M. Heigl, NanoSeis
 * @version 2021.05.04
 */
public class TileAxisDemo {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new TileAxisDemo();
      }
    });
  }

  // creates a simple mosaic with one view to test the labeling of the top
  // axis with two sets of custom labels
  public TileAxisDemo() {

    int n1 = 101;
    double d1 = 0.03;
    double f1 = -1.3;
    Sampling s1 = new Sampling(n1,d1,f1);
    int n2 = 101;
    double d2 = 0.03;
    double f2 = 0.0;
    Sampling s2 = new Sampling(n2,d2,f2);
    float[][] f = sin(rampfloat(0.0f,0.1f,0.1f,n1,n2));

    //PixelsView pxv0 = new PixelsView(s1,s2,f);
    PixelsView pxv0 = new PixelsView(f);
    pxv0.setColorModel(ColorMap.GRAY);
    pxv0.setOrientation(PixelsView.Orientation.X1DOWN_X2RIGHT);
 
    Set<Mosaic.AxesPlacement> axesPlacement = EnumSet.of(
      Mosaic.AxesPlacement.LEFT,
      Mosaic.AxesPlacement.RIGHT,
      Mosaic.AxesPlacement.TOP
    );
    Mosaic mosaic = new Mosaic(1,1,axesPlacement);
    mosaic.setBackground(Color.WHITE);

    TileZoomMode zoomMode = new TileZoomMode(mosaic.getModeManager());
    zoomMode.setActive(true);

    mosaic.getTile(0,0).addTiledView(pxv0);

    mosaic.getTileAxisTop(0).setLabel("Top Axis");
    mosaic.getTileAxisTop(0).addMouseListener(_ml);
    mosaic.getTileAxisLeft(0).setLabel("Left Axis");
    mosaic.getTileAxisLeft(0).addMouseListener(_ml);
    mosaic.getTileAxisRight(0).setLabel("Right Axis");
    mosaic.getTileAxisRight(0).addMouseListener(_ml);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(changeTopAxisButton(mosaic.getTileAxisTop(0)));
 
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(
        new Label("  Shift-click on any axis to display info about axis tics."),
        BorderLayout.NORTH);
    frame.add(mosaic,BorderLayout.CENTER);
    frame.add(buttonPanel,BorderLayout.SOUTH);
    frame.setSize(600,500);
    frame.setLocation(500,0);
    frame.setVisible(true);

  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // private

  private static final void trace(String s) { System.out.println(s); };

  // prints out information about axis tics and projectors
  private MouseListener _ml = new MouseAdapter() {
    public void mousePressed(MouseEvent e) {
      Object source = e.getSource();
      if (e.isShiftDown()) {
        if (source instanceof TileAxis) {
          TileAxis axis = (TileAxis)source;
          trace(axis.toString());
        }
      }
    }
  };

  private static JButton changeTopAxisButton(TileAxis topAxis) {
    final JButton b = new JButton("Change Top Axis Labels");
    b.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        double[] secondary = new double[] { 1,2,3,4,5,6,7,8,9,10,11 };
        double[] primary = new double[] { 1,1,1,2,2,2,3,3,3,4,4 };
        topAxis.getAxisTics().setCustomTicsPrimary(primary, "Primary Key");
        topAxis.getAxisTics().setCustomTicsSecondary(secondary, "Secondary Key");
        topAxis.setCustomTics(true);  // triggers repainting
        trace(topAxis.toString());
      }
    });
    return b;
  }


}
