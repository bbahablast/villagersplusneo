# Villagers Plus Neo

Four new villager jobs for Minecraft, on NeoForge.

| Version | Folder | JDK | NeoForge |
| --- | --- | --- | --- |
| 1.20.1 | [`1.20.1/`](1.20.1) | 17 | 47.1.106 |
| 1.21.1 | [`1.21.1/`](1.21.1) | 21 | 21.1.217 |

Each folder is its own Gradle project.

This is an unofficial port of [Villagers Plus](https://github.com/finallion/VillagersPlus)
by finallion, which stopped at 1.20.1. The mod is finallion's work this repo only
brings it to NeoForge. It isn't affiliated with them or endorsed by them, so if you
like it, go support the original on
[Modrinth](https://modrinth.com/mod/villagersplus) and
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/villagersplus-fabric).

## Building

```
cd 1.21.1
./gradlew build
```

The jar lands in that folder's `build/libs/`. The 1.20.1 build decompiles
Minecraft on the first run and takes about ten minutes.

Config options and the differences from the original are listed per version in
[1.20.1/README.md](1.20.1/README.md) and [1.21.1/README.md](1.21.1/README.md).

## License

GPL-3.0, the same license as the original full text in [LICENSE](LICENSE).

Original code and design © finallion.
