import type { FileDownload } from './types'

export function downloadFile({ blob, filename }: FileDownload) {
  const link = document.createElement('a')

  const objectURL = URL.createObjectURL(blob)

  link.href = objectURL

  link.download = filename

  link.click()

  URL.revokeObjectURL(objectURL)
}
