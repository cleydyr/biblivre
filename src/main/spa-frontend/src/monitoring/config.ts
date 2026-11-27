/**
 * Browser error monitoring settings rendered by the server into
 * `globalThis.__BIBLIVRE_MONITORING__` (see ClientMonitoringService). An empty
 * object, or no object at all, means monitoring is disabled.
 */
export interface MonitoringConfig {
  dsn?: string
  tunnel?: string
  release?: string
  environment?: string
  sampleRate?: number
  schema?: string
  userId?: string
}

export function getMonitoringConfig(): MonitoringConfig {
  return globalThis.__BIBLIVRE_MONITORING__ ?? {}
}
