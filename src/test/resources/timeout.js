// A fake axe-core that takes 5 minutes to finish
window.axe = {
  configure: function () {},
  run: function (context, options, callback) {
    return new Promise(resolve => {
      setTimeout(function () {
        resolve();
        if (callback) {
          callback();
        }
      }, 1000 * 60 * 5)
    })
  }
}
