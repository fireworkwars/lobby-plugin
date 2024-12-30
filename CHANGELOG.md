# Changelog

## Added
- Added configuration system
- Added NPCs, with working packet handling & automatic rotations
- Added working arena join GUI integrated with the core and the main plugin
- Added housekeeping events to protect the lobby world
- Added multiple firework spawn locations
- Spawn fireworks when a ranked player joins the game lobby
- Added proper join messaging based on player rank and first-time entry

## Changed
- Tweaked sounds in the NPC menu
- Increased firework flight duration in multiple updates
- Changed player teleportation to spawn location upon entry
- Updated NPC spawn locations and repositioning logic
- Removed full arenas from being listed in NPC menus
- Improved arena player count message in menu items
- Swapped locations of the two NPCs

## Fixed
- Removed vanilla join/leave messages for a cleaner user experience
- Resolved issue where a firework would spawn at the spawn location
- Fixed NPC locations being half a block off
- Remove sound effects that didn't work