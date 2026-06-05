# Git Commit Message Convention

This project uses Git hooks to enforce a standardized commit message pattern.

## Commit Message Pattern

All commit messages must follow this format:

```
TYPE|commit message
```

### Valid Types

- **FT** - New Feature
  - Use when adding new functionality to the project
  - Example: `FT|Add customer authentication endpoint`

- **DF** - Defect Fix (Bug Fix)
  - Use when fixing bugs or issues
  - Example: `DF|Fix null pointer exception in AccountsMapper`

- **CH** - Change (Logic Modification)
  - Use when modifying existing logic or refactoring
  - Example: `CH|Refactor transactional logic in AccountsService`

## Examples

```bash
# Good commit messages
git commit -m "FT|Add MapStruct to pom.xml for DTO mapping"
git commit -m "DF|Fix account number generation to ensure 16 digits"
git commit -m "CH|Update AccountsService to use Spring Data auditing"

# Bad commit messages (will be rejected)
git commit -m "Add new feature"           # Missing type
git commit -m "FT Add feature"            # Missing pipe separator
git commit -m "FT|"                       # Missing message
git commit -m "FEATURE|Add something"     # Invalid type
```

## Git Hooks

Two Git hooks are installed to help enforce this convention:

### 1. `prepare-commit-msg` Hook
- Runs before the commit message editor opens
- Provides a template with examples
- Helps guide developers on the correct format

### 2. `commit-msg` Hook
- Runs after the commit message is entered
- Validates the commit message against the pattern
- **Rejects commits** that don't follow the pattern
- Displays helpful error message with examples

## Usage

Just commit normally and the hooks will:
1. Show you a helpful template with examples
2. Validate your message follows the pattern
3. Reject the commit if the pattern is incorrect

```bash
# Example workflow
git add .
git commit -m "FT|Add new REST endpoint for account creation"
# ✅ Commit succeeds

git commit -m "Add new feature"
# ❌ Commit rejected with error message and examples
```

## Bypass Hooks (Not Recommended)

If you absolutely need to bypass the hooks (merge commits, automated commits, etc.):

```bash
git commit --no-verify -m "Your message"
```

However, this should be avoided for regular development work.

## Benefits

- ✅ Clear commit history organized by change type
- ✅ Easy to filter and search commits by type (FT, DF, CH)
- ✅ Better for changelog generation
- ✅ Team-wide consistency
- ✅ Automated enforcement prevents mistakes

