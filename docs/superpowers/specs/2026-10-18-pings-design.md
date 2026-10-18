# Pings — Design Spec

Date: 2026-10-18
Status: Approved for implementation planning

## Overview

A multiplayer ping mod for Minecraft: players mark locations or entities for
their team (or nearby players) with an in-world marker, visible through walls
via an edge-of-screen indicator when off-screen. Competes directly with
existing ping mods (`ping system (ping wheel)`, `Ping It!`, `AMS Pings`,
`SimpleMultiplayerPing`, `MCPings`) by combining both interaction models
(instant quick-ping + categorized wheel) that those mods split across
separate projects, and by reusing vanilla's Locator Bar color system so a
ping visually matches the sender's existing in-game identity color.

## Goals

- Instant, low-friction location/entity marking for small-group multiplayer
  (survival SMP, co-op, team-based minigames).
- Two complementary interactions: quick-ping (single key) and a categorized
  ping wheel (held key → radial menu).
- Cheat-resistant: the server decides who can see a ping, not the client.
- Ship on the current stable Minecraft release first; broaden version/loader
  coverage once the design is validated in practice.

## Non-goals (v1)

- Sound cues (planned, not built — see Roadmap).
- Minimap/worldmap integration, e.g. Xaero's (planned, not built — see
  Roadmap).
- Forge (legacy) support, or any Minecraft version other than the current
  stable release (see Roadmap).
- Third-party team-mod integration (FTB Teams, Open Parties & Claims) beyond
  the `TeamProvider` seam described below.

## UX

### Quick ping

- Single rebindable keybind. Pings whatever is under the crosshair:
  - A block/location → a stationary marker at that point.
  - An entity (mob or player) → a marker that tracks the entity's position
    for up to 10 seconds, then expires with the rest of the ping lifecycle.
- Marker: a diamond shape with an outline, separated from the fill by a
  margin (outline ring not touching the fill), with a distance counter
  (in blocks) floating above it.
- Lifetime: 10 seconds, then the marker despawns. (Entity-tracked pings stop
  tracking and despawn at the same 10s mark — tracking duration and ping
  lifetime are the same window.)

### Ping wheel

- Single rebindable keybind, default `R`. Holding it opens a radial menu with
  five categories: **Danger**, **Look Here**, **Come Here**, **Need Help**,
  **Mining/Resource**. Releasing over a category places a ping of that type
  at the crosshair target (same targeting rules as quick ping: block or
  entity).
- Visually identical to a quick ping (diamond, outline, margin, distance
  counter) plus a small category icon inside the diamond.

### Marker color

- A ping's diamond is colored using the **sender's vanilla Locator Bar
  color** — the same per-player color Minecraft already assigns and shows on
  the locator bar HUD. This keeps ping color consistent with each player's
  existing in-game color identity instead of inventing a second palette.
- Category (wheel pings only) is conveyed by the icon inside the diamond, not
  by color, so sender identity and ping category never conflict visually.

### Off-screen indicator

- Any active ping outside the player's view frustum shows a directional
  arrow at the edge of the screen pointing toward it, with the same distance
  counter.

### Spam control

- Base cooldown: ~1 second between pings from the same player.
- Burst throttle (League of Legends style): if a player exceeds a burst
  threshold (more than 4 pings within a 3-second rolling window), they're
  placed on a longer lockout cooldown. The lockout clears after a quiet
  period with no ping attempts.
- Exact thresholds/durations are tunable via config; the numbers above are
  the v1 defaults.

## Networking & trust model

- Client → server: a lightweight request packet containing either a target
  position or a target entity ID, plus a ping category (quick ping uses a
  reserved "plain" category).
- Server is authoritative on **recipients**:
  1. Look up the sender's team via `TeamProvider` (vanilla scoreboard teams
     for v1 — see below).
  2. If the sender is on a team, recipients = that team's online members.
  3. If the sender is on no team, recipients = players within a configurable
     radius of the sender (default radius TBD at implementation time, must
     be config-exposed).
- Server → recipient clients: a render packet (position/entity ID, category,
  sender's locator-bar color, expiry timestamp) that each client renders
  independently. The server does not track ping state beyond resolving and
  relaying it — expiry/tracking is client-local, keyed off the timestamp the
  server stamps on the packet.
- `TeamProvider` is a small interface (`getTeam(ServerPlayer): TeamId?`) with
  a single vanilla-scoreboard-backed implementation for v1. Future
  implementations (FTB Teams, Open Parties & Claims) register against the
  same interface without touching ping resolution logic.

## Rendering

- A small common interface (e.g. `PingRenderer`) invoked from world-render
  and HUD-render hooks, with one implementation registered per loader
  (Fabric, NeoForge) against that loader's native render-event APIs — no
  Architectury or other cross-loader abstraction library, consistent with
  how `bedbugs` is built.
- Responsibilities: draw the diamond (world-space, camera-facing billboard),
  the distance counter text, and the edge-of-screen arrow when the ping
  falls outside the view frustum.

## Project structure

- New repo: `~/Projects/java/pings`, git-initialized (done).
- Mirrors `bedbugs`' Stonecutter layout:
  - `stonecutter.gradle.kts` / `settings.gradle.kts` define version nodes
    per loader via the same `match(version, loader...)` helper pattern.
  - `stonecutter.properties.toml` holds centralized mod/version/dependency
    properties.
  - `build.fabric.gradle.kts`, `build.neoforge.gradle.kts` per-loader build
    scripts.
  - Single preprocessed sourceset under `src/main/java`, using Stonecutter
    preprocessor comments for loader/version-conditional code, mirroring
    `bedbugs`' `ModLoaderAccess` + per-loader entrypoint class pattern
    (`PingsFabric`, `PingsNeoForge` implementing a common interface, with
    `PingsMod` holding shared logic).
- Mod id: `pings`. Mod name: `Pings`. Package: `com.github._0x00acab.pings`
  (matches `bedbugs`' group convention; revisit if a different org/package
  is wanted before publishing).
- Icon: `target.svg` from the user's `pixelarticons` icon set — concentric
  rings read clearly at small sizes and echo the diamond/reticle ping
  marker itself.

## Version/loader scope

- **v1 (this spec)**: current Minecraft stable release (26.3) only, on
  Fabric + NeoForge.
- **Later (tracked, not built now)**: broaden to the full historical
  version/loader matrix `bedbugs` covers (back through older 1.21.x,
  1.20.x, and legacy Forge where applicable), once the core design is
  validated.

## Roadmap (explicitly deferred, not forgotten)

- Sound cues per ping category, with per-player mute/volume config.
- Xaero's Minimap/Worldmap marker integration via its API.
- Wider Minecraft version/loader matrix (see above).
- Optional soft-dependency `TeamProvider` implementations for FTB Teams and
  Open Parties & Claims.

## Open questions

None outstanding — all major decisions were resolved during brainstorming.
Implementation-time details (exact default radius, exact burst-cooldown
numbers, exact keybind defaults) are left as tunable config/constants to be
chosen during implementation, not blocking this spec.
