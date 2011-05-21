/* Selenium core extensions for Vaadin
 */

/* Also in IDE extensions */
function getVaadinConnector(wnd) {
    if (wnd.wrappedJSObject) {
        wnd = wnd.wrappedJSObject;
    }

    var connector = null;
    if (wnd.itmill) {
        connector = wnd.itmill;
    } else if (wnd.vaadin) {
        connector = wnd.vaadin;
    }

    return connector;
}

PageBot.prototype.locateElementByVaadin = function(tkString, inDocument) {

    var connector = getVaadinConnector(this.currentWindow);

    if (!connector) {
        // Not a toolkit application
        return null;
    }

    var parts = tkString.split("::");
    var appId = parts[0];

    try {
        var element = connector.clients[appId].getElementByPath(parts[1]);
        return element;
    } catch (exception) {
        LOG.error('an error occured when locating element for '+tkString+': ' + exception);
    }
    return null;
}

PageBot.prototype.locateElementByVaadin.is_fuzzy_match = function(node, target) {
    try {
        if ("unwrap" in XPCNativeWrapper) {
            target = XPCNativeWrapper.unwrap(target);
        }else if (target.wrappedJSObject) {
            target = target.wrappedJSObject;
        }
    	

        var isMatch = (node == target) || is_ancestor(node, target);
        return isMatch;
    }
    catch (e) {
        return false;
    }
}
Selenium.prototype.doWaitForVaadin = function(locator, value) {

    // max time to wait for toolkit to settle
    // TODO: Determine if this is a correct value
    //var timeout = 20000;
    var timeout = 200000;
    var foundClientOnce = false;
	
    return Selenium.decorateFunctionWithTimeout( function() {
        var wnd = selenium.browserbot.getCurrentWindow();
        var connector = getVaadinConnector(wnd);
        if (!connector) {
            // No connector found == Not a Vaadin application so we don't need to wait
            return true;
        }
		  
        var clients = connector.clients;
        if (clients) {
            for ( var client in clients) {
                if (clients[client].isActive()) {
                    return false;
                }
            }
            return true;
        } else {
            //A Vaadin connector was found so this is most likely a Vaadin application. Keep waiting.
            return false;
        }
    }, timeout);
};

Selenium.prototype.doScroll = function(locator, scrollString) {
    var element = this.page().findElement(locator);
    element.scrollTop = scrollString;
};

Selenium.prototype.doScrollLeft = function(locator, scrollString){
    var element = this.page().findElement(locator);
    element.scrollLeft = scrollString;
};

Selenium.prototype.doContextmenu = function(locator) { 
    var element = this.page().findElement(locator);
    this.page()._fireEventOnElement("contextmenu", element, 0, 0);
}; 

Selenium.prototype.doContextmenuAt = function(locator, coordString) { 
    if (!coordString)
        coordString = '2, 2';
      
    var element = this.page().findElement(locator);
    var clientXY = getClientXY(element, coordString);
    this.page()._fireEventOnElement("contextmenu", element, clientXY[0], clientXY[1]);
};

/* Empty screenCapture command for use with export test case Vaadin */
Selenium.prototype.doScreenCapture = function(locator, value){
    };

/*Enters a characte so that it gets recognized in comboboxes etc.*/
Selenium.prototype.doEnterCharacter = function(locator, value){
    var start =  new Date().getTime();
    var element = this.browserbot.findElement(locator);
    if (this.browserbot.shiftKeyDown) {
        value = new String(value).toUpperCase();
    }
    
    triggerEvent(element, 'focus', false);
    triggerEvent(element, 'select', true);
    var maxLengthAttr = element.getAttribute("maxLength");
    var actualValue = value;
    if (maxLengthAttr != null) {
        var maxLength = parseInt(maxLengthAttr);
        if (value.length > maxLength) {
            actualValue = value.substr(0, maxLength);
        }
    }

    if (getTagName(element) == "body") {
        if (element.ownerDocument && element.ownerDocument.designMode) {
            var designMode = new String(element.ownerDocument.designMode).toLowerCase();
            if (designMode == "on") {
                // this must be a rich text control!
                element.innerHTML = actualValue;
            }
        }
    } else {
        element.value = actualValue;
    }

    value = value.replace(/\n/g, "");

    if(value.length > 1){
        for(i = 0; i < value.length;i++){
            this.doKeyDown(locator, value.charAt(i));
            this.doKeyUp(locator, value.charAt(i));
			
            var end = new Date().getTime();
            var time = end - start;
            // If typing takes over 24000ms, break and continue test.
            if(time > 24000){
                break;
            }
        }
    }else{
        this.doKeyDown(locator, value);
        this.doKeyUp(locator, value);
    }
    try {
        triggerEvent(element, 'change', true);
    } catch (e) {}
};

/*Sends an arrow press recognized by browsers.*/
Selenium.prototype.doPressSpecialKey = function(locator, value){
    var shift = (new RegExp("shift")).test(value);
    var ctrl = (new RegExp("ctrl")).test(value);
    var alt = (new RegExp("alt")).test(value);
    if((new RegExp("left")).test(value.toLowerCase())){
        value="\\37";
    }else if((new RegExp("right")).test(value.toLowerCase())){
        value="\\39";
    }else if((new RegExp("up")).test(value.toLowerCase())){
        value="\\38";
    }else if((new RegExp("down")).test(value.toLowerCase())){
        value="\\40";
    }else if((new RegExp("enter")).test(value.toLowerCase())){
        value="\\13";
    }else if((new RegExp("space")).test(value.toLowerCase())){
        value="\\32";
    }else if((new RegExp("tab")).test(value.toLowerCase())){
        value="\\9";
    }else{
        value = value.substr(value.lastIndexOf(" ")+1);
    }
    var element = this.browserbot.findElement(locator);
    triggerSpecialKeyEvent(element, 'keydown', value, true, ctrl, alt, shift, this.browserbot.metaKeyDown);
    triggerSpecialKeyEvent(element, 'keypress', value, true,ctrl, alt, shift, this.browserbot.metaKeyDown);
    triggerSpecialKeyEvent(element, 'keyup', value, true, ctrl, alt, shift, this.browserbot.metaKeyDown);
};

/*Simulates the correct mouse click events.*/
Selenium.prototype.doMouseClick = function(locator, value){
    var element = this.browserbot.findElement(locator);
    value = value.split(":");
    var clientXY = getClientXY(element, value[0]);

    if(value.length > 1){
        this.browserbot.shiftKeyDown = (new RegExp("shift")).test(value[1]);
        this.browserbot.controlKeyDown = (new RegExp("ctrl")).test(value[1]);
        this.browserbot.altKeyDown = (new RegExp("alt")).test(value[1]);
    }
	
    this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
    //	element.focus();
    this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);
    this.browserbot.clickElement(element);

    this.browserbot.shiftKeyDown = this.browserbot.controlKeyDown = this.browserbot.altKeyDown = false;
};

//Expect dialog will do a normal mouse click, but the following waitForVaadin will be skipped
//as dialog is expected and the next command needs to be 'assertConfirmation'
Selenium.prototype.doExpectDialog = function(locator, value){
    var element = this.browserbot.findElement(locator);
    value = value.split(":");
    var clientXY = getClientXY(element, value[0]);

    if(value.length > 1){
        this.browserbot.shiftKeyDown = (new RegExp("shift")).test(value[1]);
        this.browserbot.controlKeyDown = (new RegExp("ctrl")).test(value[1]);
        this.browserbot.altKeyDown = (new RegExp("alt")).test(value[1]);
    }
	
    this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
    //	element.focus();
    this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);
    this.browserbot.clickElement(element);

    this.browserbot.shiftKeyDown = this.browserbot.controlKeyDown = this.browserbot.altKeyDown = false;
};

/*Opera requires a special mouseClick as it else clicks twice*/
Selenium.prototype.doMouseClickOpera = function(locator, value){
    var element = this.browserbot.findElement(locator);
    value = value.split(":");
    var clientXY = getClientXY(element, value[0]);

    if(value.length > 1){
        this.browserbot.shiftKeyDown = (new RegExp("shift")).test(value[1]);
        this.browserbot.controlKeyDown = (new RegExp("ctrl")).test(value[1]);
        this.browserbot.altKeyDown = (new RegExp("alt")).test(value[1]);
    }

    this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
    //	element.focus();
    this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);

    this.browserbot.shiftKeyDown = this.browserbot.controlKeyDown = this.browserbot.altKeyDown = false;
};

/*Does a mouseClick on the target element. Used descriptive purposes.*/
Selenium.prototype.doCloseNotification = function(locator, value){
    var element = this.browserbot.findElement(locator);
    var doc = element.document;
    this.doMouseClick(locator, value);
    var notificationHidden = function() {
        // IE does not set parentNode to null but attaches the element to a document-fragment
        var hidden = (element.parentNode == null) || element.document != doc;
        return hidden;
    }
    return Selenium.decorateFunctionWithTimeout(notificationHidden, 5000);
};

/*Does a mouse over on target element at point x,y so tooltip shows up over element and not mouse cursor position*/
Selenium.prototype.doShowTooltip = function(locator, value){
    var element = this.browserbot.findElement(locator);
    var clientXY = getClientXY(element, value);

    this.browserbot.triggerMouseEvent(element, 'mouseover', true, clientXY[0], clientXY[1]);
};

/* For adding test to be run before this test */
Selenium.prototype.doIncludeTest = function(locator, path){
    };

/**
 * Overridden the default selenium strategy because of IE trim bug
 * 
 *  OptionLocator for options identified by their labels.
 */
OptionLocatorFactory.prototype.OptionLocatorByLabel = function(label) {
    this.label = label;
    this.labelMatcher = new PatternMatcher(this.label);
    this.findOption = function(element) {
        for (var i = 0; i < element.options.length; i++) {
            // IE does not trim the text property like other browsers
            var text = element.options[i].text.replace(/^\s+|\s+$/g,"");
            if (this.labelMatcher.matches(text)) {
                return element.options[i];
            }
        }
        throw new SeleniumError("Option with label '" + this.label + "' not found");
    };

    this.assertSelected = function(element) {
        // IE does not trim the text property like other browsers
        var selectedLabel = element.options[element.selectedIndex].text.replace(/^\s+|\s+$/g,"");
        Assert.matches(this.label, selectedLabel)
    };
};

/**
 * Copies triggerKeyEvent from htmlutils.js and removes keycode for charCodeArg on firefox keyEvent
 */
function triggerSpecialKeyEvent(element, eventType, keySequence, canBubble, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown) {
    var keycode = getKeyCodeFromKeySequence(keySequence);
    canBubble = (typeof(canBubble) == undefined) ? true : canBubble;
    if (element.fireEvent && element.ownerDocument && element.ownerDocument.createEventObject) { // IE
        var keyEvent = createEventObject(element, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown);
        keyEvent.keyCode = keycode;
        try {
            element.fireEvent('on' + eventType, keyEvent);
        } catch (e) {
            if (e.number && e.number == -2147467259) {
            // IE is most likely trying to tell us that the element was
            // removed and the event could thus not be sent. We ignore this.
            } else {
                throw e;
            }
        }
    }
    else {
        var evt;
        if (window.KeyEvent) {
            evt = document.createEvent('KeyEvents');
            evt.initKeyEvent(eventType, true, true, window, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown, keycode, "");
        } else {
            // WebKit based browsers
            evt = document.createEvent('Events');
            
            evt.shiftKey = shiftKeyDown;
            evt.metaKey = metaKeyDown;
            evt.altKey = altKeyDown;
            evt.ctrlKey = controlKeyDown;

            evt.initEvent(eventType, true, true);
            evt.keyCode = parseInt(keycode);
            evt.which = keycode;
        }

        element.dispatchEvent(evt);
    }
}

Selenium.prototype.getElementPositionTop = function(locator) {
    /**
   * Retrieves the vertical position of an element
   *
   * @param locator an <a href="#locators">element locator</a> pointing to an element OR an element itself
   * @return number of pixels from the edge of the frame.
   */
    var element;
    if ("string"==typeof locator) {
        element = this.browserbot.findElement(locator);
    } else {
        element = locator;
    }

    var y = 0;
    while (element != null) {
        if(document.all) {
            if( (element.tagName != "TABLE") && (element.tagName != "BODY") ) {
                y += element.clientTop;
            }
        } else {
            // Netscape/DOM
            if(element.tagName == "TABLE") {
                var parentBorder = parseInt(element.border);
                if(isNaN(parentBorder)) {
                    var parentFrame = element.getAttribute('frame');
                    if(parentFrame != null) {
                        y += 1;
                    }
                } else if(parentBorder > 0) {
                    y += parentBorder;
                }
            } else if (!/Opera[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
                y += element.clientTop;
            }
        }
        y += element.offsetTop;
        element = element.offsetParent;
    }
    return y;
};

//Starts dragging of taget element
Selenium.prototype.doDrag = function(locator, value){
    var element = this.browserbot.findElement(locator);
    var clientXY = getClientXY(element, value);

    this.browserbot.triggerMouseEvent(element, 'mouseover', true, clientXY[0], clientXY[1]);
    this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
    this.browserbot.triggerMouseEvent(element, 'mousemove', true, clientXY[0], clientXY[1]);
    this.browserbot.triggerMouseEvent(element, 'mousemove', true, clientXY[0]+1, clientXY[1]+1);
};

// Drops target element from drag on this target element
Selenium.prototype.doDrop = function(locator, value){
    var element = this.browserbot.findElement(locator);
    var clientXY = getClientXY(element, value);

    this.browserbot.triggerMouseEvent(element, 'mouseover', true, clientXY[0]-1, clientXY[1]-1);
    this.browserbot.triggerMouseEvent(element, 'mousemove', true, clientXY[0]-1, clientXY[1]-1);
    this.browserbot.triggerMouseEvent(element, 'mousemove', true, clientXY[0], clientXY[1]);
    this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);
};

// Assert that an element has the specified css class.
Selenium.prototype.doAssertCSSClass = function(locator, value){
    var element = this.browserbot.findElement(locator);
    var css = element.className;

    if (!(new RegExp(value)).test(css)) {
        Assert.fail("Element doesn't have the " + value + " class.");
    }
};

Selenium.prototype.doAssertNotCSSClass = function(locator, value){
    var element = this.browserbot.findElement(locator);
    var css = element.className;

    if ((new RegExp(value)).test(css)) {
        Assert.fail("Element has the " + value + " class.");
    }
};

Selenium.prototype.doUploadFile = function(locator, value){
    this.doType(locator, value);
};

/*
* Copyright 2004 ThoughtWorks, Inc
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

function TestLoop(commandFactory) {
    this.commandFactory = commandFactory;
}

TestLoop.prototype = {

    start : function() {
        selenium.reset();
        LOG.debug("currentTest.start()");
        this.continueTest();
    },

    continueTest : function() {
        /**
         * Select the next command and continue the test.
         */
        LOG.debug("currentTest.continueTest() - acquire the next command");
        if (! this.aborted) {
            this.currentCommand = this.nextCommand();
        }
        if (! this.requiresCallBack) {
            this.continueTestAtCurrentCommand();
        } // otherwise, just finish and let the callback invoke continueTestAtCurrentCommand()
    },

    continueTestAtCurrentCommand : function() {
        LOG.debug("currentTest.continueTestAtCurrentCommand()");
        if (this.currentCommand) {
            // TODO: rename commandStarted to commandSelected, OR roll it into nextCommand
            this.commandStarted(this.currentCommand);
            this._resumeAfterDelay();
        } else {
            this._testComplete();
        }
    },

    _resumeAfterDelay : function() {
        /**
         * Pause, then execute the current command.
         */

        // Get the command delay. If a pauseInterval is set, use it once
        // and reset it.  Otherwise, use the defined command-interval.
        var delay = this.pauseInterval || this.getCommandInterval();
        this.pauseInterval = undefined;

        if (this.currentCommand.isBreakpoint || delay < 0) {
            // Pause: enable the "next/continue" button
            this.pause();
        } else {
            window.setTimeout(fnBind(this.resume, this), delay);
        }
    },

    resume: function() {
        /**
         * Select the next command and continue the test.
         */
        LOG.debug("currentTest.resume() - actually execute");
        try {
            selenium.browserbot.runScheduledPollers();
            this._executeCurrentCommand();
            this.continueTestWhenConditionIsTrue();
        } catch (e) {
            if (!this._handleCommandError(e)) {
                this.testComplete();
            } else {
                this.continueTest();
            }
        }
    },

    _testComplete : function() {
        selenium.ensureNoUnhandledPopups();
        this.testComplete();
    },

    _executeCurrentCommand : function() {
        /**
         * Execute the current command.
         *
         * @return a function which will be used to determine when
         * execution can continue, or null if we can continue immediately
         */
         
        var command = this.currentCommand;
        LOG.info("Executing: |" + command.command + " | " + command.target + " | " + command.value + " |");

        var handler = this.commandFactory.getCommandHandler(command.command);
        if (handler == null) {
            throw new SeleniumError("Unknown command: '" + command.command + "'");
        }

        command.target = selenium.preprocessParameter(command.target);
        command.value = selenium.preprocessParameter(command.value);
        LOG.debug("Command found, going to execute " + command.command);
        this.result = handler.execute(selenium, command);
        

        this.waitForCondition = this.result.terminationCondition;
	    
        if(!(new RegExp("expectDialog")).test(command.command)) {
            command = new Command("waitForVaadin", "", "");
            handler = this.commandFactory.getCommandHandler(command.command);
            this.vaadinresult = handler.execute(selenium, command);
            this.waitForVaadinCondition = this.vaadinresult.terminationCondition;
        }
    },

    _handleCommandError : function(e) {
        if (!e.isSeleniumError) {
            LOG.exception(e);
            var msg = "Command execution failure. Please search the forum at http://clearspace.openqa.org for error details from the log window.";
            msg += "  The error message is: " + extractExceptionMessage(e);
            return this.commandError(msg);
        } else {
            LOG.error(e.message);
            return this.commandError(e.message);
        }
    },

    continueTestWhenConditionIsTrue: function () {
        /**
         * Busy wait for waitForCondition() to become true, and then carry
         * on with test.  Fail the current test if there's a timeout or an
         * exception.
         */
        //LOG.debug("currentTest.continueTestWhenConditionIsTrue()");
        selenium.browserbot.runScheduledPollers();
        try {
            if (this.waitForCondition == null) {
                if(this.waitForVaadinCondition()){
                    LOG.debug("null condition; let's continueTest()");
                    LOG.debug("Command complete");
                    this.commandComplete(this.result);
                    this.continueTest();
                }else{
                    window.setTimeout(fnBind(this.continueTestWhenConditionIsTrue, this), 10);
                }
            } else if (this.waitForCondition()) {
                if(this.waitForVaadinCondition()){
                    LOG.debug("condition satisfied; let's continueTest()");
                    this.waitForCondition = null;
                    LOG.debug("Command complete");
                    this.commandComplete(this.result);
                    this.continueTest();
                }else{
                    this.waitForCondition = null;
                    window.setTimeout(fnBind(this.continueTestWhenConditionIsTrue, this), 10);
                }
            } else {
                //LOG.debug("waitForCondition was false; keep waiting!");
                window.setTimeout(fnBind(this.continueTestWhenConditionIsTrue, this), 10);
            }
        } catch (e) {
            this.result = {};
            this.result.failed = true;
            this.result.failureMessage = extractExceptionMessage(e);
            this.commandComplete(this.result);
            this.continueTest();
        }
    },

    pause : function() {},
    nextCommand : function() {},
    commandStarted : function() {},
    commandComplete : function() {},
    commandError : function() {},
    testComplete : function() {},

    getCommandInterval : function() {
        return 0;
    }

}


/**
 * Resizes the browser window size so the canvas has the given width and height.
 * <p>
 * Note: Does not work in Opera as Opera does not allow window.resize(w,h).
 * Opera is resized during startup using custom profiles.
 * </p>
 */
function vaadin_testbench_calculateAndSetCanvasSize(width, height) {
    var win = selenium.browserbot.getUserWindow();
    var body = win.document.body;
	
    // Need to move browser to top left before resize to avoid the
    // possibility that it goes below or to the right of the screen.
    
    win.moveTo(1,1);

    var innerWidth = win.innerWidth;
    var innerHeight = win.innerHeight;
    if (typeof innerWidth == 'undefined') {
        vaadin_testbench_hideIEScrollBar();
        innerWidth = body.clientWidth;
        innerHeight = body.clientHeight;
    }

    win.resizeBy(width-innerWidth, height-innerHeight);
	
    //	if (navigator.userAgent.indexOf("Chrome") != -1) {
    //		// Window resize functions are pretty broken in Chrome 6..
    //		innerWidth = win.innerWidth;
    //		innerHeight = win.innerHeight;
    //		win.resizeBy(width-innerWidth, height-innerHeight);
    //	}

    if (navigator.userAgent.indexOf("Linux") != -1 && navigator.userAgent.indexOf("Chrome") != -1) {
        // window.resizeTo() is pretty badly broken in Linux Chrome...

        // Need to wait for innerWidth to stabilize (Chrome issue #55409)
        //sleep(500);

        innerWidth = win.innerWidth;
        innerHeight = win.innerHeight;

        // Hide main view scrollbar to get correct measurements in IE
        // (overflow=hidden)
        if (typeof innerWidth == 'undefined') {
            body.style.overflow='hidden';
            innerWidth = body.clientWidth;
            innerHeight = body.clientHeight;
        }
        var getSize = innerWidth+','+innerHeight;
        var newSizes = getSize().split(",");
        var newWidth = parseInt(newSizes[0]);
        var newHeight = parseInt(newSizes[1]);

        var widthError = width - newWidth;
        var heightError = height - newHeight;

        // Correct the window size
        win.resizeTo(win.outerWidth - win.innerWidth + width + widthError,
            win.outerHeight - win.innerHeight + height + heightError);
    }

    var outerWidth = win.outerWidth;
    var outerHeight = win.outerHeight;
    if (typeof outerWidth == 'undefined') {
        // Find the outerWidth/outerHeight for IE by
        // resizing the window twice and measuring the
        // differences.
        var offsWidth = body.offsetWidth;
        var offsHeight = body.offsetHeight;
        win.resizeTo(500,500);
        var barsWidth = 500 - body.offsetWidth;
        var barsHidth = 500 - body.offsetHeight;
        outerWidth = barsWidth + offsWidth;
        outerHeight = barsHidth + offsHeight;
        win.resizeTo(outerWidth, outerHeight);
    }
    return outerWidth + "," + outerHeight;
}

function vaadin_testbench_hideIEScrollBar() {
    // Hide main view scrollbar to get correct measurements in IE
    // (overflow=hidden)
    if (navigator.userAgent.indexOf("MSIE") != -1) {
        selenium.browserbot.getUserWindow().document.body.style.overflow='hidden';
    }
}

function vaadin_testbench_setWindowSize(width, height) {
    var win = selenium.browserbot.getUserWindow();
    win.moveTo(1,1);
    win.resizeTo(width, height);
}

function vaadin_testbench_getCanvasWidth() {
    var win = selenium.browserbot.getUserWindow();
    if (win.innerWidth) {
        return win.innerWidth;
    }
    if (win.document.body) {
        return win.document.body.clientWidth;
    }
    if (win.document.documentElement) {
        return win.document.documentElement.clientWidth;
    }
    return 0;
}

function vaadin_testbench_getCanvasHeight() {
    var win = selenium.browserbot.getUserWindow();
    if (win.innerHeight) {
        return win.innerHeight;
    }
    if (win.document.body.clientHeight) {
        return win.document.body.clientHeight;
    }
    if (win.document.documentElement.clientHeight) {
        return win.document.documentElement.clientHeight;
    }
    return 0;
}

/**
 * Gets or calculates the x position of the canvas in the upper left corner on
 * screen
 * 
 * @return the x coordinate of the canvas
 */
function vaadin_testbench_getCanvasX() {
    var win = selenium.browserbot.getUserWindow();

    // IE
    if (navigator.userAgent.indexOf("MSIE") != -1) {
        // FIXME: Canvas position given by IE is 2px off
        return win.screenLeft + 2;
    }
    var horizontalDecorations = win.outerWidth - win.innerWidth;
    return horizontalDecorations / 2 + win.screenX;
}

/**
 * Gets or calculates the y position of the canvas in the upper left corner on
 * screen
 * 
 * @param canvasHeight
 * @return
 */
function vaadin_testbench_getCanvasY(canvasHeight) {
    if (navigator.userAgent.indexOf("MSIE") != -1) {
        // FIXME: Canvas position given by IE is 2px off
        return selenium.browserbot.getUserWindow().screenTop + 2;
    }

    // We need to guess a location that is within the canvas. The window
    // is positioned at (0,0) or (1,1) at this point.

    // Using 0.95*canvasHeight we should always be inside the canvas.
    // 0.95 is used because the detection routine used later on also
    // checks some pixels below this position (for some weird reason).
    return (canvasHeight * 0.95) | 0;
}

function vaadin_testbench_getDimensions() {
    var screenWidth = screen.availWidth;
    var screenHeight = screen.availHeight;
    var canvasWidth = vaadin_testbench_getCanvasWidth();
    var canvasHeight = vaadin_testbench_getCanvasHeight();
    var canvasX = vaadin_testbench_getCanvasX();
    var canvasY = vaadin_testbench_getCanvasY(canvasHeight);
    return "" + screenWidth + "," + screenHeight + "," + canvasWidth
    + "," + canvasHeight + "," + canvasX + "," + canvasY;
}
