/**
 * Pure Cutter-Sanborn formatting rules for bibliographic 090 $b.
 * Kept in sync with biblivre.cataloging.cutter.js (legacy MARC form).
 */

const ARTICLES: ReadonlySet<string> = new Set([
  'a',
  'an',
  'as',
  'el',
  'la',
  'las',
  'los',
  'o',
  'os',
  'the',
  'um',
  'uma',
  'umas',
  'un',
  'una',
  'uns',
])

export type AuthorNameParts = {
  surname: string
  givenName: string
}

export type CutterCallNumber = (surname: string, givenName: string) => number

export function parsePersonalName(marcName: string): AuthorNameParts {
  const trimmed = marcName.trim()
  if (!trimmed) {
    return { surname: '', givenName: '' }
  }

  const commaIndex = trimmed.indexOf(',')
  if (commaIndex < 0) {
    return { surname: trimmed, givenName: '' }
  }

  return {
    surname: trimmed.slice(0, commaIndex).trim(),
    givenName: trimmed.slice(commaIndex + 1).trim(),
  }
}

export function parseCorporateName(marcName: string): AuthorNameParts {
  const trimmed = marcName.trim()
  if (!trimmed) {
    return { surname: '', givenName: '' }
  }

  const words = trimmed.split(/\s+/)
  let surname = words[0] ?? ''

  for (const word of words) {
    const letters = lettersOnly(word)
    if (!letters) {
      continue
    }
    if (ARTICLES.has(letters.toLowerCase())) {
      continue
    }
    surname = letters
    break
  }

  return { surname, givenName: '' }
}

function lettersOnly(text: string): string {
  return text.replaceAll(/[^A-Za-z\u00C0-\u024F]/g, '')
}

function firstLetter(text: string): string {
  const match = text.match(/[A-Za-z\u00C0-\u024F]/)
  return match?.[0] ?? ''
}

function stripLeadingArticles(title: string): string {
  const text = title.trim()
  const match = /^(\S+)\s+(.*)$/.exec(text)
  if (!match) {
    return text
  }

  const firstWord = lettersOnly(match[1]).toLowerCase()
  if (ARTICLES.has(firstWord)) {
    return match[2]
  }
  return text
}

/**
 * First significant letter of the title (work letter), lowercase.
 * Prefers MARC 245 indicator 2 non-filing character count when > 0.
 */
export function workLetter(
  title: string,
  nonFilingChars?: string | number | null,
): string {
  let text = title
  const skip = Number.parseInt(String(nonFilingChars ?? ''), 10)

  if (!Number.isNaN(skip) && skip > 0) {
    text = text.slice(skip)
  } else {
    text = stripLeadingArticles(text)
  }

  return firstLetter(text).toLowerCase()
}

/**
 * Build full author code: surname letter + Cutter number + work letter.
 * Returns null when inputs or table lookup are insufficient.
 */
export function buildAuthorCode(
  surname: string,
  givenName: string,
  title: string,
  nonFilingChars: string | number | null | undefined,
  callNumber: CutterCallNumber,
): string | null {
  const cleanSurname = surname.trim()
  if (!cleanSurname) {
    return null
  }

  const surnameLetter = firstLetter(cleanSurname).toUpperCase()
  if (!surnameLetter) {
    return null
  }

  const work = workLetter(title, nonFilingChars)
  if (!work) {
    return null
  }

  const number = callNumber(
    cleanSurname.toLowerCase(),
    givenName.trim().toLowerCase(),
  )
  if (!number || number < 0) {
    return null
  }

  return `${surnameLetter}${String(number)}${work}`
}
