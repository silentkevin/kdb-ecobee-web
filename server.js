var webpack = require('webpack');
var WebpackDevServer = require('webpack-dev-server');

var config = require('./webpack.config.js');
var compiler = webpack(config);

var server = new WebpackDevServer(compiler, {
    publicPath: config.output.publicPath,
    quiet: true,
    historyApiFallback: true
    // proxy: {
    //     "/backend": "http://localhost:8080" // <- backend
    // }
});

server.listen(8081);
