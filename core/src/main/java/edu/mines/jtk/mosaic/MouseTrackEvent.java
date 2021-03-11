
package edu.mines.jtk.mosaic;

import java.util.EventObject;

/**
 * A mousetrack event that is generated by {@code MouseTrackMode} when
 * it is active. A mousetrack event provides the origin of the event
 * (typically a {@code Tile}) and the world coordinates of the mouse
 * location.
 *
 * @author Werner M. Heigl, NanoSeis
 * @version 2021.03.10
 *
 * @see MouseTrackMode
 * @see MouseTrackListener
 * @see MosaicDemo2
 */
public class MouseTrackEvent extends EventObject {

  /** World horizontal coordinate of mouse location. */
  private double _vx;

  /** World verical coordinate of mouse location. */
  private double _vy;

  /**
   * Constructs a {@code MouseTrackEvent} object.
   * <p>
   * This method throws an {@code IllegalArgumentException}
   * if {@code source} is {@code null}.
   *
   * @param source The object that originated the event.
   * @param vx The world horizontal coordinate of the mouse location.
   * @param vy The world vertical coordinate of the mouse location.
   * @throws IllegalArgumentException if {@code source} is null
   */
  public MouseTrackEvent(Object source, double vx, double vy) {
    super(source);
    this._vx = vx;
    this._vy =vy;
  }

  public double getX() {
    return _vx;
  }

  public double getY() {
    return _vy;
  }
  
}