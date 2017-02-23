# Metamorph Change Log

## Metamorph 1.1.1

This patch fixes 1.11.2 problem with morphs not getting their abilities. This was caused by change in how names were changed in Minecraft 1.11. This patch also adds a command for morphing, adds abilities for ender dragon and morph blacklist for naughty morphs.

* Added `/morph` command for making player(s) morph into given morph
* Added fire breathing ability for ender dragon
* Added entity blacklist (`blacklist.json` in config folder)
* Attempt to fix crash with Zoology mod in creative menu
* Fixed survival morphs in `1.11.2`
* Nerfed `blaze` morph, it now shoots non explosive fireballs
* Removed patched morphs category
* Swapped horse variants names (zombie and skeleton)

## Metamorph 1.1

A big update of Metamorph mod. This update introduces third-party morph support, improved GUIs and public API. A lot of code was rewritten, and there close to no morphs which use custom models (as in the first version).

* Added Public API (technical)
    * `mchorse.metamorph.api.MorphAPI` class provides API static methods
    * `IMorphProvider` – for entities that want to be rendered
    * `IHandProvider` – for `ModelBase`s who want to provide `ModelRenderer`s for first-person hand rendering
    * Multiple Forge events posted on `MinecraftForge.EVENT_BUS`:
        * `MorphEvent` – when player is about to morph or to demorph
        * `AcquireMorphEvent` – when player is about to acquire a morph
        * `MorphActionEvent` – when player is about to use morph's action
    * `IMorphFactory` – for adding and registering morphs, abilities, attacks and actions.
* Added support for third-party living base mobs (Entities which derive from `EntityLivingBase` and have `RenderLivingBase<?>` renderers)
* Added morphs user configuration (`morphs.json` in config folder)
* Added much more morphs to **Creative Morph Menu**
* Added favoriting of acquired morphs (thanks to NlL5)
* Added removal of acquired morphs (thanks to LeKoopa and NlL5)
* Enhanced **Creative Morph Menu** GUI:
    * Added "Acquire" a morph button
    * Added "Search" text field filter
    * Added sorting by category and ABC
    * Changed layout
* Enhanced **Survival Morph Menu** GUI:
    * Added a key binding to make survival menu focusable
    * Added two key bindings for switching between merged variants
    * Added "Remove" and "Favorite" buttons
    * Added filtering by favorites
    * Added sorting by ABC
    * Merged morphs by same name into one cell with vertical scrolling
* Improved `swim` ability (suffocation on the air and better mobility)
* Improved `VillagerGolem` morph (it's strong, slow and very heavy)
* Improved `sun_allergy` ability – if player wears a helmet, player doesn't burn, but helmet gets damage (thanks to @The-Fireplace)

## Metamorph 1.0.2

Small patch update that makes this mod work on Forge modded server (I thought it was working, but apparently I forgot to test it out).

Besides that morph's names were changed a little bit and Iron Golem's attack `knockback` was fixed.

* Changed the way how morph's names are displayed
* Fixed server crashes and errors (works on server, although there might be bugs)
* Fixed Iron Golem's `knockback` attack in multiplayer (previously it didn't worked)

## Metamorph 1.0.1

First patch of Metamorph mod.

This patch doesn't really add stuff, rather it improves `1.0` release based on some of my thoughts and people's input from comments on PMC, PM, mails and mod reviews.

This patch adds few configuration options, improves GUIs, fixes some bugs and improves some morphs and rendering of the hand (thanks to [Blockbuster's mod](https://github.com/mchorse/blockbuster/) hands code).

**Review video**:  
<a href="https://youtu.be/4ZD8vV5Zyuw"> 
    <img src="https://img.youtube.com/vi/4ZD8vV5Zyuw/0.jpg">
</a>

* Added acquiring **morph** overlay (replaced chat messages).
* Improved first-person hand rendering (will work even with shaders).
* Improved **Creative Morphs Menu**:
    * Added key shortcuts for scrolling:
        * Key `arrow-up`: scroll up a little bit
        * Key `arrow-down`: scroll down a little bit
        * Key `arrow-left`: scroll to top
        * Key `arrow-right`: scroll to bottom
    * Added dragable scroll bar
    * Fixed crash when morphing with no morph selected
* Improved **Survival Morph Menu**:
    * Added **demorph** key. There's no default key, so you'll have to set it up.
    * If `[` or `]` pressed with `shift` modifier, one morph will be skipped.
    * If `[` or `]` pressed with `alt` modifier, selection will go in the beginning or the end.
    * Press `[` or `]` will highlight menu even if cursor hasn't moved.
    * When pressing `enter` (apply morph), the **Survival Morph Menu** will disappear.
* Improved **morphs**:
    * Added `hostile` ability, to most of hostile **morphs** and for some neutral and animal **morphs**, making morphed players not hostile to hostile mobs.
    * Added `speed` property. Now some of the **morphs** (`Ozelot`, `EntityHorse` and `Rabbit)` run faster or (`VillagerGolem`) run slower.
    * Added `night_vision` ability to `Guardian`.
    * Increased range and accuracy of `potions` action (`Witch` morph, basically).
    * When a player morphs or pick ups a morph, popping sound effect is played
* Sound effects for `teleport` action was added

## Metamorph 1.0

Initial release of Metamorph mod. 

Metamorph mod is a **Minecraft** mod that allows players to morph into different vanilla creatures by killing them and getting their morphs (souls/ghosts/whatever). Heavily inspired by **Morph** (by iChun) and **Shape Shifter Z** (by zacuke) mods.

This version adds morphs to most of vanilla creatures.

* Added **morphs**
    * **Animals**: Bat, Cave Spider, Chicken, Cow, Horse, Mooshroom, Ocelot, Pig, Polar Bear, Rabbit, Sheep, Spider, Squid, and Wolf *morph*.
    * **Neutral mobs**: Enderman, Iron Golem, Snow Golem, Villager, and Zombie Pigman *morph*.
    * **Hostile mobs**: Blaze, Creeper, Ghast, Guardian, Magma cube, Silverfish, Skeleton, Slime, Witch, Wither Skeleton, and Zombie *morph*.
* Added **abilities**: `fly`, `glide`, `water_breath`, `swim`, `water_allergy`, `sun_allergy`, `snow_walk`, `fire_proof`, `prevent_fall`, `jumping`, `night_vision`, `climb`, `ender`, `smoke`, and `hungerless` *ability*.
* Added **attacks**: `wither`, `poison` and `knockback` *attack*.
* Added **actions**: `potions`, `snowball`, `teleport`, `fireball`, `explode` and `jump` *action*.
* Added **Creative Morph Menu** (use `B` key to open)
* Added **Survival Morph Menu** (use `[` and `]` to select a morph and `enter` to apply)
* Added **ghost** (by picking up a ghost, you gain a *morph* into your **Survival Morphing Menu**)