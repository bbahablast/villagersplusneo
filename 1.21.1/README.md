# Villagers Plus Neo (1.21.1)

Four new villager jobs for Minecraft 1.21.1 on NeoForge.
The same port for 1.20.1 is in [`1.20.1/`](../1.20.1).

This is an unofficial port of [Villagers Plus](https://github.com/finallion/VillagersPlus)
by finallion, which stopped at 1.20.1. The mod is finallion's work this repo only
brings it forward. It isn't affiliated with them or endorsed by them, so if you like
it, go support the original on
[Modrinth](https://modrinth.com/mod/villagersplus) and
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/villagersplus-fabric).

## Config

Server-side, at `<world>/serverconfig/villagersplus-server.toml`:

- how much experience the Enchanted Basin holds, and how much each click moves
- the Alchemist Table's explosion chance set it to `0` to switch it off
- how often each house shows up in each village type `0` removes it

## What's different from the original

- **Trades can't be changed by a datapack yet.** The original let you rewrite any
  trade, including vanilla villagers', from a datapack. Here the trades are read
  from inside the jar at startup. The JSON format is unchanged, so the files are
  still readable at `data/villagersplus/default_villager_trades/`, but dropping your
  own copy in a datapack won't override them.
- Two trade types the original supported (enchanted books/tools and structure maps)
  aren't implemented. Everything the four jobs actually use is.
- Custom particles are swapped for the nearest vanilla ones.
- Village houses get added to vanilla's house pools when the world loads instead of
  replacing them, so other mods that add village buildings still work.
- NeoForge only. The original was Fabric and Forge via Architectury; this is a plain
  NeoForge project, which keeps it simple to maintain.

## Building

Needs JDK 21.

```
./gradlew build
```

The jar lands in `build/libs/`. Every push is built by GitHub Actions
(`.github/workflows/build.yml` at the repo root).

## License

GPL-3.0, the same license as the original — full text in [LICENSE](../LICENSE).

Original code and design © finallion.
