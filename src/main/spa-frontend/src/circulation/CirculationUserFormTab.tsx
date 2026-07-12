import { EuiDescriptionList, EuiPanel } from '@elastic/eui'

import { useUserFields } from '../api-helpers/user-fields/hooks'
import LoadingState from '../components/LoadingState'
import {
  getLegacyTranslation,
  getLegacyUserFieldTranslation,
} from '../legacy_translations/lib'
import { element } from '../lib/arrays'

import { formatFieldValueForDisplay } from './circulationUserFormLogic'
import { formatCirculationDateTime } from './lib'

import type { FC } from 'react'

import type { User } from '../api-helpers/circulation/response-types'
import type { EuiDescriptionListItem } from '../components/types'

type Props = {
  user: User
}

const CirculationUserFormTab: FC<Props> = ({ user }) => {
  const { data: userFields = [], isLoading } = useUserFields()

  if (isLoading) {
    return <LoadingState />
  }

  const userFieldValues = user.fields as Record<string, string>

  const listItems: EuiDescriptionListItem[] = [
    ...userFields.map((field) => ({
      title: getLegacyUserFieldTranslation(field.key),
      description: formatFieldValueForDisplay(
        field,
        userFieldValues[field.key],
      ),
    })),
    ...element({
      title: getLegacyTranslation('common.created'),
      description: formatCirculationDateTime(user.created),
    }).if(Boolean(user.created)),
    ...element({
      title: getLegacyTranslation('common.modified'),
      description: formatCirculationDateTime(user.modified),
    }).if(Boolean(user.modified && user.modified !== user.created)),
  ]

  return (
    <EuiPanel hasBorder paddingSize='m'>
      <EuiDescriptionList compressed listItems={listItems} type='column' />
    </EuiPanel>
  )
}

export default CirculationUserFormTab
