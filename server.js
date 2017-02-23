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
        "/data": {
            target: "http://localhost:8080",
            secure: false
        }
    }
});

server.listen(8081);
