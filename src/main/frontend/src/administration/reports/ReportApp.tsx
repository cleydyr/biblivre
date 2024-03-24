import {
  EuiGlobalToastList,
  EuiSpacer,
  EuiTab,
  EuiTabs,
  EuiText,
  EuiTitle,
} from "@elastic/eui";
import { Toast } from "@elastic/eui/src/components/toast/global_toast_list";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { getSchemaFromURL } from "../../util";
import { GenerateReportForm } from "./GenerateReportForm";
import UploadReportForm, { UploadReportFormData } from "./UploadReportForm";
import { generateReport, listReports } from "./api";
import { Report, ReportStatus } from "./types";

const REPORT_QUERY = "reports";

const GENERATE_REPORT_QUERY = "generateReport";

export default function ReportApp() {
  const [selectedTabId, setSelectedTabId] = useState("generateReport");

  const [toasts, setToasts] = useState([] as Toast[]);

  const [submitingReport, setSubmitingReport] = useState(
    undefined as Report | undefined
  );

  function removeToast(toast: Toast): void {
    setToasts(toasts.filter((t) => t.id !== toast.id));
  }

  const queryClient = useQueryClient();

  const reportQuery = useQuery({
    queryKey: [REPORT_QUERY],
    queryFn: () => listReports(getSchemaFromURL()),
  });

  const { mutate: uploadReportMutation } = useMutation({
    mutationFn: uploadReport,
    onSuccess: handleAddNewReportSuccess,
    onError: handleAddNewReportError,
  });

  const { data: reports, isFetching } = reportQuery;

  const {
    data: pdfBlob,
    isFetching: isFetchingGeneratedReport,
    isSuccess: isSuccessGeneratedReport,
    isError: isErrorGeneratedReport,
  } = useGenerateReportQuery(submitingReport);

  const reportStatus = isSuccessGeneratedReport
    ? ReportStatus.READY
    : isFetchingGeneratedReport
    ? ReportStatus.GENERATING
    : ReportStatus.PENDING;

  const pdfUrl = isSuccessGeneratedReport
    ? URL.createObjectURL(pdfBlob)
    : undefined;

  if (isErrorGeneratedReport) {
    setToasts([
      {
        id: new Date().getTime().toString(),
        title: "Erro ao gerar relatório",
        color: "danger",
        text: "Ocorreu um erro ao gerar o relatório",
        toastLifeTimeMs: 6000,
      },
    ]);
  }

  const tabs = [
    {
      id: "generateReport",
      name: "Gerar relatório",
      content: (
        <GenerateReportForm
          reports={reports}
          isLoading={isFetching}
          reportStatus={reportStatus}
          pdfUrl={pdfUrl}
          onSubmit={(report: Report) => setSubmitingReport(report)}
        />
      ),
    },
    {
      id: "uploadReport",
      name: "Criar modelo",
      content: <UploadReportForm onSubmit={uploadReportMutation} />,
    },
  ];

  function handleAddNewReportError(
    error: Error,
    variables: UploadReportFormData,
    context: unknown
  ) {
    setToasts([
      {
        id: new Date().getTime().toString(),
        title: "Erro ao criar novo modelo de relatório",
        color: "danger",
        text: error.message,
        toastLifeTimeMs: 6000,
      },
    ]);
  }

  function handleAddNewReportSuccess() {
    // Invalidate and refetch
    queryClient.invalidateQueries({ queryKey: [REPORT_QUERY] });

    setToasts([
      {
        id: new Date().getTime().toString(),
        title: "Relatório criado com sucesso",
        color: "success",
        toastLifeTimeMs: 6000,
      },
    ]);
  }

  return (
    <>
      <EuiTitle>
        <EuiText>Relatórios personalizados</EuiText>
      </EuiTitle>
      <EuiSpacer />
      <EuiTabs>
        {tabs.map((tab) => (
          <EuiTab
            key={tab.id}
            onClick={() => setSelectedTabId(tab.id)}
            isSelected={tab.id === selectedTabId}
          >
            {tab.name}
          </EuiTab>
        ))}
      </EuiTabs>
      <EuiSpacer size="xl" />
      {tabs.find((tab) => tab.id === selectedTabId)?.content}
      <EuiGlobalToastList
        toasts={toasts}
        dismissToast={removeToast}
        toastLifeTimeMs={6000}
      />
    </>
  );
}

function useGenerateReportQuery(report: Report | undefined) {
  const reportId = report?.id || 0;

  return useQuery({
    queryKey: [GENERATE_REPORT_QUERY, reportId],
    queryFn: () => generateReport(reportId, getSchemaFromURL()),
    enabled: report !== undefined,
  });
}

/**
 * Uploads the file using the /api/v2/reports/add endpoint
 * It accepts a multipart form with a file field and POST method
 * @param file jrxml file to be uploaded
 */
async function uploadReport({
  file,
  title,
  description,
}: UploadReportFormData) {
  const schema = getSchemaFromURL();

  const formData = new FormData();
  formData.append("file", file);
  formData.append("title", title);
  formData.append("description", description);

  return fetch("/api/v2/reports/add", {
    method: "POST",
    headers: {
      "X-Biblivre-Schema": schema,
    },
    body: formData,
  });
}
