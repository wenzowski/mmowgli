/* 
 * Written by Patrick Lightbody of BrowserMob as an example for NPS MOVES
 * MMOWGLI site testing.  Excerpt of email from Patrick dtd 16 May 2011:
 *
 * All -
 * I went ahead and made a script for you that doesn't need existing logged in
 * users. Instead it registers a new user, logs in, and then "expands" on an
 * idea card.
 *
 * The script is uploaded in Don's browsermob.com account and named "Example
 * Script". I hope it gives you some ideas about what can be done. I am very
 * confident we don't need to use Vaadin Testbench and Selenium will do just
 * fine, it's just a matter of a few critical scripting techniques:
 *
 * 1) setting the timing properly with well-placed waitForNetworkTrafficToStop
 * and waitForElementPresent commands
 *
 * 2) Using advanced xpath expressions to clearly express the elements you wish
 * to interact with
 *
 * 3) Using clickAt when click doesn't properly exercise the GWT components
 *
 * Please take a look at let us know if you have any questions.
 *
 * Patrick
 *
 * --
 * Patrick Lightbody
 * +1 (415) 830-5488
 */
var selenium = browserMob.openBrowser();
var c = browserMob.getActiveHttpClient();

var username = 'test-' + new Date().getTime();
var timeout = 60000;
selenium.setTimeout(timeout);

c.autoBasicAuthorization("nps.edu", "fuzzy", "walrus");
c.blacklistRequests("http://movesinstitute\\.org/mmowMedia/mov/.*", 200);
c.blacklistRequests("https://movesinstitute\\.org/mmowMedia/mov/.*", 200);

browserMob.beginTransaction();

browserMob.beginStep("Homepage");
selenium.open("https://test.mmowgli.nps.edu/mmowgli/");
browserMob.waitForNetworkTrafficToStop(1000, timeout);
browserMob.endStep();

browserMob.beginStep("Register");
selenium.click("//img[contains(@src, 'imNewButton')]");
browserMob.waitForNetworkTrafficToStop(1000, timeout);
selenium.click("//button[contains(@class, 'acceptAndContinueButton')]");
browserMob.waitForNetworkTrafficToStop(1000, timeout);
selenium.type("xpath=(//input)[1]", username);
selenium.type("xpath=(//input)[2]", "password");
selenium.type("xpath=(//input)[3]", "password");
selenium.type("xpath=(//input)[4]", "BrowserMob");
selenium.type("xpath=(//input)[5]", "Test");
selenium.type("xpath=(//input)[6]", username + "@example.com");
selenium.click("//button[contains(@class, 'continueButton')]");
browserMob.waitForNetworkTrafficToStop(1000, timeout);
selenium.click("//button[contains(@class, 'continueButton')]");
browserMob.waitForNetworkTrafficToStop(1000, timeout);
selenium.click("//img[contains(@src, 'getABriefing')]");
selenium.waitForElementPresent("xpath=(//img[contains(@src, 'innovatePurpleSummary')])[1]");
browserMob.endStep();

browserMob.beginStep("Select card");
selenium.clickAt("xpath=(//img[contains(@src, 'innovatePurpleSummary')])[1]", "0,0");
selenium.waitForElementPresent("//img[contains(@src, 'expandOrangeTitle')]");
browserMob.endStep();

browserMob.beginStep("Expand on idea");
selenium.clickAt("//img[contains(@src, 'expandOrangeTitle')]", "0,0");

var text = "I am expanding on this idea! " + new Date().getTime();

selenium.waitForElementPresent("//textarea");
selenium.type("//textarea", text);
selenium.click("//span[text() = 'submit']");
browserMob.pause(1000);
selenium.waitForTextPresent(text);
browserMob.endStep();

browserMob.endTransaction();
