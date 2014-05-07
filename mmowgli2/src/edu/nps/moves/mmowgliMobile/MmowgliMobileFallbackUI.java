package edu.nps.moves.mmowgliMobile;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

/**
 * MmowgliMobileFallbackUI.java
 * Created on Jan 30, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliMobileFallbackUI extends UI
{
  private static final long serialVersionUID = -3852574001871621481L;
  
  private static final String MSG = "<h1>Ooops...</h1> <p>You accessed MobileMmowgli "
          + "with a browser that is not supported. "
          + "MobileMmowgli is "
          + "meant to be used with modern WebKit based mobile browsers, "
          + "e.g. with iPhone or modern Android devices. Currently those "
          + "cover a huge majority of actively used mobile browsers. "
          + "Support will be extended as other mobile browsers develop "
          + "and gain popularity. Testing ought to work with desktop "
          + "Safari or Chrome as well.";

  @Override
  protected void init(VaadinRequest request)
  {
      Label label = new Label(MSG, ContentMode.HTML);
      
      VerticalLayout content = new VerticalLayout();
      content.setMargin(true);
      content.addComponent(label);
      setContent(content);
  }
}