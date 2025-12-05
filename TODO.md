# SMP Core - Feature Implementation Checklist

## Core Features (Must Implement)

### Combat & PvP Balance
- [ ] Damage Modification System
  - [ ] Weapon damage balancing
  - [ ] Armor damage reduction tuning
- [x] Enchantment Limiters
  - [x] Protection limiter (max level enforcement)
  - [x] Sharpness limiter (max level enforcement)
- [x] Combat Item Bans
  - [x] Ban Mace
  - [x] Ban Anchors
  - [x] Ban Crystals
  - [x] Ban Pearls
  - [x] Ban Netherite items
- [ ] Combat Restrictions
  - [ ] Anti-restock in combat
  - [ ] Anti-elytra in combat
  - [ ] Ban breach swapping (hotbar swap prevention)
- [ ] Shield Mechanics
  - [ ] Mace stun shield on hit
  - [ ] Modify Shield disable duration

### Cooldowns
- [ ] Mace Cooldown
- [ ] Wind Charge Cooldown
- [ ] Riptide Cooldown
- [ ] Gap Cooldown

### Bans & Restrictions
- [x] Effect Ban System
  - [x] Configurable potion effect blacklist
  - [x] Effect removal on application
- [ ] Enchantment Ban System
  - [ ] Config-based enchantment blacklist
  - [ ] Item validation on craft/pickup
- [ ] Tipped Arrow Ban
- [ ] Bed Bombing Prevention
- [ ] TNT Minecart Restriction
- [x] Dimension Management
  - [x] Toggle Nether access
  - [x] Toggle End access

### Custom Mechanics
- [ ] Rituals System
  - [ ] Ritual configuration system
  - [ ] Ritual trigger detection
  - [ ] Particle effect system
  - [ ] Ritual completion rewards
- [ ] One-Craft Recipes
  - [ ] Track player craft history
  - [ ] Prevent duplicate crafting
  - [ ] Database/file storage
- [ ] Warden Heart Drops
  - [ ] Custom item on warden kill
  - [ ] Integration with custom recipes
- [x] Invisibility QOL (Invisible Kills)
  - [x] Anonymous player names when invisible
  - [x] Hidden death messages for invisible killers

### Server Management
- [ ] SMP Start Command
  - [ ] /smp start command
  - [ ] Grace period system
  - [ ] Scheduled start time
  - [ ] Pre-start countdown
  - [ ] State persistence

---

## Secondary Features (Other Plugin Alternatives Exist)

### Quality of Life
- [x] One Player Sleep
- [x] Item Limiter System
- [x] Item Explosion Immunity (Stop items from despawning due to explosions)
- [x] Infinite Restock Toggle
- [x] One Mace (Mace Limiter)
- [ ] Stop Item Despawning (timer-based)
- [ ] First Join Kit System
- [ ] Custom Recipes via Config
- [ ] Pearl Cooldown

### Protection Features
- [ ] Anti-Naked Killing
- [ ] Anti-AFK Killing
- [ ] Ban Killing Villagers

### PvP Toggle
- [ ] Global PvP on/off
- [ ] Command to toggle

### Doomsday Features
- [ ] Spectator Mode on Death
- [ ] Death event handling

### Anti-Cheat & Protection (ProtocolLib Required)
- [ ] Anti-Health Indicators
- [ ] Built-in Health Indicators
- [ ] Anti-Seed Cracking
- [ ] Anti-Xaero Minimap

---

## Implemented Features Summary

| Feature | Status | Config Path |
|---------|--------|-------------|
| Custom Anvil Caps | Done | features.custom-anvil-caps |
| Enchantment Replacement | Done | features.enchantment-replacement |
| Mace Limiter | Done | features.mace-limiter |
| Nether Lock | Done | features.dimension-lock-nether |
| End Lock | Done | features.dimension-lock-end |
| Netherite Disabler | Done | features.netherite-disabler |
| Invisible Kills | Done | features.invisible-kills |
| Item Explosion Immunity | Done | features.item-explosion-immunity |
| Infinite Restock | Done | features.infinite-restock |
| Item Limiter | Done | features.item-limiter |
| One Player Sleep | Done | features.one-player-sleep |
| Item Bans | Done | bans.items |
| Effect Bans | Done | bans.effects |

---

## Manager Classes

- [x] BanManager - Item/Effect ban enforcement
- [x] CooldownManager - Unified cooldown storage
- [x] FeatureManager - Dynamic feature loading
- [x] ConfigManager - Config handling
- [x] MenuManager - GUI system
- [x] MenuConfigManager - Menu config
- [x] ChatInputManager - Chat input handling
- [x] CommandManager - Command registration
- [ ] RitualManager - Ritual system (NOT IMPLEMENTED)
- [ ] RecipeManager - Custom recipes (NOT IMPLEMENTED)
- [ ] SMPStateManager - SMP start state (NOT IMPLEMENTED)
