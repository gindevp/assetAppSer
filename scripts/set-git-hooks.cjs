/* Points Git at assetAppSer/.husky (repo root = parent of this project folder). */
const { execSync } = require('node:child_process');
try {
  execSync('git config core.hooksPath assetAppSer/.husky', { stdio: 'inherit' });
} catch {
  // Not a git checkout or git missing — ignore (e.g. CI tarball).
}
