package edu.nps.moves.mmowgliMobile.ui;

import edu.nps.moves.mmowgliMobile.data.Message;

public interface MainView {

    public void setMessage(Message message,
            MessageHierarchyView messageHierarchyView);

    public void updateNewMessages();

}
