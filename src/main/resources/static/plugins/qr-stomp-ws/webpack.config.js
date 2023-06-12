const path = require('path');

module.exports = {
    entry: './src/QRStompWS.js', // Entry point of your application
    output: {
        path: path.resolve(__dirname, 'dist'), // Output directory
        filename: 'bundle.js', // Output bundle file name
    },
    mode: 'production', // Set the mode to 'production'
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/
            },
        ],
    },
};
