# SMP Core

<center>
All-in-one plugin for SMP servers. PvP balance, item limits, mob control, and more.
  
![features](https://cdn.modrinth.com/data/cached_images/97e9b2b36d52ea26bad209b15d71b9c289e8e320.jpeg)
</center>

---

## What is this?

Instead of running 10 different plugins, SMP Core handles most of what you need for an SMP:

- PvP balancing (gap limits, mace limits, enchant caps)
- Gear control (disable netherite, limit enchantments)
- Dimension locks (keep Nether/End closed until you're ready)
- Mob control (disable phantoms, creepers, whatever you want per world)
- QoL stuff (infinite villager trades, one player sleep)

Everything is off by default. Turn on what you need, ignore the rest.

---

## Features

### Combat & PvP

**Item Limiter** (1.1.0)

Limit how many of any item players can carry. Set it up through a GUI - drag an item in, type the max count. Works with gaps, totems, pearls, potions, whatever. Set to 0 to ban an item entirely. Extra items drop on the ground.

**Mace Limiter**

Set a server-wide cap on how many maces can exist. When someone crafts one, it announces to everyone. Once the limit is hit, the recipe stops working.

**Netherite Disabler**

If you want diamond meta back. Toggle armor and tools separately through the GUI.

---

### Enchantments

**Custom Anvil Caps**

Set max levels for enchantments. Sharpness 3 max, Protection 3 max, whatever you want. Blocks anvil combining and enchanting tables. You can also force one protection type per armor piece.

**Enchantment Limiter** (1.1.0)

Scans items when players pick them up or open inventories. Automatically reduces enchantments that are above your limits. Useful for cleaning up items from exploits.

---

### Mob Manager (1.1.0)

Control which mobs can spawn, per world.

- Toggle any mob type on/off
- Spawn eggs still work for admins
- WorldGuard regions can bypass the rules
- Option to clean up disabled mobs from loaded chunks

---

### Dimension Locks

Lock the Nether or End until you decide to open them. Custom messages when players try to enter. Admins can bypass for setup.

---

### Infinite Restock (1.1.0)

Villagers never run out of trades and prices don't increase. Works with wandering traders too. Has an uninstall mode if you want to revert.

---

### Other Stuff

**One Player Sleep** - One person sleeping skips the night.

**Item Explosion Immunity** - Dropped items survive creeper/TNT explosions.

**Invisible Kills** - Your name is hidden in death messages when you kill someone while invisible.

---

## Setup

1. Drop in `plugins` folder
2. Restart server
3. Run `/smp` to open settings
4. Click to toggle features, right-click for settings

Config files exist if you prefer editing those directly.

### Commands

| Command | Description |
|---------|-------------|
| `/smp` | Main settings menu |
| `/smp reload` | Reload configs |
| `/smp infiniterestock` | Villager trade settings |

### Permissions

| Permission | Description |
|------------|-------------|
| `smpcore.menu` | Access settings menu |
| `smpcore.reload` | Reload configs |
| `smpcore.bypass.*` | Bypass all restrictions |

---

## 1.1.0 Changes

- New GUI system for all settings
- Item Limiter rewrite with drag-and-drop interface
- Mob Manager with per-world control and WorldGuard support
- Infinite Restock with uninstall option
- Mace Limiter GUI
- Performance improvements
- All messages customizable in messages.yml

---

## Requirements

- Minecraft 1.21.1+
- Paper or Spigot
- Java 21+
- WorldGuard (optional, for Mob Manager region bypass)

---

## Support

Bug reports and feature requests welcome. Links in the sidebar.
