# Modrinth page

Sources for the description on [modrinth.com/mod/callouts](https://modrinth.com/mod/callouts).

| Path             | What                                                                  |
|------------------|-----------------------------------------------------------------------|
| `description.md` | The page, with `{name}` placeholders for images                       |
| `cdn-urls.json`  | Where each image lives on Modrinth's CDN                              |
| `images/`        | The published headlines and screenshots                               |
| `fonts/`         | Jersey 10 (SIL Open Font License, see `fonts/OFL.txt`), for headlines  |
| `tools/`         | Scripts to regenerate everything                                      |

Needs Python 3 with Pillow.

## Change the text only

1. Edit `description.md`
2. `python3 tools/fill.py` prints the final markdown
3. Paste it into the project's Description settings and save

## Change the headlines

1. Edit the lines in `tools/build.py`, then `python3 tools/build.py`
2. Upload each changed image through the Description editor's Image button (Upload tab)
3. Copy its `cdn.modrinth.com` URL into `cdn-urls.json`, then follow "Change the text only"

Saving through the editor, rather than the API, lets Modrinth attach the uploads to the project.

## Retake the screenshots

1. `python3 tools/demo_patch.py` copies `tools/DemoShots.java` into the mod, hooks it into the Fabric client and
   rebuilds `run/saves/Callouts Demo` from `New World` with `tools/demo-datapack` (raises the clouds)
2. `./gradlew :26.3-fabric:runClient --args='--quickPlaySingleplayer "Callouts Demo" --fullscreen'` (needs JDK 25).
   The hook finds plains, builds a raised scene and takes three screenshots, then logs `CALLOUTS-SHOT done`
3. Close the game, then `python3 tools/demo_patch.py --revert`. **Always revert before committing**
4. Copy the new files from `run/screenshots/` into `raw/` as `raw-walls.png`, `raw-field.png`, `raw-wheel.png`
   (the order they're taken in)
5. `python3 tools/build.py` rebuilds `images/`, then follow "Change the headlines" from step 2 to upload them
