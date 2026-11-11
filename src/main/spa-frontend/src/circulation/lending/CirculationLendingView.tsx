import { EuiFlexGroup, EuiFlexItem } from '@elastic/eui'
import { useState } from 'react'

import HoldingColumn from './HoldingColumn'
import ReaderColumn from './ReaderColumn'

import type { FC } from 'react'

import type { CirculationLendingState } from './useCirculationLending'

// Columns stack when the content area cannot fit two of them.
const COLUMN_CSS = { minWidth: 360 }

type Props = CirculationLendingState & {
  /** Reference date for late-day calculations; defaults to page load. */
  now?: Date
}

const CirculationLendingView: FC<Props> = ({
  now: fixedNow,
  readerSearch,
  holdingSearch,
  reader,
  userType,
  pendingFines,
  blockReason,
  receiptCount,
  isMutating,
  onSelectReader,
  onClearReader,
  onLend,
  onRenew,
  onReturn,
  onPrintReceipt,
}) => {
  const [now] = useState(() => fixedNow ?? new Date())

  return (
    <EuiFlexGroup wrap alignItems='flexStart' gutterSize='l'>
      <EuiFlexItem css={COLUMN_CSS}>
        <ReaderColumn
          blockReason={blockReason}
          isMutating={isMutating}
          now={now}
          pendingFines={pendingFines}
          reader={reader}
          readerSearch={readerSearch}
          receiptCount={receiptCount}
          userType={userType}
          onClear={onClearReader}
          onPrintReceipt={() => void onPrintReceipt()}
          onRenew={(bag) => void onRenew(bag)}
          onReturn={(bag) => void onReturn(bag)}
          onSelect={onSelectReader}
        />
      </EuiFlexItem>
      <EuiFlexItem css={COLUMN_CSS}>
        <HoldingColumn
          blockReason={blockReason}
          holdingSearch={holdingSearch}
          isMutating={isMutating}
          now={now}
          reader={reader}
          onLend={(bag) => void onLend(bag)}
          onRenew={(bag) => void onRenew(bag)}
          onReturn={(bag) => void onReturn(bag)}
        />
      </EuiFlexItem>
    </EuiFlexGroup>
  )
}

export default CirculationLendingView
