
package edu.mines.jtk.mosaic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import edu.mines.jtk.awt.Mode;
import edu.mines.jtk.awt.ModeManager;

/**
 * A mode for displaying the data values and world coordinates at the
 * mouse location. When this mode is active, the horizontal and vertical
 * world cooridnates together with the sample value at those coordinates
 * are displayed in a user-specified JLabel.
 * This mode is based on MouseTrackMode.
 * @author Werner M. Heigl, NanoSeis
 * @version 2021.03.04
 */
public class DataReadOutMode extends Mode {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a data readout  mode with specified manager.
     * @param modeManager the mode manager for this mode.
     */
    public DataReadOutMode(ModeManager modeManager) {
	super(modeManager);
	setName("DataReadOut");
	setIcon(loadIcon(MouseTrackMode.class,"Track24.gif"));
	setMnemonicKey(KeyEvent.VK_Z);
	setAcceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_T,0));
	setShortDescription("Data readout in tile");
    }

    /**
     * Returns false, to indicate that data readout mode is not exclusive.
     * @return false.
     */
    public boolean isExclusive() {
	return false;
    }

    /**
     * Registers the source of the data.
     * @param data the data displayed in the tile
     */
    public void setDataSource(float[][] data) {
	_dataSource = data;
    }

    /**
     * Registers the destination for the readout.
     * @param label the JLabel displaying the data readout
     */
    public void setDestination(JLabel label) {
	_destination = label;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // protected
  
    protected void setActive(Component component, boolean active) {
	if ((component instanceof Tile)) {
	    if (active) {
		component.addMouseListener(_ml);
	    } else {
		component.removeMouseListener(_ml);
	    }
	}
    }

    ///////////////////////////////////////////////////////////////////////////
    // private

    private Tile _tile; // tile in which tracking; null, if not tracking.
    private int _xmouse; // x coordinate where mouse last tracked
    private int _ymouse; // y coordinate where mouse last tracked
    private float[][] _dataSource; // the data displayed in the tile
    private JLabel _destination; // the JLabel displaying the readout
    
    private MouseListener _ml = new MouseAdapter() {
	    public void mouseEntered(MouseEvent e) {
		beginTracking(e);
	    }
	    public void mouseExited(MouseEvent e) {
		endTracking();
	    }
	};

    private MouseMotionListener _mml = new MouseMotionAdapter() {
	    public void mouseDragged(MouseEvent e) {
		duringTracking(e);
	    }
	    public void mouseMoved(MouseEvent e) {
		duringTracking(e);
	    }
	};

    private void beginTracking(MouseEvent e) {
	_xmouse = e.getX();
	_ymouse = e.getY();
	_tile = (Tile)e.getSource();
 
	fireTrack();
	_tile.addMouseMotionListener(_mml);
    }
    private void beginTracking(TileAxis ta, int x, int y) {
	if (ta!=null)
	    ta.beginTracking(x,y);
    }

    private void duringTracking(MouseEvent e) {
	_xmouse = e.getX();
	_ymouse = e.getY();
	_tile = (Tile)e.getSource();
 
	fireTrack();
    }
    private void duringTracking(TileAxis ta, int x, int y) {
	if (ta!=null)
	    ta.duringTracking(x,y);
    }

    private void endTracking() {
	_tile.removeMouseMotionListener(_mml);
 
	fireTrack();
	_tile = null;
    }
    private void endTracking(TileAxis ta) {
	if (ta!=null)
	    ta.endTracking();
    }

    // Someday this might fire data readout events to any listeners.
    // Currently, this method does nothing.
    private void fireTrack() {
	/*
	  if (_tile==null) {
	  } else {
	  Projector hp = _tile.getHorizontalProjector();
	  Projector vp = _tile.getVerticalProjector();
	  Transcaler ts = _tile.getTranscaler();
	  double ux = ts.x(_xmouse);
	  double uy = ts.y(_ymouse);
	  double vx = hp.v(ux);
	  double vy = vp.v(uy);
	  }
	*/
    }
}

