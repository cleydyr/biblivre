import { execFile } from 'node:child_process'
import { readFile } from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { promisify } from 'node:util'

import type { Plugin, ViteDevServer } from 'vite'

const execFileAsync = promisify(execFile)

const rootDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const langCompiledJson = path.join(rootDir, 'lang-compiled.json')
const debounceMs = 300
const i18nPattern =
  /FormattedMessage|formatMessage|defineMessages?|defaultMessage/

function isSourceFile(file: string): boolean {
  const relative = path.relative(rootDir, file)
  return (
    relative.startsWith(`src${path.sep}`) &&
    /\.tsx?$/.test(relative) &&
    !relative.endsWith('.d.ts')
  )
}

function hasI18nStrings(content: string): boolean {
  return i18nPattern.test(content)
}

async function readCompiledMessages(): Promise<string | undefined> {
  try {
    return await readFile(langCompiledJson, 'utf-8')
  } catch {
    return undefined
  }
}

async function extractAndCompile(): Promise<void> {
  await execFileAsync('yarn', ['i18n:extract-compile'], {
    cwd: rootDir,
    env: process.env,
  })
}

function createRunner(server: ViteDevServer) {
  let isRunning = false
  let pending = false
  let debounceTimer: ReturnType<typeof setTimeout> | undefined

  const run = async () => {
    if (isRunning) {
      pending = true
      return
    }

    isRunning = true

    try {
      const previousCompiled = await readCompiledMessages()

      server.config.logger.info(
        '[formatjs-i18n] Extracting and compiling messages...',
      )
      await extractAndCompile()

      const nextCompiled = await readCompiledMessages()
      if (previousCompiled === nextCompiled) {
        return
      }

      server.config.logger.info('[formatjs-i18n] Messages updated.')

      const module = server.moduleGraph.getModuleById(langCompiledJson)
      if (module) {
        server.reloadModule(module)
      }
    } catch (error) {
      server.config.logger.error(
        `[formatjs-i18n] Failed to extract/compile messages: ${error instanceof Error ? error.message : String(error)}`,
      )
    } finally {
      isRunning = false

      if (pending) {
        pending = false
        void run()
      }
    }
  }

  const scheduleRun = () => {
    clearTimeout(debounceTimer)
    debounceTimer = setTimeout(() => {
      void run()
    }, debounceMs)
  }

  return { scheduleRun }
}

async function shouldProcessFile(file: string): Promise<boolean> {
  if (!isSourceFile(file)) {
    return false
  }

  try {
    const content = await readFile(file, 'utf-8')
    return hasI18nStrings(content)
  } catch {
    return false
  }
}

export function formatjsI18nPlugin(): Plugin {
  let scheduleRun: (() => void) | undefined

  return {
    name: 'formatjs-i18n',
    apply: 'serve',

    config() {
      return {
        server: {
          watch: {
            ignored: ['**/lang.json', '**/lang-compiled.json'],
          },
        },
      }
    },

    async buildStart() {
      try {
        console.info('[formatjs-i18n] Extracting and compiling messages...')
        await extractAndCompile()
        console.info('[formatjs-i18n] Messages updated.')
      } catch (error) {
        console.error(
          `[formatjs-i18n] Failed to extract/compile messages: ${error instanceof Error ? error.message : String(error)}`,
        )
        throw error
      }
    },

    configureServer(viteServer) {
      scheduleRun = createRunner(viteServer).scheduleRun
    },

    async handleHotUpdate({ file }) {
      if (await shouldProcessFile(file)) {
        scheduleRun?.()
      }
    },
  }
}
