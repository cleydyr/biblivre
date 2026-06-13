import type { IndexingGroup } from '../../generated-sources'

export const biblioIndexingGroupsFixture: IndexingGroup[] = [
  {
    id: 0,
    translationKey: 'all',
    datafields: '',
    sortable: false,
    defaultSort: false,
  },
  {
    id: 1,
    translationKey: 'author',
    datafields: '100_a,110_a,111_a,700_a,710_a,711_a',
    sortable: true,
    defaultSort: false,
  },
  {
    id: 2,
    translationKey: 'year',
    datafields: '260_c',
    sortable: true,
    defaultSort: false,
  },
  {
    id: 3,
    translationKey: 'title',
    datafields: '245_a_b',
    sortable: true,
    defaultSort: true,
  },
  {
    id: 4,
    translationKey: 'subject',
    datafields: '650_a',
    sortable: false,
    defaultSort: false,
  },
  {
    id: 5,
    translationKey: 'isbn',
    datafields: '020_a',
    sortable: false,
    defaultSort: false,
  },
  {
    id: 7,
    translationKey: 'publisher',
    datafields: '260_b',
    sortable: true,
    defaultSort: false,
  },
  {
    id: 8,
    translationKey: 'series',
    datafields: '490_a',
    sortable: true,
    defaultSort: false,
  },
]
