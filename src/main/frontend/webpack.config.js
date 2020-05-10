const path = require('path');

module.exports = {
	mode: 'development',
	entry: './src/index.js',
	output: {
		filename: 'bundle.js',
		path: path.resolve(__dirname, '../../../WebContent/static/scripts/')
	},

	module: {
		rules: [
			{
				test: /\.js$/,
				exclude: /(node_modules|bower_components)/,
				use: {
					loader: 'babel-loader',
					options: {
						presets: ['@babel/react',],
					},
				},
			},
			{
        test: /\.css$/i,
        use: ['style-loader', 'css-loader'],
			},
			{
        test: /\.svg$/,
        use: [
          {
            loader: 'file-loader',
            options: {
							name: '[hash]-[name].[ext]',
							outputPath: '../images',
							publicPath: 'static/images'
            },
          },
        ],
      },
		],
	},
}