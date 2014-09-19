package edu.nps.moves.mmowgliMobile.ui;

import edu.nps.moves.mmowgliMobile.data.ListEntry;

public interface MainView {

    public void setMessage(ListEntry message,
            ListView messageHierarchyView);

    public void updateNewMessages();

}
