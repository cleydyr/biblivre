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

// Individual bibliographic record
export interface BibliographicRecord {
  id: number
  holdings_count: number
  holdings_lent: number
  holdings_reserved: number
  holdings_available: number
  attachments: unknown[]
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

export interface NoResultsSearchResponse {
  success: false
  message_level: 'warning'
  message: string
}

export type SearchResponse = SuccessfulSearchResponse | NoResultsSearchResponse
