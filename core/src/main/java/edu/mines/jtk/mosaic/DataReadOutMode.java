
package edu.mines.jtk.mosaic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import edu.mines.jtk.awt.Mode;
import edu.mines.jtk.awt.ModeManager;
import edu.mines.jtk.dsp.Sampling;

/**
 * A mode for displaying the data point in  world coordinates nearest to the
 * mouse location. When this mode is active, the horizontal and vertical
 * world coordinates together with the sample value at the mouse location
 * are displayed in a user-specified JLabel.
 * This mode is based on MouseTrackMode.
 * @author Werner M. Heigl, NanoSeis
 * @version 2021.03.05
 * @see MouseTrackMode
 */
public class DataReadOutMode extends Mode {
    
    private static final long serialVersionUID = 1L;
    private static final String defaultFormat = "%,.2f";

    /**
     * Constructs a data readout mode with specified manager.
     * @param modeManager the mode manager for this mode.
     */
    public DataReadOutMode(ModeManager modeManager) {
	super(modeManager);
	setName("DataReadOut");
	setIcon(loadIcon(MouseTrackMode.class,"Track24.gif"));
	setMnemonicKey(KeyEvent.VK_Z);
	setAcceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_T,0));
	setShortDescription("Data readout in tile");
	this._hFormat = defaultFormat;
	this._vFormat = defaultFormat;
	this._zFormat = defaultFormat;
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

    /**
     * Sets the format for the horizontal coordinate.
     * @param format a format string
     */
    public void setHFormat(String format) {
	this._hFormat = format;
    }
    
    /**
     * Sets the format for the vertical coordinate.
     * @param format a format string
     */
    public void setVFormat(String format) {
	this._vFormat = format;
    }
    
    /**
     * Sets the format for the data point's value.
     * @param format a format string
     */
    public void setZFormat(String format) {
	this._zFormat = format;
    }

    /**
     * Sets the sampling for the horizontal direction.
     * @param sampling a sampling
     */
    public void setHSampling(Sampling sampling) {
	this._sh = sampling;
    }
    
    /**
     * Sets the sampling for the vertical direction.
     * @param sampling a sampling
     */
    public void setVSampling(Sampling sampling) {
	this._sv = sampling;
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
    private Projector _hp; // hp & vp projects from mouse location
    private Projector _vp; // to normalized coordinates
    private Transcaler _ts; // maps normalized coordinates to world coordinates
    private float[][] _dataSource; // the data displayed in the tile
    private Sampling _sh; // horizontal sampling
    private Sampling _sv; // vertical sampling
    private double _xdata, _ydata, _zdata; // data point nearest to mouse locaiton
    private JLabel _destination; // the JLabel displaying the readout
    private String _dataPoint; // formatted data point string at mouse location
    private String _hFormat; // format for _dataPoint's horizontal coordinate
    private String _vFormat; // format for _dataPoint's vertical coordinate
    private String _zFormat; // format for _dataPoint's value
    
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
	_hp = _tile.getHorizontalProjector();
	_vp = _tile.getVerticalProjector();
	_ts = _tile.getTranscaler();
	_ts = _ts.combineWith(_hp, _vp);
	this.makeFormattedDataReadOut(_xmouse, _ymouse);
	_destination.setText(_dataPoint);
	fireTrack();
	_tile.addMouseMotionListener(_mml);
    }

    private void duringTracking(MouseEvent e) {
	_xmouse = e.getX();
	_ymouse = e.getY();
	_tile = (Tile)e.getSource();
	this.makeFormattedDataReadOut(_xmouse, _ymouse);
	_destination.setText(_dataPoint);
	fireTrack();
    }

    private void endTracking() {
	_tile.removeMouseMotionListener(_mml); 
	fireTrack();
	_tile = null;
    }

    private void makeFormattedDataReadOut(int x, int y) {
	_xdata = _sh.valueOfNearest(_ts.x(x));
	_ydata = _sv.valueOfNearest(_ts.y(y));
	_zdata = _dataSource[_sh.indexOf(_xdata)][_sv.indexOf(_ydata)];
	_dataPoint = " Mouse location :  (";
	_dataPoint += String.format(_hFormat, _ts.x(x));
	_dataPoint += ", ";
	_dataPoint += String.format(_vFormat, _ts.y(y));
	_dataPoint += ")   /  ";
	_dataPoint += "  Nearest data point :  (";
	_dataPoint += String.format(_hFormat, _xdata);
	_dataPoint += ", ";
	_dataPoint += String.format(_vFormat, _ydata);
	_dataPoint += ", ";
	_dataPoint += String.format(_zFormat, _zdata);
	_dataPoint += ")";
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

