# Bulk Fans

Bulk fan processing for NeoForge. Inspired by the Create Encased Fan.

I just wanted the fans from Create. This is that: drop items in front of a fan, blow air through some fire or water, and they get processed in bulk.

## How it works

Put an **Industrial Fan** down and give it a redstone signal. The signal strength sets how far it blows, not how fast it processes items, from 3 blocks up to 15.

Smoking and blasting use the normal smoker, blast furnace and furnace recipes. Blasting skips food, so your steak doesn't turn into charcoal. Washing and haunting have their own recipes, and they also oxidize or clean copper one stage at a time.

A full stack takes 20 seconds. Smaller stacks are quicker. Items in the airflow won't despawn, and you can't grab a stack while it's halfway done.

The fan pushes mobs and players too. Fire and lava set you on fire, soul fire gives you a bit of Darkness, and water puts you out.

Sneak + right-click the fan with an empty hand to switch between push and pull.

Airflow stops at solid blocks. Slabs, fences, open trapdoors and the like let it through.

## Catalysts

If you don't want lava pouring everywhere, there are caged versions: **Fire**, **Soul Fire**, **Lava** and **Water Catalysts**. They work exactly like the real thing.

## Tray

A **Tray** holds one stack. Put it in the airflow and the stack gets processed in place. It works with hoppers and pipes.

Put a row of trays in front of a fan and they all get processed. The air goes through all of them and stops at the last one.

## Spanner

Right-click a fan to turn it. Sneak + right-click a fan, tray or catalyst to pick it straight up.

## JEI

Every processing type gets its own JEI tab, including anything added by datapacks.

## Datapacks

Pretty much everything is data, so you can add your own recipes, catalysts, and even whole new processing types.

**Recipe** – `data/<namespace>/recipe/<name>.json`

```json
{
  "type": "bulkfans:fan_processing",
  "processing_type": "bulkfans:washing",
  "ingredient": "minecraft:gravel",
  "results": [
    { "id": "minecraft:flint" },
    { "id": "minecraft:iron_nugget", "chance": 0.1 }
  ]
}
```

**Catalyst** – `data/bulkfans/data_maps/block/fan_catalysts.json` (or `fluid/` for fluids)

```json
{
  "values": {
    "minecraft:magma_block": { "processing_type": "bulkfans:blasting" }
  }
}
```

**Processing type** – `data/<namespace>/bulkfans/processing_type/<name>.json`

```json
{
  "particle": { "type": "minecraft:falling_honey" },
  "copper_transform": "wax",
  "icon": "minecraft:honey_bottle"
}
```

Other fields: `vanilla_recipes` (`smoking` or `blasting`), `ignite_entities`, `extinguish_entities`, and `effects`.

To keep something out of blasting, or force it in, use the item tags `bulkfans:blasting_exclude` and `bulkfans:blasting_include`.

## Config

Processing time, push strength, range, particles and so on are all in the config. Server settings are per world.