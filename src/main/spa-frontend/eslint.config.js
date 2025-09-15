import elasticEuiPlugin from '@elastic/eslint-plugin-eui'
import js from '@eslint/js'
import stylistic from '@stylistic/eslint-plugin'
import importPlugin from 'eslint-plugin-import'
import jsxA11y from 'eslint-plugin-jsx-a11y'
import prettier from 'eslint-plugin-prettier'
import react from 'eslint-plugin-react'
import reactHooks from 'eslint-plugin-react-hooks'
import reactIntl from 'eslint-plugin-react-intl'
import reactRefresh from 'eslint-plugin-react-refresh'
import simpleImportSort from 'eslint-plugin-simple-import-sort'
import globals from 'globals'
import tseslint from 'typescript-eslint'

export default tseslint.config([
  // Global ignores
  {
    ignores: ['dist/**', 'node_modules/**'],
  },

  // Base configuration for all files
  {
    files: ['**/*.{js,jsx,ts,tsx}'],
    extends: [js.configs.recommended, ...tseslint.configs.recommended],
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: 'module',
      globals: {
        ...globals.browser,
        ...globals.es2022,
      },
    },
    plugins: {
      import: importPlugin,
      prettier,
      'simple-import-sort': simpleImportSort,
      eui: elasticEuiPlugin,
      '@stylistic': stylistic,
      reactIntl,
    },
    rules: {
      // Prettier integration
      'prettier/prettier': 'error',

      // Import rules - using simple-import-sort for better type separation
      'simple-import-sort/imports': [
        'error',
        {
          groups: [
            // Side effect imports
            ['^\\u0000'],
            // Node.js builtins prefixed with `node:`
            ['^node:'],
            // External packages - things that start with a letter (or digit or underscore), or `@` followed by a letter
            ['^@?\\w'],
            // Internal packages - adjust this pattern to match your internal packages
            ['^(@|components|utils|hooks|types|constants|api|lib)(/.*|$)'],
            // Parent imports - put `..` last
            ['^\\.\\.(?!/?$)', '^\\.\\./?$'],
            // Other relative imports - put same-folder imports and `.` last
            ['^\\./(?=.*/)(?!/?$)', '^\\.(?!/?$)', '^\\./?$'],
            // Style imports
            ['^.+\\.s?css$'],
            // Type-only imports from external packages
            ['^@?\\w.*\\u0000$'],
            // Type-only imports from internal packages
            [
              '^(@|components|utils|hooks|types|constants|api|lib)(/.*|$).*\\u0000$',
            ],
            // Type-only imports from parent directories
            ['^\\.\\..*\\u0000$'],
            // Type-only imports from current directory
            ['^\\./.*\\u0000$'],
          ],
        },
      ],
      'simple-import-sort/exports': 'error',
      'import/first': 'error',
      'import/newline-after-import': 'error',
      'import/no-unresolved': 'off',
      'import/no-duplicates': 'error',

      // Unused variables rules
      'no-unused-vars': 'off', // Turn off base rule
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_.*$',
          varsIgnorePattern: '^_.*$',
          destructuredArrayIgnorePattern: '^_.*$',
          caughtErrorsIgnorePattern: '^_.*$',
        },
      ],

      // Variable shadowing rules
      'no-shadow': 'off', // Turn off base rule
      '@typescript-eslint/no-shadow': [
        'warn',
        {
          builtinGlobals: false,
          hoist: 'functions',
          allow: [],
        },
      ],

      '@typescript-eslint/explicit-function-return-type': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@typescript-eslint/no-explicit-any': 'warn',
      '@typescript-eslint/no-var-requires': 'error',
      '@typescript-eslint/consistent-type-imports': [
        'error',
        {
          prefer: 'type-imports',
          disallowTypeAnnotations: false,
          fixStyle: 'separate-type-imports',
        },
      ],
      '@typescript-eslint/no-import-type-side-effects': 'error',

      // Comma rules
      '@stylistic/comma-dangle': [
        'error',
        {
          arrays: 'always-multiline',
          objects: 'always-multiline',
          imports: 'always-multiline',
          exports: 'always-multiline',
          functions: 'always-multiline',
          generics: 'ignore',
        },
      ],

      // General code quality
      'no-console': 'warn',
      'no-debugger': 'error',
      'no-alert': 'error',
      'prefer-const': 'error',
      'no-var': 'error',
      'object-shorthand': 'error',
      'prefer-template': 'error',
      'no-else-return': 'warn',
    },
  },

  // React specific configuration
  {
    files: ['**/*.{jsx,tsx}'],
    plugins: {
      react,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
      'jsx-a11y': jsxA11y,
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
    rules: {
      // React Hooks rules
      ...reactHooks.configs.recommended.rules,

      // React Refresh rules
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],

      // React specific rules
      'react/prop-types': 'off',
      'react/react-in-jsx-scope': 'off',
      'react/jsx-uses-react': 'off',
      'react/jsx-boolean-value': ['error', 'never'],
      'react/jsx-curly-brace-presence': [
        'error',
        { props: 'never', children: 'never' },
      ],
      'react/self-closing-comp': 'error',
      'react/jsx-sort-props': [
        'error',
        {
          callbacksLast: true,
          shorthandFirst: true,
          reservedFirst: true,
        },
      ],

      // Accessibility rules
      'jsx-a11y/alt-text': 'error',
      'jsx-a11y/anchor-has-content': 'error',
      'jsx-a11y/anchor-is-valid': 'error',
      'jsx-a11y/click-events-have-key-events': 'error',
      'jsx-a11y/label-has-associated-control': 'error',
    },
  },

  // Test files configuration
  {
    files: ['**/*.{test,spec}.{js,jsx,ts,tsx}', '**/test/**/*.{js,jsx,ts,tsx}'],
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.es2022,
        // Vitest globals
        describe: 'readonly',
        it: 'readonly',
        test: 'readonly',
        expect: 'readonly',
        vi: 'readonly',
        beforeEach: 'readonly',
        afterEach: 'readonly',
        beforeAll: 'readonly',
        afterAll: 'readonly',
      },
    },
    rules: {
      // Prevent skipped and exclusive tests from being committed
      // These rules help ensure all tests run in CI/CD and prevent accidental test exclusions
      'no-restricted-properties': [
        'error',
        {
          object: 'describe',
          property: 'skip',
          message:
            'Skipped test suites should not be committed. Use describe() instead of describe.skip().',
        },
        {
          object: 'it',
          property: 'skip',
          message:
            'Skipped tests should not be committed. Use it() instead of it.skip().',
        },
        {
          object: 'test',
          property: 'skip',
          message:
            'Skipped tests should not be committed. Use test() instead of test.skip().',
        },
        {
          object: 'describe',
          property: 'only',
          message:
            'Exclusive test suites should not be committed. Use describe() instead of describe.only().',
        },
        {
          object: 'it',
          property: 'only',
          message:
            'Exclusive tests should not be committed. Use it() instead of it.only().',
        },
        {
          object: 'test',
          property: 'only',
          message:
            'Exclusive tests should not be committed. Use test() instead of test.only().',
        },
      ],
      'no-restricted-globals': [
        'error',
        {
          name: 'fdescribe',
          message:
            'Exclusive test suites should not be committed. Use describe() instead of fdescribe().',
        },
        {
          name: 'fit',
          message:
            'Exclusive tests should not be committed. Use it() instead of fit().',
        },
        {
          name: 'xdescribe',
          message:
            'Skipped test suites should not be committed. Use describe() instead of xdescribe().',
        },
        {
          name: 'xit',
          message:
            'Skipped tests should not be committed. Use it() instead of xit().',
        },
        {
          name: 'xtest',
          message:
            'Skipped tests should not be committed. Use test() instead of xtest().',
        },
      ],
      // Allow console.log in tests for debugging
      'no-console': 'off',
      // Allow any type in tests for mocking
      '@typescript-eslint/no-explicit-any': 'off',
    },
  },

  // Configuration files
  {
    files: ['**/*.config.{js,ts}', 'vite.config.ts', 'eslint.config.js'],
    languageOptions: {
      globals: {
        ...globals.node,
      },
    },
    rules: {
      '@typescript-eslint/no-var-requires': 'off',
      'no-console': 'off',
    },
  },
])
