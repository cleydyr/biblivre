import {
  EuiButton,
  EuiForm,
  EuiFormRow,
  EuiSelect,
  EuiSpacer,
} from "@elastic/eui";
import React, { useState } from "react";
import { Report, ReportStatus } from "./types";

type GenerateReportFormProps = {
  reports: Report[] | undefined;
  isLoading: boolean;
  reportStatus: ReportStatus;
  pdfUrl: string | undefined;
  onSubmit: (report: Report) => void;
};

export const GenerateReportForm = ({
  reports = [],
  isLoading,
  reportStatus,
  pdfUrl,
  onSubmit,
}: GenerateReportFormProps) => {
  const [selectedReport, setSelectedReport] = useState(
    undefined as Report | undefined
  );

  function getGenerateReportButtonLabel(): string {
    return reportStatus === ReportStatus.GENERATING
      ? "Gerando relatório"
      : reportStatus === ReportStatus.PENDING
      ? "Gerar relatório"
      : "Baixar relatório";
  }

  function getCurrentDateAndHour() {
    return new Date()
      .toLocaleString()
      .replace(/\//g, "-")
      .replace(/:/g, "-")
      .replace(/,/g, "")
      .replace(/ /g, "_");
  }

  function downloadPDFFile(): void {
    if (!pdfUrl) {
      throw new Error("No PDF file available");
    }

    if (!selectedReport) {
      throw new Error("No report selected");
    }

    const a = document.createElement("a");
    a.href = pdfUrl;
    a.download = `${selectedReport.name}_${getCurrentDateAndHour()}.pdf`;
    a.click();
  }

  return (
    <EuiForm>
      <EuiFormRow label="Selecione um relatório">
        <EuiSelect
          options={reports.map((report: Report) => ({
            value: report.id,
            text: report.name,
          }))}
          hasNoInitialSelection
          isLoading={isLoading}
          onChange={(e) => {
            setSelectedReport(
              reports.find((report) => report.id.toString() === e.target.value)
            );
          }}
          value={selectedReport?.id.toString()}
          disabled={isLoading}
        />
      </EuiFormRow>
      <EuiSpacer />
      <EuiFormRow>
        <EuiButton
          fill
          onClick={
            reportStatus === ReportStatus.PENDING
              ? () => onSubmit(selectedReport as Report)
              : reportStatus === ReportStatus.READY
              ? downloadPDFFile
              : () => {}
          }
          aria-disabled={
            !selectedReport || reportStatus === ReportStatus.GENERATING
          }
          aria-label={getGenerateReportButtonLabel()}
          isLoading={reportStatus === ReportStatus.GENERATING}
        >
          {getGenerateReportButtonLabel()}
        </EuiButton>
      </EuiFormRow>
    </EuiForm>
  );
};
