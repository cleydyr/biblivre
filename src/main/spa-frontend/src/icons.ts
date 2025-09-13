import { icon as arrowDown } from '@elastic/eui/es/components/icon/assets/arrow_down'
import { icon as arrowLeft } from '@elastic/eui/es/components/icon/assets/arrow_left'
import { icon as arrowRight } from '@elastic/eui/es/components/icon/assets/arrow_right'
import { icon as arrowEnd } from '@elastic/eui/es/components/icon/assets/arrowEnd'
import { icon as arrowStart } from '@elastic/eui/es/components/icon/assets/arrowStart'
import { icon as brush } from '@elastic/eui/es/components/icon/assets/brush'
import { icon as calendar } from '@elastic/eui/es/components/icon/assets/calendar'
import { icon as check } from '@elastic/eui/es/components/icon/assets/check'
import { icon as cross } from '@elastic/eui/es/components/icon/assets/cross'
import { icon as empty } from '@elastic/eui/es/components/icon/assets/empty'
import { icon as error } from '@elastic/eui/es/components/icon/assets/error'
import { icon as help } from '@elastic/eui/es/components/icon/assets/help'
import { icon as logoElastic } from '@elastic/eui/es/components/icon/assets/logo_elastic'
import { icon as logoElasticsearch } from '@elastic/eui/es/components/icon/assets/logo_elasticsearch'
import { icon as plusInCircle } from '@elastic/eui/es/components/icon/assets/plus_in_circle'
import { icon as search } from '@elastic/eui/es/components/icon/assets/search'
import { icon as sortLeft } from '@elastic/eui/es/components/icon/assets/sortLeft'
import { icon as sortRight } from '@elastic/eui/es/components/icon/assets/sortRight'
import { icon as stopFilled } from '@elastic/eui/es/components/icon/assets/stop_filled'
import { icon as trash } from '@elastic/eui/es/components/icon/assets/trash'
import { icon as warning } from '@elastic/eui/es/components/icon/assets/warning'
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
  help,
  calendar,
  plusInCircle,
  brush,
  sortLeft,
  sortRight,
  warning,
  error,
  arrowStart,
  arrowEnd,
}

appendIconComponentCache(cachedIcons)
