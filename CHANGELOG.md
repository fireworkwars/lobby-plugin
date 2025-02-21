# Changelog

## Added
- Added configuration system
- Added NPCs, with working packet handling & automatic rotations
- Added working arena join GUI integrated with the core and the main plugin
- Added housekeeping events to protect the lobby world
- Added multiple firework spawn locations
- Spawn fireworks when a ranked player joins the game lobby
- Added proper join messaging based on player rank and first-time entry
- Added another line of text under NPC names
- Added custom tablist with correct display of ranked & unranked players
- Added custom scoreboard with dynamic and variable info
- Added fully functional friend system through commands (`/friend` * `add`, `cancel`, `accept`, `deny`, `remove`, `list`)
- Added "Online Friends" display on the scoreboard
- Added a block system (`/block` * `add`, `remove`, `list`)
- Added custom chat colors & prefixes
- Added custom chat system to make lobby messages only visible in the lobby
- Added a private messaging system with private channels (`/message`, `/reply`)
- Added `/shout` for ranked players
- Added a custom name tag system using text displays
- Added friend join/leave messages
- Added a `/fl` command as a convenient alias for `/friend list`
- Added `/block list` command
- Added `/profile` command
- Added first join date and last seen date info tracking
- Shift + right-clicking a player will show their profile
- Added per-player leaderboards for kills & wins, with 3 different timeframe options
- Added more detailed stats tracking
- Added daily & weekly stats tracking in addition to the all-time stats

## Changed
- Tweaked sounds in the NPC menu
- Increased firework flight duration in multiple updates
- Changed player teleportation to spawn location upon entry
- Updated NPC spawn locations and repositioning logic
- Removed full arenas from being listed in NPC menus
- Improved arena player count message in menu items
- Swapped locations of the two NPCs
- Update to mc-1.21.4
- Redesign friend list and block list messages
- Added a custom hover text to player names in chat
- Added a custom click event to player names in chat

## Fixed
- Removed vanilla join/leave messages for a cleaner user experience
- Resolved issue where a firework would spawn at the spawn location
- Fixed NPC locations being half a block off
- Removed sound effects that didn't work
- Removed collision & name tag visibility from NPCs
- Centered spawn location to the exact middle of the block
- Fixed tablist order being reversed (nons higher than gold rank players)
- Fixed name tags often being visible when they shouldn't
- Fixed players being able to see their own name tags
- Removed the vanilla `/msg` command
- Update all scoreboards upon: player join, player leave, player rank change
- Update all tablist upon: player rank change
- Fixed `/msg` alias not working
- Fixed updating scoreboards too early on player leave
- Fixed random `NullPointerException`
- Fixed old name tags remaining on the server when the server restarts
- Fixed NPC overhead displays having a solid black background

## Future Ideas
- None

## Very Future Ideas
- Add parties

## Known Issues
None
