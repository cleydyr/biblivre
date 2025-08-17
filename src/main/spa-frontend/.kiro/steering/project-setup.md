# Project Setup Guidelines

## Package Manager

This project uses **Yarn** as the package manager. Always use `yarn` commands instead of `npm`:

- Install dependencies: `yarn install`
- Add dependencies: `yarn add <package>`
- Add dev dependencies: `yarn add -D <package>`
- Remove dependencies: `yarn remove <package>`
- Run scripts: `yarn <script-name>`

## Key Dependencies

- **TanStack Query v5**: Used for server state management and data fetching
- **React 19**: Latest React version
- **Vite**: Build tool and dev server
- **TypeScript**: Type safety
- **Elastic UI (EUI)**: Component library for building user interfaces with Elastic's design system
- **ESLint + Prettier**: Code formatting and linting (configured for no semicolons)

## Development Notes

- ESLint is configured to allow unused variables that start with underscore (`_`)
- Prettier is configured without semicolons (`"semi": false`)
- The app builds to Spring Boot's static resources directory for integration

## UI Components

- **Use Elastic UI (EUI) components** for all UI elements to maintain consistency with Elastic's design system
- EUI provides comprehensive components like buttons, forms, tables, and layouts
- Import components from `@elastic/eui` (e.g., `import { EuiButton, EuiText } from '@elastic/eui'`)
- The app is wrapped with `EuiProvider` for theme and context support
