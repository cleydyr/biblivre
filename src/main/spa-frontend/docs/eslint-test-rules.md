# ESLint Test Rules

This project includes ESLint rules to prevent accidentally committing skipped or exclusive tests that could cause issues in CI/CD pipelines or hide failing tests.

## Rules Overview

The following test patterns will trigger ESLint errors:

### Skipped Tests (Should be avoided in commits)

- `describe.skip()` - Skipped test suites
- `it.skip()` - Skipped individual tests
- `test.skip()` - Skipped test functions
- `xdescribe()` - Alternative syntax for skipped suites
- `xit()` - Alternative syntax for skipped tests
- `xtest()` - Alternative syntax for skipped test functions

### Exclusive Tests (Should be avoided in commits)

- `describe.only()` - Exclusive test suites (only these run)
- `it.only()` - Exclusive individual tests
- `test.only()` - Exclusive test functions
- `fdescribe()` - Alternative syntax for exclusive suites
- `fit()` - Alternative syntax for exclusive tests

## Why These Rules Matter

### Skipped Tests

- **Hidden Failures**: Skipped tests might hide real bugs or regressions
- **Technical Debt**: Accumulation of skipped tests creates maintenance burden
- **CI/CD Issues**: Tests that should run might be accidentally skipped in production

### Exclusive Tests

- **Incomplete Testing**: Only exclusive tests run, skipping all other tests
- **CI/CD Failures**: Can cause builds to pass when they should fail
- **Team Confusion**: Other developers' tests won't run locally

## Examples

### ❌ Bad (Will trigger ESLint errors)

```typescript
describe.skip('User authentication', () => {
  it('should login successfully', () => {
    // This test is skipped and won't run
  })
})

describe.only('Payment processing', () => {
  it('should process payment', () => {
    // Only this test suite will run, skipping all others
  })
})

it.skip('should validate email format', () => {
  // This individual test is skipped
})

it.only('should save user data', () => {
  // Only this test will run in its describe block
})
```

### ✅ Good (No ESLint errors)

```typescript
describe('User authentication', () => {
  it('should login successfully', () => {
    // Normal test that will run
  })
})

describe('Payment processing', () => {
  it('should process payment', () => {
    // Normal test that will run
  })
})

it('should validate email format', () => {
  // Normal test that will run
})

it('should save user data', () => {
  // Normal test that will run
})
```

## When to Use Skipped/Exclusive Tests

### During Development (Temporarily)

- **Debugging**: Use `it.only()` to focus on a specific failing test
- **Work in Progress**: Use `it.skip()` for tests you're still writing
- **Flaky Tests**: Temporarily skip unstable tests while investigating

### ⚠️ Important: Always clean up before committing!

## Best Practices

### 1. Use TODO Comments for Skipped Tests

```typescript
// TODO: Fix flaky test - investigate timing issues
it.skip('should handle concurrent requests', () => {
  // Test implementation
})
```

### 2. Create Issues for Skipped Tests

```typescript
// Issue #123: Implement user preferences feature
it.skip('should save user preferences', () => {
  // Test for future feature
})
```

### 3. Use Conditional Tests Instead

```typescript
// Instead of skipping, use conditional logic
const shouldRunIntegrationTests = process.env.NODE_ENV === 'test'

describe('Integration tests', () => {
  it.skipIf(!shouldRunIntegrationTests)('should connect to database', () => {
    // Test implementation
  })
})
```

## Fixing ESLint Errors

### Quick Fix

Remove `.skip` or `.only` from your test functions:

```typescript
// Before
describe.only('My tests', () => { ... })

// After
describe('My tests', () => { ... })
```

### If You Need to Skip Tests

1. **Fix the underlying issue** instead of skipping
2. **Create a GitHub issue** to track the problem
3. **Add a TODO comment** explaining why it's skipped
4. **Set a deadline** for fixing the skipped test

### If You Need Exclusive Tests

1. **Run tests locally** during development
2. **Remove `.only`** before committing
3. **Use focused test runners** in your IDE instead

## Configuration

The rules are configured in `eslint.config.js` for all test files matching:

- `**/*.{test,spec}.{js,jsx,ts,tsx}`
- `**/test/**/*.{js,jsx,ts,tsx}`

## Disabling Rules (Not Recommended)

If you absolutely must disable these rules for a specific case:

```typescript
// eslint-disable-next-line no-restricted-properties
describe.only('Emergency debugging', () => {
  // Only use this for urgent debugging, never commit
})
```

## Integration with CI/CD

These rules help ensure:

- ✅ All tests run in CI/CD pipelines
- ✅ No tests are accidentally skipped
- ✅ Test coverage remains accurate
- ✅ Team members can rely on the full test suite
