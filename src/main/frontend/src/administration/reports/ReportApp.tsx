import {
  EuiBasicTable,
  EuiButton,
  EuiButtonEmpty,
  EuiCallOut,
  EuiConfirmModal,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlexItem,
  EuiForm,
  EuiFormRow,
  EuiGlobalToastList,
  EuiIcon,
  EuiLink,
  EuiPageTemplate,
} from "@elastic/eui";
import { Toast } from "@elastic/eui/src/components/toast/global_toast_list";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useCallback, useState } from "react";
import {
  Configuration,
  InitOverrideFunction,
  ReportTemplate,
  ReportTemplateApi,
} from "../../generated-sources";
import { getSchemaFromURL } from "../../util";
import UploadReportForm, { UploadReportFormData } from "./UploadReportForm";

const REPORT_QUERY = "getReports";

const GENERATE_REPORT_QUERY = "generateReport";

type Screen = "list" | "edit" | "upload";

const DEFAULT_FETCH_OPTIONS: InitOverrideFunction = async ({
  init,
  context,
}) => ({
  ...init,
  headers: {
    ...init.headers,
    "X-Biblivre-Schema": getSchemaFromURL(),
    Accept: "application/json",
  },
});

const toasts: Toast[] = [
  {
    id: "report-deleted",
    title: "Modelo de relatório excluído",
    color: "success",
    iconType: "check",
  },
  {
    id: "report-updated",
    title: "Modelo de relatório atualizado",
    color: "success",
    iconType: "check",
  },
] as const;

const toastsById = toasts.reduce((acc, toast) => {
  acc[toast.id] = toast;
  return acc;
}, {} as Record<(typeof toasts)[number]["id"], Toast>);

export default function ReportApp() {
  const [screen, setScreen] = useState("list" as Screen);

  const [toasts, setToasts] = useState([] as Toast[]);

  function removeToast(toast: Toast): void {
    setToasts(toasts.filter((t) => t.id !== toast.id));
  }

  const handleUpdateReportSuccess = useCallback(() => {
    setScreen("list");
    setToasts([toastsById["report-updated"]]);
  }, []);

  const handleDeleteReportSuccess = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: [REPORT_QUERY] });
    setDeletingReport(undefined);
    setToasts([toastsById["report-deleted"]]);
  }, []);

  const handleDeleteReportFailure = useCallback((error: Error) => {
    setErrors((errors) => [...errors, error.message]);
    setDeletingReport(undefined);
  }, []);

  const queryClient = useQueryClient();

  const getReportsQuery = useListReportsQuery();

  const { mutate: updateReport } = useUpdateReportMutation({
    onSuccess: handleUpdateReportSuccess,
  });

  const { mutate: deleteReport } = useDeleteReportMutation({
    onSuccess: handleDeleteReportSuccess,
    onError: handleDeleteReportFailure,
  });

  const { data: reports, isFetching } = getReportsQuery;

  const { mutate: addReport } = useAddReportMutation({
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [REPORT_QUERY] });
      setScreen("list");
      setToasts([
        {
          id: "report-uploaded",
          title: "Modelo de relatório criado",
          color: "success",
          iconType: "check",
        },
      ]);
    },
    onError: (error: Error) => {
      setErrors((errors) => [...errors, error.message]);
    },
  });

  const [editingReport, setEditingReport] = useState(
    undefined as ReportTemplate | undefined
  );

  const [deletingReport, setDeletingReport] = useState(
    undefined as ReportTemplate | undefined
  );

  const [errors, setErrors] = useState([] as string[]);

  let section;

  switch (screen) {
    case "list":
      section = renderReportList();
      break;
    case "edit":
      section = (
        <EuiFlexGroup direction="column" gutterSize="xl">
          <div>
            <EuiButtonEmpty
              iconType="arrowLeft"
              onClick={() => setScreen("list")}
            >
              Voltar
            </EuiButtonEmpty>
          </div>
          <EditReportForm
            report={editingReport}
            onSubmit={(report) => {
              updateReport(report);
              setEditingReport(undefined);
            }}
          />
        </EuiFlexGroup>
      );
      break;
    case "upload":
      section = (
        <EuiFlexGroup direction="column" gutterSize="xl">
          <div>
            <EuiButtonEmpty
              iconType="arrowLeft"
              onClick={() => setScreen("list")}
            >
              Voltar
            </EuiButtonEmpty>
          </div>
          <UploadReportForm onSubmit={addReport} />
        </EuiFlexGroup>
      );
      break;
  }

  return (
    <>
      <EuiPageTemplate>
        <EuiPageTemplate.Header
          pageTitle="Relatórios personalizados"
          description="Crie e gere relatórios personalizados"
          rightSideItems={[
            screen !== "upload" && (
              <EuiButton fill onClick={handleUploadNewReport}>
                Novo modelo
              </EuiButton>
            ),
          ]}
        />
        <EuiPageTemplate.Section>
          {errors.map((error) => (
            <EuiCallOut
              title="Desculpe. Aconteceu um erro."
              color="danger"
              iconType="error"
              key={error}
            >
              <p>{error}</p>
            </EuiCallOut>
          ))}
          {section}
        </EuiPageTemplate.Section>
      </EuiPageTemplate>
      <EuiGlobalToastList
        toasts={toasts}
        dismissToast={removeToast}
        toastLifeTimeMs={6000}
      />
      {deletingReport && (
        <EuiConfirmModal
          title="Excluir modelo de relatório"
          onCancel={() => setDeletingReport(undefined)}
          onConfirm={() => {
            deleteReport(deletingReport);
          }}
          cancelButtonText="Cancelar"
          confirmButtonText="Excluir"
          buttonColor="danger"
        >
          <p>
            Você tem certeza que deseja excluir o modelo de relatório
            {` ${deletingReport.name}`}?
          </p>
        </EuiConfirmModal>
      )}
    </>
  );

  function handleUploadNewReport() {
    setScreen("upload");
  }

  function handleDeleteReportClicked(report: ReportTemplate) {
    setDeletingReport(report);
  }

  function renderReportList() {
    return (
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
                onClick: (report: ReportTemplate) => {
                  setEditingReport(report);
                  setScreen("edit");
                },
              },
              {
                name: "Excluir",
                description: "Excluir este modelo de relatório",
                type: "icon",
                icon: "trash",
                onClick: handleDeleteReportClicked,
              },
              {
                name: "Preencher",
                description: "Gerar um relatório",
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
    );
  }
}

type ReportFormProps = {
  report: ReportTemplate | undefined;
  onSubmit: (report: ReportTemplate) => void;
};

const EditReportForm = ({ report, onSubmit }: ReportFormProps) => {
  const [name, setName] = useState(report?.name ?? "");

  const [description, setDescription] = useState(report?.description);

  if (report === undefined) {
    return null;
  }

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

const useUpdateReportMutation = (
  options: Parameters<
    typeof useMutation<ReportTemplate, Error, ReportTemplate, unknown>
  >[0]
) => {
  const apiConfiguration = new Configuration({
    basePath: "http://localhost:8090/api/v2",
  });

  const api = new ReportTemplateApi(apiConfiguration);

  return useMutation({
    mutationKey: [REPORT_QUERY],
    mutationFn: (reportTemplate: ReportTemplate) =>
      api.updateReport(
        {
          reportTemplateId: reportTemplate.id ?? 0,
          reportTemplate,
        },
        DEFAULT_FETCH_OPTIONS
      ),
    ...options,
  });
};

const useDeleteReportMutation = (
  options: Parameters<
    typeof useMutation<void, Error, ReportTemplate, unknown>
  >[0]
) => {
  const apiConfiguration = new Configuration({
    basePath: "http://localhost:8090/api/v2",
  });

  const api = new ReportTemplateApi(apiConfiguration);

  return useMutation({
    mutationKey: ["delete"],
    mutationFn: (report: ReportTemplate) =>
      api.deleteReport(
        {
          reportTemplateId: report.id,
        },
        DEFAULT_FETCH_OPTIONS
      ),
    ...options,
  });
};

const useListReportsQuery = () => {
  const apiConfiguration = new Configuration({
    basePath: "http://localhost:8090/api/v2",
  });

  const api = new ReportTemplateApi(apiConfiguration);

  return useQuery({
    queryKey: [REPORT_QUERY],
    queryFn: () => api.getReportTemplates(DEFAULT_FETCH_OPTIONS),
  });
};

const useAddReportMutation = (
  options: Parameters<
    typeof useMutation<void, Error, ReportTemplate, unknown>
  >[0]
) => {
  const apiConfiguration = new Configuration({
    basePath: "http://localhost:8090/api/v2",
  });

  const api = new ReportTemplateApi(apiConfiguration);

  return useMutation({
    mutationKey: ["upload"],
    mutationFn: ({ title, description, file }: UploadReportFormData) =>
      api.compileReportTemplate(
        {
          name: title,
          description,
          file,
        },
        DEFAULT_FETCH_OPTIONS
      ),
  });
};
