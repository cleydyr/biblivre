import { icon as arrowDown } from '@elastic/eui/es/components/icon/assets/arrow_down'
import { icon as arrowLeft } from '@elastic/eui/es/components/icon/assets/arrow_left'
import { icon as arrowRight } from '@elastic/eui/es/components/icon/assets/arrow_right'
import { icon as check } from '@elastic/eui/es/components/icon/assets/check'
import { icon as cross } from '@elastic/eui/es/components/icon/assets/cross'
import { icon as empty } from '@elastic/eui/es/components/icon/assets/empty'
import { icon as logoElastic } from '@elastic/eui/es/components/icon/assets/logo_elastic'
import { icon as logoElasticsearch } from '@elastic/eui/es/components/icon/assets/logo_elasticsearch'
import { icon as search } from '@elastic/eui/es/components/icon/assets/search'
import { icon as stopFilled } from '@elastic/eui/es/components/icon/assets/stop_filled'
import { icon as trash } from '@elastic/eui/es/components/icon/assets/trash'
import { appendIconComponentCache } from '@elastic/eui/es/components/icon/icon'

import type { ICON_TYPES } from '@elastic/eui'
import type { ValuesType } from 'utility-types'

type IconComponentNameType = ValuesType<typeof ICON_TYPES>
type IconComponentCacheType = Partial<Record<IconComponentNameType, unknown>>

const cachedIcons: IconComponentCacheType = {
  logoElasticsearch,
  logoElastic,
  stopFilled,
  empty,
  check,
  cross,
  arrowRight,
  arrowLeft,
  arrowDown,
  search,
  trash,
}

appendIconComponentCache(cachedIcons)
