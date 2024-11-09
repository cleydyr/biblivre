const path = require("path");

module.exports = {
  entry: "./src/index.tsx",
  mode: "production",
  output: {
    path: path.resolve(
      __dirname,
      "../resources/META-INF/resources/static/scripts"
    ),
    filename: "bundle.js",
  },
  devServer: {
    static: "../resources/META-INF/resources/static/scripts",
    hot: true,
  },
  resolve: {
    modules: [__dirname, "src", "node_modules"],
    extensions: [".js", ".jsx", ".tsx", ".ts"],
  },
  module: {
    rules: [
      {
        test: /\.(js|ts)x?$/,
        use: ["babel-loader"],
      },
      {
        test: /\.css$/,
        use: ["style-loader", "css-loader"],
      },
      {
        test: /\.(png|svg|jpg|gif)$/,
        use: ["file-loader"],
      },
    ],
  },
};
