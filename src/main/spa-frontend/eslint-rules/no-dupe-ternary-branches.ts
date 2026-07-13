import type { Rule } from 'eslint'

/**
 * Disallow ternary expressions where both branches are identical.
 *
 * Catches mistakes like `condition ? null : null` where the condition has no effect.
 */
const rule: Rule.RuleModule = {
  meta: {
    type: 'problem',
    docs: {
      description:
        'Disallow ternary expressions where consequent and alternate are identical',
    },
    fixable: 'code',
    schema: [],
    messages: {
      duplicateBranches:
        'This conditional returns the same value whether the condition is true or false.',
    },
  },
  create(context) {
    const sourceCode = context.sourceCode

    return {
      ConditionalExpression(node) {
        const { consequent, alternate } = node
        const consequentText = sourceCode.getText(consequent)
        const alternateText = sourceCode.getText(alternate)

        if (consequentText === alternateText) {
          context.report({
            node,
            messageId: 'duplicateBranches',
            fix(fixer) {
              return fixer.replaceText(node, consequentText)
            },
          })
        }
      },
    }
  },
}

export default rule
