import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { EuiProvider } from "@elastic/eui";
import React from "react";

import "@elastic/eui/dist/eui_theme_light.css";
import ReportApp from "./administration/reports/ReportApp";

const queryClient = new QueryClient();

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <EuiProvider colorMode="light">
        <ReportApp />
      </EuiProvider>
    </QueryClientProvider>
  );
};

export default App;
