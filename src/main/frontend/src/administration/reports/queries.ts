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

const REPORT_QUERY = "getReports";

const GENERATE_REPORT_QUERY = "generateReport";

const DEFAULT_FETCH_OPTIONS: InitOverrideFunction = async ({ init }) => ({
  ...init,
  headers: {
    ...init.headers,
    "X-Biblivre-Schema": getSchemaFromURL(),
    Accept: "application/json",
  },
});

function apiHost() {
  return `/api/v2`;
}

type UseMutationOptions<TVariables, TData, TError = Error> = Parameters<
  typeof useMutation<TData, TError, TVariables, unknown>
>[0];

export const useFillReportMutation = (
  options: UseMutationOptions<ReportFillRequest, ReportFill>
) => {
  const queryClient = useQueryClient();

  const apiConfiguration = new Configuration({
    basePath: apiHost(),
  });

  const api = new ReportFillApi(apiConfiguration);

  return useMutation({
    mutationKey: [GENERATE_REPORT_QUERY],
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
      queryClient.invalidateQueries({ queryKey: [GENERATE_REPORT_QUERY] });
      options.onSuccess?.(data, variables, context);
    },
    ...options,
  });
};

const useUpdateReportMutation = (
  options: UseMutationOptions<ReportTemplate, ReportTemplate, Error>
) => {
  const queryClient = useQueryClient();

  const apiConfiguration = new Configuration({
    basePath: apiHost(),
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
    onSuccess: (data, variables, context) => {
      queryClient.invalidateQueries({ queryKey: [REPORT_QUERY] });
      options.onSuccess?.(data, variables, context);
    },
    ...options,
  });
};

const useDeleteReportMutation = (
  options: UseMutationOptions<ReportTemplate, void, Error> = {}
) => {
  const queryClient = useQueryClient();

  const apiConfiguration = new Configuration({
    basePath: apiHost(),
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
    onSuccess: (data, variables, context) => {
      queryClient.invalidateQueries({ queryKey: [REPORT_QUERY] });
      options.onSuccess?.(data, variables, context);
    },
    ...options,
  });
};

const useListReportsQuery = () => {
  const apiConfiguration = new Configuration({
    basePath: apiHost(),
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
    basePath: apiHost(),
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

export {
  useUpdateReportMutation,
  useDeleteReportMutation,
  useListReportsQuery,
  useAddReportMutation,
};
