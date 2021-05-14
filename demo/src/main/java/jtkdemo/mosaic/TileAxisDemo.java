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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.util.Pair;
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
 * @version 2021.05.14
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

    Arrays.setAll(secondaryKeyTics, d -> d+1); // start at 1
    Arrays.setAll(primaryKeyTics, d -> d+0.1); // there are 10x more secondary tics

    PixelsView pxv0 = new PixelsView(s1,s2,vertData);
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

    mosaic.getTileAxisTop(0).setLabel(slowAxisLabel);
    mosaic.getTileAxisTop(0).addMouseListener(_ml);
    mosaic.getTileAxisLeft(0).setLabel(fastAxisLabel);
    mosaic.getTileAxisLeft(0).addMouseListener(_ml);
    mosaic.getTileAxisRight(0).setLabel(fastAxisLabel);
    mosaic.getTileAxisRight(0).addMouseListener(_ml);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(changeSlowAxisStateButton(mosaic));
    buttonPanel.add(changeDataStateButton(mosaic));
 
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(
        new Label("  Shift-click on any axis to display info about axis tics."),
        BorderLayout.NORTH);
    frame.add(mosaic,BorderLayout.CENTER);
    frame.add(buttonPanel,BorderLayout.SOUTH);
    frame.setSize(500,750);
    frame.setLocation(100,0);
    frame.setVisible(true);

  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // private

  private static final void trace(String s) { System.out.println(s); }

  private static final Sampling s1 = new Sampling(151,0.03,-1.3); // vertical axis
  private static final Sampling s2 = new Sampling(101,1.0,1.0);   // horizontal axis
  private static final float[][] vertData = sin(rampfloat(0.0f,0.1f,0.1f,151,101)); // fake seismic frame
  private static final float[][] horzData = ArrayMath.transpose(vertData);

  private static final String slowAxisLabel = "Slow Axis";
  private static final String fastAxisLabel = "Fast Axis";
  private static final String primaryKeyLabel = "Primary Key";
  private static final String secondaryKeyLabel = "Secondary Key";
  private static double[] primaryKeyTics = new double[11];
  private static double[] secondaryKeyTics = new double[101];

  private static DataState dataState = DataState.VERTICAL; // initial vertical data
  private static TicsState ticsState = TicsState.DEFAULT;  // default tics on slow axis
  private static ViewState viewState = ViewState.VERTICAL_DEFAULT;

  private static JButton changeSlowAxisStateButton(Mosaic mosaic) {
    final JButton b = new JButton("Toggle Slow Axis State");
    b.setToolTipText("Toggles between default and custom axis tics");
    b.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ticsState = (ticsState==TicsState.DEFAULT?TicsState.CUSTOM:TicsState.DEFAULT);
        nextViewState(mosaic);
      } 
    });
    return b;
  }

  private static JButton changeDataStateButton(Mosaic mosaic) {
    final JButton b = new JButton("Toggle Data State");
    b.setToolTipText("Toggles between vertical and horizontal traces");
    b.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dataState = (dataState==DataState.HORIZONTAL?DataState.VERTICAL:DataState.HORIZONTAL);
        nextViewState(mosaic);
      }
    });
    return b;
  }

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

  // Changing the view is implemented as a finite state machine.

  private enum DataState {
    VERTICAL, HORIZONTAL;
  }

  private enum TicsState {
    DEFAULT, CUSTOM;
  }

  private enum ViewState {
      VERTICAL_DEFAULT(DataState.VERTICAL, TicsState.DEFAULT),
      VERTICAL_CUSTOM(DataState.VERTICAL, TicsState.CUSTOM),
      HORIZONTAL_DEFAULT(DataState.HORIZONTAL, TicsState.DEFAULT),
      HORIZONTAL_CUSTOM(DataState.HORIZONTAL, TicsState.CUSTOM);
      private static final Map<Pair<DataState,TicsState>,ViewState> BY_KEYS = new HashMap<>();
      static {
        for (ViewState viewState: values())
          BY_KEYS.put(new Pair(viewState.dataState,viewState.ticsState),viewState);
      }
      private static ViewState valueOfKeys(Pair keys) {
        return BY_KEYS.get(keys);
      }
      private final DataState dataState;
      private final TicsState ticsState;
      private ViewState(DataState dataState, TicsState ticsState) {
        this.dataState = dataState;
        this.ticsState = ticsState;
      }
  }

  private static void nextViewState(Mosaic mosaic) {
    TileAxis topAxis = mosaic.getTileAxisTop(0);
    TileAxis leftAxis = mosaic.getTileAxisLeft(0);
    TileAxis rightAxis = mosaic.getTileAxisRight(0);
    PixelsView pxv = (PixelsView)mosaic.getTile(0,0).getTiledView(0);
    viewState = ViewState.valueOfKeys(new Pair(dataState,ticsState));
    trace("viewState="+viewState.name());
    switch (viewState) {
      case VERTICAL_DEFAULT:
        leftAxis.setCustomTics(false);
        leftAxis.setLabel(fastAxisLabel);
        topAxis.setCustomTics(false);
        topAxis.setLabel(slowAxisLabel);
        rightAxis.setLabel(fastAxisLabel);
        rightAxis.setCustomTics(false);
        pxv.set(s1,s2,vertData);
        break;
      case VERTICAL_CUSTOM:
        leftAxis.setCustomTics(false);
        leftAxis.setLabel(fastAxisLabel);
        topAxis.getAxisTics().setCustomTicsPrimary(primaryKeyTics, primaryKeyLabel);
        topAxis.getAxisTics().setCustomTicsSecondary(secondaryKeyTics, secondaryKeyLabel);
        topAxis.setCustomTics(true);
        rightAxis.setLabel(fastAxisLabel);
        rightAxis.setCustomTics(false);
        pxv.set(s1,s2,vertData);
        break;
      case HORIZONTAL_DEFAULT:
        leftAxis.setCustomTics(false);
        leftAxis.setLabel(slowAxisLabel);
        topAxis.setCustomTics(false);
        topAxis.setLabel(fastAxisLabel);
        rightAxis.setCustomTics(false);
        rightAxis.setLabel(slowAxisLabel);
        pxv.set(s2,s1,horzData);
        break;
      case HORIZONTAL_CUSTOM:
        leftAxis.getAxisTics().setCustomTicsPrimary(primaryKeyTics, primaryKeyLabel);
        leftAxis.getAxisTics().setCustomTicsSecondary(secondaryKeyTics, secondaryKeyLabel);
        leftAxis.setCustomTics(true);
        topAxis.setCustomTics(false);
        topAxis.setLabel(fastAxisLabel);
        rightAxis.getAxisTics().setCustomTicsPrimary(primaryKeyTics, primaryKeyLabel);
        rightAxis.getAxisTics().setCustomTicsSecondary(secondaryKeyTics, secondaryKeyLabel);
        rightAxis.setCustomTics(true);
        pxv.set(s2,s1,horzData);
        break;
      default:
        break;
    }
  }

}
