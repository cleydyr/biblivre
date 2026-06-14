import { ResponseError } from '../../generated-sources'

export async function getRestApiErrorMessage(
  error: unknown,
  fallback = 'Ocorreu um erro inesperado.',
): Promise<string> {
  if (!(error instanceof ResponseError)) {
    return error instanceof Error ? error.message : fallback
  }

  try {
    const body = (await error.response.clone().json()) as { message?: string }

    if (body.message) {
      return body.message
    }
  } catch {
    // ignore JSON parse errors
  }

  return fallback
}
