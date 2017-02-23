var webpack = require('webpack');
var WebpackDevServer = require('webpack-dev-server');

var config = require('./webpack.config.js');
var compiler = webpack(config);

var server = new WebpackDevServer(compiler, {
    contentBase: config.output.path,
    publicPath: config.output.publicPath,
    quiet: true,
    historyApiFallback: true,
    proxy: {
        "/data": "http://localhost:8080"
    }
});

server.listen(8081);
