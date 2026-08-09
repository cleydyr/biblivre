# Biblivre Circulation

Library circulation: lending materials to patrons and taking them back.

## Language

**Holding**:
A physical copy of a bibliographic work that can be lent.
_Avoid_: Item, copy, exemplar (in UI copy; domain term is Holding)

**Lending**:
An open loan of one Holding to one User, closed when the Holding is returned.
_Avoid_: Loan, checkout, borrow record

**Lend**:
The use case that opens a Lending (and renews open ones). Does not close Lendings or settle Fines.
_Avoid_: Checkout, borrow, issue (as primary product language)

**Renew**:
Extending an open Lending’s expected return date without closing it. Part of Lend’s scope, not Return’s.
_Avoid_: Extend loan, roll over

**Return**:
Closing one open Lending as soon as its Holding is identified by unique identifier (accession number or holding id), holding-first and immediate — not a pending basket. Title/author search lists candidates; it does not auto-close. Does not start from browsing a User’s loans. Does not open or renew Lendings. Lateness does not block completion; Fine handling is non-blocking after the Lending is closed. Scanning a Holding with no open Lending is not a Return — staff see that it is not on loan, plus the last closed Lending when history exists. If the title has active Reservations, Return informs staff who is next; it does not change reservation state or Holding availability rules.
_Avoid_: Check-in, give back, return session, batch return (as the primary flow)

**Fine**:
A monetary charge tied to a Lending from a late Return. Created automatically as unpaid at the estimated amount when that Return closes the Lending; staff may then pay, adjust, or waive without blocking Return.
_Avoid_: Fee, penalty, debt (as primary product language)

**Reservation**:
A User’s claim on a bibliographic title (not on a specific Holding). Return may surface the next Reservation after a close; fulfilling or clearing it remains a Lend-time concern.
_Avoid_: Hold, request, queue entry (as primary product language)

**Return undo**:
A short-lived reversal of the last Return on the Return page: reopens that Lending and reverses its auto-created Fine only if the Fine is still unpaid and unaltered. Ends when the window expires or another Return succeeds. An undone Return is removed from the Return slip.
_Avoid_: Cancel lending, delete return, void (as primary product language)

**Return slip**:
A printable record of Returns already completed in the current Return visit (a print buffer, not a pending basket). One slip can cover many closed Lendings.
_Avoid_: Receipt basket, batch confirm, return cart

**User**:
A registered library patron who may have open Lendings.
_Avoid_: Reader, patron, borrower (in domain docs; UI may still say reader where legacy does)
