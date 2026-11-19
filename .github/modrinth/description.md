![callouts: ping it, your team sees it]({hero})

[github](https://github.com/nocturne-team/pings) | [issues](https://github.com/nocturne-team/pings/issues)

## overview

point at something, press a key, and your team sees it! callouts puts a marker over the spot, shows how far away it is, and points you back with an arrow when it's behind you. markers show right through walls.

![pings from teammates across an open field, each with a distance label, from 11m to 120m away]({s-field})

## controls

| key | does |
|---|---|
| `Z` | quick ping: pings whatever's under your crosshair, a block, a mob or a player |
| `R` (hold) | ping wheel: pick danger, look here, come here, need help or mining/resource |

you can keep moving while the wheel is open, and pinged mobs keep their marker as they walk around.

![the ping wheel open with danger highlighted at the top]({s-wheel})

## features

- each marker uses the sender's locator bar color, so you can tell who pinged without reading a name
- far away pings shrink so they don't crowd your screen
- the server decides who sees a ping, not the client
- players without the mod still see pings through vanilla stuff: an action bar line, particles, a bell sound and a glow on pinged mobs
- marker size, opacity, distance labels, arrows and the ping sound are all in the settings (modmenu on fabric, the mod list on neoforge)
- drop your own `.ogg` into `config/callouts/sounds/` or pick any note block instrument and pitch. use a mono file to hear which direction a ping came from

![danger, come here and mining pings showing through a stone brick wall, and an arrow pointing to a ping off screen]({s-walls})

## who sees a ping

if you're on a scoreboard team, your whole team does. if not, everyone within 128 blocks. every marker disappears after 10 seconds.

there's a short cooldown between pings, and pinging too many times in a row locks you out for a few seconds. server owners can change that, the team-less radius and the max ping distance in `config/callouts.properties`.

## dependencies

- install it on the server and on every client that wants to send pings
- [fabric api](https://modrinth.com/mod/fabric-api) (on fabric)

## credits

thank you to:

- [Pixabay](https://pixabay.com/service/license-summary/) artists for the default ping sounds (these aren't CC0 like the rest of the mod)
- [Jersey 10](https://fonts.google.com/specimen/Jersey+10) (SIL Open Font License) for the headline font

callouts is not affiliated with or endorsed by Mojang or Microsoft. Minecraft is a trademark of Mojang Studios.
