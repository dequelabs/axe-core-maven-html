// Create a "fake" axe-core object which returns errors from "axe.run()".
window.axe = {
  configure: function () {
    return { allowedOrigins: ['<unsafe_all_origins>'] }
  },
  run: function (context, options, callback) {
    var error = new Error('boom!')
    callback(error, null)
  }
}
