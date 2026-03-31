import {
  EuiButton,
  EuiCallOut,
  EuiConfirmModal,
  EuiFieldText,
  EuiFlyout,
  EuiFlyoutBody,
  EuiFlyoutHeader,
  EuiForm,
  EuiFormRow,
  EuiGlobalToastList,
  EuiPageTemplate,
  EuiTitle,
  useGeneratedHtmlId,
} from '@elastic/eui'
import { useCallback, useState } from 'react'

import {
  useAddReportMutation,
  useDeleteReportMutation,
  useFillReportMutation,
  useListReportsQuery,
  useUpdateReportMutation,
} from './queries'
import ReportFillForm from './ReportFillForm'
import ReportTemplateTable from './ReportTemplateTable'
import UploadReportForm from './UploadReportForm'

import type { Toast } from '@elastic/eui/src/components/toast/global_toast_list'

import type { ReportFill, ReportTemplate } from '../../generated-sources'

type Screen = 'list' | 'edit' | 'upload' | 'fill'

const TOASTS: Toast[] = [
  {
    id: 'report-deleted',
    title: 'Modelo de relatório excluído',
    color: 'success',
    iconType: 'check',
  },
  {
    id: 'report-updated',
    title: 'Modelo de relatório atualizado',
    color: 'success',
    iconType: 'check',
  },
  {
    id: 'report-uploaded',
    title: 'Modelo de relatório criado',
    color: 'success',
    iconType: 'check',
  },
  {
    id: 'report-filled',
    title: 'Relatório gerado',
    color: 'success',
    iconType: 'check',
  },
] as const

const toastsById = TOASTS.reduce(
  (acc, toast) => {
    acc[toast.id] = toast
    return acc
  },
  {} as Record<(typeof TOASTS)[number]['id'], Toast>,
)

export default function ReportApp() {
  const [screen, setScreen] = useState('list' as Screen)

  const [toasts, setToasts] = useState([] as Toast[])

  function removeToast(toast: Toast): void {
    setToasts(toasts.filter((t) => t.id !== toast.id))
  }

  const handleUpdateReportSuccess = useCallback(() => {
    setScreen('list')
    setToasts([toastsById['report-updated']])
  }, [])

  const handleDeleteReportSuccess = useCallback(() => {
    setDeletingReport(undefined)
    setToasts([toastsById['report-deleted']])
  }, [])

  const handleDeleteReportFailure = useCallback((error: Error) => {
    setErrors((errors) => [...errors, error.message])
    setDeletingReport(undefined)
  }, [])

  const handleAddReportSuccess = useCallback(() => {
    setScreen('list')
    setToasts([toastsById['report-uploaded']])
  }, [])

  const getReportsQuery = useListReportsQuery()

  const { mutate: updateReport } = useUpdateReportMutation({
    onSuccess: handleUpdateReportSuccess,
  })

  const { mutate: deleteReport } = useDeleteReportMutation({
    onSuccess: handleDeleteReportSuccess,
    onError: handleDeleteReportFailure,
  })

  const { data: reports } = getReportsQuery

  const { mutate: addReport } = useAddReportMutation({
    onSuccess: handleAddReportSuccess,
    onError: (error: Error) => {
      setErrors((errors) => [...errors, error.message])
    },
  })

  const { mutate: fillReport, isPending: isFillReportPending } =
    useFillReportMutation({
      onSuccess: (reportFill: ReportFill) => {
        setReportFill(reportFill)
      },
      onError: (error: Error) => {
        setErrors((errors) => [...errors, error.message])
      },
    })

  const [editingReport, setEditingReport] = useState(
    undefined as ReportTemplate | undefined,
  )

  const [deletingReport, setDeletingReport] = useState(
    undefined as ReportTemplate | undefined,
  )

  const [errors, setErrors] = useState([] as string[])

  const [reportTemplateToFill, setReportTemplateToFill] = useState<
    ReportTemplate | undefined
  >(undefined)

  const [reportFill, setReportFill] = useState<ReportFill | undefined>(
    undefined,
  )

  const screenFlyoutId = useGeneratedHtmlId({
    prefix: screen,
  })

  const downloadReportFillBanner = screen === 'fill' && reportFill && (
    <EuiCallOut>
      <p>
        O relatório foi gerado com sucesso.{' '}
        <a href={`${import.meta.env.VITE_BIBLIVRE_ENDPOINT}/${reportFill.uri}`}>
          Clique para baixar o relatório gerado.
        </a>
      </p>
    </EuiCallOut>
  )

  let flyout

  switch (screen) {
    case 'fill':
      flyout = reportTemplateToFill && (
        <EuiFlyout
          ownFocus
          aria-labelledby={screenFlyoutId}
          onClose={() => {
            setReportTemplateToFill(undefined)
            setReportFill(undefined)
          }}
        >
          <EuiFlyoutHeader hasBorder>
            <EuiTitle size='m'>
              <h2 id={screenFlyoutId}>Preencher relatório</h2>
            </EuiTitle>
          </EuiFlyoutHeader>
          <EuiFlyoutBody banner={downloadReportFillBanner}>
            <ReportFillForm
              pending={isFillReportPending}
              report={reportTemplateToFill}
              onSubmit={fillReport}
            />
          </EuiFlyoutBody>
        </EuiFlyout>
      )
      break
    case 'edit':
      flyout = (
        <EuiFlyout
          ownFocus
          aria-labelledby={screenFlyoutId}
          onClose={() => setScreen('list')}
        >
          <EuiFlyoutHeader hasBorder>
            <EuiTitle size='m'>
              <h2 id={screenFlyoutId}>Editar modelo de relatório</h2>
            </EuiTitle>
          </EuiFlyoutHeader>
          <EuiFlyoutBody>
            <EditReportForm
              report={editingReport}
              onSubmit={(report) => {
                updateReport(report)
                setEditingReport(undefined)
                setScreen('list')
              }}
            />
          </EuiFlyoutBody>
        </EuiFlyout>
      )
      break
    case 'upload':
      flyout = (
        <EuiFlyout
          ownFocus
          aria-labelledby={screenFlyoutId}
          onClose={() => setScreen('list')}
        >
          <EuiFlyoutHeader hasBorder>
            <EuiTitle size='m'>
              <h2 id={screenFlyoutId}>Novo modelo de relatório</h2>
            </EuiTitle>
          </EuiFlyoutHeader>
          <EuiFlyoutBody>
            <UploadReportForm onSubmit={addReport} />
          </EuiFlyoutBody>
        </EuiFlyout>
      )
      break
  }

  return (
    <>
      <EuiPageTemplate>
        <EuiPageTemplate.Header
          description='Crie e gere relatórios personalizados'
          pageTitle='Relatórios personalizados'
          rightSideItems={[
            screen !== 'upload' && (
              <EuiButton fill onClick={() => setScreen('upload')}>
                Novo modelo
              </EuiButton>
            ),
          ]}
        />
        <EuiPageTemplate.Section>
          {errors.map((error) => (
            <EuiCallOut
              key={error}
              color='danger'
              iconType='error'
              title='Desculpe. Aconteceu um erro.'
            >
              <p>{error}</p>
            </EuiCallOut>
          ))}
          <ReportTemplateTable
            reports={reports ?? []}
            onDelete={(report) => {
              setDeletingReport(report)
            }}
            onEdit={(report) => {
              setEditingReport(report)
              setScreen('edit')
            }}
            onFill={(report) => {
              setReportTemplateToFill(report)
              setScreen('fill')
            }}
          />
          {flyout}
        </EuiPageTemplate.Section>
      </EuiPageTemplate>
      <EuiGlobalToastList
        dismissToast={removeToast}
        toastLifeTimeMs={6000}
        toasts={toasts}
      />
      {deletingReport && (
        <EuiConfirmModal
          buttonColor='danger'
          cancelButtonText='Cancelar'
          confirmButtonText={`Excluir ${deletingReport.name}`}
          title='Excluir modelo de relatório'
          onCancel={() => setDeletingReport(undefined)}
          onConfirm={() => {
            deleteReport(deletingReport)
          }}
        >
          <p>
            Você tem certeza que deseja excluir o modelo de relatório
            {` ${deletingReport.name}`}?
          </p>
        </EuiConfirmModal>
      )}
    </>
  )
}

type ReportFormProps = {
  report: ReportTemplate | undefined
  onSubmit: (report: ReportTemplate) => void
}

const EditReportForm = ({ report, onSubmit }: ReportFormProps) => {
  const [name, setName] = useState(report?.name ?? '')

  const [description, setDescription] = useState(report?.description)

  if (report === undefined) {
    return null
  }

  return (
    <EuiForm>
      <EuiFormRow
        error={['O nome do relatório é obrigatório']}
        isInvalid={name.length === 0}
        label='Nome'
      >
        <EuiFieldText
          aria-required
          required
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </EuiFormRow>
      <EuiFormRow label='Descrição'>
        <EuiFieldText
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </EuiFormRow>
      <EuiButton
        fill
        onClick={() => {
          onSubmit({ ...report, name, description })
        }}
      >
        Salvar
      </EuiButton>
    </EuiForm>
  )
}
