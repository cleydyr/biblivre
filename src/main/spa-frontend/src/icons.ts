import { icon as boxesVertical } from '@elastic/eui/es/components/icon/assets/boxes_vertical'
import { icon as brush } from '@elastic/eui/es/components/icon/assets/brush'
import { icon as calendar } from '@elastic/eui/es/components/icon/assets/calendar'
import { icon as check } from '@elastic/eui/es/components/icon/assets/check'
import { icon as chevronLimitLeft } from '@elastic/eui/es/components/icon/assets/chevron_limit_left'
import { icon as chevronLimitRight } from '@elastic/eui/es/components/icon/assets/chevron_limit_right'
import { icon as chevronSingleDown } from '@elastic/eui/es/components/icon/assets/chevron_single_down'
import { icon as chevronSingleLeft } from '@elastic/eui/es/components/icon/assets/chevron_single_left'
import { icon as chevronSingleRight } from '@elastic/eui/es/components/icon/assets/chevron_single_right'
import { icon as copy } from '@elastic/eui/es/components/icon/assets/copy'
import { icon as cross } from '@elastic/eui/es/components/icon/assets/cross'
import { icon as documents } from '@elastic/eui/es/components/icon/assets/documents'
import { icon as download } from '@elastic/eui/es/components/icon/assets/download'
import { icon as empty } from '@elastic/eui/es/components/icon/assets/empty'
import { icon as error } from '@elastic/eui/es/components/icon/assets/error'
import { icon as external } from '@elastic/eui/es/components/icon/assets/external'
import { icon as eye } from '@elastic/eui/es/components/icon/assets/eye'
import { icon as folderOpen } from '@elastic/eui/es/components/icon/assets/folder_open'
import { icon as help } from '@elastic/eui/es/components/icon/assets/help'
import { icon as lock } from '@elastic/eui/es/components/icon/assets/lock'
import { icon as logoElastic } from '@elastic/eui/es/components/icon/assets/logo_elastic'
import { icon as logoElasticsearch } from '@elastic/eui/es/components/icon/assets/logo_elasticsearch'
import { icon as magnify } from '@elastic/eui/es/components/icon/assets/magnify'
import { icon as moon } from '@elastic/eui/es/components/icon/assets/moon'
import { icon as pencil } from '@elastic/eui/es/components/icon/assets/pencil'
import { icon as play } from '@elastic/eui/es/components/icon/assets/play'
import { icon as plus } from '@elastic/eui/es/components/icon/assets/plus'
import { icon as plusCircle } from '@elastic/eui/es/components/icon/assets/plus_circle'
import { icon as sortLeft } from '@elastic/eui/es/components/icon/assets/sort_left'
import { icon as sortRight } from '@elastic/eui/es/components/icon/assets/sort_right'
import { icon as stopFill } from '@elastic/eui/es/components/icon/assets/stop_fill'
import { icon as sun } from '@elastic/eui/es/components/icon/assets/sun'
import { icon as trash } from '@elastic/eui/es/components/icon/assets/trash'
import { icon as upload } from '@elastic/eui/es/components/icon/assets/upload'
import { icon as warning } from '@elastic/eui/es/components/icon/assets/warning'
import { appendIconComponentCache } from '@elastic/eui/es/components/icon/icon'

import type { ICON_TYPES } from '@elastic/eui'
import type { ValuesType } from 'utility-types'

type IconComponentNameType = ValuesType<typeof ICON_TYPES>
type IconComponentCacheType = Partial<Record<IconComponentNameType, unknown>>

const cachedIcons: IconComponentCacheType = {
  logoElasticsearch,
  logoElastic,
  stopFill,
  empty,
  check,
  cross,
  chevronSingleRight,
  chevronSingleLeft,
  chevronSingleDown,
  magnify,
  trash,
  help,
  calendar,
  plusCircle,
  brush,
  sortLeft,
  sortRight,
  warning,
  error,
  chevronLimitLeft,
  chevronLimitRight,
  folderOpen,
  moon,
  sun,
  download,
  external,
  documents,
  upload,
  plus,
  importAction: download,
  boxesVertical,
  play,
  pencil,
  copy,
  lock,
  eye,
}

appendIconComponentCache(cachedIcons)
