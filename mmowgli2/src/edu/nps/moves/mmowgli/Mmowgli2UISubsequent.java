package edu.nps.moves.mmowgli;

import com.vaadin.annotations.*;

/**
 * Mmowgli2UISubsequent.java
 * Created on Apr 28, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

//Watch out for some of the annotations interfering with hbncontainer (see MmowgliMobileUI comments)
//@Push(PushMode.MANUAL)

//This preserves the UI across page reloads
@PreserveOnRefresh
@StyleSheet({"https://fonts.googleapis.com/css?family=Nothing+You+Could+Do", // jason-style-handwriting
          "https://fonts.googleapis.com/css?family=Varela+Round", // like vagabond
          "https://fonts.googleapis.com/css?family=Special+Elite",// typewriter
          "https://fonts.googleapis.com/css?family=Open+Sans:700&subset=latin,latin-ext", // army sci tech
          "https://fonts.googleapis.com/css?family=Gentium+Book+Basic&subset=latin,latin-ext"})//ditto

//Loading the google code this way (anno) runs into the X-Frame-Options SAMEORIGIN error
@JavaScript ({
           "https://platform.twitter.com/widgets.js",
           "http://openlayers.org/api/OpenLayers.js",
           "http://ol3js.org/en/master/build/ol.js",
           //"http://maps.google.com/maps/api/js?v=3&output=embed"})  // last one for openstrmap plus google layers
           //"https://maps.google.com/maps/api/js?v=3&key=AIzaSyBeWoPydbJRnvH0D8DnCCeLDP1VVPURKh0&sensor=false&output=embed"})
         })
@Theme("mmowgli2")
@Widgetset("edu.nps.moves.mmowgli.widgetset.Mmowgli2Widgetset")

public class Mmowgli2UISubsequent extends Mmowgli2UI
{
  private static final long serialVersionUID = -6366320429083964969L;
  public Mmowgli2UISubsequent()
  {
    super(false);
  }
}
