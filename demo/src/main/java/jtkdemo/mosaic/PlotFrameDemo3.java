/****************************************************************************
  Copyright 2006, Colorado School of Mines and others.
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
import javax.swing.*;

import edu.mines.jtk.mosaic.*;
import static edu.mines.jtk.util.ArrayMath.*;

/**
 * A simple demonstration of {@link edu.mines.jtk.mosaic.PlotFrame}.
 * It also demonstrates how to synchronize the widths of two panels.
 * @author Dave Hale, Colorado School of Mines
 * @version 2006.07.03
 * @author Werner M. Heigl, NanoSeis
 * @version 2021.04.22
 */
public class PlotFrameDemo3 {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new PlotFrameDemo3();
      }
    });
  }

  public PlotFrameDemo3() {
    // create data
    float[] x = rampfloat(0.0f,4.0f*FLT_PI/200.0f,201);
    float[] s = sin(x);
    float[] c = mul(10000f, cos(x));

    _plotPanelTL = new PlotPanel();
    _plotPanelTL.setTitle("The sine function");
    _plotPanelTL.setHLabel("x");
    _plotPanelTL.setVLabel("sin(x)");
    _gridViewTL = _plotPanelTL.addGrid();
    _pointsViewTL = _plotPanelTL.addPoints(x,s);
    _pointsViewTL.setStyle("r-o");
    _plotPanelTL.getTile(0,0).addMouseListener(_ml);

    _plotPanelBR = new PlotPanel();
    _plotPanelBR.setTitle("The cosine function");
    _plotPanelBR.setHLabel("x");
    _plotPanelBR.setVLabel("10000*cos(x)");
    _gridViewBR = _plotPanelBR.addGrid();
    _pointsViewBR = _plotPanelBR.addPoints(x,c);
    _pointsViewBR.setStyle("r-o");
    _plotPanelBR.getTile(0,0).addMouseListener(_ml);

    _plotFrame = new PlotFrame(_plotPanelTL, _plotPanelBR, PlotFrame.Split.VERTICAL);
    _plotFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    _plotFrame.setFont(new Font("Arial",Font.PLAIN,14));
    _plotFrame.add(
        new Label(" In either plot or axes, click-drag to zoom, click to unzoom."),
        BorderLayout.NORTH);
    _plotFrame.add(
        new Label(" Shift+Mouse provides mosaic sizes, Right MouseButton adjust widths"),
        BorderLayout.SOUTH);
    //_plotFrame.pack();
    _plotFrame.setVisible(true);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // private

  private static final void trace(String s) { System.out.println(s); };

  private PlotFrame _plotFrame;
  private PlotPanel _plotPanelTL;
  private PointsView _pointsViewTL;
  private GridView _gridViewTL;
  private PlotPanel _plotPanelBR;
  private PointsView _pointsViewBR;
  private GridView _gridViewBR;

  private MouseListener _ml = new MouseAdapter() {
    public void mousePressed(MouseEvent e) {
      Object source = e.getSource();
      if (e.isShiftDown()) {  // Ctrl+Alt+Mouse is caught in TileZoomMode
        if (source instanceof Tile) {
          Tile tile = (Tile)source;
          Mosaic mosaic = tile.getMosaic();
          double pwidth = mosaic.getSize().getWidth();
          double pheight = mosaic.getSize().getHeight();
          double pratio = pwidth/pheight;
          System.out.printf("Mosaic: pixel width  = %1d%n",(int)pwidth);
          System.out.printf("        pixel height = %1d%n",(int)pheight);
          System.out.printf("        pixel ratio  = %1.4g%n",pratio);
          pwidth = tile.getWidth();
          pheight = tile.getHeight();
          pratio = pwidth/pheight; 
          System.out.printf("Tile:   pixel width  = %1d%n",(int)pwidth);
          System.out.printf("        pixel height = %1d%n",(int)pheight);
          System.out.printf("        pixel ratio  = %1.4g%n",pratio);
          TileAxis axis = mosaic.getTileAxisLeft(0);
          pwidth = axis.getWidth();
          pheight = axis.getHeight();
          pratio = pwidth/pheight; 
          System.out.printf("Axis:   pixel width  = %1d%n",(int)pwidth);
          System.out.printf("        pixel height = %1d%n",(int)pheight);
          System.out.printf("        pixel ratio  = %1.4g%n",pratio);
        }
      } else if (e.getButton() == MouseEvent.BUTTON3) {  // synchronizes widths in plot panels
        if (source instanceof Tile) {
          // find the panel with the narrower tile
          Tile wideTile = _plotPanelTL.getTile(0,0);
          Tile narrowTile = _plotPanelBR.getTile(0,0);
          if (_plotPanelTL.getTile(0,0).getWidth() < _plotPanelBR.getTile(0,0).getWidth()) {
            narrowTile = _plotPanelTL.getTile(0,0);
            wideTile = _plotPanelBR.getTile(0,0);
            trace("plotPanelBR has the wide tile");
          } else {
            trace("plotPanelTL has the wide tile");
          }
          // synchronize widths of components in mosaic with wide tile with those in mosaic
          // with narrow tile
          // try left axis
          Mosaic mosaic = wideTile.getMosaic();
          Dimension leftAxisSize = narrowTile.getMosaic().getTileAxisLeft(0).getSize();
          Dimension tileSize = narrowTile.getMosaic().getTile(0,0).getSize();
          Dimension bottomAxisSize = narrowTile.getMosaic().getTileAxisBottom(0).getSize();
          mosaic.getTileAxisLeft(0).setWidthMinimum(leftAxisSize.width);
          trace("axisLeft:   " + wideTile.getMosaic().getTileAxisLeft(0).getSize().toString());
          trace("tile:       " + wideTile.getMosaic().getTile(0,0).getSize().toString());
          trace("axisBottom: " + wideTile.getMosaic().getTileAxisBottom(0).getSize().toString());
        }
      }
    }
  };

}
