const webpack = require('webpack');
const path = require('path');

module.exports = {
    entry: {
      index: path.join(__dirname, "frontend/src/typescript/index.tsx")
    },
    mode: "development",
    output: {
        path: path.join(__dirname, "frontend/dist/"),
        filename: "[name].js"
    },
    module: {
      rules: [
        {
          test: /\.tsx$/,
          exclude: /node_modules/,
          use: ['ts-loader'],
        },
        {
          test: /\.ts$/,
          exclude: /node_modules/,
          use: ['ts-loader'],
        },
      ],
    },
    resolve: {
      extensions: ['.ts', '.tsx', '.js']
    },
  };