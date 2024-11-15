import React from "react";
import ReactDOM from "react-dom/client";
import ReportApp from "./administration/reports/App";
import FormCustomization from "./administration/customization/App";
import "@elastic/eui/dist/eui_theme_light.css";

const reportRoot = document.getElementById("biblivre-root");

if (reportRoot) {
  const root = ReactDOM.createRoot(reportRoot);

  root.render(
    <React.StrictMode>
      <ReportApp />
    </React.StrictMode>,
  );
}

const formCustomizationRoot = document.getElementById("form-customization-app");

if (formCustomizationRoot) {
  const root = ReactDOM.createRoot(formCustomizationRoot);

  root.render(
    <React.StrictMode>
      <FormCustomization />
    </React.StrictMode>,
  );
}
