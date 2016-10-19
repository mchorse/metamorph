# Metamorph

Metamorph is a simple morphing survival mod, inspired by 
[Morph](https://github.com/iChun/Morph) and 
[Shape Shifter Z](http://www.planetminecraft.com/mod/shape-shifter-1736589/). 
It is for Minecraft 1.10.2 and 1.9.4 and Forge compatible (built with Forge 
12.18.1.2073). 

It allows player to morph into different creatures (list of available creatures 
located below). When you kill an entity that has morph, a ghost version of this 
mob appears from this entity and grants you this entity's morph. Then you can 
use `[` and `]` keys to select a morph and `enter` key to apply it onto 
yourself.

Most of the morphs have some of abilities, attacks and/or actions. 

Ability is basically a custom behavior that makes you do something. For example, 
Chicken morph has ability to `glide`, meaning it will slowly float down toward 
the ground (without inflicting any fall damage).

Attacks are custom effects that are triggered on an entity when you attack it. 
There are several attacks available in the mod like `knockback` attack, which 
throws your target high in the air (~5 blocks), and also `poison` and `wither` 
attacks, which, basically, apply `poison` or `wither` potions onto your targets. 

And finally, actions are some special actions that are triggered when you press 
`V` key (by default). When you're creeper, you explode and die when using this 
action. When you're enderman, you teleport wherever you're looking, and so on.

Well, that's it. Have fun!

## Installation 

Install [Minecraft Forge](http://files.minecraftforge.net/), then go to 
[releases](https://github.com/mchorse/metamorph/releases) and download the 
latest stable version (not a pre-release) of jar file. Put it in minecraft's `mods` 
folder, and launch the game. 

After that, Metamorph mod should be installed and will appear in Minecraft's 
mods menu. If Metamorph didn't appear in the mods menu, then something went 
wrong.

## Available morphs

### Animal mobs

* Bat (`fly` and `night_vision` abilities, `6` health)
* Cave spider (`climb` abilities, `poison` attack, `12` health)
* Chicken (`glide` abilities, `4` health)
* Cow (`10` health)
* Mooshroom (`10` health)
* Ocelot (`10` health)
* Pig (`10` health)
* PolarBear (`30` health)
* Rabbit (`prevent_fall` abilities, `jump` action, `3` health)
* Sheep (`8` health)
* Spider (`climb` abilities, `16` health)
* Squid (`water_breath` and `swim` abilities, `10` health)
* Wolf (`8` health)

### Neutral mobs

* Enderman (`water_allergy` abilities, `teleport` action, `40` health)
* Iron golem (`knockback` attack, `100` health)
* Snow golem (`snow_walk` abilities, `snowball` action, `4` health)
* Villager (`20` health)
* Zombie pigman (`fire_proof` abilities, `20` health)

### Hostile mobs

* Blaze (`fly` and `fire_proof` abilities, `fireball` action, `20` health)
* Creeper (`explode` action, `20` health)
* Ghast (`fly` and `fire_proof` abilities, `fireball` action, `10` health)
* Guardian (`water_breath` and `swim` abilities, `30` health)
* Magma cube (`jumping` and `fire_proof` abilities, `4` health)
* Silverfish (`8` health)
* Slime (`jumping` abilities, `4` health)
* Skeleton (`sun_allergy` abilities, `20` health)
* Wither skeleton (`fire_proof` abilities, `wither` attack, `20` health)
* Witch (`potions` action, `26` health)
* Zombie (`sun_allergy` abilities, `20` health)