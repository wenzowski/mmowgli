package createMmowgliUser.osx_firefox35;

import com.vaadin.testbench.testcase.AbstractVaadinTestCase;
import com.vaadin.testbench.util.CurrentCommand;

public class createUserMainGame extends AbstractVaadinTestCase {

    int MAX_USERS = 55;
    int count = 0;

    @Override
    public void setUp() {}

    public void testosx_firefox35() throws Throwable {
        startBrowser("osx-firefox35");
        for (int ix = 0; ix<MAX_USERS; ix++) {
            internal_createMmowgliUser();
            count++;
        }
    }

    private void internal_createMmowgliUser() throws Throwable {
        CurrentCommand cmd = new CurrentCommand("createMmowgliUser");
        setTestName("createMmowgliUser");
        try {
            cmd.setCommand("open", "/mmowgli/", "");
            open("/mmowgli/");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VNativeButton[0]", "164,11");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VNativeButton[0]", "164,11");

            cmd.setCommand("scroll", "vaadin=mmowgli::", "300");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "300"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[8]/VHorizontalLayout[0]/ChildComponentContainer[3]/VNativeButton[0]", "72,21");
            doMouseClick("vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[8]/VHorizontalLayout[0]/ChildComponentContainer[3]/VNativeButton[0]", "72,21");

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "testbench" + count);
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "testbench" + count});

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPasswordField[0]", "test");
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPasswordField[0]", "test"});

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPasswordField[1]", "test");
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPasswordField[1]", "test"});

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "42,8");
            doMouseClick("vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "42,8");

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "test");
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "test"});

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[1]", "bench" + count);
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[1]", "bench" + count});

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[2]", "test@bench" + count + ".org");
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[2]", "test@bench" + count + ".org"});

            cmd.setCommand("pressSpecialKey", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[2]", "enter");
            doCommand("pressSpecialKey", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[7]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[2]", "enter"});

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[1]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[1]", "13,11");
            doMouseClick("vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[1]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[1]", "13,11");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::Root/VFilterSelect$SuggestionPopup[0]/VFilterSelect$SuggestionMenu[0]#item1", "87,6");
            doMouseClick("vaadin=mmowgli::Root/VFilterSelect$SuggestionPopup[0]/VFilterSelect$SuggestionMenu[0]#item1", "87,6");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[1]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "80,13");
            doMouseClick("vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[1]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]", "80,13");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[4]/VHorizontalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "54,17");
            doMouseClick("vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[4]/VHorizontalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "54,17");

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VTextField[0]", "global");
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VTextField[0]", "global"});

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VTextArea[0]", "stuff");
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VTextArea[0]", "stuff"});

            cmd.setCommand("pressSpecialKey", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VTextArea[0]", "enter");
            doCommand("pressSpecialKey", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VTextArea[0]", "enter"});

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClickAndWait", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[9]/VNativeButton[0]", "25,26");
            doCommand("mouseClickAndWait", new String[]{"vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[9]/VNativeButton[0]", "25,26"});

        } catch (Throwable e) {
            createFailureScreenshot("createMmowgliUser");
            throw new java.lang.AssertionError(cmd.getInfo() + "\n Message: " + e.getMessage() + "\nRemote control: " + getRemoteControlName());
        }
        handleSoftErrors();
    }
}
