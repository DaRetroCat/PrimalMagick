package com.verdantartifice.primalmagic.common.containers.slots;

import java.util.Optional;

import com.verdantartifice.primalmagic.common.crafting.IArcaneRecipe;
import com.verdantartifice.primalmagic.common.crafting.RecipeTypesPM;
import com.verdantartifice.primalmagic.common.crafting.WandInventory;
import com.verdantartifice.primalmagic.common.research.ResearchDiscipline;
import com.verdantartifice.primalmagic.common.research.ResearchDisciplines;
import com.verdantartifice.primalmagic.common.research.ResearchEntries;
import com.verdantartifice.primalmagic.common.research.ResearchEntry;
import com.verdantartifice.primalmagic.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagic.common.sources.SourceList;
import com.verdantartifice.primalmagic.common.stats.Stat;
import com.verdantartifice.primalmagic.common.stats.StatsManager;
import com.verdantartifice.primalmagic.common.wands.IWand;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fmllegacy.hooks.BasicEventHooks;

/**
 * Custom GUI slot for arcane crafting results.
 * 
 * @author Daedalus4096
 */
public class ArcaneCraftingResultSlot extends Slot {
    protected final CraftingContainer craftingInventory;
    protected final WandInventory wandInventory;
    protected final Player player;
    protected int amountCrafted;

    public ArcaneCraftingResultSlot(Player player, CraftingContainer craftingInventory, WandInventory wandInventory, Container inventoryIn, int slotIndex, int xPosition, int yPosition) {
        super(inventoryIn, slotIndex, xPosition, yPosition);
        this.craftingInventory = craftingInventory;
        this.wandInventory = wandInventory;
        this.player = player;
    }
    
    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
    
    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.amountCrafted += Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }
    
    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    protected void onSwapCraft(int amount) {
        this.amountCrafted += amount;
    }
    
    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        // Do additional processing if the crafted recipe was arcane
        if (this.container instanceof RecipeHolder) {
            RecipeHolder holder = (RecipeHolder)this.container;
            if (holder.getRecipeUsed() != null && holder.getRecipeUsed() instanceof IArcaneRecipe) {
                // Consume the recipe's mana cost from the wand
                SourceList manaCosts = ((IArcaneRecipe)holder.getRecipeUsed()).getManaCosts();
                if (!manaCosts.isEmpty()) {
                    ItemStack wandStack = this.wandInventory.getItem(0);
                    if (wandStack != null && !wandStack.isEmpty() && wandStack.getItem() instanceof IWand) {
                        ((IWand)wandStack.getItem()).consumeRealMana(wandStack, this.player, manaCosts);
                    }
                }
                
                // Increment the craft counter for the recipe's discipline
                if (this.player instanceof ServerPlayer) {
                    SimpleResearchKey key = ((IArcaneRecipe)holder.getRecipeUsed()).getRequiredResearch();
                    ResearchEntry entry = ResearchEntries.getEntry(key);
                    if (entry != null) {
                        ResearchDiscipline disc = ResearchDisciplines.getDiscipline(entry.getDisciplineKey());
                        if (disc != null) {
                            Stat craftingStat = disc.getCraftingStat();
                            if (craftingStat != null) {
                                StatsManager.incrementValue((ServerPlayer)this.player, craftingStat, stack.getCount());
                            }
                        }
                    }
                }
            }
        }
        
        // Fire crafting handlers
        if (this.amountCrafted > 0) {
            stack.onCraftedBy(this.player.level, this.player, this.amountCrafted);
            BasicEventHooks.firePlayerCraftingEvent(this.player, stack, this.craftingInventory);
        }
        if (this.container instanceof RecipeHolder) {
            ((RecipeHolder)this.container).awardUsedRecipes(this.player);
        }
        
        // Reset crafted amount
        this.amountCrafted = 0;
    }
    
    @Override
    public ItemStack onTake(Player thePlayer, ItemStack stack) {
        this.checkTakeAchievements(stack);
        ForgeHooks.setCraftingPlayer(thePlayer);
        
        // Get the remaining items from the recipe, checking arcane recipes first, then vanilla recipes
        NonNullList<ItemStack> remainingList;
        Optional<IArcaneRecipe> arcaneOptional = thePlayer.level.getRecipeManager().getRecipeFor(RecipeTypesPM.ARCANE_CRAFTING, this.craftingInventory, thePlayer.level);
        if (arcaneOptional.isPresent()) {
            remainingList = arcaneOptional.get().getRemainingItems(this.craftingInventory);
        } else {
            Optional<CraftingRecipe> vanillaOptional = thePlayer.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, this.craftingInventory, thePlayer.level);
            if (vanillaOptional.isPresent()) {
                remainingList = vanillaOptional.get().getRemainingItems(this.craftingInventory);
            } else {
                remainingList = NonNullList.withSize(this.craftingInventory.getContainerSize(), ItemStack.EMPTY);
                for (int index = 0; index < remainingList.size(); index++) {
                    remainingList.set(index, this.craftingInventory.getItem(index));
                }
            }
        }
        
        ForgeHooks.setCraftingPlayer(null);
        
        for (int index = 0; index < remainingList.size(); index++) {
            ItemStack materialStack = this.craftingInventory.getItem(index);
            ItemStack remainingStack = remainingList.get(index);
            if (!materialStack.isEmpty()) {
                this.craftingInventory.removeItem(index, 1);
                materialStack = this.craftingInventory.getItem(index);
            }
            
            if (!remainingStack.isEmpty()) {
                if (materialStack.isEmpty()) {
                    this.craftingInventory.setItem(index, remainingStack);
                } else if (ItemStack.isSame(materialStack, remainingStack) && ItemStack.tagMatches(materialStack, remainingStack)) {
                    remainingStack.grow(materialStack.getCount());
                    this.craftingInventory.setItem(index, remainingStack);
                } else if (!this.player.getInventory().add(remainingStack)) {
                    this.player.drop(remainingStack, false);
                }
            }
        }
        
        return stack;
    }
}
