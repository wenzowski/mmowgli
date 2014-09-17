package edu.nps.moves.mmowgli.signupServer;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@SuppressWarnings("serial")

@WebServlet(value = "/signup/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false,ui = SignupServer.class)

public class SignupServlet extends VaadinServlet
{
  //Used only for the annotations
}
