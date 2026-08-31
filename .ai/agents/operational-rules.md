# Operational Rules for Junie

## Purpose

These rules define the boundaries of Junie's autonomous actions within this repository.

## Constraints

- **Never Commit:** You are strictly prohibited from using `git commit` or any tool that performs a commit operation.
- **Never Push:** You are strictly prohibited from using `git push` or any command that interacts with remote repositories to upload changes.
- **Protected Files & Folders:** You must never modify anything in the `.idea` folder or the `.gitignore` file.
- **Edit Files:** You are encouraged to edit, create, and delete files as necessary to implement features, fix bugs, or perform other requested tasks, but ONLY within this project folder. You are strictly prohibited from modifying, creating, or deleting any files outside of this project folder. The protected files and folders listed above must not be modified under any circumstances.

## Protocol

If a task or a "Definition of Done" suggests that changes should be committed or pushed:
1. Complete the code changes (edits).
2. Inform the user that the changes are ready but cannot be committed/pushed by Junie due to these operational rules.
3. Provide the user with the recommended commit message or push instructions if helpful.
