# Internationalization Guidelines

## Localizable Strings

**Always use localizable strings for rendering text content.** Never use hardcoded strings in components.

### Use FormattedMessage for JSX Content

```tsx
import { FormattedMessage } from 'react-intl'

// Good
<EuiButton>
  <FormattedMessage
    defaultMessage="Search"
    id="search.button.search"
  />
</EuiButton>

// Bad
<EuiButton>Search</EuiButton>
```

### Use formatMessage for String Values

```tsx
import { useIntl } from 'react-intl'

const { formatMessage } = useIntl()

// Good - for placeholders, aria-labels, etc.
<EuiFieldSearch
  placeholder={formatMessage({
    defaultMessage: 'Enter search terms',
    id: 'search.field.placeholder'
  })}
  aria-label={formatMessage({
    defaultMessage: 'Search input field',
    id: 'search.field.aria_label'
  })}
/>

// Bad
<EuiFieldSearch
  placeholder="Enter search terms"
  aria-label="Search input field"
/>
```

## Message ID Conventions

- Use dot notation: `module.component.purpose`
- Examples:
  - `search.button.search`
  - `search.field.placeholder`
  - `search.results.no_results`
  - `common.button.cancel`

## Required Imports

Always import the necessary internationalization utilities:

```tsx
import { FormattedMessage, useIntl } from 'react-intl'
```
