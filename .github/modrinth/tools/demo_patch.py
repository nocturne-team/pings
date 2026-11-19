"""Temporarily patches the mod with the screenshot hook (tools/DemoShots.java).

	python3 tools/demo_patch.py           # apply
	./gradlew :26.3-fabric:runClient --args='--quickPlaySingleplayer "Callouts Demo" --fullscreen'
	python3 tools/demo_patch.py --revert  # always do this before committing

Applying also rebuilds run/saves/Callouts Demo from run/saves/New World, with tools/demo-datapack (higher clouds)
installed. The hook builds a small scene, takes three screenshots into run/screenshots and logs "CALLOUTS-SHOT done".
"""
import pathlib
import shutil
import subprocess
import sys

R = pathlib.Path(__file__).resolve().parents[3]
PKG = R / 'src/main/java/com/github/_0x00acab/callouts'
ENTRY = PKG / 'CalloutsFabricClient.java'
HOOK = PKG / 'DemoShots.java'
SAVES = R / 'run/saves'
DEMO_WORLD = SAVES / 'Callouts Demo'

if '--revert' in sys.argv:
	subprocess.run(['git', 'checkout', '--', str(ENTRY.relative_to(R))], cwd=R, check=True)
	HOOK.unlink(missing_ok=True)
	shutil.rmtree(DEMO_WORLD, ignore_errors=True)
	print('reverted')
	sys.exit(0)

shutil.copy(pathlib.Path(__file__).with_name('DemoShots.java'), HOOK)
s = ENTRY.read_text()
anchor = '\t\tHudElementRegistry.attachElementBefore('
assert anchor in s
ENTRY.write_text(s.replace(anchor, '\t\tDemoShots.start();\n' + anchor, 1))
shutil.rmtree(DEMO_WORLD, ignore_errors=True)
shutil.copytree(SAVES / 'New World', DEMO_WORLD, ignore=shutil.ignore_patterns('session.lock'))
shutil.copytree(pathlib.Path(__file__).with_name('demo-datapack'), DEMO_WORLD / 'datapacks/callouts-demo')
print('patched')
