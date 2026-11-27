import { spawnSync } from 'node:child_process'
import { createRequire } from 'node:module'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const rootDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const require = createRequire(path.join(rootDir, 'package.json'))
const packageJsonPath = require.resolve('@formatjs/cli/package.json')
const formatjsBin = path.join(path.dirname(packageJsonPath), 'bin', 'formatjs')

const result = spawnSync(
  process.execPath,
  [formatjsBin, ...process.argv.slice(2)],
  {
    cwd: rootDir,
    stdio: 'inherit',
  },
)

if (result.error) {
  console.error(result.error.message)
  process.exit(1)
}

process.exit(result.status ?? 1)
