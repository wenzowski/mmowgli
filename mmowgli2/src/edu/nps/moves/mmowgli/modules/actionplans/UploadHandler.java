/*
 * Copyright (c) 1995-2010 held by the author(s).  All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  
 *  * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer
 *       in the documentation and/or other materials provided with the
 *       distribution.
 *  * Neither the names of the Naval Postgraduate School (NPS)
 *       Modeling Virtual Environments and Simulation (MOVES) Institute
 *       (http://www.nps.edu and http://www.MovesInstitute.org)
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific
 *       prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package edu.nps.moves.mmowgli.modules.actionplans;

import java.io.*;

import com.vaadin.ui.*;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

import edu.nps.moves.mmowgli.components.HtmlLabel;

/**
 * UploadHandler.java Created on Jan 6, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class UploadHandler implements Upload.Receiver
{
  private static final long serialVersionUID = 2752969569776708080L;

  public static long MAXUPLOADSIZE = 2*1048576; // 2 meg
  public static String MAXUPLOADSTRING = "2M";
  private Upload upload;
  private UploadStatus window;
  private String savePath;
  private String finalFullFilePath;
  
  public String getFullUploadedPath()
  {
    return finalFullFilePath;
  }
  
  @SuppressWarnings("serial")
  public UploadHandler(Upload upld, UploadStatus panel, String savePath)
  {
    this.upload = upld;
    this.savePath = savePath;   
    this.window = panel;
    new File(savePath).mkdirs();
    
    upload.addStartedListener(new Upload.StartedListener()
    {
      public void uploadStarted(StartedEvent event)
      {
        // this method gets called immediately after upload is started
        window.pi.setValue(0f);
        window.pi.setVisible(true);
        UI.getCurrent().setPollInterval(500); // hit server frequently to get
        window.textualProgress.setVisible(true);
        // updates to client
        window.state.setValue("Uploading");
        window.fileName.setValue(event.getFilename());

        window.cancelProcessing.setVisible(true);
      }
    });

    upload.addProgressListener(new Upload.ProgressListener()
    {
      public void updateProgress(long readBytes, long contentLength)
      {
        // this method gets called several times during the update
        if(readBytes>MAXUPLOADSIZE) {
          window.textualProgress.setValue("Upload failed: "+MAXUPLOADSTRING+" limit");
          upload.interruptUpload();
          UI.getCurrent().setPollInterval(-1);
          window.pi.setVisible(false);
        }
        else {
          window.pi.setValue(new Float(readBytes / (float) contentLength));
          window.textualProgress.setValue("Processed " + readBytes + " bytes of " + contentLength);
          window.result.setValue(counter + " (counting...)");
        }
      }
    });

    upload.addSucceededListener(new Upload.SucceededListener()
    {
      public void uploadSucceeded(SucceededEvent event)
      {
        window.result.setValue(counter + " (total)");
        UI.getCurrent().setPollInterval(-1);
      }
    });

    upload.addFailedListener(new Upload.FailedListener()
    {
      public void uploadFailed(FailedEvent event)
      {
        window.result.setValue(counter + " (upload interrupted at " + Math.round(100 * (Float) window.pi.getValue()) + "%)");
        finalFullFilePath = null;  // indicate error
        UI.getCurrent().setPollInterval(-1);
      }
    });

    upload.addFinishedListener(new Upload.FinishedListener()
    {
      public void uploadFinished(FinishedEvent event)
      {
        window.state.setValue("Finished");
     // window.pi.setVisible(false);
     // window.textualProgress.setVisible(false);
        window.cancelProcessing.setVisible(false);
        UI.getCurrent().setPollInterval(-1);
      }
    });   
  }

  private File findUniqueName(String fileName) throws IOException
  {
    fileName=fileName.replace(' ', '_');
    String firstPart=fileName;
    String lastPart="";
    int len = fileName.length();
    int idx;
    if((idx=fileName.lastIndexOf('.')) != -1) {
      firstPart=fileName.substring(0,idx);
      lastPart = fileName.substring(idx,len);  // include .
    }
    
    int ii=-1;
    String add="";
    File f;
    while((f=new File(savePath+firstPart+add+lastPart)).exists())
      add = ""+ ++ii;

    f.createNewFile();
    return f;
  }
  
  private void checkError(String error, long total) throws IOException
  {
//    if(error != null)
//      throw new IOException(error);
//    
//    if(total > MAXUPLOADSIZE)
//      throw new IOException("File exceeds size limit of "+MAXUPLOADSIZE);
  }
  
  long counter;
  String error=null;  // not used
  @Override
  public OutputStream receiveUpload(String filename, String MIMEType)
  {
    counter = 0;
    new File(savePath).mkdirs();
    FileOutputStream os = null;
    try {
      File f = findUniqueName(filename);    
      finalFullFilePath=f.getAbsolutePath();
      
      os = new FileOutputStream(f)
      {
        @Override
        public void write(byte[] buff, int offs, int len) throws IOException
        {
          counter += len;
          checkError(error,counter);
          super.write(buff, offs, len);
        }
        @Override
        public void write(byte[] buff) throws IOException
        {
          counter += buff.length;
          checkError(error,counter);
          super.write(buff);
        }
        @Override
        public void write(int b) throws IOException
        {
          counter++;
          checkError(error,counter);
          super.write(b);
        }       
      };
    }
    catch(IOException ex) {
      throw new RuntimeException(ex.getLocalizedMessage());     
    }
    return os;
  }

  public static class UploadStatus extends VerticalLayout
  {
    private static final long serialVersionUID = 1874813181781718474L;
    
    Label state = new HtmlLabel();
    Button cancelProcessing = new Button("Cancel");

    Label result = new Label();
    Label fileName = new Label();
    Label textualProgress = new Label();
    ProgressBar pi = new ProgressBar();

    public UploadStatus(Upload upload)
    {
      addComponent(new Label("Upload Status:"));

      FormLayout l = new FormLayout();
      l.setMargin(true);
      l.addStyleName("m-greyborder");
      l.setWidth("100%");
      addComponent(l);
      HorizontalLayout stateLayout = new HorizontalLayout();
      stateLayout.setSpacing(true);
      stateLayout.addComponent(state);
      stateLayout.setCaption("Current state");
      state.setValue("Idle");
      l.addComponent(stateLayout);
      fileName.setCaption("File name");
      l.addComponent(fileName);
      result.setCaption("Bytes counted");
      l.addComponent(result);
      pi.setCaption("Progress");
      pi.setVisible(false);
      l.addComponent(pi);
      textualProgress.setVisible(false);
      l.addComponent(textualProgress);

//      getContent().addComponent(cancelProcessing);
//      //cancelProcessing.setStyleName(BaseTheme.BUTTON_LINK);
//
//      cancelProcessing.addListener(new Button.ClickListener()
//      {
//        public void buttonClick(ClickEvent event)
//        {
//          myupload.interruptUpload();
//        }
//      });
      
//      setWidth("400px"); //"350px");
//      setHeight("300px"); //"260px");
    }
  }
}
