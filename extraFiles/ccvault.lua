---@meta

---@alias Rarity
---| "SCRAPPY"
---| "COMMON"
---| "RARE"
---| "EPIC"
---| "OMEGA"

---@alias ItemTypes
---|"Charm"
---|"Trinket"
---|"Jewel"
---|"Inscription"
---|"Tool"
---|"Gear"
---|"Catalyst"
---|"Charm"
---|"Unknown"


---@class GearModifier
---@field name string
---@field Legendary? boolean
---@field Crafted? boolean
---@field Unusual? boolean
---@field Greater? boolean
---@field Frozen? boolean

---@class ValueModifier : GearModifier
---@field value string | integer | number


---@class RangedModifier : ValueModifier
---@field tier  integer
---@field min  integer | number
---@field max  integer | number

---@class RepairSlots
---@field Total integer Total Repair Slots
---@field Used integer Slots Already Used Up

---@class Durability
---@field Total integer Total Durability
---@field Current integer Current Durability

---@class Jewel
---@field Name string the name of the item
---@field Type "Jewel"
---@field Level integer The Jewel Level
---@field Rarity Rarity The Jewels Rarity
---@field Implicits (GearModifier|ValueModifier|RangedModifier)[]
---@field Prefixes (GearModifier|ValueModifier|RangedModifier)[]
---@field Suffixes (GearModifier|ValueModifier|RangedModifier)[]

---@class Tool
---@field Name string the name of the item
---@field Type "Tool"
---@field Level integer The Tool Level
---@field Rarity Rarity The Tool Rarity
---@field RepairSlots RepairSlots
---@field Durability Durability
---@field Implicits (GearModifier|ValueModifier)[]
---@field Prefixes (GearModifier|ValueModifier)[]
---@field Suffixes (GearModifier|ValueModifier)[]

---@class UnidentifiedGear
---@field Name string the name of the item
---@field Type "Gear"
---@field Level integer The Tool Level
---@field Rarity Rarity The Tool Rarity
---@field Identified false Is The gear Identified

---@class Gear : UnidentifiedGear
---@field Identified true
---@field RepairSlots RepairSlots
---@field Durability Durability
---@field Attributes (GearModifier|ValueModifier|RangedModifier)[]
---@field Implicits (GearModifier|ValueModifier|RangedModifier)[]
---@field Prefixes (GearModifier|ValueModifier|RangedModifier)[]
---@field Suffixes (GearModifier|ValueModifier|RangedModifier)[]

---@class Trinket
---@field Identified boolean
---@field Uses? integer
---@field Slot? string
---@field Name? string

---@class Inscription
---@field Size integer
---@field Rooms string[]

---@class Catalyst
---@field Size integer
---@field Modifiers string[]


---@class Charm
---@field Identified boolean
---@field Uses? integer
---@field God? string
---@field Prefixes? ValueModifier[]

---@class vaultReader: ccTweaked.peripheral.Inventory
local reader = {}

---@return integer level level of the item inside the reader
function reader.getItemLevel() end

---@return Rarity rarity the item rarity in full caps:
function reader.getRarity() end

---@return integer slots max repair slots of the item
function reader.getRepairSlots() end

---@return integer slots the amount of repair slots that have been used on the item
function reader.getUsedRepairSlots() end

---@return integer count the amount of implicit slots the item has
function reader.getImplicitCount() end

---@return integer count the amount of prefix slots the item has (including empty ones)
function reader.getPrefixCount() end

---@return integer count the amount of suffix slots the item has (including empty ones)
function reader.getSuffixCount() end

---@class modifierString: string

---@return (GearModifier|ValueModifier|RangedModifier)[]
function reader.getImplicits(index) end

---@return (GearModifier|ValueModifier|RangedModifier)[]
function reader.getPrefixes() end

---@return (GearModifier|ValueModifier|RangedModifier)[]
function reader.getSuffixes() end

---@return ItemTypes |
---| nil # if the slot is empty
function reader.getItemType() end

---@return Jewel
---| nil # if the slot is empty
---@throws If the item isnt Jewel
function reader.getJewelDetails() end

---@return Tool
---| nil # if the slot is empty
---@throws If the item isnt a Tool
function reader.getToolDetails() end

---@return UnidentifiedGear | Gear |nil
---@throws If the item isnt Gear
function reader.getGearDetails() end

---@return Trinket |nil
---@throws If the item isnt an Trinket
function reader.getTrinketDetails() end

---@return Inscription |nil
---@throws If the item isnt an Inscription
function reader.getInscriptionDetails() end

---@return Catalyst |nil
---@throws If the item isnt a Catalyst
function reader.getCatalystDetails() end
