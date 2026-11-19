# callouts

[modrinth](https://modrinth.com/mod/callouts) | [issues](https://github.com/nocturne-team/pings/issues)

## overview

point at something, press a key, and your team sees it! callouts puts a marker over the spot, shows how far away it is, and points you back with an arrow when it's behind you. markers show right through walls.

## controls

| key | does |
|---|---|
| `Z` | quick ping: pings whatever's under your crosshair, a block, a mob or a player |
| `R` (hold) | ping wheel: pick danger, look here, come here, need help or mining/resource |

both are rebindable. you can keep moving while the wheel is open, and pinged mobs keep their marker as they walk around.

## features

- each marker uses the sender's locator bar color, so you can tell who pinged without reading a name
- far away pings shrink so they don't crowd your screen
- the server decides who sees a ping, not the client
- players without the mod still see pings through vanilla stuff: an action bar line with the distance and direction, a column of particles, a bell sound and a glow on pinged mobs
- bring your own ping sound, or use any note block instrument and pitch

## who sees a ping

if you're on a scoreboard team, your whole team does. if not, everyone within 128 blocks. every marker disappears after 10 seconds.

## client settings

open them from modmenu on fabric or the mod list on neoforge. you can change marker size and opacity, distance labels, off-screen arrows, the ping sound and volume, and hold vs toggle for the wheel. they're saved to `config/callouts-client.properties`.

for your own sound, drop an `.ogg` into `config/callouts/sounds/` (the sound picker has an "open sounds folder" button) and pick it from the list. use a mono file if you want to hear which direction a ping came from, stereo files play without direction. max 2 mb.

when a ping doesn't go through, a low bass note plays and the action bar says why: on cooldown, too many pings in a row, out of range, or the server doesn't have the mod. the bass note can be turned off.

## server config

the server writes `config/callouts.properties` on first start:

| key | default | what it does |
|---|---|---|
| `cooldown_millis` | `1000` | time between pings from one player |
| `burst_limit` | `4` | pings allowed in the burst window before a lockout |
| `burst_window_millis` | `3000` | how long the burst window is |
| `lockout_quiet_millis` | `5000` | how long you have to stay quiet to clear a lockout |
| `no_team_radius` | `128.0` | who gets pings from players without a team, in blocks |
| `max_ping_distance` | `256.0` | how far away you can ping, in blocks |
| `vanilla_fallback` | `true` | show pings to players without the mod |

## dependencies

- install it on the server and on every client that wants to send pings
- [fabric api](https://modrinth.com/mod/fabric-api) (on fabric)

a modded client still works on a server without callouts, pings just won't do anything there.

## versions

fabric and neoforge on Minecraft 26.1 to 26.3, plus neoforge on 1.20.1.

## building

built with [stonecutter](https://stonecutter.kikugie.dev/). gradle needs java 25:

```
./gradlew build
```

jars end up in `versions/<minecraft>-<loader>/build/libs/`.

## planned

xaero's minimap/worldmap integration, and team support for ftb teams and open parties & claims.

## license

the code and textures are [CC0](LICENSE), so public domain!

the ping sounds in `src/main/resources/assets/callouts/sounds/` aren't CC0. they come from Pixabay under the [Pixabay Content License](https://pixabay.com/service/license-summary/) and can't be redistributed on their own. thank you to:

- [SoundShelfStudio](https://pixabay.com/users/soundshelfstudio-46480698/) for `click_soft`, `click_deep`, `click_retro`, `focus` and `swipe_confirm`
- [Arnav Geddada](https://pixabay.com/users/arnav_geddada-51302802/) for `chime`
- [u_o8xh7gwsrj](https://pixabay.com/pl/users/u_o8xh7gwsrj-54433977/) for `tick`
