# Metamorph Change Log

## Metamorph 1.1.9

This is a small patch update with lots of awesome bug fixes pull requests from asanetargoss and Johni0702!

* Fixed increasing health when morphing with modifier, and other cases. Fixed by **asanetargoss**
* Fixed small morphs suffocating in ceiling. Fixed by **asanetargoss**
* Fixed player having multiple chicken morphs. Fixed by **asanetargoss**
* Fixed bugs for BetterPortals compatibility. Fixed by **Johni0702**
* Fixed buttons syncrhonization in survival morph menu
* Fixed survival morph overlay disappearing on vsync/unlimited frame rates  

## Metamorph 1.1.8

This is a small patch update which is mostly oriented toward Blockbuster's compatibility, but also has some internal changes and few bug fixes.

**Compatible** with McLib `1.0.1`. It doesn't mean that future versions of McLib would be incompatible, but older versions are most likely incompatible.

* Added morph sounds (implemented thanks to asanetargoss)
* Added item stack support to body part system
* Added morph editor panel API based on McLib's panel base GUI
* Extracted body part system from Blockbuster
* Fixed ender dragon morph attacking itself (implemented thanks to asanetargoss)

## Metamorph 1.1.7

This is a small patch update which is mostly oriented toward Blockbuster's compatibility.

**Compatible** with McLib `1.0`. It doesn't mean that future versions of McLib would be incompatible, but older versions are most likely incompatible.

* Added `mclib` library mod as a dependency
* Added morph merging API
* Added custom payload listener (now plugins are able to modify of the players via custom payload packets)
* Fixed issues with player render mods (like Mo' Bends or Emoticons) by changing player morphing animation
* Full rewrite of Metamorph's GUIs to support McLib GUI framework
* Prevent morphing into the bounds of other blocks (added by asanetargoss)
* Removed morph builders (but replaced with morph editors)

## Metamorph 1.1.6

This is a small and quick patch which (again) provides several bug fixes mainly for Blockbuster's update.

* Disable delay and particles when morph animation is turned off
* Fixed MPM compatibility
* Fixed losing morphs when returning from end (fixed by asanetargoss)
* Fixed OpenGL error with transformation matrix (not popped stack)
* Fixed NPE in morph builder drop down
* Fixed low max health when demorphing
* Fixed sun allergy not causing damage in 1.11.2+
* Fixed issue with player morph's hand not rendering in first person

## Metamorph 1.1.5

This is a small, quick and dirty patch that provides several bugfixes (mainly for Blockbuster's update).

* Add acquired morphs to creative morph menu (to be able to configure acquired morphs in the morph builder)
* Allow using ESC button to close the creative morph menu
* Delegate RenderSubPlayer methods to original RenderPlayer method
* Disable change of morphs in adventure mode
* Fix `ShulkerMorph` NPE crash
* Fix `BlockMorph` returning `null` in the clone method (now block morphs can be used with Blockbuster)
* Fix class cast crash with RFTools
* Fix name tags appearing for actor entities
* Fix player morph SkinType values in NBT
* Restore back the old health
* Move damage logic from `swim` ability to `water_breath`

## Metamorph 1.1.4

This is a patch update has no aim altogether. It's just has few fixes and lots of nice features being added like an ability to morph into players (and have their skins) and blocks, more mob actions and etc.

<a href="https://youtu.be/zAOq_VP-vuE"><img src="https://img.youtube.com/vi/zAOq_VP-vuE/0.jpg"></a> 

* Added block morphs
* Added players morphs
* Added command `/metamorph` which can be used for reloading morph blacklist and settings
* Added configurable morphing animation 
* Added morph builder GUI to creative morph menu
    * `player` builder – allows to create a player morph based on player's username
    * `nbt` builder – allows to create a morph based on NBT data
* Add more vanilla morph features which resembles their vanilla mobs
    * `shulker` – freeze on a place, and with action key fires anti-gravity missiles
    * `llama` – spits on action key
    * `silverfish` – can hide in a block when action key
    * `endermite` – teleports randomly on action key
* Added more event hooks #64 (thanks to asanetargoss)
* Don't allow survival morph overlay to show up when using `;` and `\` keys when there are no variants
* Improve creative morphs menu 
    * Add "Scroll to top" button (suggested by ivandoesyt on Twitter)
    * Increase scrolling speed
    * Increase morphs per row according to pane's width
    * Show only the first variant (other variant should be collapsed)
    * Allow several variant groups to be shown in creative morph menu
* Improve ghosts
    * Add `Username` property to ghosts to allow spawn mobs for specific players
    * Add `Ownerless` property to make ghost acquire-able by the first player to pick it up
    * Make ghosts like items (unpushable and stuff)
    * Make ghosts hover and rotate
    * Make ghost item-like pickup animation 
* Fixed "healing" bug between morphings #64 (thanks to asanetargoss)
* Fixed the undead hurting sounds #59 
* Fixed left hand side
* Fixed orientation of sitting for morphs
* Fixed name tags – now it respects the team options #50 
* Fixed Ender Dragon transformations/model visibility #62

## Metamorph 1.1.3

This is a small patch fix which aims at fixing more issues (reported by mod users).

* Added German translation (thanks to ACGaming)
* Fixes crash when you click on top or lower parts when demorph (default player morph) is selected
* Fixed potion ability causing flashing before expiration for 15 seconds (thanks to code from mallrat208), now it's continious
* Fixed crahs NoSuchMethodException on server side when teleporting in enderman morph (thanks to code from mallrat208)
* Fixed crash NullPointerException when ghosts from older versions are spawned

## Metamorph 1.1.2

Metamorph 1.1.2 is a patch fix which aims at fixing some issues related to other mods, improving some stuff, and adding new code for custom models (because of Blockbuster mod).

Numbers in the parenthesis following by a hash is the number of issue in Metamorph's GitHub issue tracker. 

* Added `/acquire_morph` command 
* Added mechanism for spotting errored entities in creative and survival morph menu
* Add some Blockbuster sensitive code for custom models
    * JSON deserializers
    * Custom model utilities
    * Custom poses
    * Fixed scale
    * Added limb color and opacity  
* Command `/morph` can demorph player
* Disable stripping of custom name tag (#22)
* Increase scrolling in creative morph menu in two times
* Mouse control in survival morph menu (#18)
* MorePlayerModels POV option fix (#24)
* Optimized rendering in creative morph menu
* Tough-as-Nails temporary option fix (#28)

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

<a href="https://youtu.be/4BrvdKbVAHo"><img src="https://img.youtube.com/vi/4BrvdKbVAHo/0.jpg"></a> 

* Added Public API (technical)
    * `mchorse.metamorph.api.MorphAPI` class provides API static methods
    * `IMorphProvider` – for entities that want to be rendered
    * `IHandProvider` – for `ModelBase`s who want to provide `ModelRenderer`s for first-person hand rendering
    * Multiple Forge events posted on `MinecraftForge.EVENT_BUS`:
        * `MorphEvent` – when player is about to morph or to demorph
        * `AcquireMorphEvent` – when player is about to acquire a morph
        * `MorphActionEvent` – when player is about to use morph's action
    * `IMorphFactory` – for adding and registering morphs, abilities, attacks and actions.
* Added support for third-party living base mobs (Entities which derive from `EntityLivingBase` and have `RenderLivingBase&lt;?&gt;` renderers)
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

<a href="https://youtu.be/4ZD8vV5Zyuw"><img src="https://img.youtube.com/vi/4ZD8vV5Zyuw/0.jpg"></a> 

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