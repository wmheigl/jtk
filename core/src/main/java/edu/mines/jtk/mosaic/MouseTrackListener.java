
package edu.mines.jtk.mosaic;

import java.util.EventListener;


/**
 * A mousetrack listener listens to mousetrack events.
 *
 * @author Werner M. Heigl, NanoSeis
 * @version 2021.03.09
 *
 * @see MouseTrackMode
 * @see MouseTrackEvent
 */
public interface MouseTrackListener extends EventListener {

  /**
   * Called when a mousetrack event has been fired.
   * @param e The mousetrack event.
   */
  public void mouseTracked(MouseTrackEvent e);
  
}
