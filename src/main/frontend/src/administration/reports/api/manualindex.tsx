import { Report } from "../types";

export async function listReports(schema: string): Promise<Report[]> {
  const response = await fetch("/api/v2/reports/list", {
    headers: {
      "X-Biblivre-Schema": schema,
    },
  });

  return await response.json();
}

export async function generateReport(
  reportId: number,
  schema: string
): Promise<Blob> {
  const response = await fetch(`/api/v2/reports/generate/${reportId}`, {
    headers: {
      "X-Biblivre-Schema": schema,
    },
  });

  return response.blob();
}
