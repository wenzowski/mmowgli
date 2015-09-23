var page = require('webpage').create();

page.open('http://direct.mmowgli.nps.edu/piracy', function(status) 
  {
    
    console.log("Status: " + status);
    if(status ==="success")
    {
      page.render("piracy.png");
    }
    phantom.exit();
});
