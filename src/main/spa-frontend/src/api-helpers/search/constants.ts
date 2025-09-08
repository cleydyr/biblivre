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
} as const
