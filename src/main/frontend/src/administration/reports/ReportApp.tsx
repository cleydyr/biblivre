import {
  EuiBasicTable,
  EuiButton,
  EuiFieldText,
  EuiForm,
  EuiFormRow,
  EuiGlobalToastList,
  EuiPageTemplate,
} from "@elastic/eui";
import { Toast } from "@elastic/eui/src/components/toast/global_toast_list";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { Report, ReportApi } from "./api";

const REPORT_QUERY = "getReports";

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

  const api = new ReportApi({}, "http://localhost:8090/api/v2");

  const getReportsQuery = useQuery({
    queryKey: [REPORT_QUERY],
    queryFn: () =>
      api.getReports({
        headers: {
          "X-Biblivre-Schema": "bcuniaodosaber",
          Accept: "application/json",
        },
      }),
  });

  const { mutate: updateReport } = useMutation({
    mutationKey: [REPORT_QUERY],
    mutationFn: (report: Report) =>
      api.updateReport(report, report.id ?? 0, {
        headers: {
          "X-Biblivre-Schema": "bcuniaodosaber",
          Accept: "application/json",
        },
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [REPORT_QUERY] });
    },
  });

  const { data: reports, isFetching } = getReportsQuery;

  const [editingReport, setEditingReport] = useState(
    undefined as Report | undefined
  );

  return (
    <>
      <EuiPageTemplate>
        <EuiPageTemplate.Header
          pageTitle="Relatórios personalizados"
          description="Crie e gere relatórios personalizados"
          rightSideItems={[<EuiButton fill>Novo modelo</EuiButton>]}
        />
        <EuiPageTemplate.Section>
          {editingReport ? (
            <EditReportForm
              report={editingReport}
              onSubmit={(report) => {
                setEditingReport(undefined);
                updateReport(report);
              }}
            />
          ) : (
            <EuiBasicTable
              tableCaption="Demo of EuiBasicTable"
              items={reports ?? []}
              rowHeader="firstName"
              columns={[
                {
                  field: "name",
                  name: "Título",
                  sortable: true,
                },
                {
                  field: "description",
                  name: "Descrição",
                },
                {
                  name: "Ações",
                  actions: [
                    {
                      name: "Editar",
                      description: "Editar este modelo de relatório",
                      type: "icon",
                      icon: "pencil",
                      onClick: setEditingReport,
                    },
                    {
                      name: "Excluir",
                      description: "Excluir este modelo de relatório",
                      type: "icon",
                      icon: "trash",
                      onClick: () => {},
                    },
                    {
                      name: "Preencher",
                      description: "Preencher com tooltip",
                      type: "icon",
                      icon: "playFilled",
                      onClick: () => {},
                      isPrimary: true,
                    },
                    {
                      name: "Histórico",
                      description: "Baixar relatórios gerados anteriormente",
                      type: "icon",
                      icon: "tableOfContents",
                      onClick: () => {},
                    },
                  ],
                },
              ]}
              loading={isFetching}
            />
          )}
        </EuiPageTemplate.Section>
      </EuiPageTemplate>
      <EuiGlobalToastList
        toasts={toasts}
        dismissToast={removeToast}
        toastLifeTimeMs={6000}
      />
    </>
  );
}

type ReportFormProps = {
  report: Report | undefined;
  onSubmit: (report: Report) => void;
};

const EditReportForm = ({ report, onSubmit }: ReportFormProps) => {
  const [name, setName] = useState(report?.name ?? "");

  const [description, setDescription] = useState(report?.description);

  return (
    <EuiForm>
      <EuiFormRow
        label="Nome"
        isInvalid={name.length === 0}
        error={["O nome do relatório é obrigatório"]}
      >
        <EuiFieldText
          value={name}
          onChange={(e) => setName(e.target.value)}
          aria-required={true}
          required={true}
        />
      </EuiFormRow>
      <EuiFormRow label="Descrição">
        <EuiFieldText
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </EuiFormRow>
      <EuiButton
        fill
        onClick={() => {
          onSubmit({ ...report, name, description });
        }}
      >
        Salvar
      </EuiButton>
    </EuiForm>
  );
};
