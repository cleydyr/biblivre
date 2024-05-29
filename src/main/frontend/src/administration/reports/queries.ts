import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Configuration,
  InitOverrideFunction,
  ReportFill,
  ReportFillApi,
  ReportFillRequest,
  ReportTemplate,
  ReportTemplateApi,
} from "../../generated-sources";
import { getSchemaFromURL } from "../../util";
import { UploadReportFormData } from "./UploadReportForm";

const LIST_REPORTS = "listReports";

const DEFAULT_FETCH_OPTIONS: InitOverrideFunction = async ({ init }) => ({
  ...init,
  headers: {
    ...init.headers,
    "X-Biblivre-Schema": getSchemaFromURL(),
    Accept: "application/json",
  },
});

function baseEndpointPath() {
  return `${window.location.origin}/api/v2`;
}

type UseMutationOptions<TVariables, TData, TError = Error> = Parameters<
  typeof useMutation<TData, TError, TVariables, unknown>
>[0];

export const useFillReportMutation = (
  options: UseMutationOptions<ReportFillRequest, ReportFill> = {}
) => {
  const queryClient = useQueryClient();

  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  });

  const api = new ReportFillApi(apiConfiguration);

  return useMutation({
    ...options,
    mutationFn: (request: ReportFillRequest) =>
      api.createReportFill(
        {
          reportFillRequest: {
            reportTemplateId: request.reportTemplateId,
            parameters: request.parameters,
          },
        },
        DEFAULT_FETCH_OPTIONS
      ),
    onSuccess: (data, variables, context) => {
      options.onSuccess?.(data, variables, context);
    },
  });
};

const useUpdateReportMutation = (
  options: UseMutationOptions<ReportTemplate, ReportTemplate> = {}
) => {
  const queryClient = useQueryClient();

  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  });

  const api = new ReportTemplateApi(apiConfiguration);

  return useMutation({
    ...options,
    mutationFn: (reportTemplate: ReportTemplate) =>
      api.updateReport(
        {
          reportTemplateId: reportTemplate.id ?? 0,
          reportTemplate,
        },
        DEFAULT_FETCH_OPTIONS
      ),
    onSuccess: (data, variables, context) => {
      queryClient.invalidateQueries({ queryKey: [LIST_REPORTS] });
      options.onSuccess?.(data, variables, context);
    },
  });
};

const useDeleteReportMutation = (
  options: UseMutationOptions<ReportTemplate, void> = {}
) => {
  const queryClient = useQueryClient();

  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  });

  const api = new ReportTemplateApi(apiConfiguration);

  return useMutation({
    ...options,
    mutationFn: (report: ReportTemplate) =>
      api.deleteReport(
        {
          reportTemplateId: report.id,
        },
        DEFAULT_FETCH_OPTIONS
      ),
    onSuccess: (data, variables, context) => {
      queryClient.invalidateQueries({ queryKey: [LIST_REPORTS] });
      options.onSuccess?.(data, variables, context);
    },
  });
};

const useListReportsQuery = () => {
  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  });

  const api = new ReportTemplateApi(apiConfiguration);

  return useQuery({
    queryKey: [LIST_REPORTS],
    queryFn: () => api.getReportTemplates(DEFAULT_FETCH_OPTIONS),
  });
};

const useAddReportMutation = (
  options: UseMutationOptions<UploadReportFormData, void> = {}
) => {
  const queryClient = useQueryClient();

  const apiConfiguration = new Configuration({
    basePath: baseEndpointPath(),
  });

  const api = new ReportTemplateApi(apiConfiguration);

  return useMutation({
    ...options,
    mutationFn: ({ title, description, file }: UploadReportFormData) =>
      api.compileReportTemplate(
        {
          name: title,
          description,
          file,
        },
        DEFAULT_FETCH_OPTIONS
      ),
    onSuccess: (data, variables, context) => {
      queryClient.invalidateQueries({ queryKey: [LIST_REPORTS] });
      options.onSuccess?.(data, variables, context);
    },
  });
};

export {
  useUpdateReportMutation,
  useDeleteReportMutation,
  useListReportsQuery,
  useAddReportMutation,
};
