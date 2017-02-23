var path = require("path");
var Dashboard = require('webpack-dashboard');
var DashboardPlugin = require('webpack-dashboard/plugin');
var dashboard = new Dashboard();

module.exports = {
    entry: './src/main/js/app.js',

    devtool: 'sourcemaps',

    cache: true,

    debug: true,

    output: {
        path: __dirname + '/src/main/resources/static',
        filename: 'built.js',
        publicPath: '/'
    },

    stats: {
        colors: true
    },

    plugins: [
        new DashboardPlugin(dashboard.setData)
    ],

    module: {
        loaders: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                loader: 'babel',
                query: {
                    cacheDirectory: true,
                    presets: ['es2015', 'react']
                }
            }
        ]
    }
};
