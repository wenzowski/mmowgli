package createClusterUser.osx_firefox35;

import com.vaadin.testbench.testcase.AbstractVaadinTestCase;
import com.vaadin.testbench.util.CurrentCommand;

public class createClusterUser extends AbstractVaadinTestCase {

    int MAX_USERS = 55;
    int count = 0;

    @Override
    public void setUp() {}

    public void testosx_firefox35() throws Throwable {
        startBrowser("osx-firefox35");
        for (int ix = 0; ix<MAX_USERS; ix++) {
            internal_createClusterUser();
            count++;
        }
    }

    private void internal_createClusterUser() throws Throwable {
        CurrentCommand cmd = new CurrentCommand("createClusterUser");
        setTestName("createClusterUser");
        try {
            cmd.setCommand("open", "/cluster/", "");
            open("/cluster/");

            cmd.setCommand("mouseClick", "vaadin=cluster::/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VNativeButton[0]", "164,11");
            doMouseClick("vaadin=cluster::/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VNativeButton[0]", "164,11");

            cmd.setCommand("scroll", "vaadin=cluster::", "300");
            doCommand("scroll", new String[]{"vaadin=cluster::", "300"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[8]/VHorizontalLayout[0]/ChildComponentContainer[3]/VNativeButton[0]", "72,21");
            doMouseClick("vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[8]/VHorizontalLayout[0]/ChildComponentContainer[3]/VNativeButton[0]", "72,21");

            cmd.setCommand("enterCharacter", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "testbench" + count);
            doCommand("enterCharacter", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "testbench" + count});

            cmd.setCommand("enterCharacter", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPasswordField[0]", "test");
            doCommand("enterCharacter", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPasswordField[0]", "test"});

            cmd.setCommand("enterCharacter", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPasswordField[1]", "test");
            doCommand("enterCharacter", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPasswordField[1]", "test"});

            cmd.setCommand("mouseClick", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "42,8");
            doMouseClick("vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "42,8");

            cmd.setCommand("enterCharacter", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "test");
            doCommand("enterCharacter", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "test"});

            cmd.setCommand("enterCharacter", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[1]", "bench" + count);
            doCommand("enterCharacter", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[1]", "bench" + count});

            cmd.setCommand("enterCharacter", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[2]", "test@bench" + count + ".org");
            doCommand("enterCharacter", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[2]", "test@bench" + count + ".org"});

            cmd.setCommand("pressSpecialKey", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[2]", "enter");
            doCommand("pressSpecialKey", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[2]", "enter"});

            cmd.setCommand("mouseClick", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[1]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[1]", "13,11");
            doMouseClick("vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[1]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[1]", "13,11");

            cmd.setCommand("mouseClick", "vaadin=cluster::Root/VFilterSelect$SuggestionPopup[0]/VFilterSelect$SuggestionMenu[0]#item1", "87,6");
            doMouseClick("vaadin=cluster::Root/VFilterSelect$SuggestionPopup[0]/VFilterSelect$SuggestionMenu[0]#item1", "87,6");

            cmd.setCommand("mouseClick", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[1]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "80,13");
            doMouseClick("vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[1]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "80,13");

            cmd.setCommand("mouseClick", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[4]/VHorizontalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "54,17");
            doMouseClick("vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[4]/VHorizontalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "54,17");

            cmd.setCommand("enterCharacter", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VTextField[0]", "global");
            doCommand("enterCharacter", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VTextField[0]", "global"});

            cmd.setCommand("enterCharacter", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VTextArea[0]", "stuff");
            doCommand("enterCharacter", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VTextArea[0]", "stuff"});

            cmd.setCommand("pressSpecialKey", "vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VTextArea[0]", "enter");
            doCommand("pressSpecialKey", new String[]{"vaadin=cluster::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VTextArea[0]", "enter"});

            cmd.setCommand("scroll", "vaadin=cluster::", "0");
            doCommand("scroll", new String[]{"vaadin=cluster::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClickAndWait", "vaadin=cluster::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[9]/VNativeButton[0]", "25,26");
            doCommand("mouseClickAndWait", new String[]{"vaadin=cluster::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[9]/VNativeButton[0]", "25,26"});

        } catch (Throwable e) {
            createFailureScreenshot("createClusterUser");
            throw new java.lang.AssertionError(cmd.getInfo() + "\n Message: " + e.getMessage() + "\nRemote control: " + getRemoteControlName());
        }
        handleSoftErrors();
    }
}
