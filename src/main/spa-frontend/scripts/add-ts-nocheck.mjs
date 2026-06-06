import fs from 'node:fs'
import path from 'node:path'

const TS_NOCHECK = '// @ts-nocheck\n'

function addTsNocheck(dir) {
  if (!fs.existsSync(dir) || !fs.statSync(dir).isDirectory()) {
    return
  }

  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const filePath = path.join(dir, entry.name)

    if (entry.isDirectory()) {
      addTsNocheck(filePath)
      continue
    }

    if (!entry.name.endsWith('.ts')) {
      continue
    }

    const content = fs.readFileSync(filePath, 'utf8')
    if (content.startsWith(TS_NOCHECK) || content.startsWith('// @ts-nocheck')) {
      continue
    }

    fs.writeFileSync(filePath, `${TS_NOCHECK}${content}`)
  }
}

for (const target of process.argv.slice(2)) {
  addTsNocheck(path.resolve(target))
}
