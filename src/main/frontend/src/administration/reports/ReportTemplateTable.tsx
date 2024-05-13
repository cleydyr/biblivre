import { EuiBasicTable } from "@elastic/eui";
import { ReportTemplate } from "../../generated-sources";

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
            // {
            //   name: "Histórico",
            //   description: "Baixar relatórios gerados anteriormente",
            //   type: "icon",
            //   icon: "tableOfContents",
            //   onClick: () => {},
            // },
          ],
        },
      ]}
      noItemsMessage="Nenhum modelo de relatório encontrado"
    />
  );
};

export default ReportTemplateTable;
