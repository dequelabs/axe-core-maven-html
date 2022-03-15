if (document.documentElement.classList.contains('crash')) {
  window.axe.run = function () {
    throw new Error('Crashing axe.run(). Boom!');
  };

  if (window.axe.runPartial) {
    window.axe.runPartial = function () {
      throw new Error('Crashing axe.runPartial(). Boom!');
    };
  } else if (axe.utils.respondable) {
    axe.utils.respondable.subscribe('axe.ping', () => {
      // Makes axe-core unresponsive to other frames
      // Timeouts will do the rest
    });
  }
}