// Imports the @clayui/css package CSS
import "@clayui/css/lib/css/atlas.css";

import ClayNavigationBar from '@clayui/navigation-bar';
import React from 'react';
import ReactDOM from "react-dom";

import spritemap from "@clayui/css/lib/images/icons/icons.svg";

const Component = () => {
  const btnStyle = {
    padding: "5.5px 16px 5.5px 16px",
    borderColor: "var(--indigo)"
  };

  return (
    <ClayNavigationBar triggerLabel="Item 1" spritemap={spritemap}>
      <ClayNavigationBar.Item active>
        <button
          className="btn btn-unstyled btn-block btn-sm"
          style={btnStyle}
          type="button"
        >
          Item 1
        </button>
      </ClayNavigationBar.Item>
      <ClayNavigationBar.Item>
        <button
          className="btn btn-unstyled btn-block btn-sm"
          style={btnStyle}
          type="button"
        >
          Item 2
        </button>
      </ClayNavigationBar.Item>
      <ClayNavigationBar.Item>
        <button
          className="btn btn-unstyled btn-block btn-sm"
          style={btnStyle}
          type="button"
        >
          Item 3
        </button>
      </ClayNavigationBar.Item>
    </ClayNavigationBar>
  );
};

const rootElement = document.getElementById("root");
ReactDOM.render(<Component />, rootElement);
