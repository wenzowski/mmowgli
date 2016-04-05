package edu.nps.moves.mmowgli.test;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@SuppressWarnings("serial")

@WebServlet(value = "/loadtest/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false,ui = ImageLoadTestUI.class)
public class ImageLoadTestServlet extends VaadinServlet
{

}
