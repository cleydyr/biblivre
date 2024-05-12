import {
  EuiBasicTable,
  EuiButton,
  EuiButtonEmpty,
  EuiCallOut,
  EuiConfirmModal,
  EuiDescribedFormGroup,
  EuiFieldText,
  EuiFlexGroup,
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutHeader,
  EuiForm,
  EuiFormRow,
  EuiGlobalToastList,
  EuiPageTemplate,
  EuiPanel,
  EuiTitle,
  useGeneratedHtmlId,
} from "@elastic/eui";
import { Toast } from "@elastic/eui/src/components/toast/global_toast_list";
import { Fragment, useCallback, useState } from "react";
import {
  ReportFill,
  ReportFillRequest,
  ReportTemplate,
} from "../../generated-sources";
import UploadReportForm from "./UploadReportForm";
import {
  useAddReportMutation,
  useDeleteReportMutation,
  useFillReportMutation,
  useListReportsQuery,
  useUpdateReportMutation,
} from "./queries";

type Screen = "list" | "edit" | "upload";

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
  {
    id: "report-uploaded",
    title: "Modelo de relatório criado",
    color: "success",
    iconType: "check",
  },
  {
    id: "report-filled",
    title: "Relatório gerado",
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
    setDeletingReport(undefined);
    setToasts([toastsById["report-deleted"]]);
  }, []);

  const handleDeleteReportFailure = useCallback((error: Error) => {
    setErrors((errors) => [...errors, error.message]);
    setDeletingReport(undefined);
  }, []);

  const handleAddReportSuccess = useCallback(() => {
    setScreen("list");
    setToasts([toastsById["report-uploaded"]]);
  }, []);

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
    onSuccess: handleAddReportSuccess,
    onError: (error: Error) => {
      setErrors((errors) => [...errors, error.message]);
    },
  });

  const { mutate: fillReport } = useFillReportMutation({
    onSuccess: (reportFill: ReportFill) => {
      setReportFill(reportFill);
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

  const [reportTemplateToFill, setReportTemplateToFill] = useState<
    ReportTemplate | undefined
  >(undefined);

  const [reportFill, setReportFill] = useState<ReportFill | undefined>(
    undefined
  );

  const reportFillFlyoutId = useGeneratedHtmlId();

  let section;

  let reportFillFlyout;

  if (reportTemplateToFill) {
    const downloadReportFillBanner = reportFill && (
      <EuiCallOut>
        <p>
          O relatório foi gerado com sucesso.{" "}
          <a href={reportFill.uri}>Clique para baixar o relatório gerado.</a>
        </p>
      </EuiCallOut>
    );
    reportFillFlyout = (
      <EuiFlyout
        ownFocus
        onClose={() => setReportTemplateToFill(undefined)}
        aria-labelledby={reportFillFlyoutId}
      >
        <EuiFlyoutHeader hasBorder>
          <EuiTitle size="m">
            <h2 id={reportFillFlyoutId}>Preencher relatório</h2>
          </EuiTitle>
        </EuiFlyoutHeader>
        <EuiFlyoutBody banner={downloadReportFillBanner}>
          <ReportFillForm report={reportTemplateToFill} onSubmit={fillReport} />
        </EuiFlyoutBody>
      </EuiFlyout>
    );
  }

  switch (screen) {
    case "list":
      section = (
        <ReportTemplateTable
          reports={reports ?? []}
          onEdit={(report) => {
            setEditingReport(report);
            setScreen("edit");
          }}
          onDelete={(report) => {
            setDeletingReport(report);
          }}
          onFill={(report) => {
            setReportTemplateToFill(report);
          }}
        />
      );
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
          {reportFillFlyout}
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
          confirmButtonText={`Excluir ${deletingReport.name}`}
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
}

const ReportFillForm = ({
  report,
  onSubmit,
}: {
  report: ReportTemplate;
  onSubmit: (report: ReportFillRequest) => void;
}) => {
  return (
    <EuiFlexGroup direction="column">
      <EuiCallOut iconType="iInCircle">
        <p>Este relatório não aceita parâmetros.</p>
      </EuiCallOut>
      <EuiForm
        onSubmit={(e) => {
          e.preventDefault();
          onSubmit({ reportTemplateId: report.id, parameters: {} });
        }}
      >
        <EuiButton
          fill
          onClick={() => {
            onSubmit({ reportTemplateId: report.id, parameters: {} });
          }}
        >
          Gerar relatório
        </EuiButton>
      </EuiForm>
    </EuiFlexGroup>
  );
};

const ReportTemplateTable = ({
  reports,
  onEdit,
  onDelete,
  onFill,
}: {
  reports: ReportTemplate[];
  onEdit: (report: ReportTemplate) => void;
  onDelete: (report: ReportTemplate) => void;
  onFill: (report: ReportTemplate) => void;
}) => {
  return (
    <EuiBasicTable
      tableCaption="Demo of EuiBasicTable"
      items={reports}
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
              onClick: onEdit,
            },
            {
              name: "Excluir",
              description: "Excluir este modelo de relatório",
              type: "icon",
              icon: "trash",
              onClick: onDelete,
            },
            {
              name: "Preencher",
              description: "Gerar um relatório",
              type: "icon",
              icon: "playFilled",
              onClick: onFill,
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
    />
  );
};

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
