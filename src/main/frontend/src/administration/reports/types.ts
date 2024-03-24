export interface Report {
  id: number;
  name: string;
  description?: string;
}

export enum ReportStatus {
  PENDING = "PENDING",
  GENERATING = "GENERATING",
  READY = "READY",
}
