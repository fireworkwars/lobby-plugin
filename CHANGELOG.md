# Changelog
## Release v1.0.0
- Added configuration system

- Added NPCs, with working packet handling & automatic rotations
- Removed collision & name tag visibility from NPCs
- Added another line of text under NPC names

- Added working arena join GUI integrated with the core and the main plugin
- Removed full arenas from being listed in NPC menus

- Added housekeeping events to protect the lobby world
- Removed vanilla join/leave messages for a cleaner user experience

- Teleport players to the spawn locations upon joining
- Spawn fireworks when a ranked player joins the game lobby
- Added multiple firework spawn locations
- Added proper join messaging based on player rank and first-time entry

- Added a custom name tag system using text displays
- Fixed players being able to see their own name tags

- Added fully functional friend system through commands (`/friend` * `add`, `cancel`, `accept`, `deny`, `remove`, `list`)
- Added a `/fl` command as a convenient alias for `/friend list`
- Added friend join/leave messages

- Added a block system (`/block` * `add`, `remove`, `list`)
- Added `/block list` command
- Redesigned friend list and block list messages

- Added a private messaging system with private channels (`/message`, `/reply`)
- Removed the vanilla `/msg` command

- Added `/shout` for ranked players

- Added `/profile` command
- Shifting and right-clicking a player will show their profile

- Added per-player leaderboards for kills & wins, with 3 different timeframe options
- Added more detailed stats tracking
- Added daily & weekly stats tracking in addition to the all-time stats
- Added first join date and last seen date info tracking

- Added custom tablist with correct display of ranked & unranked players
- Added custom scoreboard with dynamic and variable info
- Added "Online Friends" display on the scoreboard

- Added custom chat system to make lobby messages only visible in the lobby
- Added custom chat colors & prefixes
- Added custom click & hover events to player names
# Release v1.1.0
- Updated all imports from core plugin (foundation.esoteric.fireworkwarscore -> xyz.fireworkwars.core)
- Updated communication interface implementations to match the new names
- Massive code refactor
# Release v1.1.1
- Fixed an `NPE`
# Release v1.1.2
- Updated some interface & method names to match the core dependency
- Fixed name tags remaining on the server and persisting over restarts
# Release v1.1.3
- Build against the latest version of the core plugin
# Release v1.1.4
- Fixed NPC name tags not being transparent
# Release v1.2.0
- Changed to packet-based display entities for NPC name tags
# Release v1.3.0
- Build against the latest version of the core plugin
# Release v1.3.1
- Build against the latest version of the core plugin
# Release v1.4.0
- Build against the latest version of the core plugin
# Release v1.5.0
- Build against the latest version of the core plugin
# Release v1.5.1
- Build against the latest version of the core plugin
# Release v1.5.2
- Fixed leaderboard displaying players other than the top 10
- Fixed explosion events being cancelled too late by housekeeping listeners


## Future Ideas
- None

## Very Future Ideas
- Add parties

## Known Issues
None
