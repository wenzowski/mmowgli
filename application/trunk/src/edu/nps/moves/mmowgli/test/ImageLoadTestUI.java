package edu.nps.moves.mmowgli.test;

import java.io.File;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Theme;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.utility.BrowserPerformanceLogger;

@SuppressWarnings("serial")
@JavaScript ({
  "../../VAADIN/instrument/userinfo.1.1.1.min.js",
  "../../VAADIN/instrument/mmowgli1.js"
})
@Theme("mmowgli2")
public class ImageLoadTestUI extends UI
{
  VerticalLayout vl;
  @Override
  protected void init(VaadinRequest request)
  {
    BrowserPerformanceLogger.registerCurrentPageLoadTest();
    BrowserPerformanceLogger.registerCurrentPage(); // for previous test
    vl = new VerticalLayout();
    setContent(vl);    

    GridLayout gl = new GridLayout();
    gl.setSpacing(true);
    gl.setColumns(12);
    vl.addComponent(gl);
    vl.setComponentAlignment(gl, Alignment.TOP_CENTER);
    fillGrid(gl);
  }
  
  @Override
  public void detach()
  {
    BrowserPerformanceLogger.unregisterCurrentPage(); // previous test
    BrowserPerformanceLogger.unregisterCurrentPageLoadTest();
    super.detach();
  }

  private void fillGrid(GridLayout gl)
  {
    File f = VaadinService.getCurrent().getBaseDirectory();
    f = new File(f,"VAADIN/art");
    addAllImages(f,gl);
  }
  
  //int count=1;
  private void addAllImages(File dir, GridLayout gl)
  {
    if(dir.isFile()) {
      //System.out.println(""+count++ +" "+dir.getName());
      Image img = new Image(null,new FileResource(dir));
      img.setWidth("100px");
      img.setHeight("100px");
      img.setDescription(dir.getName());
      gl.addComponent(img);
      return;
    }
    File[] fs = dir.listFiles();
    for(File f : fs)
      addAllImages(f,gl);
  }
}
