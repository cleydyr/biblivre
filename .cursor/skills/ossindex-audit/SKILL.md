---
name: ossindex-audit
description: >-
  Runs Sonatype OSS Index Maven audit (ossindex-maven-plugin:3.2.0:audit),
  re-evaluates excluded CVEs, and proposes dependency upgrades when a fix exists.
  Use when the user asks to audit Maven CVEs, OSS Index, ossindex-maven-plugin,
  excludeVulnerabilityIds, or to check whether dependency vulnerability exclusions
  are still needed.
---

# OSS Index Maven audit

Audit the Maven dependency tree with OSS Index, re-check pom exclusions, and
propose upgrades. Do **not** apply pom changes unless the user asked to fix or
update dependencies. Always report first.

Plugin is configured in the root `pom.xml` (`excludeVulnerabilityIds` + comments).
CI uses Maven server id `ossindex` (`OSSINDEX_USERNAME` / `OSSINDEX_TOKEN`).

## Workflow

Copy and track:

```
- [ ] Run audit and capture full output
- [ ] Parse reported vulnerabilities (GAV + CVE/OSS Index id + CVSS)
- [ ] Re-evaluate every excludeVulnerabilityId in pom.xml
- [ ] For each open or excluded CVE, check if a fix version exists
- [ ] Report proposed upgrades / exclusion removals
```

### 1. Run the audit

From the repo root. Capture stdout and stderr. The goal **fails the build** when
unexcluded vulnerabilities exist (`exit code != 0` is expected; still parse the log).

```bash
mvn org.sonatype.ossindex.maven:ossindex-maven-plugin:3.2.0:audit
```

Do not add `-q` (hides the vulnerability list). If the log is huge, write it to a
temp file and read the `[ERROR]` / `[WARNING]` sections.

**Rate limit / auth:** configuration uses `<authId>ossindex</authId>`. If the run
fails with HTTP 429 or missing credentials, look for a Maven `settings.xml` server
with id `ossindex`. Do not invent tokens. Tell the user if auth is required and
missing.

Typical finding block:

```
[ERROR]   groupId:artifactId:version
[ERROR]     * [CVE-YYYY-NNNNN] … (cvss)
```

Record for each finding: coordinates, vulnerability id, title, CVSS, and whether
it is **direct** or **transitive** (`mvn dependency:tree -Dincludes=group:artifact`
when needed).

### 2. Re-evaluate exclusions

Read every `<excludeVulnerabilityId>` in `pom.xml` and the HTML comment after it
(artifact, last-reviewed version, false-positive notes).

Excluded ids **do not appear** in a normal audit. Investigate each one anyway:

1. Identify the affected GAV from the comment and current `pom.xml` / tree.
2. Look up the id on NVD, GitHub Advisory, OSS Index, and the library’s release
   notes / changelog.
3. Decide:

| Situation | Action |
|-----------|--------|
| Fixed in a version we can adopt | Propose upgrading that dependency (and BOM/parent if that is how the version is managed). **Remove the exclusion in the same change** as the upgrade. Do not drop the exclusion alone — the next audit would fail. |
| Current version already contains the fix (stale exclusion) | Propose removing the exclusion with no version bump. |
| Documented false positive, still no real fix | Keep the exclusion. Refresh the comment if the current version or rationale changed. |
| Still vulnerable, no fix / disputed / unmaintained | Keep the exclusion. Note residual risk and any workaround. |

Do not keep an exclusion “just in case” when a real fix version exists.

### 3. Investigate reported (non-excluded) vulnerabilities

For each finding from step 1:

1. Confirm the CVE/GHSA applies to **this** artifact (watch OSS Index false positives).
2. Find the **minimum fixed version** (advisory `fixed` / patched range).
3. Check Maven Central (or the project’s BOM) for that version and whether a
   newer patch on the same minor/major is preferable.
4. Map how Biblivre pins the version: direct `<dependency>`, `<dependencyManagement>`,
   Spring Boot parent, or a transitive library.

Propose the smallest safe bump:

- Prefer a patch/minor on the same major.
- If only a major bump fixes it, say so and flag breaking-change risk; do not
  silently jump majors.
- For transitives, prefer forcing a compatible version in `dependencyManagement`
  only when a parent/BOM bump is not available; say if that override fights the BOM.

If no fix exists, propose adding an `<excludeVulnerabilityId>` **only** with a
short comment: why, which GAV, and which version was checked.

### 4. Report

Use this structure (skip empty sections):

```markdown
## OSS Index audit

Command: `mvn org.sonatype.ossindex.maven:ossindex-maven-plugin:3.2.0:audit`
Result: pass | fail (N vulnerable components)

### Proposed upgrades
- `group:artifact` `current` → `fixed` — CVE-… — direct|transitive — notes

### Exclusions to remove
- `CVE-…` — reason (fixed in `x.y.z` / stale)

### Exclusions to keep
- `CVE-…` — GAV — why (no fix / false positive) — version checked

### No fix yet
- `CVE-…` — GAV — residual risk
```

When the user asked to apply fixes: one cohesive pom change per upgrade (version
+ matching exclusion removal). Follow the compile-sanity-check rule after pom
edits (`mvn -DskipTests compile -Pdeveloper`). Re-run the audit after changes.

## Constraints

- Pin the plugin as `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.2.0:audit`.
- Do not weaken the audit (`fail=false`, emptying exclusions) except as a local
  experiment; restore the pom before finishing.
- Do not commit unless the user asked.
- Do not copy secrets from `settings.xml` into chat.
