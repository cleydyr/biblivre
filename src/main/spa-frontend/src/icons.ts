import { icon as logoElastic } from '@elastic/eui/es/components/icon/assets/logo_elastic'
import { icon as logoElasticsearch } from '@elastic/eui/es/components/icon/assets/logo_elasticsearch'
import { icon as stopFilled } from '@elastic/eui/es/components/icon/assets/stop_filled'
import { appendIconComponentCache } from '@elastic/eui/es/components/icon/icon'

import type { ICON_TYPES } from '@elastic/eui'
import type { ValuesType } from 'utility-types'

type IconComponentNameType = ValuesType<typeof ICON_TYPES>
type IconComponentCacheType = Partial<Record<IconComponentNameType, unknown>>

const cachedIcons: IconComponentCacheType = {
  logoElasticsearch,
  logoElastic,
  stopFilled,
}

appendIconComponentCache(cachedIcons)
