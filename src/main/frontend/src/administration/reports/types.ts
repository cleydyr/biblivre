export type ReportStatus = "generating" | "pending" | "ready";

export interface Report {
  id: number;
  name: string;
  description?: string;
}
