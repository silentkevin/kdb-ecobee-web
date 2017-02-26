let path = require("path");
let Dashboard = require('webpack-dashboard');
let DashboardPlugin = require('webpack-dashboard/plugin');

let dashboard = new Dashboard();

module.exports = {
    entry: './src/main/js/app.js',

    devtool: 'sourcemaps',

    cache: true,

    debug: true,

    output: {
        path: __dirname + '/src/main/resources/static/js',
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
                include: /\.json$/,
                loaders: ["json-loader"]
            },
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
    },

    resolve: {
        extensions: ['', '.json', '.jsx', '.js']
    }
};
