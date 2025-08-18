
---
`getItemType()` 
Gets the type the item in the reader 
#### returns
- `string` The Item Type can be one of 
`"Charm","Trinket","Jewel","Inscription","Tool","Gear","Catalyst","Unknown"`
- `nil` if the slot is empty

---

`getToolDetails()` 
Gets the details of a Vault Tool in the reader
#### returns
`table` a table of type [Tool](#tool)
`nil` if the slot is empty
#### throws
- if the Item is not a vault Tool

---

`getJewelDetails()` 
Gets the details of a Vault Jewel in the reader
#### returns
`table` a table of type [Jewel](#jewel)
`nil` if the slot is empty
#### throws
- if the Item is not a Jewel

---

`getGearDetails()` 
Gets the details of a Vault Gear in the reader
#### returns
`table` a table of type [UnidentifiedGear](#unidentifiedgear) or [Gear](#gear)
`nil` if the slot is empty
#### throws
- if the Item is not Gear
---
`getInscriptionDetails()` 
Gets the details of a Inscription in the reader
#### returns
`table` a table of type [Inscription](#inscription)
`nil` if the slot is empty
#### throws
- if the Item is not an Inscription

---
`getCatalystDetails()` 
Gets the details of a Catalyst in the reader
#### returns
`table` a table of type [Catalyst](#catalyst)
`nil` if the slot is empty
#### throws
- if the Item is not an Catalyst


---
`getTrinketDetails()` 
Gets the details of a Trinket in the reader
#### returns
`table` a table of type [Trinket](#trinket)
`nil` if the slot is empty
#### throws
- if the Item is not an Trinket
# Types
--- 
## Tool
- Name `string` the name of the item
- Type `"Tool"`
- Level `integer` The Tool Level
- Rarity [Rarity](#rarity) The Tool Rarity
- RepairSlots [RepairSlots](#repairslots)
- Durability [Durability](#durability)
- Implicits ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier))[]
- Preffixes ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier))[]
- Suffixes ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier))[]
---
## Rarity
`string` one of the following values
- SCRAPPY
- COMMON
- RARE
- EPIC
- OMEGA
---
## RepairSlots
- Total `integer` Total Repair Slots
- Used `integer` Slots Already Used Up
---
## Durability
- Total `integer` Total Durability
- Current `integer` Current Durability
---
## GearModifier
- name `string` The modifier name
Legendary `?boolean` optional, the Modifier is legendary
Crafted `?boolean` optional, the Modifier is crafted
Unusual `?boolean` optional, the Modifier is unusual
Greater `?boolean` optional, the Modifier is greated
Frozen `?boolean` optional, the Modifier is frozen
---
## ValueModifier
Extends [GearModifier](#gearmodifier) so it has all its field plus
- value `string | integer | number` The value of the modifier
---
## RangedModifier 
Extends [GearModifier](#valuemodifier) so it has all its field plus
-  tier `integer` The rolled Tier
-  min `number` The maximum possible roll for the modifier
-  max `number` The minimum possible roll for the modifier
---
## Jewel
- Name `string` the name of the item
- Type `"Jewel"`
- Level `integer` The Jewel Level
- Rarity [Rarity](#rarity) The Jewel's Rarity
- Implicits ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier)|[RangedModifier](#rangedmodifier))[]
- Preffixes ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier)|[RangedModifier](#rangedmodifier))[]
- Suffixes ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier)|[RangedModifier](#rangedmodifier))[]
---

## UnidentifiedGear
- Name `string` the name of the item
- Type `"Gear"`
- Level `integer` The Tool Level
- Rarity [Rarity](#rarity) The Gear's Rarity
- Identified `false` Is The gear Identified
--- 

## Gear 
Identified Gear Has all the fields of [UnidientifiedGear](#unidentifiedgear) plus the following
- Identified `true` overrides the one set by [UnidientifiedGear](#unidentifiedgear)
- RepairSlots [RepairSlots](#repairslots)
- Durability [Durability](#durability)
- Attributes ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier)|[RangedModifier](#rangedmodifier))[]
- Implicits ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier)|[RangedModifier](#rangedmodifier))[]
- Preffixes ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier)|[RangedModifier](#rangedmodifier))[]
- Suffixes ([GearModifier](#gearmodifier)|[ValueModifier](#valuemodifier)|[RangedModifier](#rangedmodifier))[]

##  Trinket
Optional fields are populated only if the trinket is identified
- Identified `boolean` Is the trinket Identified
- Name `?string` optional,The name of the Trinket
- Uses `?integer` optional,The number of uses left in the trinket
- Slot `?string` optional, The slot the trinket uses

##  Inscription
- Size `integer` The size of the Inscription
- Rooms `string[]` The names of the rooms the Inscription adds

##  Catalyst
- Size `integer` The size of the Catalyst
- Modifiers `string[]` The resource Locations of the added vault effects eg: "the_vault:challenger_stack"

##  charm
Optional fields are populated only if the charm is identified
- Identified `boolean` Is the charm Identified
- God `?string` optional,The God associated with the charm
- Uses `?integer` optional,The number of uses left in the charm
- Preffixes ?[ValueModifier](#valuemodifier)[] optional, The Preffixes on the Charm