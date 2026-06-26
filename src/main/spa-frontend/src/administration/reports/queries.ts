import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

import { getStoredSchema } from '../../api-helpers/schema/storage'
import { defaultRestApiFetchOptions } from '../../api-helpers/rest-api'
import {
  Configuration,
  ReportFillApi,
  ReportTemplateApi,
} from '../../generated-sources'

import type {
  ReportFill,
  ReportFillRequest,
  ReportTemplate,
} from '../../generated-sources'

import type { UploadReportFormData } from './UploadReportForm'

const LIST_REPORTS = 'listReports'

function baseEndpointPath() {
  return `${import.meta.env.VITE_BIBLIVRE_ENDPOINT}/api/v2`
}

type UseMutationOptions<TVariables, TData, TError = Error> = Parameters<
  typeof useMutation<TData, TError, TVariables, unknown>
>[0]

export const useFillReportMutation = (
  options: UseMutationOptions<ReportFillRequest, ReportFill> = {},
) => {
  const queryClient = useQueryClient()

  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  })

  const api = new ReportFillApi(apiConfiguration)

  return useMutation({
    ...options,
    mutationFn: (request: ReportFillRequest) => {
      if (!getStoredSchema()) {
        return Promise.reject(
          new Error(
            'Selecione uma biblioteca antes de gerar o relatório.',
          ),
        )
      }

      return api.createReportFill(
        {
          reportFillRequest: {
            reportTemplateId: request.reportTemplateId,
            parameters: request.parameters,
          },
        },
        defaultRestApiFetchOptions,
      )
    },
    onSuccess: (data, variables, onMutateResult, context) => {
      queryClient.invalidateQueries({ queryKey: [LIST_REPORTS] })
      options.onSuccess?.(data, variables, onMutateResult, context)
    },
  })
}

const useUpdateReportMutation = (
  options: UseMutationOptions<ReportTemplate, ReportTemplate> = {},
) => {
  const queryClient = useQueryClient()

  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  })

  const api = new ReportTemplateApi(apiConfiguration)

  return useMutation({
    ...options,
    mutationFn: (reportTemplate: ReportTemplate) =>
      api.updateReport(
        {
          reportTemplateId: reportTemplate.id ?? 0,
          reportTemplate,
        },
        defaultRestApiFetchOptions,
      ),
    onSuccess: (data, variables, onMutateResult, context) => {
      queryClient.invalidateQueries({ queryKey: [LIST_REPORTS] })
      options.onSuccess?.(data, variables, onMutateResult, context)
    },
  })
}

const useDeleteReportMutation = (
  options: UseMutationOptions<ReportTemplate, void> = {},
) => {
  const queryClient = useQueryClient()

  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  })

  const api = new ReportTemplateApi(apiConfiguration)

  return useMutation({
    ...options,
    mutationFn: (report: ReportTemplate) =>
      api.deleteReport(
        {
          reportTemplateId: report.id,
        },
        defaultRestApiFetchOptions,
      ),
    onSuccess: (data, variables, onMutateResult, context) => {
      queryClient.invalidateQueries({ queryKey: [LIST_REPORTS] })
      options.onSuccess?.(data, variables, onMutateResult, context)
    },
  })
}

const useListReportsQuery = () => {
  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  })

  const api = new ReportTemplateApi(apiConfiguration)

  return useQuery({
    queryKey: [LIST_REPORTS],
    queryFn: () => api.getReportTemplates(defaultRestApiFetchOptions),
  })
}

const useAddReportMutation = (
  options: UseMutationOptions<UploadReportFormData, ReportTemplate> = {},
) => {
  const queryClient = useQueryClient()

  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  })

  const api = new ReportTemplateApi(apiConfiguration)

  return useMutation({
    ...options,
    mutationFn: ({ title, description, file }: UploadReportFormData) => {
      if (!getStoredSchema()) {
        return Promise.reject(
          new Error(
            'Selecione uma biblioteca antes de enviar o modelo de relatório.',
          ),
        )
      }

      return api.compileReportTemplate(
        {
          name: title,
          description,
          file,
        },
        defaultRestApiFetchOptions,
      )
    },
    onSuccess: (data, variables, onMutateResult, context) => {
      queryClient.invalidateQueries({ queryKey: [LIST_REPORTS] })
      options.onSuccess?.(data, variables, onMutateResult, context)
    },
  })
}

export {
  useAddReportMutation,
  useDeleteReportMutation,
  useListReportsQuery,
  useUpdateReportMutation,
}
