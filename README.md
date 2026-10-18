# Pings

A multiplayer ping mod for Minecraft: mark locations or entities for your
team (or nearby players), visible through walls via an edge-of-screen
arrow when off-screen.

Combines two interaction models other ping mods split across separate
projects:

- **Quick ping** — a single key pings whatever's under your crosshair.
- **Ping wheel** — hold a key to open a radial menu with five categories:
  Danger, Look Here, Come Here, Need Help, Mining/Resource.

Each marker is colored with the sender's vanilla **Locator Bar** color, so
pings match each player's existing in-game identity instead of a second
palette. The server decides who can see a ping — not the client — so it's
resistant to client-side cheats.

## How it works

- **Recipients**: on a scoreboard team → that team's online members. No
  team → players within a configurable radius.
- **Spam control**: a short base cooldown between pings, plus a burst
  lockout if you ping too many times in a short window.
- **Lifetime**: every marker despawns after 10 seconds. Entity-tracked
  pings follow the entity for that same window.

## Install

Requires Minecraft 26.3, on **Fabric** or **NeoForge**. Grab the right
jar for your loader from [Releases](../../releases).

## Keybinds (rebindable)

| Action     | Default |
|------------|---------|
| Quick ping | `Z`     |
| Ping wheel | `R`     |

## Server config

Written to `config/pings.properties` on first run:

| Key                    | Default  | Meaning                                   |
|-------------------------|----------|--------------------------------------------|
| `cooldown_millis`       | `1000`   | Minimum time between pings from one player |
| `burst_limit`           | `4`      | Pings allowed in the burst window before lockout |
| `burst_window_millis`   | `3000`   | Rolling window the burst limit is measured over |
| `lockout_quiet_millis`  | `5000`   | Quiet time needed to clear a burst lockout |
| `no_team_radius`        | `128.0`  | Recipient radius (blocks) for players with no team |
| `max_ping_distance`     | `256.0`  | Max distance (blocks) a ping target can be from the sender |

## Building

```
./gradlew :26.3-fabric:build :26.3-neoforge:build
```

## Roadmap

Not built yet, but planned: sound cues per category, Xaero's
Minimap/Worldmap integration, a wider Minecraft version/loader matrix, and
soft-dependency `TeamProvider` implementations for FTB Teams and Open
Parties & Claims.

## License

[CC0 1.0](LICENSE) — public domain.
