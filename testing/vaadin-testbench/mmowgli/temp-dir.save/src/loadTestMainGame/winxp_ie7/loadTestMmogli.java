package loadTestMmogli.winxp_ie7;

import com.vaadin.testbench.testcase.AbstractVaadinTestCase;
import com.vaadin.testbench.util.CurrentCommand;

public class loadTestMainGame extends AbstractVaadinTestCase {

    int MAX_USERS = 55;
    int count = 0;

    @Override
    public void setUp() {
    }

    public void testwinxp_ie7() throws Throwable {
        startBrowser("winxp-ie7");

        // Cycle through each of the 55 testbench users indefinitely
        while (true) {
            for (int ix = 0; ix < MAX_USERS; ix++) {
                internal_loadTestMmogli();
                count++;
            }
        }
    }

    private void internal_loadTestMmogli() throws Throwable {
        CurrentCommand cmd = new CurrentCommand("loadTestMmogli");
        setTestName("loadTestMmogli");
        try {
            cmd.setCommand("open", "/mmowgli/", "");
            open("/mmowgli/");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[3]/VVerticalLayout[0]/ChildComponentContainer[0]/VNativeButton[0]", "85,9");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[3]/VVerticalLayout[0]/ChildComponentContainer[0]/VNativeButton[0]", "85,9");

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VTextField[0]", "testbench0");
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VTextField[0]", "testbench0"});

            cmd.setCommand("enterCharacter", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VPasswordField[0]", "test");
            doCommand("enterCharacter", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VPasswordField[0]", "test"});

            cmd.setCommand("pressSpecialKey", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VPasswordField[0]", "enter");
            doCommand("pressSpecialKey", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[3]/VPasswordField[0]", "enter"});

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VNativeButton[0]", "43,3");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VNativeButton[0]", "43,3");

            cmd.setCommand("scroll", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VScrollTable[0]/domChild[1]", "1436");
            doCommand("scroll", new String[]{"vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VScrollTable[0]/domChild[1]", "1436"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VScrollTable[0]/domChild[1]", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VScrollTable[0]/domChild[1]", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VNativeButton[0]", "16,7");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VNativeButton[0]", "16,7");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[3]/VNativeButton[0]", "45,5");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[3]/VNativeButton[0]", "45,5");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[4]/VNativeButton[0]", "46,4");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[4]/VNativeButton[0]", "46,4");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VEmbedded[0]/domChild[0]", "767,71");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VEmbedded[0]/domChild[0]", "767,71");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[5]/VNativeButton[0]", "75,4");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[5]/VNativeButton[0]", "75,4");

            cmd.setCommand("scroll", "vaadin=mmowgli::", "515");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "515"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "69,-474");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "69,-474");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[3]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "32,-481");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[3]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "32,-481");

            cmd.setCommand("scroll", "vaadin=mmowgli::", "669");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "669"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[3]/VLabel[0]", "84,-636");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[3]/VLabel[0]", "84,-636");

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "397");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "397"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "594");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "594"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "479");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "479"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "907");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "907"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "38,18");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "38,18");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "61,71");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "61,71");

            cmd.setCommand("scroll", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTreeTable[0]/domChild[1]", "148");
            doCommand("scroll", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTreeTable[0]/domChild[1]", "148"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "102,9");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "102,9");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "144,10");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "144,10");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[3]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "47,11");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[3]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "47,11");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "75,17");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "75,17");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "70,70");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "70,70");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTreeTable[0]/FocusableScrollPanel[0]/VTreeTable$VTreeTableScrollBody[0]/VTreeTable$VTreeTableScrollBody$VTreeTableRow[0]/VLabel[0]", "49,2");
            doMouseClick("vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTreeTable[0]/FocusableScrollPanel[0]/VTreeTable$VTreeTableScrollBody[0]/VTreeTable$VTreeTableScrollBody$VTreeTableRow[0]/VLabel[0]", "49,2");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTreeTable[0]/FocusableScrollPanel[0]/VTreeTable$VTreeTableScrollBody[0]/VTreeTable$VTreeTableScrollBody$VTreeTableRow[0]/VLabel[0]", "49,2");
            doMouseClick("vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTreeTable[0]/FocusableScrollPanel[0]/VTreeTable$VTreeTableScrollBody[0]/VTreeTable$VTreeTableScrollBody$VTreeTableRow[0]/VLabel[0]", "49,2");

            cmd.setCommand("doubleClickAt", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTreeTable[0]/FocusableScrollPanel[0]/VTreeTable$VTreeTableScrollBody[0]/VTreeTable$VTreeTableScrollBody$VTreeTableRow[0]/VLabel[0]", "49,2");
            doCommand("doubleClickAt", new String[]{"vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTreeTable[0]/FocusableScrollPanel[0]/VTreeTable$VTreeTableScrollBody[0]/VTreeTable$VTreeTableScrollBody$VTreeTableRow[0]/VLabel[0]", "49,2"});

            cmd.setCommand("scroll", "vaadin=mmowgli::", "907");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "907"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "436");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "436"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "70,69");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "70,69");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "40,26");
            doMouseClick("vaadin=mmowgli::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "40,26");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "72,2");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[1]/VLabel[0]", "72,2");

            cmd.setCommand("scroll", "vaadin=mmowgli::", "907");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "907"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "71,81");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "71,81");

            cmd.setCommand("scroll", "vaadin=mmowgli::", "70");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "70"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "25,-68");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VNativeButton[0]", "25,-68");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "30,-62");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "30,-62");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[3]/VNativeButton[0]", "27,-64");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[3]/VNativeButton[0]", "27,-64");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[4]/VNativeButton[0]", "27,-67");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[4]/VNativeButton[0]", "27,-67");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[5]/VNativeButton[0]", "28,-63");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[2]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[0]/VVerticalLayout[0]/ChildComponentContainer[5]/VNativeButton[0]", "28,-63");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[6]/VHorizontalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "68,-40");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[6]/VHorizontalLayout[0]/ChildComponentContainer[2]/VNativeButton[0]", "68,-40");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[6]/VHorizontalLayout[0]/ChildComponentContainer[3]/VNativeButton[0]", "105,-39");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[6]/VHorizontalLayout[0]/ChildComponentContainer[3]/VNativeButton[0]", "105,-39");

            cmd.setCommand("mouseClick", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[6]/VHorizontalLayout[0]/ChildComponentContainer[4]/VNativeButton[0]", "119,-39");
            doMouseClick("vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[2]/VVerticalLayout[0]/ChildComponentContainer[0]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[6]/VHorizontalLayout[0]/ChildComponentContainer[4]/VNativeButton[0]", "119,-39");

            cmd.setCommand("scroll", "vaadin=mmowgli::", "0");
            doCommand("scroll", new String[]{"vaadin=mmowgli::", "0"});

            cmd.setCommand("pause", "", "300");
            pause(300);

            cmd.setCommand("mouseClickAndWait", "vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[9]/VNativeButton[0]", "46,10");
            doCommand("mouseClickAndWait", new String[]{"vaadin=mmowgli::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VAbsoluteLayout[0]/VAbsoluteLayout$AbsoluteWrapper[9]/VNativeButton[0]", "46,10"});

        } catch (Throwable e) {
            createFailureScreenshot("loadTestMmogli");
            throw new java.lang.AssertionError(cmd.getInfo() + "\n Message: " + e.getMessage() + "\nRemote control: " + getRemoteControlName());
        }
        handleSoftErrors();
    }
}
