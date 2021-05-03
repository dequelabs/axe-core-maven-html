var callback = arguments[arguments.length - 1];

var context = typeof arguments[0] === 'string' ? JSON.parse(arguments[0]) : arguments[0];
context = context || document;

var options = JSON.parse(arguments[1]);

var result = { error: '', results: null };

axe.configure({ allowedOrigins: ['<unsafe_all_origins>'] });

axe.run(context, options, function (err, res) {
    {
        if (err) {
            result.error = err.message;
        } else {
            result.results = res;
        }
        callback(JSON.stringify(result));
    }
});