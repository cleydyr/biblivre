export const FIELDS = {
  ANY: '0',
  AUTHOR: '1',
  YEAR: '2',
  TITLE: '3',
  SUBJECT: '4',
  ISBN: '5',
  ISSN: '6',
  PUBLISHER: '7',
  SERIES: '8',
  CREATED: 'created',
  MODIFIED: 'modified',
} as const

export const ACTIONS = {
  SEARCH: 'search',
  PAGINATE: 'paginate',
  OPEN: 'open',
  EXPORT: 'export_records',
  DOWNLOAD_EXPORT: 'download_export',
} as const

export const INDICATORS = [1, 2] as const

export const DIGITS = [
  '0',
  '1',
  '2',
  '3',
  '4',
  '5',
  '6',
  '7',
  '8',
  '9',
] as const
