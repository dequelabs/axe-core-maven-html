;
delete window.axe.runPartial; 
delete window.axe.finishRun; 
var origRun =  window.axe.run; 
window.axe.run = function() { 
  return origRun.apply(window.axe, arguments).then(function (results) {
    results.testEngine.name = 'axe-legacy'
    return results;
  });
}
