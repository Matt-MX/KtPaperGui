package com.mattmx.ktgui.item

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.material.MaterialData
import java.util.*
import java.util.function.Consumer

class ItemBuilder {
    private var item: ItemStack
    private var itemMeta: ItemMeta?

    /**
     * Init item chainable via given Material parameter.
     *
     * @param itemType
     * the [Material] to initiate the instance with.
     *
     * @since 1.0
     */
    constructor(itemType: Material?) {
        item = ItemStack(itemType!!)
        itemMeta = item.itemMeta
    }

    /**
     * Init item chainable via given ItemStack parameter.
     *
     * @param itemStack
     * the [ItemStack] to initialize the instance with.
     *
     * @since 1.0
     */
    constructor(itemStack: ItemStack) {
        item = itemStack
        itemMeta = item.itemMeta
    }

    /**
     * Init the item chainable with no defined Material/ItemStack
     *
     * @since 1.0
     */
    constructor() {
        item = ItemStack(Material.AIR)
        itemMeta = item.itemMeta
    }

    fun replaceName(regex: String?, replace: String?): ItemBuilder {
        itemMeta!!.setDisplayName(itemMeta!!.displayName.replace(regex!!, replace!!))
        return this
    }

    fun clearName(): ItemBuilder {
        itemMeta!!.setDisplayName(null)
        return this
    }

    /**
     * Changes the Material type of the [ItemStack]
     *
     * @param material
     * the new [Material] to set for the ItemStack.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun type(material: Material?): ItemBuilder {
        item.type = material!!
        return this
    }

    fun type(): Material {
        return item.type
    }

    /**
     * Changes the [ItemStack]s size.
     *
     * @param itemAmt
     * the new Integer count of the ItemStack.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun amount(itemAmt: Int?): ItemBuilder {
        item.amount = itemAmt!!
        return this
    }

    fun amount(): Int {
        return item.amount
    }

    /**
     * Changes the [ItemStack]s display name.
     *
     * @param name
     * the new String for the ItemStack's display name to be set to.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun name(name: String?): ItemBuilder {
        meta()?.setDisplayName(name)
        make().itemMeta = meta()
        return this
    }

    /**
     * Adds a line of lore to the [ItemStack]
     *
     * @param lore
     * String you want to add to the ItemStack's lore.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun lore(lore: String?): ItemBuilder {
        var lores = meta()!!.lore
        if (lores == null) {
            lores = ArrayList()
        }
        lores.add(lore)
        meta()?.lore = lores
        make().itemMeta = meta()
        return this
    }

    fun lore(lores: Array<String?>): ItemBuilder {
        var lore = meta()?.lore
        if (lore == null) {
            lore = ArrayList()
        }
        lore.addAll(Arrays.asList(*lores))
        meta()?.lore = lore
        return this
    }

    /**
     * Clears the [ItemStack]s lore and replaces it with the defined String array.
     *
     * @param lores
     * String array you want to set the ItemStack's lore to.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun lores(lores: Array<String?>): ItemBuilder {
//        List<String> loresList = meta().getLore();
//        if(loresList == null){loresList = new ArrayList<>();}
//        else{loresList.clear();}
//        Collections.addAll(loresList, lores);
        val loresList = Arrays.asList(*lores)
        meta()?.lore = loresList
        return this
    }

    fun lores(loresList: List<String?>?): ItemBuilder {
        meta()?.lore = loresList
        return this
    }

    /**
     * Changes the durability of the current [ItemStack]
     *
     * @param durability
     * the new int amount to set the ItemStack's durability to.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun durability(durability: Int): ItemBuilder {
        item.durability = durability.toShort()
        return this
    }

    /**
     * Changes the data value of the [ItemStack]
     *
     * @param data
     * the new int data value (parsed as byte) to set the ItemStack's durability to.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun data(data: Int): ItemBuilder {
        item.data = MaterialData(make().type, data.toByte())
        return this
    }

    /**
     * Adds and UnsafeEnchantment to the [ItemStack] with a defined level int value.
     *
     * @param enchantment
     * the [Enchantment] to add to the ItemStack.
     *
     * @param level
     * the int amount that the Enchantment's level will be set to.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun enchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        itemMeta?.addEnchant(enchantment, level, true)
        return this
    }

    val enchantments: Map<Enchantment, Int>
        get() = item.enchantments

    /**
     * Adds and UnsafeEnchantment to the {@Link} with a level int value of 1.
     *
     * @param enchantment
     * the [Enchantment] to add to the ItemStack.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun enchantment(enchantment: Enchantment): ItemBuilder {
        itemMeta?.addEnchant(enchantment, 1, true)
        return this
    }

    /**
     * Clears all [Enchantment]s from the current [ItemStack] then adds the defined array of Enchantments to the ItemStack.
     *
     * @param enchantments
     * the Enchantment array to replace any current enchantments applied on the ItemStack.
     *
     * @param level
     * the int level value for all Enchantments to be set to.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun enchantments(enchantments: Array<Enchantment>, level: Int): ItemBuilder {
        item.enchantments.clear()
        for (enchantment in enchantments) {
            item.addUnsafeEnchantment(enchantment, level)
        }
        return this
    }

    /**
     * Clears all [Enchantment]s from the current [ItemStack] then adds the defined array of Enchantments to the ItemStack with a level int value of 1.
     *
     * @param enchantments
     * the Enchantment array to replace any current enchantments applied on the ItemStack.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    //    public ItemBuilder enchantments(final Enchantment[] enchantments){
    //        itemMeta.getEnchantments().clear();
    //        for(Enchantment enchantment : enchantments){
    //            itemMeta.addUnsafeEnchantment(enchantment, 1);
    //        }
    //        return this;
    //    }
    fun enchantments(enchantments: Map<Enchantment, Int>): ItemBuilder {
        itemMeta?.enchants?.clear()
        enchantments.keys.forEach(Consumer { e: Enchantment ->
            itemMeta?.addEnchant(
                e,
                enchantments[e]!!, true
            )
        })
        return this
    }

    fun addEnchantments(enchantments: Map<Enchantment, Int>): ItemBuilder {
        enchantments.keys.forEach(Consumer { e: Enchantment ->
            itemMeta?.addEnchant(
                e,
                enchantments[e]!!, true
            )
        })
        return this
    }

    /**
     * Clears the defined [Enchantment] from the [ItemStack]
     *
     * @param enchantment
     * the Enchantment to remove from the ItemStack.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun clearEnchantment(enchantment: Enchantment): ItemBuilder {
        val itemEnchantments = make().enchantments
        for (enchantmentC in itemEnchantments.keys) {
            if (enchantment === enchantmentC) {
                itemEnchantments.remove(enchantmentC)
            }
        }
        return this
    }

    /**
     * Clears all [Enchantment]s from the [ItemStack]
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun clearEnchantments(): ItemBuilder {
        for (e in item.enchantments.keys) {
            item.removeEnchantment(e)
        }
        for (e in itemMeta?.enchants?.keys!!) {
            itemMeta?.removeEnchant(e)
        }
        return this
    }

    fun setEnchantment(e: Enchantment, level: Int): ItemBuilder {
        clearEnchantments()
        item.addUnsafeEnchantment(e, level)
        return this
    }

    /**
     * Clears the defined [String] of lore from the [ItemStack]
     *
     * @param lore
     * the String to be removed from the ItemStack.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun clearLore(lore: String): ItemBuilder {
        if (meta()?.lore?.contains(lore) == true) {
            meta()?.lore?.remove(lore)
        }
        make().itemMeta = meta()
        return this
    }

    /**
     * Clears all lore [String]s from the [ItemStack]
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun clearLores(): ItemBuilder {
//        meta().getLore().clear();
//        make().setItemMeta(meta());
        meta()?.lore = null
        return this
    }

    fun clearLores(i1: Int): ItemBuilder {
        val newList = meta()!!.lore
        newList?.subList(i1, newList.size)?.clear()
        meta()?.lore = newList
        return this
    }

    fun clearLores(i1: Int, i2: Int): ItemBuilder {
        val newList = meta()?.lore
        newList?.subList(i1, i2)?.clear()
        meta()?.lore = newList
        return this
    }

    /**
     * Sets the [Color] of any LEATHER_ARMOR [Material] types of the [ItemStack]
     *
     * @param color
     * the Color to set the LEATHER_ARMOR ItemStack to.
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun color(color: Color): ItemBuilder {
        if (make().type == Material.LEATHER_HELMET || make().type == Material.LEATHER_CHESTPLATE || make().type == Material.LEATHER_LEGGINGS || make().type == Material.LEATHER_BOOTS) {
            val meta = meta() as LeatherArmorMeta?
            meta!!.setColor(color)
            make().itemMeta = meta
        }
        return this
    }

    /**
     * Clears the [Color] of any LEATHER_ARMOR [Material] types of the [ItemStack]
     *
     * @return the current instance for chainable application.
     * @since 1.0
     */
    fun clearColor(): ItemBuilder {
        if (make().type == Material.LEATHER_HELMET || make().type == Material.LEATHER_CHESTPLATE || make().type == Material.LEATHER_LEGGINGS || make().type == Material.LEATHER_BOOTS) {
            val meta = meta() as LeatherArmorMeta?
            meta!!.setColor(null)
            make().itemMeta = meta
        }
        return this
    }

    /**
     * Sets the skullOwner [SkullMeta] of the current SKULL_ITEM [Material] type [ItemStack]
     *
     * @param name
     * the [String] value to set the SkullOwner meta to for the SKULL_ITEM Material type ItemStack.
     *
     * @return the current instance for chainable application
     * @since 1.0
     */
    fun skullOwner(name: String?): ItemBuilder {
        if (make().type == Material.PLAYER_HEAD && make().durability == 3.toByte().toShort()) {
            val skullMeta = meta() as SkullMeta?
            skullMeta!!.owner = name
            make().itemMeta = meta()
        }
        return this
    }

    /**
     * Returns the [ItemMeta] of the [ItemStack]
     *
     * @return the ItemMeta of the ItemStack.
     */
    fun meta(): ItemMeta? {
        return itemMeta
    }

    /**
     * Returns the [ItemStack] of the [ItemBuilder] instance.
     *
     * @return the ItemStack of the ItemBuilder instance.
     */
    fun make(): ItemStack {
        item.itemMeta = itemMeta?.clone()
        return item.clone()
    }

    fun copy(): ItemBuilder {
        val ib = ItemBuilder(item.type)
        ib.item = item.clone()
        ib.itemMeta = itemMeta?.clone()
        return ib
    }

    companion object {
        fun from(i: ItemStack): ItemBuilder {
            val builder = ItemBuilder(i.type)
            builder.item = i
            builder.itemMeta = i.itemMeta
            return builder
        }
    }
}