import { paginateCatalographicSearchResults } from '.'

import type { AsyncBidirectionalIterator } from '../../lib/async-bidirectional-iterator'

import type {
  BibliographicRecord,
  SuccessfulSearchResponse,
} from './response-types'

class SearchResultsBidirectionalIterator
  implements AsyncBidirectionalIterator<BibliographicRecord>
{
  // private fields
  #searchResults: SuccessfulSearchResponse

  #currentRecordIndex: number = 0

  #hasNext: boolean = false

  #hasPrevious: boolean = false

  constructor(
    searchResults: SuccessfulSearchResponse,
    initialRecordIndex?: number,
  ) {
    this.#searchResults = searchResults

    this.#currentRecordIndex = initialRecordIndex ?? 0

    this.#hasNext =
      this.#currentRecordIndex < searchResults.search.data.length ||
      searchResults.search.page < searchResults.search.page_count

    this.#hasPrevious = searchResults.search.page > 1
  }

  async hasNext(): Promise<boolean> {
    return this.#hasNext
  }

  async hasPrevious(): Promise<boolean> {
    return this.#hasPrevious
  }

  async next(): Promise<BibliographicRecord> {
    // If there are more records on the current page, return the next record
    if (this.#currentRecordIndex < this.#searchResults.search.data.length - 1) {
      return this.#searchResults.search.data[this.#currentRecordIndex++]
    }

    // If there are no more records on the current page and there are more pages, paginate to the next page
    await this.#paginateForward()

    return this.next()
  }

  async previous(): Promise<BibliographicRecord> {
    // If there are previous records on the current page, return the previous record
    if (this.#currentRecordIndex > 0) {
      return this.#searchResults.search.data[this.#currentRecordIndex--]
    }

    // If there are no more records on the current page and there are more pages, paginate to the next page
    await this.#paginateBackwards()

    return this.next()
  }

  async #paginateForward(): Promise<void> {
    if (this.isLastPage()) {
      throw new Error(
        `Can't iterate forward to beyond last page ${this.#searchResults.search.page_count}`,
      )
    }
    const response = await paginateCatalographicSearchResults(
      this.#searchResults.search.id.toString(),
      this.#searchResults.search.page + 1,
    )

    if (!response.success) {
      throw new Error(response.message)
    }

    this.#searchResults = response

    this.#currentRecordIndex = 0
  }

  private isLastPage() {
    return (
      this.#searchResults.search.page === this.#searchResults.search.page_count
    )
  }

  async #paginateBackwards(): Promise<void> {
    if (this.#searchResults.search.page === 1) {
      throw new Error(`Can't iterate back to before page 1`)
    }

    const response = await paginateCatalographicSearchResults(
      this.#searchResults.search.id.toString(),
      this.#searchResults.search.page - 1,
    )

    if (!response.success) {
      throw new Error(response.message)
    }

    this.#searchResults = response

    this.#currentRecordIndex = response.search.records_per_page - 1
  }
}

export default SearchResultsBidirectionalIterator
