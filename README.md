# PlayerAbilityLib

[![](https://jitpack.io/v/Ladysnake/PlayerAbilityLib.svg)](https://jitpack.io/#Ladysnake/PlayerAbilityLib)

A lightweight library to provide compatibility between mods that make use of player abilities.

*credits to Chloe Dawn for some of the API design*

## Adding PAL to your project

You can add the library by inserting the following in your `build.gradle` :

```gradle
repositories {
	maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation "io.github.ladysnake:PlayerAbilityLib:${pal_version}"
    include "io.github.ladysnake:PlayerAbilityLib:${pal_version}"
}
```

You can then add the library version to your `gradle.properties`file:

```properties
# PlayerAbilityLib
pal_version = 1.x.y
```

You can find the current version of PAL in the [releases](https://github.com/Ladysnake/PlayerAbilityLib/releases) tab of the repository on Github.

## Using PAL

You can find a couple examples in the [Test Mod](https://github.com/Ladysnake/PlayerAbilityLib/tree/master/src/testmod/java/io/github/ladysnake/paltest).

[Item that toggles an ability](https://github.com/Ladysnake/PlayerAbilityLib/blob/master/src/testmod/java/io/github/ladysnake/paltest/AbilityToggleItem.java) :
```java
public static final AbilitySource CHARM_FLIGHT = Pal.getAbilitySource("mymod", "charm_flight"));  // works like an identifier
    
@Override
public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    if (!world.isClient) {
        if (CHARM_FLIGHT.grants(user, VanillaAbilities.ALLOW_FLYING)) { // check whether the source is granting the ability
            CHARM_FLIGHT.revokeFrom(user, VanillaAbilities.ALLOW_FLYING); // if it is, revoke it
       } else {
            CHARM_FLIGHT.grantTo(user, VanillaAbilities.ALLOW_FLYING);  // otherwise, grant it
        }
    }
    return TypedActionResult.success(user.getStackInHand(hand));
}
```

[Potion that grants an ability](https://github.com/Ladysnake/PlayerAbilityLib/blob/master/src/testmod/java/io/github/ladysnake/paltest/FlightEffect.java) :
```java
public static final AbilitySource POTION_FLIGHT = Pal.getAbilitySource("mymod", "potion_flight");

@Override
public void onApplied(LivingEntity effected, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int amplifier) {
    if (effected instanceof PlayerEntity) {
        POTION_FLIGHT.grantTo((PlayerEntity) effected, VanillaAbilities.ALLOW_FLYING);
    }
}

@Override
public void onRemoved(LivingEntity effected, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int amplifier) {
    if (effected instanceof PlayerEntity) {
        POTION_FLIGHT.revokeFrom((PlayerEntity) effected, VanillaAbilities.ALLOW_FLYING);
    }
}
```
