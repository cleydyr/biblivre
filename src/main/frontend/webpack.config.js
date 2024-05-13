const HtmlWebpackPlugin = require("html-webpack-plugin");
const path = require("path");

module.exports = {
  entry: "./src/index.tsx",
  mode: "development",
  output: {
    path: path.resolve(
      __dirname,
      "../resources/META-INF/resources/static/scripts"
    ),
    filename: "bundle.js",
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
  plugins: [
    new HtmlWebpackPlugin({
      template: "index.html",
    }),
  ],
  devtool: "inline-source-map",
  devServer: {
    static: path.join(__dirname, "../resources/META-INF/resources/static"),
    port: 3000,
    proxy: [
      {
        context: ["/api/v2"],
        target: "http://localhost:8090/api/v2",
        secure: false,
        changeOrigin: true,
      },
    ],
  },
  // optimization: {
  //   runtimeChunk: "single",
  // },
};
