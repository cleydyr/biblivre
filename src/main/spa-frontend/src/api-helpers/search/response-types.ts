import type { UUID } from '../../types'
import type { NonSuccessfulResponse, PaginatedResponsePayload } from '../types'

import type { DIGITS, INDICATORS } from './constants'

// MARC field structure for bibliographic records
export type Digit = (typeof DIGITS)[number]

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

export type FieldIndicatorIndex = (typeof INDICATORS)[number]

export type FieldIndicator = `ind${FieldIndicatorIndex}`

export type MarcControlField = `00${Digit}`

export type MarcDatafield = Exclude<
  `${Digit}${Digit}${Digit}`,
  MarcControlField
>

export type MarcField = MarcDatafield | MarcControlField

export type MarcDatafieldValue = {
  [indicator in FieldIndicator]?: Digit | ' '
} & {
  [subfield in MarcSubfield]?: string[]
}

export type MarcJson = {
  [field in MarcDatafield]?: MarcDatafieldValue[]
} & {
  [field in MarcControlField]?: string
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
  json: MarcJson
  issn?: string
  isrc?: string
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

export type SuccessfulSearchResponse = PaginatedResponsePayload<
  BibliographicRecord,
  'id'
> & {
  indexing_group_count: IndexingGroupCount[]
  record_type: string
}

export type SearchResponse = SuccessfulSearchResponse | NonSuccessfulResponse

export interface SuccessfulExportResponse {
  success: true
  uuid: UUID
}

export interface NoResultsExportResponse {
  success: false
  message_level: 'warning'
  message: string
}

export type ExportResponse = SuccessfulExportResponse | NoResultsExportResponse

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
  json: MarcJson
  id: number
  location_d: string
}

export interface Field {
  datafield: MarcDatafield
  value: string
}

export interface OpenResult extends BibliographicRecord {
  fields?: Field[]
  holdings?: Holding[]
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
