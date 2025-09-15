// MARC field structure for bibliographic records
export type Digit = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'

export type MarcSubfield =
  | 'a'
  | 'b'
  | 'c'
  | 'd'
  | 'e'
  | 'h'
  | 'l'
  | 'n'
  | 'p'
  | 'q'
  | 'v'
  | 'x'
  | 'y'
  | 'z'
  | Digit

export type FieldIndicator = 'ind1' | 'ind2'

export type MarcField = `${Digit}${Digit}${Digit}`

export type MarcSubfieldData = string | string[]

export type MarcFieldData = {
  [indicator in FieldIndicator]: string
} & {
  [subfield in MarcSubfield]: MarcSubfieldData
}

export type BibliographicRecordAttachment = {
  file: string
  name: string
  path: string
  uri: string
}

// Individual bibliographic record
export interface BibliographicRecord {
  id: number
  holdings_count: number
  holdings_lent: number
  holdings_reserved: number
  holdings_available: number
  attachments: BibliographicRecordAttachment[]
  created: string
  modified: string
  author: string
  subject: string
  isbn?: string
  title: string
  shelf_location: string
  material_type: string
  database: string
  publication_year: string
  marc: string
  json: MarcFieldData
}

// Indexing group count
export interface IndexingGroupCount {
  group_id: number
  result_count: number
}

// Indexing group definition
export interface IndexingGroup {
  id: number
  translation_key: string
  default_sort: boolean
  sortable: boolean
  datafields?: string
}

type SearchResult = {
  record_count: number
  record_limit: number
  records_per_page: number
  indexing_group_count: IndexingGroupCount[]
  data: BibliographicRecord[]
  page: number
  time: number
  id: number
  page_count: number
  record_type: string
}

// Main search response structure
export interface SuccessfulSearchResponse {
  search: SearchResult
  indexing_groups: IndexingGroup[]
  success: true
}

export type NoResultsSearchResponse = {
  success: false
  message_level: 'warning'
  message: string
}

export type SearchResponse = SuccessfulSearchResponse | NoResultsSearchResponse

export interface Holding {
  attachments: unknown[]
  created: string
  availability: 'available' | 'unavailable'
  shelf_location: string
  material_type: string
  accession_number: string
  record_id: number
  database: string
  marc: string
  modified: string
  json: {
    [field in MarcField]?: MarcFieldData[] | string
  }
  id: number
  location_d: string
}

export interface Field {
  datafield: string
  value: string
}

export type Lending = {
  id: number
  holding_id: number
  user_id: number
  previous_lending_id: number
  expected_return_date: string
  return_date: string
  daily_fine: number
  days_late: number
  estimated_fine: number
}

export interface OpenResult {
  attachments: BibliographicRecordAttachment[]
  author?: string
  created: string
  database?: string
  fields: Field[]
  holdings?: Holding[]
  holdings_available: number
  holdings_count: number
  holdings_lent: number
  holdings_reserved: number
  id: number
  isbn?: string
  issn?: string
  isrc?: string
  json: {
    [field in MarcField]?: MarcFieldData[] | string
  }
  marc: string
  material_type: string
  modified: string
  publication_year?: string
  shelf_location?: string
  subject?: string
  title?: string
  lendings?: Record<string, Lending>
}

export interface SuccessfulOpenResponse {
  data: OpenResult
  success: true
}

export interface NoResultsOpenResponse {
  success: false
  message_level: 'warning'
  message: string
}

export type OpenResponse = SuccessfulOpenResponse | NoResultsOpenResponse
