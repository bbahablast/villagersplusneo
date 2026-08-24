# Villagers Plus Neo (1.20.1)

Four new villager jobs for Minecraft 1.20.1 on NeoForge.

This is an unofficial port of [Villagers Plus](https://github.com/finallion/VillagersPlus)
by finallion. The mod is finallion's work this repo only brings it to NeoForge. It
isn't affiliated with them or endorsed by them, so if you like it, go support the
original on
[Modrinth](https://modrinth.com/mod/villagersplus) and
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/villagersplus-fabric).

The same port for 1.21.1 is in [`1.21.1/`](../1.21.1).

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

## Notes on the 1.20.1 build

NeoForge for 1.20.1 is still on the pre-rename Forge API, so the code targets
`net.minecraftforge.*`, `META-INF/mods.toml` and Java 17. It builds with
ForgeGradle 6 rather than NeoGradle, because 1.20.1 runs on SRG names and only
ForgeGradle reobfuscates the jar for you; NeoGradle 7 has no obfuscation
subsystem at all and silently ships a jar that crashes on load. CI asserts the
jar's bytecode is SRG-named so that cannot regress. Compared with the 1.21.1
port:

- The house pool is edited through reflection instead of an access transformer.
  Access transformers on 1.20.1 are written against SRG names, which would pin the
  mod to one mapping set; looking the field up by its shape does not.
- Potions use `PotionUtils` rather than the 1.20.5+ data components.
- The alchemist screen is registered in `FMLClientSetupEvent`, since
  `RegisterMenuScreensEvent` does not exist yet.

## Building

Needs JDK 17. The first build decompiles Minecraft and takes roughly ten
minutes; later builds reuse the cache and take a couple.

```
./gradlew build
```

The jar lands in `build/libs/`. Every push is built by GitHub Actions
(`.github/workflows/build.yml` at the repo root).

## License

GPL-3.0, the same license as the original — full text in [LICENSE](../LICENSE). The
source has to stay available, which is what this repo is for.

Original code and design © finallion.
