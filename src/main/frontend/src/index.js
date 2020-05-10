// Imports the @clayui/css package CSS

import React from 'react';
import ReactDOM from "react-dom";
import Navigation from './navigation/navigation';

import "@clayui/css/lib/css/atlas.css";


const rootElement = document.getElementById("root");
ReactDOM.render(<Navigation />, rootElement);
