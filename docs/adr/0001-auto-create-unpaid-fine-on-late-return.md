# Auto-create unpaid Fine on late Return

Legacy Return only wrote a Fine when staff chose apply/pay at return time (`fineValue > 0`). The new Return use case is holding-first and immediate, so lateness must not gate closing the Lending. We therefore create an unpaid Fine at the estimated amount whenever a late Return closes a Lending; pay, adjust, and waive happen afterward as non-blocking actions (and Return undo may reverse the Fine only while it remains unpaid and unaltered).

## Considered Options

- **Create Fine only on staff action** (legacy) — preserves explicit consent, but busy desks skip the step and charges are lost
- **Auto-create unpaid Fine** (chosen) — treats lateness as a fact of the closed Lending; escape hatches remain pay/adjust/waive/undo
- **Auto-create with locked amount** — same persistence, but no adjust on the Return flow; rejected as too rigid for desk exceptions

## Consequences

- Diverges from legacy `doReturn(lending, fineValue, paid)` semantics: a late Return always implies a Fine row unless later waived or undone
- Financial records become the source of truth for “was late,” not whether staff clicked through a popup
