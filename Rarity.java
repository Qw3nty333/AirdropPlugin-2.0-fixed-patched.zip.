# AirdropPlugin 2.0
# Время в секундах.

open-min-seconds: 420
open-max-seconds: 600
ultra-dome-seconds: 180
moving-items-ticks: 6
radius: 20

knockback-min: 7.0
knockback-max: 15.0
combat-damage: 6.0

# Открытый аирдроп удаляется через это время после открытия.
cleanup-after-open-seconds: 300

# Автоматический спавн.
auto-spawn:
  enabled: true
  interval-minutes: 20
  max-active: 3
  world: world
  # Если x/z = null, используется spawn мира.
  center-x: null
  center-z: null
  radius: 600
  min-distance-between-drops: 120
  # Вес выбора редкости.
  rarity-weights:
    peaceful: 45
    epic: 28
    legendary: 16
    mythic: 8
    ultra: 3

# Для автоматических аирдропов плагин выравнивает круг радиусом 20.
# Если false, блоки внутри круга не перезаписываются, а оформление
# ставится только в воздухе. Для "арены" лучше true.
replace-terrain: true
spawn-height-offset: 1

# Голограмма.
hologram:
  enabled: true
  height: 3.2
  update-ticks: 10
  show-progress-bar: true

# GUI.
gui:
  enabled: true
  title: "§8Аирдропы §7• §fактивные"
  rows: 3

# Звуки/частицы.
effects:
  spawn-sound: BLOCK_BEACON_ACTIVATE
  open-sound: BLOCK_ENDER_CHEST_OPEN
  dome-sound: BLOCK_BEACON_POWER_SELECT
  spawn-particles: true

# Лут. Сумма здесь 100%.
loot-chances:
  junk: 45
  normal: 20
  rare: 18
  epic: 10
  valuable: 5
  ultra: 2
