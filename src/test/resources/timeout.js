// A fake axe-core that takes 5 minutes to finish
window.axe = {
  configure: function () {
    return { allowedOrigins: ['<unsafe_all_origins>'] }
  },
  run: function (context, options, callback) {
    setTimeout(function () {
      callback()
    }, 1000 * 60 * 5)
  }
}
