import type { TSESLint, TSESTree } from '@typescript-eslint/utils'

/**
 * Prefer explicit <Fragment> over the shorthand <>...</> form.
 * Autofixes tags and ensures `Fragment` is imported from `react`.
 */
const rule: TSESLint.RuleModule<'preferElement'> = {
  meta: {
    type: 'suggestion',
    docs: {
      description: 'Prefer <Fragment> over the shorthand <> fragment syntax',
    },
    fixable: 'code',
    schema: [],
    messages: {
      preferElement: 'Prefer <Fragment> over the shorthand <> fragment syntax.',
    },
  },
  defaultOptions: [],
  create(context) {
    const sourceCode = context.sourceCode
    let importFixScheduled = false

    function hasFragmentImport(
      importDeclarations: TSESTree.ImportDeclaration[],
    ): boolean {
      return importDeclarations.some((declaration) => {
        if (
          declaration.source.value !== 'react' ||
          declaration.importKind === 'type'
        ) {
          return false
        }

        return declaration.specifiers.some(
          (specifier) =>
            specifier.type === 'ImportSpecifier' &&
            getImportedName(specifier) === 'Fragment',
        )
      })
    }

    function getImportedName(
      specifier: TSESTree.ImportSpecifier,
    ): string | null {
      if (specifier.imported.type === 'Identifier') {
        return specifier.imported.name
      }
      if (specifier.imported.type === 'Literal') {
        return String(specifier.imported.value)
      }
      return null
    }

    function ensureFragmentImport(
      fixer: TSESLint.RuleFixer,
    ): TSESLint.RuleFix | null {
      const program = sourceCode.ast
      const importDeclarations = program.body.filter(
        (node): node is TSESTree.ImportDeclaration =>
          node.type === 'ImportDeclaration',
      )

      if (hasFragmentImport(importDeclarations)) {
        return null
      }

      const reactValueImport = importDeclarations.find(
        (declaration) =>
          declaration.source.value === 'react' &&
          declaration.importKind !== 'type',
      )

      if (reactValueImport) {
        const namedSpecifiers = reactValueImport.specifiers.filter(
          (specifier): specifier is TSESTree.ImportSpecifier =>
            specifier.type === 'ImportSpecifier',
        )

        if (namedSpecifiers.length > 0) {
          const insertBefore = namedSpecifiers.find((specifier) => {
            const name = getImportedName(specifier)
            return name != null && name.localeCompare('Fragment') > 0
          })

          if (insertBefore) {
            return fixer.insertTextBefore(insertBefore, 'Fragment, ')
          }

          const lastNamed = namedSpecifiers[namedSpecifiers.length - 1]
          return fixer.insertTextAfter(lastNamed, ', Fragment')
        }

        const defaultSpecifier = reactValueImport.specifiers.find(
          (specifier) => specifier.type === 'ImportDefaultSpecifier',
        )
        if (defaultSpecifier) {
          return fixer.insertTextAfter(defaultSpecifier, ', { Fragment }')
        }
      }

      const reactTypeImport = importDeclarations.find(
        (declaration) =>
          declaration.source.value === 'react' &&
          declaration.importKind === 'type',
      )
      if (reactTypeImport) {
        return fixer.insertTextBefore(
          reactTypeImport,
          "import { Fragment } from 'react'\n",
        )
      }

      const lastImport = importDeclarations.at(-1)
      if (lastImport) {
        return fixer.insertTextAfter(
          lastImport,
          "\nimport { Fragment } from 'react'",
        )
      }

      const firstNode = program.body[0]
      if (firstNode) {
        return fixer.insertTextBefore(
          firstNode,
          "import { Fragment } from 'react'\n\n",
        )
      }

      return fixer.insertTextAfterRange(
        [0, 0],
        "import { Fragment } from 'react'\n\n",
      )
    }

    return {
      JSXFragment(node) {
        context.report({
          node,
          messageId: 'preferElement',
          fix(fixer) {
            const fixes: TSESLint.RuleFix[] = [
              fixer.replaceText(node.openingFragment, '<Fragment>'),
              fixer.replaceText(node.closingFragment, '</Fragment>'),
            ]

            if (!importFixScheduled) {
              importFixScheduled = true
              const importFix = ensureFragmentImport(fixer)
              if (importFix) {
                fixes.push(importFix)
              }
            }

            return fixes
          },
        })
      },
    }
  },
}

export default rule
