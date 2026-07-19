import { useIntl } from 'react-intl'

import { BIBLIVRE_ENDPOINT } from '../api-helpers/constants'
import { getStoredSchema } from '../api-helpers/schema/storage'
import { when } from '../lib/arrays'

import { UserStatusBadge, UserTypeBadge } from './UserBadges'

import type { Moment } from 'moment'

import type { User } from '../api-helpers/circulation/response-types'
import type { CirculationSearchPayload } from '../api-helpers/circulation/types'
import type { BibliographicRecord } from '../api-helpers/search/response-types'
import type { EuiDescriptionListItem } from '../components/types'
import type { ISO8601Date } from '../types'

import type { CirculationSearchControlConfig } from './types'

type BiblioForDescriptionList = Pick<
  BibliographicRecord,
  'title' | 'author' | 'publication_year' | 'shelf_location'
>

export function useBiblioDescriptionListItems(
  biblio: BiblioForDescriptionList,
  shelfLocation?: string,
): EuiDescriptionListItem[] {
  const { formatMessage } = useIntl()

  const location = shelfLocation ?? biblio.shelf_location

  return [
    ...when(biblio.title).element((title) => ({
      title: formatMessage({
        defaultMessage: 'Título',
        id: 'search.bibliographic.title',
      }),
      description: title,
    })),
    ...when(biblio.author).element((author) => ({
      title: formatMessage({
        defaultMessage: 'Autor',
        id: 'search.bibliographic.author',
      }),
      description: author,
    })),
    ...when(biblio.publication_year).element((publicationYear) => ({
      title: formatMessage({
        defaultMessage: 'Ano de publicação',
        id: 'search.bibliographic.publication_year',
      }),
      description: publicationYear,
    })),
    ...when(location).element((shelfLocationValue) => ({
      title: formatMessage({
        defaultMessage: 'Localização',
        id: 'search.bibliographic.shelf_location',
      }),
      description: shelfLocationValue,
    })),
  ]
}

export function getUserPhotoUrl(photoId: string): string {
  const schema = getStoredSchema()
  const mediaPath = schema
    ? `${schema}/DigitalMediaController/?id=${encodeURIComponent(photoId)}`
    : `DigitalMediaController/?id=${encodeURIComponent(photoId)}`

  return `${BIBLIVRE_ENDPOINT}/${mediaPath}`
}

export function useUserPanelDescriptionListItems(
  user: User,
): EuiDescriptionListItem[] {
  const { formatMessage } = useIntl()

  return [
    {
      title: formatMessage({
        defaultMessage: 'Nome',
        id: 'circulation.user.flyout.name',
      }),
      description: user.name,
    },
    {
      title: formatMessage({
        defaultMessage: 'Matrícula',
        id: 'circulation.user.flyout.enrollment',
      }),
      description: user.enrollment || String(user.id),
    },
    {
      title: formatMessage({
        defaultMessage: 'Tipo',
        id: 'circulation.user.flyout.type',
      }),
      description: <UserTypeBadge type={user.type} />,
    },
    {
      title: formatMessage({
        defaultMessage: 'Situação',
        id: 'circulation.user.flyout.status',
      }),
      description: <UserStatusBadge status={user.status} />,
    },
  ].filter((item) => item.description)
}

export function formatCirculationDateTime(date: string | undefined): string {
  if (!date) {
    return '-'
  }

  return Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(date))
}

export function formatCirculationDate(date: string | undefined): string {
  if (!date) {
    return '-'
  }

  return Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
  }).format(new Date(date))
}

export function toISO8601Date(date: Moment | null): ISO8601Date | '' {
  if (!date) {
    return ''
  }

  return date
    .clone()
    .startOf('day')
    .toISOString()
    .slice(0, -'.000Z'.length) as ISO8601Date
}

export function toCirculationSearchPayload(
  searchConfig: CirculationSearchControlConfig,
): CirculationSearchPayload {
  const { query, searchField, isAdvancedSearch } = searchConfig

  if (isAdvancedSearch) {
    const {
      createdAtRange,
      modifiedAtRange,
      usersWithPendingFines,
      usersWithLateLendings,
      usersWhoHaveLoginAccess,
      usersWithoutUserCard,
      inactiveUsersOnly,
    } = searchConfig.filters

    return {
      query,
      field: searchField === 'name_or_id' ? '' : searchField,
      mode: 'advanced',
      created_start: toISO8601Date(createdAtRange.from),
      created_end: toISO8601Date(createdAtRange.to),
      modified_start: toISO8601Date(modifiedAtRange.from),
      modified_end: toISO8601Date(modifiedAtRange.to),
      users_with_pending_fines: usersWithPendingFines,
      users_with_late_lendings: usersWithLateLendings,
      users_who_have_login_access: usersWhoHaveLoginAccess,
      users_without_user_card: usersWithoutUserCard,
      inactive_users_only: inactiveUsersOnly,
    }
  }

  return {
    query,
    field: searchField === 'name_or_id' ? '' : searchField,
    mode: 'simple',
  }
}
