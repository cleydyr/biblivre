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

export const SEARCHABLE_FIELDS = [
  FIELDS.ANY,
  FIELDS.AUTHOR,
  FIELDS.YEAR,
  FIELDS.TITLE,
  FIELDS.SUBJECT,
  FIELDS.ISBN,
  FIELDS.ISSN,
  FIELDS.PUBLISHER,
  FIELDS.SERIES,
  FIELDS.CREATED,
  FIELDS.MODIFIED,
] as const

export const ACTIONS = {
  SEARCH: 'search',
  PAGINATE: 'paginate',
  OPEN: 'open',
} as const
